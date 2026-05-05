package com.naturetools.app.ui.screens.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun BinauralBeatsScreen(navController: NavHostController) {
    var isPlaying by remember { mutableStateOf(false) }
    var baseFreq by remember { mutableFloatStateOf(200f) }
    var beatFreq by remember { mutableFloatStateOf(10f) }
    val sampleRate = 44100
    val bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT)

    val audioTrack = remember {
        AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_STEREO).build())
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    DisposableEffect(Unit) { onDispose { isPlaying = false; audioTrack.release() } }

    LaunchedEffect(isPlaying, baseFreq, beatFreq) {
        if (isPlaying) {
            withContext(Dispatchers.Default) {
                audioTrack.play()
                val samples = ShortArray(bufferSize)
                var angleL = 0.0; var angleR = 0.0
                val fL = baseFreq.toDouble(); val fR = (baseFreq + beatFreq).toDouble()
                while (isActive && isPlaying) {
                    for (i in 0 until bufferSize step 2) {
                        samples[i] = (sin(angleL) * 16383.0).toInt().toShort()
                        samples[i+1] = (sin(angleR) * 16383.0).toInt().toShort()
                        angleL += 2.0 * PI * fL / sampleRate; angleR += 2.0 * PI * fR / sampleRate
                    }
                    audioTrack.write(samples, 0, bufferSize)
                }
                audioTrack.stop()
            }
        }
    }

    ToolScreen(title = "Binaural Beats", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Requires Headphones", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(32.dp))
            Text("Base: ${baseFreq.toInt()} Hz", fontWeight = FontWeight.Bold)
            Slider(value = baseFreq, onValueChange = { baseFreq = it }, valueRange = 100f..500f)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Beat: ${String.format("%.1f", beatFreq)} Hz", fontWeight = FontWeight.Bold)
            Slider(value = beatFreq, onValueChange = { beatFreq = it }, valueRange = 0.5f..40f)
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = { isPlaying = !isPlaying }, modifier = Modifier.fillMaxWidth().height(64.dp)) {
                Icon(if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow, null)
                Text(if (isPlaying) "Stop" else "Start")
            }
        }
    }
}
