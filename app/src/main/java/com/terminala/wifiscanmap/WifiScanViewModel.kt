package com.terminala.wifiscanmap

import android.app.Application
import android.net.wifi.ScanResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.terminala.wifiscanmap.database.WifiScanDatabase
import com.terminala.wifiscanmap.database.WifiScanRepository
import kotlinx.coroutines.launch

class WifiScanViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WifiScanRepository

    init {
        val database = WifiScanDatabase.getDatabase(application)
        repository = WifiScanRepository(database.scanDao())
    }

    fun saveScan(normalizedX: Double, normalizedY: Double, scanResults: List<ScanResult>) {
        viewModelScope.launch {
            repository.saveScan(normalizedX, normalizedY, scanResults)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WifiScanViewModel(application) as T
        }
    }
}