package omni.toolbox.ui.screens.productivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.data.local.AppDatabase
import omni.toolbox.model.TimeLogEntry
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimeLoggerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val timeLogDao = database.timeLogDao()
    val logs by timeLogDao.getAllLogs().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    ToolScreen(
        title = "Time Logger",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(logs, key = { it.id }) { log ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(log.activity, style = MaterialTheme.typography.titleMedium)
                                Text(formatDuration(log.durationMillis), style = MaterialTheme.typography.bodyMedium)
                                Text(formatTimestamp(log.timestamp), style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = { scope.launch { timeLogDao.deleteLog(log) } }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            var activity by remember { mutableStateOf("") }
            var duration by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Log Time") },
                text = {
                    Column {
                        OutlinedTextField(value = activity, onValueChange = { activity = it }, label = { Text("Activity") })
                        OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (min)") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val d = duration.toLongOrNull()
                        if (activity.isNotBlank() && d != null) {
                            scope.launch { timeLogDao.insertLog(TimeLogEntry(activity = activity, durationMillis = d * 60 * 1000)) }
                            showAddDialog = false
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

private fun formatDuration(millis: Long): String {
    val mins = millis / (60 * 1000)
    val hours = mins / 60
    val remainingMins = mins % 60
    return if (hours > 0) "${hours}h ${remainingMins}m" else "${remainingMins}m"
}

private fun formatTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
}
