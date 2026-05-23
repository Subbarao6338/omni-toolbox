package com.naturetools.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LeafGreen,
    secondary = EarthBrown,
    tertiary = SkyBlue,
    background = ForestNight,
    surface = DeepBark,
    onPrimary = NatureWhite,
    onSecondary = NatureWhite,
    onTertiary = NatureWhite,
    onBackground = NatureWhite,
    onSurface = NatureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = LeafGreen,
    secondary = EarthBrown,
    tertiary = SkyBlue,
    background = NatureWhite,
    surface = NatureWhite,
    onPrimary = NatureWhite,
    onSecondary = NatureWhite,
    onTertiary = NatureWhite,
    onBackground = EarthBrown,
    onSurface = EarthBrown
)

@Composable
fun NatureToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        accentColor != null -> {
            if (darkTheme) {
                darkColorScheme(primary = accentColor, background = ForestNight, surface = DeepBark)
            } else {
                lightColorScheme(primary = accentColor, background = NatureWhite, surface = NatureWhite)
            }
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
