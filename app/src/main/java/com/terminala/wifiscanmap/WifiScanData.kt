package com.terminala.wifiscanmap

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class WifiScanData(
    val coordinates: Coordinates,
    @Transient val timestamp: Long = System.currentTimeMillis(),
    val measurements: List<WifiMeasurement> = emptyList()
) {
    @Serializable
    data class Coordinates(
        val x: Double,
        val y: Double
    )

    @Serializable
    data class WifiMeasurement(
        val bssid: String,
        val rssi: Int,
    )
}