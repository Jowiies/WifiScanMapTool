package com.terminala.wifiscanmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.terminala.wifiscanmap.ui.theme.WifiScanMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WifiScanMapTheme {
                WifiTapMap(imageRes = R.drawable.planol)
            }
        }
    }
}