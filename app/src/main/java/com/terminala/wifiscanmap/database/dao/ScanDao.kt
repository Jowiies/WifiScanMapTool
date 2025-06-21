package com.terminala.wifiscanmap.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.terminala.wifiscanmap.database.entitites.ScanLocation
import com.terminala.wifiscanmap.database.entitites.WifiMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Insert
    suspend fun insertLocation(scanLocation: ScanLocation): Long

    @Insert
    suspend fun insertMeasurements(measurements: List<WifiMeasurement>)

    @Transaction
    @Query("SELECT * FROM scan_locations ORDER BY timestamp DESC")
    fun getAllScanLocationsWithMeasurements(): Flow<List<ScanLocationWithMeasurements>>

    @Query("DELETE FROM scan_locations")
    suspend fun clearAllData()
}