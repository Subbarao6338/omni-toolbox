package com.naturetools.app.ui.screens.utility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.io.File

data class FileItem(val name: String, val isDirectory: Boolean, val size: String = "", val file: File? = null)

@Composable
fun FileToolScreen(navController: NavHostController, title: String) {
    val context = LocalContext.current
    val rootDir = context.filesDir
    var currentDir by remember { mutableStateOf(rootDir) }
    var fileItems by remember { mutableStateOf(listOf<FileItem>()) }
    var showRenameDialog by remember { mutableStateOf<File?>(null) }
    var newFileName by remember { mutableStateOf("") }

    fun refreshFiles() {
        val files = currentDir.listFiles()?.toList() ?: emptyList()
        fileItems = files.map { file ->
            FileItem(
                name = file.name,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) "" else "${file.length() / 1024} KB",
                file = file
            )
        }.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
    }

    LaunchedEffect(currentDir) {
        // Seed some initial files if empty for demonstration
        if (currentDir == rootDir && (rootDir.listFiles()?.isEmpty() == true)) {
            File(rootDir, "Welcome_Note.txt").writeText("Welcome to Omni Toolbox File Manager!")
            File(rootDir, "System_Logs").mkdir()
            File(File(rootDir, "System_Logs"), "boot.log").writeText("System initialized.")
        }
        refreshFiles()
    }

    ToolScreen(
        title = title,
        onBack = {
            if (currentDir != rootDir) {
                currentDir = currentDir.parentFile ?: rootDir
            } else {
                navController.popBackStack()
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (title == "Storage Cleaner") {
                StorageCleanerHeader()
            }

            Text(
                text = "Path: ${currentDir.absolutePath.replace(context.applicationInfo.dataDir, "")}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(fileItems) { item ->
                    ListItem(
                        headlineContent = { Text(item.name) },
                        supportingContent = { if (!item.isDirectory) Text(item.size) },
                        leadingContent = {
                            Icon(
                                if (item.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                                contentDescription = null,
                                tint = if (item.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = {
                                    showRenameDialog = item.file
                                    newFileName = item.name
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Rename", modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = {
                                    item.file?.delete()
                                    refreshFiles()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        modifier = Modifier.clickable {
                            if (item.isDirectory && item.file != null) {
                                currentDir = item.file
                            }
                        }
                    )
                }
            }
        }

        if (showRenameDialog != null) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = null },
                title = { Text("Rename File") },
                text = {
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("New Name") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val file = showRenameDialog!!
                        val newFile = File(file.parentFile, newFileName)
                        if (file.renameTo(newFile)) {
                            refreshFiles()
                        }
                        showRenameDialog = null
                    }) {
                        Text("Rename")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StorageCleanerHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Storage Usage", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Used: 48 GB", style = MaterialTheme.typography.bodySmall)
                Text("Free: 16 GB", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Clean */ }, modifier = Modifier.align(Alignment.End)) {
                Text("Clean Now")
            }
        }
    }
}
