package com.naturetools.app.ui.screens.system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class PermissionInfo(val name: String, val status: String, val count: Int)

@Composable
fun SystemLabScreen(navController: NavHostController, title: String) {
    val permissions = listOf(
        PermissionInfo("Camera", "Accessed by 12 apps", 12),
        PermissionInfo("Location", "Accessed by 24 apps", 24),
        PermissionInfo("Microphone", "Accessed by 5 apps", 5),
        PermissionInfo("Contacts", "Accessed by 8 apps", 8)
    )

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Privacy Score: 85/100", style = MaterialTheme.typography.titleLarge)
                        Text("Great! Most permissions are restricted.")
                    }
                }
            }

            Text("Permission Usage", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
            LazyColumn {
                items(permissions) { item ->
                    ListItem(
                        headlineContent = { Text(item.name) },
                        supportingContent = { Text(item.status) },
                        trailingContent = { Text("${item.count} apps") }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
