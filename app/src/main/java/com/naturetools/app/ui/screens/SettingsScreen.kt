package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeMode: String,
    onThemeChange: (String) -> Unit
) {
    ToolScreen(title = "Settings", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            ThemeOption(
                title = "System Default",
                selected = themeMode == "system",
                onClick = { onThemeChange("system") },
                icon = Icons.Default.SettingsSystemDaydream
            )
            ThemeOption(
                title = "Light",
                selected = themeMode == "light",
                onClick = { onThemeChange("light") },
                icon = Icons.Default.Brightness7
            )
            ThemeOption(
                title = "Dark",
                selected = themeMode == "dark",
                onClick = { onThemeChange("dark") },
                icon = Icons.Default.Brightness4
            )
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
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f))
            RadioButton(selected = selected, onClick = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
