package omni.toolbox.ui.screens.astronomy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.random.Random

@Composable
fun StarMapScreen(navController: NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val stars = remember {
        List(300) {
            val color = when(Random.nextInt(4)) {
                0 -> Color(0xFFADCFFF) // Blue-ish
                1 -> Color(0xFFFFF4EA) // White-ish
                2 -> Color(0xFFFFD2A1) // Yellow-ish
                else -> Color(0xFFFFCC6F) // Orange-ish
            }
            Triple(Offset(Random.nextFloat(), Random.nextFloat()), Random.nextFloat() * 2.5f + 0.5f, color)
        }
    }

    ToolScreen(
        title = "Star Map",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF000510))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offset += pan
                    }
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            ) {
                stars.forEach { (pos, size, color) ->
                    drawCircle(
                        color = color.copy(alpha = Random.nextFloat() * 0.4f + 0.6f),
                        radius = size,
                        center = Offset(pos.x * this.size.width, pos.y * this.size.height)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text("Pinch to zoom, drag to pan", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
