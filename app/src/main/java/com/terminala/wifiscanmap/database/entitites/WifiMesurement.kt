package com.terminala.wifiscanmap.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wifi_measurements",
    foreignKeys = [
        ForeignKey(
            entity = ScanLocation::class,
            parentColumns = ["id"],
            childColumns = ["location_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["location_id"])]
)
data class WifiMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "location_id")
    val locationId: Long,

    @ColumnInfo(name = "bssid")
    val bssid: String,

    @ColumnInfo(name = "ssid")
    val ssid: String,

    @ColumnInfo(name = "rssi")
    val rssi: Double,

    @ColumnInfo(name = "frequency")
    val frequency: Int
)