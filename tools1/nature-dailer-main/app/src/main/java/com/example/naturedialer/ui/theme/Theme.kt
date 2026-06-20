package com.example.naturedialer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class DialerTheme {
    FOREST, OCEAN, DESERT, FROST, NIGHT, ARTISTIC
}

data class NatureColorScheme(
    val bg: Color,
    val text: Color,
    val accent: Color,
    val surface: Color,
    val dial: Color
)

fun getNatureColors(theme: DialerTheme): NatureColorScheme {
    return when (theme) {
        DialerTheme.FOREST -> NatureColorScheme(ForestBg, ForestText, ForestAccent, ForestSurface, ForestDial)
        DialerTheme.OCEAN -> NatureColorScheme(OceanBg, OceanText, OceanAccent, OceanSurface, OceanDial)
        DialerTheme.DESERT -> NatureColorScheme(DesertBg, DesertText, DesertAccent, DesertSurface, DesertDial)
        DialerTheme.FROST -> NatureColorScheme(FrostBg, FrostText, FrostAccent, FrostSurface, FrostDial)
        DialerTheme.NIGHT -> NatureColorScheme(NightBg, NightText, NightAccent, NightSurface, NightDial)
        DialerTheme.ARTISTIC -> NatureColorScheme(ArtisticBg, ArtisticText, ArtisticAccent, ArtisticSurface, ArtisticDial)
    }
}

@Composable
fun NatureDialerTheme(
    dialerTheme: DialerTheme = DialerTheme.FOREST,
    content: @Composable () -> Unit
) {
    val natureColors = getNatureColors(dialerTheme)

    val colorScheme = if (dialerTheme == DialerTheme.ARTISTIC) {
        lightColorScheme(
            primary = natureColors.accent,
            onPrimary = natureColors.bg,
            background = natureColors.bg,
            onBackground = natureColors.text,
            surface = natureColors.surface,
            onSurface = natureColors.text
        )
    } else {
        darkColorScheme(
            primary = natureColors.accent,
            onPrimary = Color.Black,
            background = natureColors.bg,
            onBackground = natureColors.text,
            surface = natureColors.surface,
            onSurface = natureColors.text
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
