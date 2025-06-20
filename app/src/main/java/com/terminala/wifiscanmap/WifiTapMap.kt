package com.terminala.wifiscanmap

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.core.content.ContextCompat

@Composable
fun WifiTapMap(
    imageRes: Int,
    context: Context = LocalContext.current
) {
    val imageSizePx = IntSize(3850, 2569)
    val aspectRatio = imageSizePx.width.toFloat() / imageSizePx.height

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var lastTapOffset by remember { mutableStateOf<Offset?>(null) }
    val scanResultsState = remember { mutableStateListOf<ScanResult>() }
    var isScanning by remember { mutableStateOf(false)}

    val wifiManager = remember {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    val scanSuccessState = remember { mutableStateOf<Boolean?>(null) }

    // Request location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Register scan result receiver
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                val appCtx = ctx?.applicationContext ?: return
                isScanning = false
                if (ContextCompat.checkSelfPermission(appCtx, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(appCtx, "Acces fine location permission not granted!!!", Toast.LENGTH_SHORT).show()
                    return
                }

                if (success) {
                    val results = wifiManager.scanResults
                    // Post results to Compose state safely
                    scanResultsState.clear()
                    scanResultsState.addAll(results)
                    scanSuccessState.value = true
                    Toast.makeText(appCtx, "Scan successful (${results.size} networks)", Toast.LENGTH_SHORT).show()
                } else {
                    scanSuccessState.value = false
                    Toast.makeText(appCtx, "Scan failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    // Main layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .onGloballyPositioned { containerSize = it.size }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, _, _ ->
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val relativeX = (tapOffset.x - offsetX).coerceIn(0f, containerSize.width.toFloat())
                        val relativeY = (tapOffset.y - offsetY).coerceIn(0f, containerSize.height.toFloat())

                        val normX = (relativeX / containerSize.width).coerceIn(0f, 1f)
                        val normY = 1f - (relativeY / containerSize.height).coerceIn(0f, 1f)

                        lastTapOffset = Offset(relativeX, relativeY)

                        Toast.makeText(
                            context,
                            "Tap normalized: x=%.2f, y=%.2f".format(normX, normY),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        ) {
            // Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Map Image",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                contentScale = ContentScale.FillBounds
            )

            // Tap marker
            lastTapOffset?.let { point ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (point.x + offsetX).toInt() - 5,
                                    y = (point.y + offsetY).toInt() - 5
                                )
                            }
                            .size(5.dp)
                            .background(Color.Red, shape = CircleShape)
                    )
                }
            }
        }

        // Reset Button
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White,
                disabledContentColor = Color.White,
                disabledContainerColor = Color.DarkGray
            ),
            onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val started = wifiManager.startScan()
                    if (!started) {
                        Toast.makeText(context, "Failed to start Wi-Fi scan", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        isScanning = true
                    }
                } else {
                    Toast.makeText(context, "Location permission required to scan Wi-Fi", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isScanning,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (isScanning) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            }
            else {
                Text("Scan")
            }
        }
    }
}
