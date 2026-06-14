package com.naturetools.app.ui.screens.system

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun DeveloperScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Specifications", "App Manager", "Integrity")

    ToolScreen(
        title = "Developer & Hardware Console",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> SpecificationsTab()
                    1 -> AppManagerTab()
                    2 -> IntegrityTab()
                }
            }
        }
    }
}

@Composable
fun SpecificationsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SpecCard("Board", Build.BOARD, Icons.Default.Memory)
        SpecCard("Architecture", Build.SUPPORTED_ABIS.joinToString(", "), Icons.Default.Architecture)
        SpecCard("Kernel", System.getProperty("os.version") ?: "Unknown", Icons.Default.SettingsInputComponent)
        SpecCard("Bootloader", Build.BOOTLOADER, Icons.Default.Lock)
        SpecCard("Hardware", Build.HARDWARE, Icons.Default.DeveloperBoard)
        SpecCard("Model", Build.MODEL, Icons.Default.Smartphone)
    }
}

@Composable
fun AppManagerTab() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Active Packages", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        repeat(5) { index ->
            val pkgName = listOf("com.android.chrome", "com.google.android.youtube", "com.whatsapp", "com.instagram.android", "com.spotify.music")[index]
            val appName = listOf("Chrome", "YouTube", "WhatsApp", "Instagram", "Spotify")[index]

            ListItem(
                headlineContent = { Text(appName) },
                supportingContent = { Text(pkgName) },
                leadingContent = { Icon(Icons.Default.Android, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingContent = {
                    Row {
                        IconButton(onClick = {}) { Icon(Icons.Default.ContentCopy, contentDescription = "Copy Package") }
                        IconButton(onClick = {}) { Icon(Icons.Default.Backup, contentDescription = "Backup APK") }
                    }
                }
            )
            HorizontalDivider()
        }

        Button(
            onClick = { /* Export all */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export All Installed Packages")
        }
    }
}

@Composable
fun IntegrityTab() {
    var diagnosticDensity by remember { mutableFloatStateOf(0.5f) }
    var fakeRootEnabled by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Sandbox Integrity Controllers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Column {
            Text("Diagnostic Density Filter", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = diagnosticDensity,
                onValueChange = { diagnosticDensity = it },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Level: ${(diagnosticDensity * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }

        ListItem(
            headlineContent = { Text("Simulated Root Access") },
            supportingContent = { Text("Toggles a fake root-interceptor state for compiler trials.") },
            trailingContent = {
                Switch(checked = fakeRootEnabled, onCheckedChange = { fakeRootEnabled = it })
            }
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "Integrity check passed. Rootless user-space environment confirmed.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SpecCard(label: String, value: String, icon: ImageVector) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}
