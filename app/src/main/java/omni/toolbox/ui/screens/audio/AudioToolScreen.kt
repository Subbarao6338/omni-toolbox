package omni.toolbox.ui.screens.audio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import omni.toolbox.ui.components.AdjustmentSlider
import omni.toolbox.ui.components.ToolScreen
import kotlin.random.Random

@Composable
fun AudioToolScreen(navController: NavHostController, title: String, mimeType: String = "audio/*") {
    if (title == "Video Merger" || title == "Audio Joiner" || title == "Audio Mixer") {
        MultiMediaScreen(navController, title, mimeType)
    } else {
        AudioToolScreenSingle(navController, title, mimeType)
    }
}

@Composable
fun AudioToolScreenSingle(navController: NavHostController, title: String, mimeType: String) {
    AudioBaseScreen(navController = navController, title = title, mimeType = mimeType) { _, selectedFileUri ->
        var isPlaying by remember { mutableStateOf(false) }
        var progress by remember { mutableFloatStateOf(0f) }
        val scrollState = rememberScrollState()
        var showStems by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
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
                        "Vocal Remover", "ai_stems_splitter" -> {
                            if (!showStems) {
                                Button(onClick = { showStems = true }) {
                                    Text("Extract Stems")
                                }
                            } else {
                                StemControl("Vocals", 0.8f)
                                StemControl("Drums", 0.5f)
                                StemControl("Bass", 0.6f)
                                StemControl("Other", 0.4f)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            AdjustmentSlider("AI Model Accuracy", initialValue = 0.9f)
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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Process and Save")
            }
        }
    }
}

@Composable
fun StemControl(label: String, initialValue: Float) {
    var volume by remember { mutableFloatStateOf(initialValue) }
    var isMuted by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { isMuted = !isMuted }) {
            Icon(if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, contentDescription = null)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Slider(value = if (isMuted) 0f else volume, onValueChange = { volume = it }, enabled = !isMuted)
        }
    }
}

@Composable
fun MultiMediaScreen(navController: NavHostController, title: String, mimeType: String) {
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        selectedFiles = it
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (selectedFiles.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(onClick = { launcher.launch(mimeType) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Multiple Files")
                    }
                }
            } else {
                Text("Selected Files:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    selectedFiles.forEachIndexed { index, uri ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${index + 1}.", modifier = Modifier.width(24.dp))
                                Text(uri.lastPathSegment ?: "Unknown file", modifier = Modifier.weight(1f), maxLines = 1)
                                IconButton(onClick = { selectedFiles = selectedFiles.filter { it != uri } }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    Button(onClick = { launcher.launch(mimeType) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Add More")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (title == "Audio Mixer") {
                    Text("Mix Levels", style = MaterialTheme.typography.titleSmall)
                    selectedFiles.forEach { uri ->
                        StemControl(uri.lastPathSegment ?: "Track", 0.5f)
                    }
                }

                Button(
                    onClick = { /* Actual Join/Merge/Mix logic */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (title == "Audio Mixer") "Mix and Export" else "Merge and Save")
                }
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
    val secondaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)

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

