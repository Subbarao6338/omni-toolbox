package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.data.local.AppDatabase
import com.naturetools.app.model.TaskEntry
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.launch

@Composable
fun TaskBoardScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val taskDao = db.taskDao()
    val scope = rememberCoroutineScope()

    val tasks by taskDao.getAllTasks().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }

    ToolScreen(
        title = "Task Board",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskColumn("To Do", "TODO", tasks,
                onMove = { task -> scope.launch { taskDao.updateTask(task.copy(status = "DOING")) } },
                onDelete = { task -> scope.launch { taskDao.deleteTask(task) } }
            )
            TaskColumn("Doing", "DOING", tasks,
                onMove = { task -> scope.launch { taskDao.updateTask(task.copy(status = "DONE")) } },
                onDelete = { task -> scope.launch { taskDao.deleteTask(task) } }
            )
            TaskColumn("Done", "DONE", tasks,
                onMove = { },
                onDelete = { task -> scope.launch { taskDao.deleteTask(task) } }
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Task") },
                text = {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Task Title") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            scope.launch {
                                taskDao.insertTask(TaskEntry(java.util.UUID.randomUUID().toString(), newTaskTitle, "TODO"))
                            }
                            newTaskTitle = ""
                            showAddDialog = false
                        }
                    }) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun RowScope.TaskColumn(
    title: String,
    status: String,
    tasks: List<TaskEntry>,
    onMove: (TaskEntry) -> Unit,
    onDelete: (TaskEntry) -> Unit
) {
    Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(tasks.filter { it.status == status }) { task ->
                TaskCard(task, onMove, onDelete)
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskEntry, onMove: (TaskEntry) -> Unit, onDelete: (TaskEntry) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(task.title, style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onDelete(task) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                }
                if (task.status != "DONE") {
                    IconButton(onClick = { onMove(task) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
