package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkMinimalPrimary,
    onPrimary = DarkMinimalSurface,
    primaryContainer = MinimalPrimary,
    onPrimaryContainer = MinimalPrimaryContainer,
    secondary = DarkMinimalTextMain,
    onSecondary = DarkMinimalSurface,
    background = DarkMinimalBackground,
    onBackground = DarkMinimalTextMain,
    surface = DarkMinimalSurface,
    onSurface = DarkMinimalTextMain,
    surfaceVariant = Color(0xFF2D2F39),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = MinimalPrimary,
    onPrimary = Color.White,
    primaryContainer = MinimalPrimaryContainer,
    onPrimaryContainer = MinimalOnPrimaryContainer,
    secondary = MinimalTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = MinimalSurfaceVariantAlt,
    onSecondaryContainer = MinimalTextMain,
    background = MinimalBackground,
    onBackground = MinimalTextMain,
    surface = MinimalSurface,
    onSurface = MinimalTextMain,
    surfaceVariant = MinimalSurfaceVariant,
    onSurfaceVariant = MinimalTextSecondary,
    outline = MinimalOutline,
    error = MinimalError,
    errorContainer = MinimalErrorContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color by default to preserve the exact custom theme design
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
