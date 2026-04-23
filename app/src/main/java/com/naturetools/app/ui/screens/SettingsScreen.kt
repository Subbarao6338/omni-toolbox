package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeMode: String,
    onThemeChange: (String) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit
) {
    ToolScreen(title = "Settings", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            ThemeOption(
                title = "System Default",
                selected = themeMode == "system",
                onClick = { onThemeChange("system") },
                icon = Icons.Default.SettingsSuggest
            )
            ThemeOption(
                title = "Light",
                selected = themeMode == "light",
                onClick = { onThemeChange("light") },
                icon = Icons.Default.LightMode
            )
            ThemeOption(
                title = "Dark",
                selected = themeMode == "dark",
                onClick = { onThemeChange("dark") },
                icon = Icons.Default.DarkMode
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Palette, contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Dynamic Color", style = MaterialTheme.typography.bodyLarge)
                    Text("Use system colors (Android 12+)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                Switch(checked = dynamicColor, onCheckedChange = onDynamicColorChange)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("About", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Nature Tools", style = MaterialTheme.typography.bodyLarge)
                    Text("Version 1.0.0 (Build 1)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else LocalContentColor.current)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            RadioButton(selected = selected, onClick = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
