package com.terminala.wifiscanmap.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.terminala.wifiscanmap.database.dao.ScanDao
import com.terminala.wifiscanmap.database.entitites.ScanLocation
import com.terminala.wifiscanmap.database.entitites.WifiMeasurement

@Database(
    entities = [ScanLocation::class, WifiMeasurement::class],
    version = 1,
    exportSchema = true
)
abstract class WifiScanDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao

    companion object {
        @Volatile
        private var INSTANCE: WifiScanDatabase? = null

        fun getDatabase(context: Context): WifiScanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WifiScanDatabase::class.java,
                    "wifi_scan_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("Database", "Database created")
                        }
                    })
                    .fallbackToDestructiveMigrationOnDowngrade(false)
                    .build()

                instance.openHelper.writableDatabase

                INSTANCE = instance
                instance
            }
        }
    }
}