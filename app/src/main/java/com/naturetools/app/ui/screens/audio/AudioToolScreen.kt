package com.naturetools.app.ui.screens.audio

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlin.random.Random

@Composable
fun AudioToolScreen(navController: NavHostController, title: String, mimeType: String = "audio/*") {
    AudioBaseScreen(navController = navController, title = title, mimeType = mimeType) { _, selectedFileUri ->
        var isPlaying by remember { mutableStateOf(false) }
        var progress by remember { mutableFloatStateOf(0f) }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mimeType.startsWith("image")) {
                AsyncImage(
                    model = selectedFileUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 16.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
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
                        text = "Adjustments & Filters",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val isVideo = mimeType.startsWith("video")
                    when (title) {
                        "Equalizer", "Audio Equalizer" -> {
                            AdjustmentSlider("Low Bass (32Hz)", initialValue = 0.5f)
                            AdjustmentSlider("Bass (125Hz)", initialValue = 0.5f)
                            AdjustmentSlider("Mid (500Hz)", initialValue = 0.5f)
                            AdjustmentSlider("High Mid (2kHz)", initialValue = 0.5f)
                            AdjustmentSlider("Treble (8kHz)", initialValue = 0.5f)
                        }
                        "Speed Changer", "Video Speed", "Change Speed", "Change Video Speed" -> {
                            AdjustmentSlider("Playback Speed", valueRange = 0.25f..4.0f, initialValue = 1.0f)
                            if (!isVideo) AdjustmentSlider("Maintain Pitch", valueRange = 0f..1f, initialValue = 1f)
                        }
                        "Normalize", "Audio Normalizer", "Volume Booster", "Video Volume", "Multi Volume Booster" -> {
                            AdjustmentSlider("Gain (dB)", valueRange = -20f..20f, initialValue = 0f)
                            AdjustmentSlider("Peak Normalization", initialValue = 0.95f)
                        }
                        "Compress", "Audio Compressor", "Video Compressor", "Compress Video" -> {
                            if (isVideo) {
                                AdjustmentSlider("Target Bitrate (kbps)", valueRange = 500f..10000f, initialValue = 2500f)
                                AdjustmentSlider("Video Quality (CRF)", valueRange = 0f..51f, initialValue = 23f)
                            } else {
                                AdjustmentSlider("Threshold (dB)", valueRange = -60f..0f, initialValue = -20f)
                                AdjustmentSlider("Ratio", valueRange = 1f..20f, initialValue = 4f)
                                AdjustmentSlider("Attack (ms)", valueRange = 1f..100f, initialValue = 10f)
                            }
                        }
                        "Silence Remover" -> {
                            AdjustmentSlider("Silence Threshold (dB)", valueRange = -100f..0f, initialValue = -50f)
                            AdjustmentSlider("Min Silence Duration (ms)", valueRange = 100f..5000f, initialValue = 500f)
                            AdjustmentSlider("Padding (ms)", valueRange = 0f..500f, initialValue = 100f)
                        }
                        "Audio Noise Remover", "Video Noise Remover", "Noise Remover" -> {
                            AdjustmentSlider("Noise Floor (dB)", valueRange = -100f..0f, initialValue = -60f)
                            AdjustmentSlider("Noise Reduction (dB)", valueRange = 0f..40f, initialValue = 12f)
                            AdjustmentSlider("Smoothing", initialValue = 0.5f)
                        }
                        "Vocal Remover" -> {
                            AdjustmentSlider("Vocal Reduction", initialValue = 0.8f)
                            AdjustmentSlider("Instrumental Boost", initialValue = 0.2f)
                            AdjustmentSlider("Frequency Cutoff", valueRange = 100f..10000f, initialValue = 5000f)
                        }
                        "Reverb Remover", "Echo Remover" -> {
                            AdjustmentSlider("De-reverb Intensity", initialValue = 0.6f)
                            AdjustmentSlider("Damping", initialValue = 0.4f)
                            AdjustmentSlider("Dry/Wet Mix", initialValue = 0.5f)
                        }
                        "Vocal AutoTuner", "Voice Mimic" -> {
                            AdjustmentSlider("Correction Speed", initialValue = 0.7f)
                            AdjustmentSlider("Humanize", initialValue = 0.3f)
                            AdjustmentSlider("Scale Sensitivity", initialValue = 0.5f)
                        }
                        "3D Audio", "8d Audio" -> {
                            AdjustmentSlider("Rotation Speed (Hz)", valueRange = 0.1f..5f, initialValue = 0.5f)
                            AdjustmentSlider("Orbit Radius", initialValue = 0.8f)
                            AdjustmentSlider("Doppler Effect", initialValue = 0.2f)
                        }
                        "Video Flip", "Rotate/Crop Video" -> {
                            AdjustmentSlider("Rotation (°)", valueRange = 0f..360f, initialValue = 0f)
                            AdjustmentSlider("Horizontal Flip", valueRange = 0f..1f, initialValue = 0f)
                            AdjustmentSlider("Vertical Flip", valueRange = 0f..1f, initialValue = 0f)
                        }
                        "Video To GIF" -> {
                            AdjustmentSlider("Frames Per Second", valueRange = 1f..30f, initialValue = 15f)
                            AdjustmentSlider("Scale (Width)", valueRange = 100f..1080f, initialValue = 480f)
                            AdjustmentSlider("Dithering Intensity", initialValue = 0.5f)
                        }
                        "Thumbnail Extractor" -> {
                            AdjustmentSlider("Seek Position (s)", valueRange = 0f..600f, initialValue = 10f)
                            AdjustmentSlider("Quality (JPEG)", initialValue = 0.9f)
                        }
                        "Audio Converter", "Convert to MP4", "Multi Convert" -> {
                            AdjustmentSlider("Sample Rate (kHz)", valueRange = 8f..192f, initialValue = 44.1f)
                            AdjustmentSlider("Channels (1:Mono, 2:Stereo)", valueRange = 1f..2f, initialValue = 2f)
                            AdjustmentSlider("Bitrate (kbps)", valueRange = 32f..320f, initialValue = 192f)
                        }
                        "Record Audio", "Karaoke Maker" -> {
                            AdjustmentSlider("Input Gain", initialValue = 0.5f)
                            AdjustmentSlider("Mic Echo", initialValue = 0.1f)
                            AdjustmentSlider("Noise Gate", initialValue = 0.2f)
                        }
                        else -> {
                            AdjustmentSlider("Intensity", initialValue = 0.5f)
                            AdjustmentSlider("Threshold", initialValue = 0.3f)
                            AdjustmentSlider("Quality", initialValue = 0.8f)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Process file logic */ },
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
    initialValue: Float = 0.5f,
    onValueChange: (Float) -> Unit = {}
) {
    var value by remember { mutableFloatStateOf(initialValue) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(java.lang.String.format(java.util.Locale.US, "%.2f", value), style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = {
                value = it
                onValueChange(it)
            },
            valueRange = valueRange
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}
