package com.naturetools.app.ui.screens

import android.media.MediaPlayer
import android.media.MediaRecorder
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VoiceMemoScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var currentFile by remember { mutableStateOf<File?>(null) }
    var recordings by remember { mutableStateOf(context.filesDir.listFiles { _, name -> name.endsWith(".m4a") }?.toList() ?: emptyList()) }

    ToolScreen(
        title = "Voice Memo",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isRecording) "Recording..." else "Ready to Record", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(24.dp))

                    if (!isRecording) {
                        FloatingActionButton(
                            onClick = {
                                val file = File(context.filesDir, "memo_${System.currentTimeMillis()}.m4a")
                                currentFile = file
                                mediaRecorder = MediaRecorder().apply {
                                    setAudioSource(MediaRecorder.AudioSource.MIC)
                                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                    setOutputFile(file.absolutePath)
                                    prepare()
                                    start()
                                }
                                isRecording = true
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Record", modifier = Modifier.size(36.dp))
                        }
                    } else {
                        FloatingActionButton(
                            onClick = {
                                mediaRecorder?.apply {
                                    stop()
                                    release()
                                }
                                mediaRecorder = null
                                isRecording = false
                                recordings = context.filesDir.listFiles { _, name -> name.endsWith(".m4a") }?.toList() ?: emptyList()
                            },
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Recordings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(recordings.sortedByDescending { it.lastModified() }) { file ->
                    ListItem(
                        headlineContent = { Text(file.name) },
                        supportingContent = {
                            val df = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                            Text(df.format(Date(file.lastModified())))
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                MediaPlayer().apply {
                                    setDataSource(file.absolutePath)
                                    prepare()
                                    start()
                                }
                            }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
