package com.naturetools.app.ui.screens.music
import androidx.compose.ui.geometry.Offset

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun MusicToolScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (title) {
                "Guitar Tuner" -> GuitarTuner()
                else -> Text("Musical utility for $title")
            }
        }
    }
}

@Composable
fun GuitarTuner() {
    var detectedFreq by remember { mutableFloatStateOf(440f) }
    val targetFreq = 440f // A4

    val infiniteTransition = rememberInfiniteTransition()
    val drift by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tuning: A (440Hz)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height)
                rotate(degrees = (detectedFreq + drift - targetFreq) * 10, pivot = center) {
                    drawLine(
                        color = Color.Red,
                        start = center,
                        end = Offset(size.width / 2, 20f),
                        strokeWidth = 4f
                    )
                }
                drawCircle(Color.Gray, radius = 10f, center = center)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(if (Math.abs(detectedFreq + drift - targetFreq) < 0.5f) "In Tune" else "Adjust String")
    }
}
