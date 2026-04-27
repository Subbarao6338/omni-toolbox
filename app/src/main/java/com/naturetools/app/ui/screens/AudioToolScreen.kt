package com.naturetools.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlin.random.Random

@Composable
fun AudioToolScreen(navController: NavHostController, title: String) {
    AudioBaseScreen(navController = navController, title = title) { _, uri ->
        var isPlaying by remember { mutableStateOf(false) }
        var progress by remember { mutableFloatStateOf(0f) }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Waveform Visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                WaveformVisualizer(isPlaying = isPlaying)
            }

            // Progress Slider
            Slider(
                value = progress,
                onValueChange = { progress = it },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0:00", style = MaterialTheme.typography.bodySmall)
                Text("3:45", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { progress = (progress - 0.1f).coerceAtLeast(0f) }) {
                    Icon(Icons.Default.Replay10, contentDescription = "Rewind 10s")
                }
                Spacer(modifier = Modifier.width(16.dp))
                LargeFloatingActionButton(
                    onClick = { isPlaying = !isPlaying },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { progress = (progress + 0.1f).coerceAtMost(1f) }) {
                    Icon(Icons.Default.Forward10, contentDescription = "Forward 10s")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tool-Specific Controls
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Adjustments",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when (title) {
                        "Equalizer" -> {
                            AdjustmentSlider("Bass")
                            AdjustmentSlider("Mid")
                            AdjustmentSlider("Treble")
                        }
                        "Speed Changer" -> {
                            AdjustmentSlider("Playback Speed", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                            AdjustmentSlider("Pitch", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                        }
                        "Normalize" -> {
                            AdjustmentSlider("Target Volume")
                        }
                        "Compress" -> {
                            AdjustmentSlider("Compression Ratio", valueRange = 1f..20f, initialValue = 4f)
                            AdjustmentSlider("Threshold (dB)", valueRange = -60f..0f, initialValue = -20f)
                        }
                        "Silence Remover" -> {
                            AdjustmentSlider("Silence Threshold", valueRange = -100f..0f, initialValue = -50f)
                            AdjustmentSlider("Min Silence Duration (ms)", valueRange = 100f..5000f, initialValue = 500f)
                        }
                        "MIX" -> {
                            AdjustmentSlider("Track 1 Volume")
                            AdjustmentSlider("Track 2 Volume")
                        }
                        "Channel Manipulation" -> {
                            AdjustmentSlider("Left Volume")
                            AdjustmentSlider("Right Volume")
                        }
                        "Audio Noise Remover", "Video Noise Remover", "AI Noise Remover" -> {
                            AdjustmentSlider("Noise Reduction (dB)", valueRange = 0f..30f, initialValue = 12f)
                            AdjustmentSlider("Sensitivity")
                        }
                        "Vocal Remover" -> {
                            AdjustmentSlider("Vocal Suppression")
                            AdjustmentSlider("Center Pan Width")
                        }
                        "Echo Remover" -> {
                            AdjustmentSlider("Echo Intensity")
                            AdjustmentSlider("Delay Feedback")
                        }
                        "Reverb Remover" -> {
                            AdjustmentSlider("Room Size Reduction")
                            AdjustmentSlider("Damping")
                        }
                        "Vocal AutoTuner" -> {
                            AdjustmentSlider("Correction Amount")
                            AdjustmentSlider("Retune Speed")
                        }
                        "8d Audio" -> {
                            AdjustmentSlider("Rotation Speed", valueRange = 0.1f..2.0f, initialValue = 0.5f)
                            AdjustmentSlider("Orbit Radius")
                        }
                        "Noise Generator" -> {
                            AdjustmentSlider("White Noise Level")
                            AdjustmentSlider("Pink Noise Level")
                        }
                        "Wave Generator" -> {
                            AdjustmentSlider("Frequency (Hz)", valueRange = 20f..20000f, initialValue = 440f)
                            AdjustmentSlider("Amplitude")
                        }
                        "Key BPM finder" -> {
                            AdjustmentSlider("Detection Sensitivity")
                        }
                        "Record", "Fun Recording", "Karaoke" -> {
                            AdjustmentSlider("Microphone Gain")
                            AdjustmentSlider("Monitor Volume")
                        }
                        else -> {
                            AdjustmentSlider("Intensity")
                            AdjustmentSlider("Threshold")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Process file */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Process and Save")
            }
        }
    }
}

@Composable
fun WaveformVisualizer(isPlaying: Boolean) {
    val barCount = 40
    val bars = remember { List(barCount) { Random.nextFloat() * 0.8f + 0.2f } }
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveform_anim"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val barWidth = width / (barCount * 1.5f)
        val spacing = barWidth * 0.5f

        bars.forEachIndexed { index, baseHeight ->
            val animatedHeight = if (isPlaying) {
                val factor = (animationProgress + (index.toFloat() / barCount)).let {
                    if (it > 1f) it - 1f else it
                }
                baseHeight * (0.8f + 0.4f * kotlin.math.sin(factor * 2 * Math.PI.toFloat()))
            } else {
                baseHeight
            }

            val barHeight = animatedHeight * height
            val x = index * (barWidth + spacing)
            val y = (height - barHeight) / 2

            drawRoundRect(
                color = if (index % 2 == 0) primaryColor else secondaryColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2)
            )
        }
    }
}

@Composable
fun AdjustmentSlider(
    label: String,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    initialValue: Float = 0.5f
) {
    var value by remember { mutableFloatStateOf(initialValue) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(String.format("%.1f", value), style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = { value = it },
            valueRange = valueRange
        )
    }
}
