package com.nature.docs.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class NatureTheme(val id: Int, val primaryColor: Color) {
    PARCHMENT(0, BotanicalGreen),
    MOONLIT(1, MoonlitPrimary),
    MEADOW(2, MeadowPrimary),
    SUNSET(3, SunsetPrimary);

    companion object {
        fun fromId(id: Int) = values().find { it.id == id } ?: PARCHMENT
    }
}

private val LightColorScheme = lightColorScheme(
    primary = BotanicalGreen,
    secondary = InkBrown,
    tertiary = Terracotta,
    background = ParchmentBg,
    surface = ParchmentBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = InkBrown,
    onSurface = InkBrown,
)

private val MoonlitColorScheme = lightColorScheme(
    primary = MoonlitPrimary,
    secondary = MoonlitAccent,
    background = MoonlitBg,
    surface = MoonlitBg,
    onBackground = InkBrown,
    onSurface = InkBrown,
)

private val MeadowColorScheme = lightColorScheme(
    primary = MeadowPrimary,
    background = MeadowBg,
    surface = MeadowBg,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val SunsetColorScheme = lightColorScheme(
    primary = SunsetPrimary,
    background = SunsetBg,
    surface = SunsetBg,
    onBackground = InkBrown,
    onSurface = InkBrown,
)

@Composable
fun NatureToolsTheme(
    themeId: Int = 0,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val natureTheme = NatureTheme.fromId(themeId)
    val colorScheme = when (natureTheme) {
        NatureTheme.PARCHMENT -> LightColorScheme
        NatureTheme.MOONLIT -> MoonlitColorScheme
        NatureTheme.MEADOW -> MeadowColorScheme
        NatureTheme.SUNSET -> SunsetColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
