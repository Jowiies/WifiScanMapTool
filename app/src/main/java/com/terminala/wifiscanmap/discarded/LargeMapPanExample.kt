import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ZoomableMap(
    imageRes: Int,
    scaleFactor: Float = 2f,
    context: Context = LocalContext.current
) {
    val painter = painterResource(id = imageRes)
    val intrinsicSize = painter.intrinsicSize
    val imageWidth = intrinsicSize.width
    val imageHeight = intrinsicSize.height

    // Calculate scaled size preserving aspect ratio
    val scaledWidth = imageWidth * scaleFactor
    val scaledHeight = imageHeight * scaleFactor

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        Image(
            painter = painter,
            contentDescription = "Map Image",
            modifier = Modifier
                .size(width = scaledWidth.dp, height = scaledHeight.dp)
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                },
            contentScale = ContentScale.None // Use None because we manually set size
        )
    }
}

