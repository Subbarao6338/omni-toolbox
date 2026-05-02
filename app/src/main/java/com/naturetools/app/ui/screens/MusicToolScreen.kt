package com.naturetools.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MusicToolScreen(navController: NavHostController, title: String) {
    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (title == "Guitar Tuner") {
                GuitarTunerTool()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$title is coming soon!")
                }
            }
        }
    }
}

@Composable
fun GuitarTunerTool() {
    var isListening by remember { mutableStateOf(false) }
    var currentFrequency by remember { mutableFloatStateOf(0f) }
    var targetNote by remember { mutableStateOf("E2") }
    var centsOff by remember { mutableFloatStateOf(0f) }

    val guitarNotes = mapOf(
        "E2" to 82.41f,
        "A2" to 110.00f,
        "D3" to 146.83f,
        "G3" to 196.00f,
        "B3" to 246.94f,
        "E4" to 329.63f
    )

    // Simulation of listening
    LaunchedEffect(isListening) {
        if (isListening) {
            while (true) {
                // Randomly drift around the target frequency
                val targetFreq = guitarNotes[targetNote] ?: 82.41f
                currentFrequency = targetFreq + (Math.random().toFloat() - 0.5f) * 10f
                centsOff = (currentFrequency - targetFreq) * 5f // Rough cents simulation
                kotlinx.coroutines.delay(100)
            }
        } else {
            currentFrequency = 0f
            centsOff = 0f
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TunerGauge(centsOff = centsOff)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = targetNote,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (Math.abs(centsOff) < 2f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = String.format("%.2f Hz", currentFrequency),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            guitarNotes.keys.forEach { note ->
                FilterChip(
                    selected = targetNote == note,
                    onClick = { targetNote = note },
                    label = { Text(note) }
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        LargeFloatingActionButton(
            onClick = { isListening = !isListening },
            containerColor = if (isListening) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop Listening" else "Start Listening",
                modifier = Modifier.size(36.dp),
                tint = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = if (isListening) "Listening..." else "Tap to start tuning",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun TunerGauge(centsOff: Float) {
    val animatedCents by animateFloatAsState(
        targetValue = centsOff.coerceIn(-50f, 50f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tuner_needle"
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Draw arc
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw ticks
            for (i in -50..50 step 10) {
                val angle = 270f + (i * 1.8f)
                val rad = angle * PI / 180f
                val start = Offset(
                    center.x + (radius - 20.dp.toPx()) * cos(rad).toFloat(),
                    center.y + (radius - 20.dp.toPx()) * sin(rad).toFloat()
                )
                val end = Offset(
                    center.x + radius * cos(rad).toFloat(),
                    center.y + radius * sin(rad).toFloat()
                )
                drawLine(
                    color = if (i == 0) Color(0xFF4CAF50) else Color.Gray,
                    start = start,
                    end = end,
                    strokeWidth = if (i == 0) 4.dp.toPx() else 2.dp.toPx()
                )
            }

            // Draw needle
            val needleAngle = 270f + (animatedCents * 1.8f)
            val needleRad = needleAngle * PI / 180f
            val needleEnd = Offset(
                center.x + (radius - 10.dp.toPx()) * cos(needleRad).toFloat(),
                center.y + (radius - 10.dp.toPx()) * sin(needleRad).toFloat()
            )

            drawLine(
                color = if (Math.abs(centsOff) < 2f) Color(0xFF4CAF50) else Color.Red,
                start = center,
                end = needleEnd,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = primaryColor,
                radius = 8.dp.toPx(),
                center = center
            )
        }
    }
}
