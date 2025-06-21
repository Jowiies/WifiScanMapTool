package com.terminala.wifiscanmap
import android.content.Context
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ScanFileManager(private val context: Context) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun saveScan(scanData: WifiScanData): String {
        return try {
            val timestamp = dateFormat.format(Date(scanData.timestamp))
            val fileName = "scan_${timestamp}.json"
            val file = File(context.filesDir, fileName)
            file.writeText(json.encodeToString(scanData))
            fileName
        } catch (e: Exception) {
            throw Exception("Failed to save scan: ${e.message}")
        }
    }

    fun getScanFiles(): List<String> {
        return context.filesDir.listFiles()
            ?.filter { it.name.startsWith("scan_") && it.name.endsWith(".json") }
            ?.map { it.name }
            ?: emptyList()
    }

    fun getScan(fileName: String): WifiScanData? {
        return try {
            val file = File(context.filesDir, fileName)
            json.decodeFromString(file.readText())
        } catch (e: Exception) {
            Log.e("FileRead", "Error reading file $fileName", e)
            null
        }
    }
}