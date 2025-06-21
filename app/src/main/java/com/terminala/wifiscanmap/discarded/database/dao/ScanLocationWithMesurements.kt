package com.terminala.wifiscanmap.discarded.database.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.terminala.wifiscanmap.discarded.database.entitites.ScanLocation
import com.terminala.wifiscanmap.discarded.database.entitites.WifiMeasurement

data class ScanLocationWithMeasurements(
    @Embedded val location: ScanLocation,
    @Relation(
        parentColumn = "id",
        entityColumn = "location_id"
    )
    val measurements: List<WifiMeasurement>
)