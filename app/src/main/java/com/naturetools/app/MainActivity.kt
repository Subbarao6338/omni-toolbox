package com.naturetools.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.naturetools.app.navigation.NatureToolsApp
import com.naturetools.app.ui.theme.NatureToolsTheme

class MainActivity : ComponentActivity() {
    private val intentState = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentState.value = intent
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        setContent {
            var themeMode by rememberSaveable { mutableStateOf(prefs.getString("theme_mode", "system") ?: "system") }
            var dynamicColor by rememberSaveable { mutableStateOf(prefs.getBoolean("dynamic_color", true)) }
            var showCategoryCounts by rememberSaveable { mutableStateOf(prefs.getBoolean("show_category_counts", true)) }
            var aiApiKey by rememberSaveable { mutableStateOf(prefs.getString("ai_api_key", "") ?: "") }
            var accentColorHex by rememberSaveable { mutableStateOf(prefs.getString("accent_color", "") ?: "") }

            val accentColor = remember(accentColorHex) {
                if (accentColorHex.isEmpty()) null else Color(accentColorHex.toULong(16))
            }

            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            NatureToolsTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, accentColor = accentColor) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NatureToolsApp(
                        intent = intentState.value,
                        themeMode = themeMode,
                        onThemeChange = {
                            themeMode = it
                            prefs.edit().putString("theme_mode", it).apply()
                        },
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = {
                            dynamicColor = it
                            prefs.edit().putBoolean("dynamic_color", it).apply()
                        },
                        showCategoryCounts = showCategoryCounts,
                        onShowCategoryCountsChange = {
                            showCategoryCounts = it
                            prefs.edit().putBoolean("show_category_counts", it).apply()
                        },
                        aiApiKey = aiApiKey,
                        onAiApiKeyChange = {
                            aiApiKey = it
                            prefs.edit().putString("ai_api_key", it).apply()
                        },
                        accentColor = accentColor,
                        onAccentColorChange = { color ->
                            val hex = if (color == null) "" else color.value.toString(16)
                            accentColorHex = hex
                            prefs.edit().putString("accent_color", hex).apply()
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intentState.value = intent
    }
}
