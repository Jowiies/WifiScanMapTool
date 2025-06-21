package com.terminala.wifiscanmap.discarded.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_locations")
data class ScanLocation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "normalized_x")
    val normalizedX: Double,
    @ColumnInfo(name = "normalized_y")
    val normalizedY: Double
)