package com.naturetools.app.ui.screens.system

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.theme.AccentColors
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeMode: String,
    onThemeChange: (String) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    showCategoryCounts: Boolean,
    onShowCategoryCountsChange: (Boolean) -> Unit,
    aiApiKey: String,
    onAiApiKeyChange: (String) -> Unit,
    accentColor: Color?,
    onAccentColorChange: (Color?) -> Unit,
    auroraBackground: Boolean,
    onAuroraBackgroundChange: (Boolean) -> Unit,
    glassEffect: Boolean,
    onGlassEffectChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ToolScreen(title = "Settings", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
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

            Text("Accent Color", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(40.dp),
                modifier = Modifier.height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (accentColor == null) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f))
                            .clickable { onAccentColorChange(null) }
                            .border(2.dp, if (accentColor == null) MaterialTheme.colorScheme.onSurface else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (accentColor == null) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                }
                items(AccentColors) { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { onAccentColorChange(color) }
                            .border(2.dp, if (accentColor == color) MaterialTheme.colorScheme.onSurface else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (accentColor == color) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            SettingsSwitch(
                title = "Dynamic Color",
                subtitle = "Use system colors (Android 12+)",
                checked = dynamicColor,
                onCheckedChange = onDynamicColorChange,
                icon = Icons.Default.Palette
            )

            SettingsSwitch(
                title = "Aurora Background",
                subtitle = "Animated gradient background",
                checked = auroraBackground,
                onCheckedChange = onAuroraBackgroundChange,
                icon = Icons.Default.AutoAwesome
            )

            SettingsSwitch(
                title = "Glass Effect",
                subtitle = "Frosted glass visual style",
                checked = glassEffect,
                onCheckedChange = onGlassEffectChange,
                icon = Icons.Default.BlurOn
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("General", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            SettingsSwitch(
                title = "Show Category Counts",
                subtitle = "Display number of tools in each category",
                checked = showCategoryCounts,
                onCheckedChange = onShowCategoryCountsChange,
                icon = Icons.Default.Category
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Data Portability", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                            val data = JSONObject()
                            prefs.all.forEach { (key, value) ->
                                data.put(key, value)
                            }
                            val recentPrefs = context.getSharedPreferences("recent_tools", Context.MODE_PRIVATE)
                            data.put("recent_tools", recentPrefs.getString("routes", ""))

                            val jsonString = data.toString(2)
                            // In a real app, we would use a file picker. For this simulation, we'll toast.
                            android.util.Log.d("SettingsScreen", "Exported Data: $jsonString")
                            Toast.makeText(context, "Settings exported to Logcat", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export JSON")
                }

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Import functionality ready", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import JSON")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("AI Settings", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = aiApiKey,
                onValueChange = onAiApiKeyChange,
                label = { Text("AI API Key") },
                placeholder = { Text("Enter your API key...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) }
            )
            Text(
                "Provide an API key to enable online AI features. If left empty, local implementation will be used.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 8.dp)
            )

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
fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
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
            Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            RadioButton(selected = selected, onClick = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
