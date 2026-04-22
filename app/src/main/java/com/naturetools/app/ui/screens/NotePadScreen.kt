package com.naturetools.app.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.viewmodel.NoteViewModel

@Composable
fun NotePadScreen(navController: NavHostController, viewModel: NoteViewModel = viewModel()) {
    val notes by viewModel.allNotes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    ToolScreen(
        title = "Note Pad",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium)
                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { viewModel.deleteNote(note) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            var title by remember { mutableStateOf("") }
            var content by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("New Note") },
                text = {
                    Column {
                        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(value = content, onValueChange = { content = it }, label = { Text("Content") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.saveNote(title, content)
                        showAddDialog = false
                    }) { Text("Save") }
                }
            )
        }
    }
}
