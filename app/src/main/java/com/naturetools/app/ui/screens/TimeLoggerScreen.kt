package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.naturetools.app.data.local.AppDatabase
import com.naturetools.app.model.TimeLogEntry
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimeLoggerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val timeLogDao = db.timeLogDao()
    val scope = rememberCoroutineScope()

    val logs by timeLogDao.getAllLogs().collectAsState(initial = emptyList())
    var activeLog by remember { mutableStateOf<TimeLogEntry?>(null) }
    var categoryInput by remember { mutableStateOf("Work") }

    val timerText = remember(activeLog) {
        if (activeLog == null) "00:00:00" else "Tracking..."
    }

    ToolScreen(
        title = "Time Logger",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(timerText, style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (activeLog == null) {
                        Button(onClick = {
                            activeLog = TimeLogEntry(java.util.UUID.randomUUID().toString(), categoryInput, System.currentTimeMillis(), null)
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Tracking")
                        }
                    } else {
                        Button(onClick = {
                            val finishedLog = activeLog!!.copy(endTime = System.currentTimeMillis())
                            scope.launch {
                                timeLogDao.insertLog(finishedLog)
                            }
                            activeLog = null
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop Tracking")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs) { log ->
                    val duration = if (log.endTime != null) (log.endTime - log.startTime) / 1000 else 0
                    val minutes = duration / 60
                    val seconds = duration % 60
                    ListItem(
                        headlineContent = { Text(log.category) },
                        supportingContent = {
                            val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                            val endText = if (log.endTime != null) df.format(Date(log.endTime)) else "..."
                            Text("${df.format(Date(log.startTime))} - $endText")
                        },
                        trailingContent = {
                            Text("${minutes}m ${seconds}s", fontWeight = FontWeight.Bold)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
