package omni.toolbox.ui.screens.audio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class VoiceMemo(val name: String, val length: String)

@Composable
fun VoiceMemoScreen(navController: NavHostController) {
    var isRecording by remember { mutableStateOf(false) }
    val memos = remember { mutableStateListOf(
        VoiceMemo("Meeting Note", "02:15"),
        VoiceMemo("Idea Draft", "00:45")
    ) }

    ToolScreen(
        title = "Voice Memos",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isRecording) {
                    Text("Recording...", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.error)
                } else {
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                }
            }

            Surface(tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { isRecording = !isRecording },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = if (isRecording) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
                    ) {
                        Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null)
                        Text(if (isRecording) "  Stop Recording" else "  Start Recording")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your Memos", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(memos) { memo ->
                            ListItem(
                                headlineContent = { Text(memo.name) },
                                trailingContent = {
                                    Row {
                                        Text(memo.length)
                                        IconButton(onClick = {}) { Icon(Icons.Default.PlayArrow, contentDescription = "Play") }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
