package com.naturetools.app.ui.screens.utility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class FileItem(val name: String, val isDirectory: Boolean, val size: String = "")

@Composable
fun FileToolScreen(navController: NavHostController, title: String) {
    val items = remember {
        listOf(
            FileItem("Documents", true),
            FileItem("Downloads", true),
            FileItem("Pictures", true),
            FileItem("report.pdf", false, "1.2 MB"),
            FileItem("photo.jpg", false, "3.5 MB"),
            FileItem("notes.txt", false, "4 KB")
        )
    }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (title == "Storage Cleaner") {
                StorageCleanerHeader()
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items) { item ->
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
                        modifier = Modifier.clickable { /* Action */ }
                    )
                }
            }
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
