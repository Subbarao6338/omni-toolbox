package omni.toolbox.ui.screens.productivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import omni.toolbox.model.TaskEntry
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.launch

@Composable
fun TaskBoardScreen(navController: NavHostController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val taskDao = database.taskDao()
    val tasks by taskDao.getAllTasks().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KanbanColumn("Todo", tasks.filter { it.status == "Todo" }, onTaskDelete = { scope.launch { taskDao.deleteTask(it) } }, onStatusChange = { task, status -> scope.launch { taskDao.updateTask(task.copy(status = status)) } })
            KanbanColumn("In Progress", tasks.filter { it.status == "In Progress" }, onTaskDelete = { scope.launch { taskDao.deleteTask(it) } }, onStatusChange = { task, status -> scope.launch { taskDao.updateTask(task.copy(status = status)) } })
            KanbanColumn("Done", tasks.filter { it.status == "Done" }, onTaskDelete = { scope.launch { taskDao.deleteTask(it) } }, onStatusChange = { task, status -> scope.launch { taskDao.updateTask(task.copy(status = status)) } })
        }

        if (showAddDialog) {
            var taskTitle by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Task") },
                text = {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Task Title") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (taskTitle.isNotBlank()) {
                            scope.launch { taskDao.insertTask(TaskEntry(title = taskTitle, status = "Todo")) }
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
fun KanbanColumn(name: String, tasks: List<TaskEntry>, onTaskDelete: (TaskEntry) -> Unit, onStatusChange: (TaskEntry, String) -> Unit) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
        LazyColumn {
            items(tasks, key = { it.id }) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(task.title, style = MaterialTheme.typography.bodyLarge)
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            if (task.status != "Todo") IconButton(onClick = { onStatusChange(task, "Todo") }) { Text("T", style = MaterialTheme.typography.labelSmall) }
                            if (task.status != "In Progress") IconButton(onClick = { onStatusChange(task, "In Progress") }) { Text("P", style = MaterialTheme.typography.labelSmall) }
                            if (task.status != "Done") IconButton(onClick = { onStatusChange(task, "Done") }) { Text("D", style = MaterialTheme.typography.labelSmall) }
                            IconButton(onClick = { onTaskDelete(task) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
