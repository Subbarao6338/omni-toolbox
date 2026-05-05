package com.naturetools.app.ui.screens.productivity

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.*

data class LogEntry(val task: String, val duration: String, val date: String)

@Composable
fun TimeLoggerScreen(navController: NavHostController) {
    var isTracking by remember { mutableStateOf(false) }
    var taskName by remember { mutableStateOf("") }
    val logs = remember { mutableStateListOf<LogEntry>() }

    ToolScreen(
        title = "Time Logger",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isTracking) {
                        logs.add(LogEntry(taskName, "0h 45m", "Today"))
                        taskName = ""
                    }
                    isTracking = !isTracking
                },
                modifier = Modifier.fillMaxWidth(),
                colors = if (isTracking) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) else ButtonDefaults.buttonColors()
            ) {
                Icon(if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isTracking) "Stop Tracking" else "Start Tracking")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Logs", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(logs.reversed()) { entry ->
                    ListItem(
                        headlineContent = { Text(entry.task) },
                        supportingContent = { Text(entry.date) },
                        trailingContent = { Text(entry.duration) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
