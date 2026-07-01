package omni.toolbox.ui.screens.system

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.BuildConfig
import omni.toolbox.model.ToolProvider
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.ui.theme.AccentColors
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
    stableDiffusionUrl: String,
    onStableDiffusionUrlChange: (String) -> Unit,
    accentColor: Color?,
    onAccentColorChange: (Color?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportContent by remember { mutableStateOf("") }

    val fileSaverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        context.contentResolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(exportContent.toByteArray())
                        }
                        Toast.makeText(context, "Settings exported successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to export settings", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                scope.launch {
                    try {
                        val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.readText() ?: ""
                        val data = JSONObject(content)
                        // This is a simplified import. In a real app, we'd update all prefs.
                        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        val edit = prefs.edit()
                        data.keys().forEach { key ->
                            val value = data.get(key)
                            when (value) {
                                is Boolean -> edit.putBoolean(key, value)
                                is Int -> edit.putInt(key, value)
                                is Long -> edit.putLong(key, value)
                                is Float -> edit.putFloat(key, value)
                                is String -> edit.putString(key, value)
                            }
                        }
                        edit.apply()
                        Toast.makeText(context, "Settings imported. Please restart app.", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to import settings", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

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

            Text("Quick Tiles", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Assign tools to Custom Tiles in Quick Settings", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            val tilePrefs = context.getSharedPreferences("dynamic_tiles", Context.MODE_PRIVATE)
            (1..3).forEach { i ->
                QuickTileSetting(i, tilePrefs)
            }

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
                            exportContent = jsonString
                            fileSaverLauncher.launch("omni_settings.json")
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
                        filePickerLauncher.launch(arrayOf("application/json"))
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = stableDiffusionUrl,
                onValueChange = onStableDiffusionUrlChange,
                label = { Text("Stable Diffusion API URL") },
                placeholder = { Text("http://your-sd-url:7860") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Cloud, contentDescription = null) }
            )
            Text(
                "API URL for Stable Diffusion (webui with --api or similar).",
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
                    Text("Omni Toolbox", style = MaterialTheme.typography.bodyLarge)
                    Text("Version ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun QuickTileSetting(i: Int, tilePrefs: android.content.SharedPreferences) {
    var tileRoute by remember { mutableStateOf(tilePrefs.getString("tile_$i", "home") ?: "home") }

    OutlinedTextField(
        value = tileRoute,
        onValueChange = { newValue ->
            tileRoute = newValue
            tilePrefs.edit().putString("tile_$i", newValue).apply()
            val toolName = ToolProvider.tools.find { it.route == newValue }?.name ?: "Nature Tool"
            tilePrefs.edit().putString("tile_name_$i", toolName).apply()
        },
        label = { Text("Custom Tile $i Route") },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        trailingIcon = {
            IconButton(onClick = { /* Could show tool picker dialog */ }) {
                Icon(Icons.Default.Link, contentDescription = null)
            }
        }
    )
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
