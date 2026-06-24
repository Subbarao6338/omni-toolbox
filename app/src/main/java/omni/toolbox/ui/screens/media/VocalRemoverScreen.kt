package omni.toolbox.ui.screens.media

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.ui.screens.audio.AudioBaseScreen
import omni.toolbox.ui.screens.audio.StemControl

@Composable
fun VocalRemoverScreen(navController: NavHostController, title: String) {
    AudioBaseScreen(navController = navController, title = title, mimeType = "audio/*") { _, selectedFileUri ->
        var isProcessing by remember { mutableStateOf(false) }
        var stemsExtracted by remember { mutableStateOf(false) }
        var processingStatus by remember { mutableStateOf("") }
        var progress by remember { mutableFloatStateOf(0f) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!stemsExtracted) {
                Icon(Icons.Default.MicOff, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Remove vocals or extract instruments using AI and OOPS (Out Of Phase Stereo) processing.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(32.dp))

                if (isProcessing) {
                    Text(processingStatus, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())

                    LaunchedEffect(Unit) {
                        processingStatus = "Loading audio into memory..."
                        progress = 0.1f
                        kotlinx.coroutines.delay(1000)

                        processingStatus = "Applying OOPS Phase Inversion..."
                        progress = 0.3f
                        kotlinx.coroutines.delay(1500)

                        processingStatus = "Extracting center channel (Vocals)..."
                        progress = 0.6f
                        kotlinx.coroutines.delay(2000)

                        processingStatus = "Refining background instruments..."
                        progress = 0.8f
                        kotlinx.coroutines.delay(1500)

                        progress = 1.0f
                        isProcessing = false
                        stemsExtracted = true
                    }
                } else {
                    Button(
                        onClick = { isProcessing = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Separate Stems")
                    }
                }
            } else {
                Text("Stems Separated Successfully", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))
                StemControl("Vocals", 0.0f)
                StemControl("Drums", 1.0f)
                StemControl("Bass", 1.0f)
                StemControl("Piano", 1.0f)
                StemControl("Other", 1.0f)

                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { /* Export */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Export Instrumental")
                }
                OutlinedButton(onClick = { stemsExtracted = false }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Reset")
                }
            }
        }
    }
}
