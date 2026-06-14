package omni.toolbox.ui.screens.astronomy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
        List(200) {
            Offset(Random.nextFloat(), Random.nextFloat()) to Random.nextFloat() * 2f + 1f
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
                stars.forEach { (pos, size) ->
                    drawCircle(
                        color = Color.White.copy(alpha = Random.nextFloat() * 0.5f + 0.5f),
                        radius = size,
                        center = Offset(pos.x * this.size.width, pos.y * this.size.height)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Text("Pinch to zoom, drag to pan", color = Color.White, style = MaterialTheme.typography.bodySmall)
                Text("Interactive Star Chart (Placeholder)", color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
