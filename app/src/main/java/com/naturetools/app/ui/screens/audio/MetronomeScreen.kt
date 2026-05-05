package com.naturetools.app.ui.screens.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun MetronomeScreen(navController: NavHostController) {
    var bpm by remember { mutableFloatStateOf(120f) }
    var isPlaying by remember { mutableStateOf(false) }

    val sampleRate = 44100
    val durationMs = 30
    val numSamples = (sampleRate * durationMs / 1000)
    val generatedSnd = remember {
        val sample = DoubleArray(numSamples)
        val snd = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val fade = if (i > numSamples - 100) (numSamples - i) / 100.0 else 1.0
            sample[i] = sin(2 * PI * i / (sampleRate / 880.0)) * fade
            snd[i] = (sample[i] * 32767).toInt().toShort()
        }
        snd
    }

    val audioTrack = remember {
        AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSnd.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
    }

    LaunchedEffect(generatedSnd) {
        audioTrack.write(generatedSnd, 0, generatedSnd.size)
    }

    DisposableEffect(Unit) {
        onDispose {
            isPlaying = false
            audioTrack.release()
        }
    }

    LaunchedEffect(isPlaying, bpm) {
        if (isPlaying) {
            withContext(Dispatchers.Default) {
                val intervalNanos = (60_000_000_000L / bpm).toLong()
                var nextTick = System.nanoTime()

                while (isActive && isPlaying) {
                    val now = System.nanoTime()
                    if (now >= nextTick) {
                        audioTrack.stop()
                        audioTrack.reloadStaticData()
                        audioTrack.play()
                        nextTick += intervalNanos
                    }
                    if (nextTick - now > 2_000_000L) {
                        Thread.sleep(1)
                    }
                }
            }
        }
    }

    ToolScreen(
        title = "Metronome",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${bpm.toInt()}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 100.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "BPM",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            Slider(
                value = bpm,
                onValueChange = { bpm = it },
                valueRange = 40f..240f,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("40")
                Text("240")
            }

            Spacer(modifier = Modifier.height(48.dp))

            LargeFloatingActionButton(
                onClick = { isPlaying = !isPlaying },
                containerColor = if (isPlaying) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (isPlaying) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Stop" else "Start",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
