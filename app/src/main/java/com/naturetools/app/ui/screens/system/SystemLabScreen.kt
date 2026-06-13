package com.naturetools.app.ui.screens.system

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay

data class PermissionInfo(val name: String, val status: String, val count: Int)

@Composable
fun SystemLabScreen(navController: NavHostController, title: String) {
    val permissions = listOf(
        PermissionInfo("Camera", "Accessed by 12 apps", 12),
        PermissionInfo("Location", "Accessed by 24 apps", 24),
        PermissionInfo("Microphone", "Accessed by 5 apps", 5),
        PermissionInfo("Contacts", "Accessed by 8 apps", 8)
    )

    var cpuLoad by remember { mutableFloatStateOf(0.24f) }
    var ramUsage by remember { mutableFloatStateOf(0.65f) }
    val logs = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        logs.add("[INFO] Initializing System Diagnostic Engine...")
        delay(500)
        logs.add("[OK] Hardware Abstraction Layer linked.")
        delay(300)
        logs.add("[OK] Sensor Hub online.")

        while(true) {
            cpuLoad = (0.1f + Math.random().toFloat() * 0.4f)
            ramUsage = (0.6f + Math.random().toFloat() * 0.1f)
            if (logs.size > 10) logs.removeAt(0)
            logs.add("[DEBUG] CPU Thread ${ (1..8).random() } active: ${ (cpuLoad * 100).toInt() }%")
            delay(2000)
        }
    }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            if (title.contains("Hub", ignoreCase = true) || title == "System Lab Core") {
                Text("Diagnostic Dashboard", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MonitoringCard("CPU Load", cpuLoad, "${(cpuLoad * 100).toInt()}%", Modifier.weight(1f), MaterialTheme.colorScheme.primary)
                    MonitoringCard("RAM Usage", ramUsage, "${(ramUsage * 8).toInt()}.${(ramUsage * 10).toInt() % 10} GB", Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terminal Log Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(150.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Column(modifier = Modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
                        logs.forEach { log ->
                            Text(
                                text = log,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
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
fun MonitoringCard(label: String, progress: Float, value: String, modifier: Modifier, color: Color) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(64.dp),
                    color = color,
                    strokeWidth = 6.dp,
                    trackColor = color.copy(alpha = 0.1f)
                )
                Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
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
