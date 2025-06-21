package com.terminala.wifiscanmap

import android.app.Application
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WifiScanViewModel(application: Application) : AndroidViewModel(application) {
    private val fileManager = ScanFileManager(application)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun saveScan(x: Double, y: Double, scanResults: List<ScanResult>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scanData = WifiScanData(
                    coordinates = WifiScanData.Coordinates(x, y),
                    timestamp = System.currentTimeMillis(),
                    measurements = scanResults.map {
                        WifiScanData.WifiMeasurement(
                            bssid = it.BSSID.replace(":",""),
                            rssi = it.level,
                        )
                    }
                )

                val fileName = fileManager.saveScan(scanData)
                Log.d("ScanSave", "Scan saved to $fileName")
            } catch (e: Exception) {
                Log.e("ScanSave", "Failed to save scan", e)
            }
        }
    }

    fun getFormattedScanList(): List<String> {
        return fileManager.getScanFiles().mapNotNull { fileName ->
            fileManager.getScan(fileName)?.let { scan ->
                "Scan at ${dateFormat.format(Date(scan.timestamp))} - " +
                        "${scan.measurements.size} networks at " +
                        "(${String.format("%.2f", scan.coordinates.x)}, " +
                        "${String.format("%.2f", scan.coordinates.y)})"
            }
        }
    }
}