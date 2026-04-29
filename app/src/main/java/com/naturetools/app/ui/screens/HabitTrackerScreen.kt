package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Habit(val name: String, var isCompleted: Boolean = false)

@Composable
fun HabitTrackerScreen(navController: NavHostController) {
    var habits by remember { mutableStateOf(listOf(Habit("Drink Water"), Habit("Exercise"), Habit("Read"))) }
    var showDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }

    ToolScreen(
        title = "Habit Tracker",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(habits) { habit ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        habits = habits.map {
                            if (it.name == habit.name) it.copy(isCompleted = !it.isCompleted) else it
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(habit.name, style = MaterialTheme.typography.bodyLarge)
                        Checkbox(checked = habit.isCompleted, onCheckedChange = null)
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("New Habit") },
                text = {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        label = { Text("Habit Name") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newHabitName.isNotBlank()) {
                            habits = habits + Habit(newHabitName)
                            newHabitName = ""
                            showDialog = false
                        }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
