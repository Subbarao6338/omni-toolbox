package com.nature.docs.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.ui.theme.*

@Composable
fun SettingsView(
    isDarkMode: Boolean,
    onDarkModeChange: (Int) -> Unit,
    onThemeIdChange: (Int) -> Unit,
    onNavigateToAbout: (String) -> Unit
) {
    val context = LocalContext.current
    
    var autoAuthor by remember { mutableStateOf(PreferencesManager.getDefaultAuthor(context)) }
    var currentDarkMode by remember { mutableIntStateOf(PreferencesManager.getDarkMode(context)) }
    var currentThemeId by remember { mutableIntStateOf(PreferencesManager.getThemeId(context)) }
    var historyRetention by remember { mutableIntStateOf(PreferencesManager.getHistoryRetention(context)) }
    
    var showRetentionDialog by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    LinenCanvas(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            // Standardized Header
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Studio Settings.",
                        style = MaterialTheme.typography.displayLarge,
                        color = InkBrown
                    )
                    Logo(modifier = Modifier.size(24.dp), partColor = InkBrown)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 160.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    SettingsGroup("APPEARANCE") {
                        Column(Modifier.padding(16.dp)) {
                            Text("STUDIO LIGHTING", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ThemeOption("Natural", Icons.Outlined.SettingsSuggest, currentDarkMode == 0, Modifier.weight(1f)) {
                                    currentDarkMode = 0
                                    onDarkModeChange(0)
                                }
                                ThemeOption("Morning", Icons.Outlined.LightMode, currentDarkMode == 1, Modifier.weight(1f)) {
                                    currentDarkMode = 1
                                    onDarkModeChange(1)
                                }
                                ThemeOption("Dusk", Icons.Outlined.DarkMode, currentDarkMode == 2, Modifier.weight(1f)) {
                                    currentDarkMode = 2
                                    onDarkModeChange(2)
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                            Text("NATURAL PALETTES", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen)
                            Spacer(Modifier.height(12.dp))

                            androidx.compose.foundation.lazy.LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(end = 16.dp)
                            ) {
                                val themes = NatureTheme.values()

                                items(themes.size) { index ->
                                    val theme = themes[index]
                                    val selected = currentThemeId == theme.id

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.clickable {
                                            currentThemeId = theme.id
                                            onThemeIdChange(theme.id)
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .clip(CircleShape)
                                                .background(theme.primaryColor)
                                                .border(
                                                    width = 2.dp,
                                                    color = if (selected) InkBrown else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (selected) {
                                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        val name = theme.name.lowercase().replaceFirstChar { it.uppercase() }
                                        Text(name, style = MaterialTheme.typography.labelSmall, color = if (selected) InkBrown else InkBrown.copy(0.4f))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SettingsGroup("COLLECTOR PROFILE") {
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                            Text("DEFAULT COLLECTOR", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen)
                            Spacer(Modifier.height(4.dp))
                            Text("Automatically added to the 'Author' field of your specimens.", style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.6f))
                            Spacer(Modifier.height(12.dp))
                            TextField(
                                value = autoAuthor,
                                onValueChange = {
                                    autoAuthor = it
                                    PreferencesManager.setDefaultAuthor(context, it)
                                },
                                placeholder = { Text("e.g. Alexander von Humboldt", style = MaterialTheme.typography.bodyMedium) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = BotanicalGreen,
                                    cursorColor = BotanicalGreen
                                ),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(color = InkBrown)
                            )
                        }
                    }
                }

                item {
                    SettingsGroup("ARCHIVE & CACHE") {
                        SettingsItem(Icons.Outlined.AutoDelete, "Auto-Prune Archives", when(historyRetention) {
                            0 -> "Perpetual Storage"
                            1 -> "Prune after 24 hours"
                            7 -> "Prune after 7 days"
                            else -> "Prune after 30 days"
                        }) {
                            showRetentionDialog = true
                        }
                        SettingsItem(Icons.Outlined.DeleteSweep, "Empty Journal", "Clear all archived specimens") {
                            showClearConfirm = true
                        }
                        SettingsItem(Icons.Outlined.DeleteForever, "Purge Studio Cache", "Free up storage from temporary files") {
                            val cacheDir = context.cacheDir
                            var success = true
                            try {
                                coil.ImageLoader(context).diskCache?.clear()
                                if (cacheDir.exists()) cacheDir.deleteRecursively()
                            } catch (e: Exception) {
                                success = false
                            }
                            Toast.makeText(context, if (success) "Studio purged" else "Partial purge", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                item {
                    SettingsGroup("NATURALIST SOCIETY") {
                        SettingsItem(Icons.Outlined.Info, "About the Studio", "Our mission and ecological roots") { onNavigateToAbout("main") }
                    }
                }

                item {
                    Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nature Tools V1.1", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.4f), letterSpacing = 2.sp)
                    }
                }
            }
        }
    }

    // DIALOGS
    if (showRetentionDialog) {
        AlertDialog(
            onDismissRequest = { showRetentionDialog = false },
            containerColor = ParchmentBg,
            title = { Text("Archive Retention", style = MaterialTheme.typography.headlineMedium, color = InkBrown) },
            text = {
                Column {
                    val options = listOf(0 to "Perpetual", 1 to "24 Hours", 7 to "7 Days", 30 to "30 Days")
                    options.forEach { (days, label) ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                historyRetention = days
                                PreferencesManager.setHistoryRetention(context, days)
                                showRetentionDialog = false
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = historyRetention == days, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = BotanicalGreen))
                            Spacer(Modifier.width(12.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge, color = InkBrown)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = ParchmentBg,
            title = { Text("Empty Journal?", style = MaterialTheme.typography.headlineMedium, color = InkBrown) },
            text = { Text("This will permanently remove all entries from your history archives.", style = MaterialTheme.typography.bodyMedium, color = InkBrown) },
            confirmButton = {
                Button(
                    onClick = {
                        SessionManager.clearHistory()
                        showClearConfirm = false
                        Toast.makeText(context, "Journal emptied", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta, contentColor = Color.White)
                ) { Text("EMPTY EVERYTHING", style = MaterialTheme.typography.labelSmall) }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) { Text("CANCEL", color = BotanicalGreen) }
            }
        )
    }
}

@Composable
fun ThemeOption(label: String, icon: ImageVector, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) BotanicalGreen.copy(alpha = 0.1f) else Color.Transparent,
        border = BorderStroke(1.dp, if (selected) BotanicalGreen else InkBrown.copy(alpha = 0.1f))
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = if (selected) BotanicalGreen else InkBrown.copy(0.4f))
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) BotanicalGreen else InkBrown.copy(0.4f))
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = InkBrown.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        AgedPaperCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .alpha(if (enabled) 1f else 0.5f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BotanicalGreen.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = BotanicalGreen
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = InkBrown)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.5f))
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = InkBrown.copy(alpha = 0.2f),
            modifier = Modifier.size(20.dp)
        )
    }
}
