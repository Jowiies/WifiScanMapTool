package com.terminala.wifiscanmap.discarded.database

import android.net.wifi.ScanResult
import com.terminala.wifiscanmap.discarded.database.dao.ScanDao
import com.terminala.wifiscanmap.discarded.database.entitites.ScanLocation
import com.terminala.wifiscanmap.discarded.database.entitites.WifiMeasurement

class WifiScanRepository(private val scanDao: ScanDao) {
    suspend fun saveScan(normalizedX: Double, normalizedY: Double, scanResults: List<ScanResult>) {
        val locationId = scanDao.insertLocation(
            ScanLocation(
                normalizedX = normalizedX,
                normalizedY = normalizedY
            )
        )

        val measurements = scanResults.map { result ->
            WifiMeasurement(
                locationId = locationId,
                bssid = result.BSSID,
                ssid = result.SSID,
                rssi = result.level.toDouble(),
                frequency = result.frequency
            )
        }

        scanDao.insertMeasurements(measurements)
    }
}