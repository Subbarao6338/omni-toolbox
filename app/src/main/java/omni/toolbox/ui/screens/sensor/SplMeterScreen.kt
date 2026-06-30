package omni.toolbox.ui.screens.sensor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log10
import kotlin.random.Random

@SuppressLint("MissingPermission")
@Composable
fun SplMeterScreen(navController: NavHostController) {
    var dbLevel by remember { mutableFloatStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            withContext(Dispatchers.IO) {
                val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize)

                val buffer = ShortArray(bufferSize)
                audioRecord.startRecording()

                try {
                    while (isRunning) {
                        val read = audioRecord.read(buffer, 0, buffer.size)
                        if (read > 0) {
                            var sum = 0.0
                            for (i in 0 until read) {
                                sum += buffer[i] * buffer[i]
                            }
                            val amplitude = Math.sqrt(sum / read)
                            // Convert amplitude to dB (approximate)
                            // reference value for 0dB can vary, 1.0 is a common placeholder for digital full scale
                            // but here we use a common heuristic for Android mics
                            val db = if (amplitude > 0) 20 * log10(amplitude / 32767.0) + 90 else 0.0
                            withContext(Dispatchers.Main) {
                                dbLevel = db.toFloat().coerceIn(0f, 120f)
                            }
                        }
                        kotlinx.coroutines.delay(100)
                    }
                } finally {
                    audioRecord.stop()
                    audioRecord.release()
                }
            }
        }
    }

    ToolScreen(
        title = "SPL Meter",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sound Pressure Level", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = String.format("%.1f", dbLevel),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                color = when {
                    dbLevel > 85 -> Color.Red
                    dbLevel > 70 -> Color(0xFFFFA000)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Text("dB", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(64.dp))

            LinearProgressIndicator(
                progress = { (dbLevel / 120f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(16.dp),
                color = if (dbLevel > 85) Color.Red else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isRunning) "Stop Measuring" else "Start Measuring")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Real-time audio levels measured via microphone.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
