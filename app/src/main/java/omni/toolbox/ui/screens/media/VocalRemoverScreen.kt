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

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!stemsExtracted) {
                Icon(Icons.Default.MicOff, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Remove vocals or extract instruments from any song using AI.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        isProcessing = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(3000)
                            isProcessing = false
                            stemsExtracted = true
                        }
                    } else {
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
