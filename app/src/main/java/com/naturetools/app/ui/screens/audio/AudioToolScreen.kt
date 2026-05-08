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
                        text = "Adjustments",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val isVideo = mimeType.startsWith("video")
                    when (title) {
                        "Equalizer", "Audio Equalizer" -> {
                            AdjustmentSlider("Bass")
                            AdjustmentSlider("Mid")
                            AdjustmentSlider("Treble")
                        }
                        "Speed Changer", "Video Speed", "Change Speed", "Change Video Speed" -> {
                            if (isVideo) {
                                AdjustmentSlider("Playback Speed", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                                AdjustmentSlider("Maintain Pitch (0/1)", valueRange = 0f..1f, initialValue = 1f)
                            } else {
                                AdjustmentSlider("Playback Speed", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                                AdjustmentSlider("Pitch", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                            }
                        }
                        "Normalize", "Audio Normalizer", "Volume Booster", "Video Volume", "Multi Volume Booster" -> {
                            AdjustmentSlider("Target Volume")
                        }
                        "Compress", "Audio Compressor", "Video Compressor", "Compress Video" -> {
                            if (isVideo) {
                                AdjustmentSlider("Quality (0-100)", valueRange = 0f..100f, initialValue = 80f)
                                AdjustmentSlider("Target Resolution (p)", valueRange = 144f..1080f, initialValue = 720f)
                            } else {
                                AdjustmentSlider("Compression Ratio", valueRange = 1f..20f, initialValue = 4f)
                                AdjustmentSlider("Threshold (dB)", valueRange = -60f..0f, initialValue = -20f)
                            }
                        }
                        "Silence Remover" -> {
                            AdjustmentSlider("Silence Threshold", valueRange = -100f..0f, initialValue = -50f)
                            AdjustmentSlider("Min Silence Duration (ms)", valueRange = 100f..5000f, initialValue = 500f)
                        }
                        "MIX", "Audio Mixer", "Multi Mix Audio" -> {
                            AdjustmentSlider("Track 1 Volume")
                            AdjustmentSlider("Track 2 Volume")
                        }
                        "Audio Pan" -> {
                            AdjustmentSlider("Left/Right Balance", valueRange = -1f..1f, initialValue = 0f)
                        }
                        "Audio Noise Remover", "Video Noise Remover", "AI Noise Remover", "Noise Remover" -> {
                            AdjustmentSlider("Noise Reduction (dB)", valueRange = 0f..30f, initialValue = 12f)
                            AdjustmentSlider("Sensitivity")
                        }
                        "Video Frame Annotator" -> {
                            AdjustmentSlider("Frame Number", valueRange = 0f..1000f, initialValue = 0f)
                            AdjustmentSlider("Pen Size", valueRange = 1f..20f, initialValue = 5f)
                            Text("Draw on the video frame selected.")
                        }
                        "Merge Videos" -> {
                            Text("Select multiple files to merge.")
                            AdjustmentSlider("Overlap Duration (s)", valueRange = 0f..5f, initialValue = 0f)
                        }
                        "Convert to MP4", "Audio Converter" -> {
                            Text("Select target format and quality.")
                            AdjustmentSlider("Quality (0-100)", valueRange = 0f..100f, initialValue = 85f)
                        }
                        "Video to PDF" -> {
                            Text("Converting to PDF format.")
                            AdjustmentSlider("Page Margin", valueRange = 0f..50f, initialValue = 20f)
                        }
                        "Video to Images" -> {
                            Text("Extracting frames.")
                            AdjustmentSlider("Interval/Frequency")
                        }
                        "Rotate/Crop Video" -> {
                            AdjustmentSlider("Rotation Angle", valueRange = 0f..360f, initialValue = 0f)
                            AdjustmentSlider("Crop Scale", valueRange = 0.1f..1.0f, initialValue = 1.0f)
                        }
                        "Mute/Extract Audio" -> {
                            AdjustmentSlider("Extract Track Index", valueRange = 0f..5f, initialValue = 0f)
                            Text("Enable 'Mute' to remove audio.")
                        }
                        "Thumbnail Extractor" -> {
                            AdjustmentSlider("Timestamp (s)", valueRange = 0f..300f, initialValue = 10f)
                            Text("Capture moment as thumbnail.")
                        }
                        "Add Audio to Video" -> {
                            Text("Layer audio over video.")
                            AdjustmentSlider("Audio Volume Balance", valueRange = 0f..1f, initialValue = 0.5f)
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
                        "Text to Speech", "Text To Speech" -> {
                            AdjustmentSlider("Pitch", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                            AdjustmentSlider("Speech Rate", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                        }
                        "8d Audio", "3D Audio" -> {
                            AdjustmentSlider("Rotation Speed", valueRange = 0.1f..2.0f, initialValue = 0.5f)
                            AdjustmentSlider("Orbit Radius")
                        }
                        "Noise Generator" -> {
                            AdjustmentSlider("Noise Level")
                            AdjustmentSlider("White/Pink/Brown (0-1)", valueRange = 0f..1f, initialValue = 0f)
                        }
                        "Wave Generator" -> {
                            AdjustmentSlider("Frequency (Hz)", valueRange = 20f..20000f, initialValue = 440f)
                            AdjustmentSlider("Amplitude")
                        }
                        "Key BPM Finder", "Key BPM finder" -> {
                            AdjustmentSlider("Detection Sensitivity")
                        }
                        "Trim", "Splitter", "Delete", "Silence", "Audio Cutter", "Audio Splitter", "Video Editor", "Video Splitter", "Delete Segment", "Silence Video" -> {
                            AdjustmentSlider("Start Position (s)", valueRange = 0f..300f, initialValue = 0f)
                            AdjustmentSlider("End Position (s)", valueRange = 0f..300f, initialValue = 30f)
                        }
                        "Mix Video Audio" -> {
                            AdjustmentSlider("Video Volume")
                            AdjustmentSlider("Audio Overlay Volume")
                        }
                        "Video SFX" -> {
                            AdjustmentSlider("Effect Intensity")
                            AdjustmentSlider("Color Saturation")
                        }
                        "Video To GIF" -> {
                            AdjustmentSlider("FPS", valueRange = 1f..60f, initialValue = 15f)
                            AdjustmentSlider("Loop Count", valueRange = 0f..10f, initialValue = 0f)
                        }
                        "Loop", "Audio Loop", "Loop Video" -> {
                            AdjustmentSlider("Repeat Count", valueRange = 1f..100f, initialValue = 1f)
                        }
                        "Record", "Fun Recording", "Karaoke", "Karaoke Maker", "Record Audio", "Karaoke Effect" -> {
                            AdjustmentSlider("Microphone Gain")
                            AdjustmentSlider("Monitor Volume")
                        }
                        "Pitch Changer", "Audio Pitch" -> {
                            AdjustmentSlider("Pitch Octave", valueRange = -2f..2f, initialValue = 0f)
                        }
                        "Bass Booster" -> {
                            AdjustmentSlider("Bass Gain (dB)", valueRange = 0f..15f, initialValue = 6f)
                        }
                        "Echo Effect", "Audio Echo" -> {
                            AdjustmentSlider("Echo Delay", valueRange = 0f..2f, initialValue = 0.5f)
                            AdjustmentSlider("Echo Decay", valueRange = 0f..1f, initialValue = 0.5f)
                        }
                        "Scientific Calc" -> {
                            Text("Scientific mode active. Advanced functions: sin, cos, tan, log, ln, sqrt.")
                            AdjustmentSlider("Precision (dec)", valueRange = 0f..10f, initialValue = 2f)
                        }
                        "Device ID" -> {
                            val deviceId = remember { java.util.UUID.randomUUID().toString().take(16) }
                            Text("Hardware ID: $deviceId")
                            Text("Model: ${android.os.Build.MODEL}")
                            Text("OS Version: ${android.os.Build.VERSION.RELEASE}")
                        }
                        "Air Quality" -> {
                            Text("Location-based Air Quality Index (AQI)")
                            AdjustmentSlider("Refresh Rate (min)", valueRange = 1f..60f, initialValue = 15f)
                        }
                        "UV Index" -> {
                            Text("Current UV Intensity")
                            AdjustmentSlider("Sensitivity")
                        }
                        "Habit Tracker" -> {
                            Text("Daily Progress")
                            AdjustmentSlider("Target Goal", valueRange = 1f..10f, initialValue = 5f)
                        }
                        "Meditation Timer" -> {
                            AdjustmentSlider("Duration (min)", valueRange = 1f..60f, initialValue = 10f)
                            AdjustmentSlider("Interval Bell (min)", valueRange = 0f..30f, initialValue = 0f)
                        }
                        "SPL Meter" -> {
                            Text("Sound Pressure Level (dB)")
                            AdjustmentSlider("Calibration (dB)", valueRange = -10f..10f, initialValue = 0f)
                        }
                        "Data Visualizer" -> {
                            Text("Import CSV or JSON to visualize")
                            AdjustmentSlider("Graph Type (0: Bar, 1: Line)", valueRange = 0f..1f, initialValue = 0f)
                        }
                        "AI Image Gen" -> {
                            Text("Enter prompt for image generation")
                            AdjustmentSlider("Steps", valueRange = 10f..50f, initialValue = 20f)
                        }
                        "Base Converter" -> {
                            Text("Convert between Binary, Octal, Hex, Decimal")
                            AdjustmentSlider("Target Base", valueRange = 2f..36f, initialValue = 16f)
                        }
                        "Constants Table" -> {
                            Text("Scientific and Physical Constants (Pi, e, G, c, etc.)")
                        }
                        "Light Pollution" -> {
                            Text("Bortle Scale and Sky Brightness")
                            AdjustmentSlider("Latitude")
                            AdjustmentSlider("Longitude")
                        }
                        "Tax Calculator" -> {
                            AdjustmentSlider("Income Amount")
                            AdjustmentSlider("Tax Rate (%)", valueRange = 0f..50f, initialValue = 15f)
                        }
                        "Calorie Calc" -> {
                            AdjustmentSlider("Weight (kg)", valueRange = 30f..200f, initialValue = 70f)
                            AdjustmentSlider("Activity Level", valueRange = 1f..2f, initialValue = 1.2f)
                        }
                        "Exif Viewer" -> {
                            Text("Select an image to view EXIF metadata")
                        }
                        "Port Scanner" -> {
                            Text("Scan for open ports on an IP")
                            AdjustmentSlider("Start Port", valueRange = 1f..65535f, initialValue = 80f)
                        }
                        "Pomodoro" -> {
                            AdjustmentSlider("Work Duration (min)", valueRange = 1f..60f, initialValue = 25f)
                            AdjustmentSlider("Break Duration (min)", valueRange = 1f..30f, initialValue = 5f)
                        }
                        "Hash Generator" -> {
                            Text("MD5, SHA-1, SHA-256 generation")
                            AdjustmentSlider("Salt Length", valueRange = 0f..32f, initialValue = 0f)
                        }
                        "Sensors List" -> {
                            Text("Available hardware sensors on this device")
                        }
                        "Lorem Ipsum" -> {
                            AdjustmentSlider("Paragraph Count", valueRange = 1f..10f, initialValue = 3f)
                        }
                        "Vibration Test" -> {
                            Text("Test haptic feedback motors")
                            AdjustmentSlider("Intensity")
                        }
                        "Single Edit" -> {
                            AdjustmentSlider("Brightness")
                            AdjustmentSlider("Contrast")
                            AdjustmentSlider("Saturation")
                        }
                        "Resize and Convert" -> {
                            AdjustmentSlider("Quality", initialValue = 0.8f)
                            AdjustmentSlider("Scale", initialValue = 1.0f, valueRange = 0.1f..2.0f)
                        }
                        "Format Conversion" -> {
                            AdjustmentSlider("Quality", initialValue = 0.9f)
                        }
                        "Crop" -> {
                            AdjustmentSlider("Top Margin", initialValue = 0f)
                            AdjustmentSlider("Bottom Margin", initialValue = 0f)
                            AdjustmentSlider("Left Margin", initialValue = 0f)
                            AdjustmentSlider("Right Margin", initialValue = 0f)
                        }
                        "Image Cutting" -> {
                            AdjustmentSlider("Columns", initialValue = 2f, valueRange = 1f..10f)
                            AdjustmentSlider("Rows", initialValue = 2f, valueRange = 1f..10f)
                        }
                        "Resize by Weight" -> {
                            AdjustmentSlider("Target Size (KB)", initialValue = 500f, valueRange = 10f..5000f)
                        }
                        "Resize by Limits" -> {
                            AdjustmentSlider("Max Width", initialValue = 1920f, valueRange = 100f..8000f)
                            AdjustmentSlider("Max Height", initialValue = 1080f, valueRange = 100f..8000f)
                        }
                        "Edit EXIF" -> {
                            AdjustmentSlider("Metadata Detail", initialValue = 0.5f)
                        }
                        "Delete EXIF" -> {
                            Text("All EXIF metadata will be removed from the selected image.")
                        }
                        "Filter" -> {
                            AdjustmentSlider("Intensity")
                        }
                        "Draw" -> {
                            AdjustmentSlider("Brush Size", initialValue = 10f, valueRange = 1f..100f)
                            AdjustmentSlider("Opacity", initialValue = 1.0f)
                        }
                        "Background Remover" -> {
                            AdjustmentSlider("Edge Smoothing")
                            AdjustmentSlider("Sensitivity")
                        }
                        "Markup Layers" -> {
                            AdjustmentSlider("Layer Count", initialValue = 1f, valueRange = 1f..10f)
                        }
                        "Collage Maker" -> {
                            AdjustmentSlider("Margin", initialValue = 8f, valueRange = 0f..50f)
                            AdjustmentSlider("Corner Radius", initialValue = 8f, valueRange = 0f..100f)
                        }
                        "AI Tools" -> {
                            AdjustmentSlider("Model Version", initialValue = 1f, valueRange = 1f..3f)
                            AdjustmentSlider("Denoise Strength")
                        }
                        "Image Stitching" -> {
                            AdjustmentSlider("Spacing", initialValue = 0f, valueRange = 0f..100f)
                            AdjustmentSlider("Orientation (0:H, 1:V)", initialValue = 0f, valueRange = 0f..1f)
                        }
                        "Image Stacking" -> {
                            AdjustmentSlider("Blend Mode", initialValue = 0f, valueRange = 0f..10f)
                            AdjustmentSlider("Opacity")
                        }
                        "Watermarking" -> {
                            AdjustmentSlider("Text Size", initialValue = 24f, valueRange = 10f..100f)
                            AdjustmentSlider("Transparency", initialValue = 0.5f)
                        }
                        "Noise Generation" -> {
                            AdjustmentSlider("Noise Type", initialValue = 0f, valueRange = 0f..5f)
                            AdjustmentSlider("Scale", initialValue = 1.0f, valueRange = 0.1f..10.0f)
                        }
                        "Compare" -> {
                            AdjustmentSlider("Diff Threshold")
                            AdjustmentSlider("Highlight Color (0-1)", initialValue = 0f)
                        }
                        "Wallpapers Export" -> {
                            Text("Current system wallpapers will be exported.")
                            AdjustmentSlider("Output Quality", initialValue = 1.0f)
                        }
                        "Images to SVG" -> {
                            AdjustmentSlider("Simplify Threshold")
                            AdjustmentSlider("Detail Level", initialValue = 0.7f)
                        }
                        "Web Image Loading" -> {
                            Text("Enter URL to load and process image.")
                            AdjustmentSlider("Cache Size (MB)", initialValue = 50f, valueRange = 10f..500f)
                        }
                        "OCR" -> {
                            Text("Optical Character Recognition")
                            AdjustmentSlider("Language Index", initialValue = 0f, valueRange = 0f..10f)
                            AdjustmentSlider("DPI", initialValue = 300f, valueRange = 72f..600f)
                        }
                        "Image Preview" -> {
                            AdjustmentSlider("Zoom Level", initialValue = 1.0f, valueRange = 1.0f..5.0f)
                        }
                        "Base64 Tools" -> {
                            Text("Encode to or Decode from Base64 string.")
                            AdjustmentSlider("Line Wrap Width", initialValue = 76f, valueRange = 0f..120f)
                        }
                        "Palette Tools" -> {
                            AdjustmentSlider("Max Colors", initialValue = 16f, valueRange = 2f..256f)
                            AdjustmentSlider("Color Space (0:RGB, 1:LAB)", initialValue = 0f)
                        }
                        "Color Picker" -> {
                            AdjustmentSlider("Precision")
                            Text("Touch the image to pick a color.")
                        }
                        "Mask Filter" -> {
                            AdjustmentSlider("Radius", initialValue = 10f, valueRange = 1f..50f)
                            AdjustmentSlider("Sigma", initialValue = 5f, valueRange = 1f..20f)
                        }
                        "Draw on background", "Layers on background" -> {
                            AdjustmentSlider("Background Color (0-1)")
                            AdjustmentSlider("Canvas Width", initialValue = 1080f, valueRange = 100f..4000f)
                            AdjustmentSlider("Canvas Height", initialValue = 1920f, valueRange = 100f..4000f)
                        }
                        "Layers on image" -> {
                            AdjustmentSlider("Opacity")
                            AdjustmentSlider("Overlay Position X")
                            AdjustmentSlider("Overlay Position Y")
                        }
                        "Open project" -> {
                            Text("Select a project file to resume editing.")
                        }
                        "Audio Editor", "Audio Joiner", "Audio Tag Editor", "Reverse Audio", "Mute Audio", "Ringtone Maker", "Sound Mastering", "Add SFX", "Video to Audio", "Reverse Video", "Multi Convert", "Multi Video To Audio", "Metronome", "Audio Info", "Video Info", "Device Codec", "Audio Output", "Video Output" -> {
                            AdjustmentSlider("Intensity")
                            AdjustmentSlider("Threshold")
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
            Text(java.lang.String.format(java.util.Locale.US, "%.1f", value), style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = {
                value = it
                onValueChange(it)
            },
            valueRange = valueRange
        )
    }
}
