package com.naturetools.app.ui.screens.productivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Task(val id: Int, val title: String, val status: String)

@Composable
fun TaskBoardScreen(navController: NavHostController) {
    val tasks = remember { mutableStateListOf(
        Task(1, "Fix navigation", "Todo"),
        Task(2, "Update icons", "In Progress"),
        Task(3, "Refactor code", "Done")
    ) }

    ToolScreen(
        title = "Task Board",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { /* Add task */ }) {
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
            KanbanColumn("Todo", tasks.filter { it.status == "Todo" })
            KanbanColumn("In Progress", tasks.filter { it.status == "In Progress" })
            KanbanColumn("Done", tasks.filter { it.status == "Done" })
        }
    }
}

@Composable
fun KanbanColumn(name: String, tasks: List<Task>) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
        tasks.forEach { task ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Text(task.title, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
