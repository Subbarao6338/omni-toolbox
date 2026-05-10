package com.naturetools.app.ui.screens.system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            if (title.contains("Hub", ignoreCase = true)) {
                Text("Smart Dashboard", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusCard("CPU", "24%", Modifier.weight(1f), MaterialTheme.colorScheme.primary)
                    StatusCard("RAM", "2.4 GB", Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusCard("Storage", "85%", Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
                    StatusCard("Battery", "92%", Modifier.weight(1f), MaterialTheme.colorScheme.error)
                }
            } else {
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
            }

            Text("System Analysis", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            permissions.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.name) },
                    supportingContent = { Text(item.status) },
                    trailingContent = { Text("${item.count} apps") }
                )
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { /* Refresh */ }, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 32.dp)) {
                Text("Refresh Diagnostics")
            }
        }
    }
}

@Composable
fun StatusCard(label: String, value: String, modifier: Modifier, color: Color) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
        }
    }
}
