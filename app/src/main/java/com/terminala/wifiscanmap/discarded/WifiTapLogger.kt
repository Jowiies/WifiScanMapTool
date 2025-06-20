package com.terminala.wifiscanmap.discarded

import android.content.*
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset

@Composable
fun WifiTapLogger(
    imageRes: Int,
    context: Context = LocalContext.current
) {
    val imageSizePx = IntSize(3850, 2569)
    val aspectRatio = imageSizePx.width.toFloat() / imageSizePx.height

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var lastTapOffset by remember { mutableStateOf<Offset?>(null) }

    val wifiManager = remember {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    val scanResultsState = remember { mutableStateListOf<ScanResult>() }

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
                        val normY = (relativeY / containerSize.height).coerceIn(0f, 1f)

                        lastTapOffset = Offset(relativeX, relativeY)

                        Toast.makeText(
                            context,
                            "Tapped normalized: x=%.2f, y=%.2f".format(normX, normY),
                            Toast.LENGTH_SHORT
                        ).show()

                        wifiManager.startScan()
                    }
                }
        ) {
            // Image with panning
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

            lastTapOffset?.let { point ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (point.x + offsetX).toInt() - 10,
                                    y = (point.y + offsetY).toInt() - 10
                                )
                            }
                            .size(5.dp)
                            .background(Color.Red, shape = CircleShape)
                    )
                }
            }
        }


        Button(
            onClick = {
                offsetX = 0f
                offsetY = 0f
                lastTapOffset = null
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Reset")
        }
    }
}
