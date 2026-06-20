package com.nature.files.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlin.random.Random

private val ForestFloorColorScheme = lightColorScheme(
    primary = CanopyGreen,
    onPrimary = Color.White,
    primaryContainer = LichenGrey.copy(alpha = 0.3f),
    onPrimaryContainer = BarkBrown,
    secondary = BarkBrown,
    onSecondary = Color.White,
    tertiary = SunbeamYellow,
    background = ForestFloorBackground,
    surface = ForestFloorBackground,
    onBackground = BarkBrown,
    onSurface = BarkBrown,
    surfaceVariant = LichenGrey.copy(alpha = 0.2f),
    onSurfaceVariant = BarkBrown,
    error = Color(0xFFB00020),
    onError = Color.White
)

private val AutumnCanopyColorScheme = lightColorScheme(
    primary = MapleRed,
    onPrimary = Color.White,
    secondary = HarvestOrange,
    background = AutumnBackground,
    surface = AutumnBackground,
    onBackground = BarkBrown,
    onSurface = BarkBrown
)

private val MidnightGroveColorScheme = darkColorScheme(
    primary = Moonlight,
    onPrimary = DeepForest,
    background = DeepForest,
    surface = DeepForest,
    onBackground = Moonlight,
    onSurface = Moonlight
)

private val AlpineSnowColorScheme = lightColorScheme(
    primary = FrostBlue,
    onPrimary = Color.White,
    background = SnowBackground,
    surface = SnowBackground,
    onBackground = BarkBrown,
    onSurface = BarkBrown
)

private val VolcanicColorScheme = darkColorScheme(
    primary = MagmaRed,
    onPrimary = Color.White,
    background = Obsidian,
    surface = Obsidian,
    onBackground = MagmaRed,
    onSurface = MagmaRed
)

private val SaharaSandsColorScheme = lightColorScheme(
    primary = DesertSun,
    onPrimary = Color.White,
    background = SaharaBackground,
    surface = SaharaBackground,
    onBackground = BarkBrown,
    onSurface = BarkBrown
)

private val DeepSeaColorScheme = lightColorScheme(
    primary = AbyssalBlue,
    onPrimary = Color.White,
    background = SeaBackground,
    surface = SeaBackground,
    onBackground = AbyssalBlue,
    onSurface = AbyssalBlue
)

private val BambooForestColorScheme = lightColorScheme(
    primary = BambooStem,
    onPrimary = Color.White,
    background = BambooBackground,
    surface = BambooBackground,
    onBackground = ZenGrey,
    onSurface = ZenGrey
)

enum class NatureTheme {
    FOREST_FLOOR, AUTUMN_CANOPY, MIDNIGHT_GROVE, ALPINE_SNOW, VOLCANIC, SAHARA_SANDS, DEEP_SEA, BAMBOO_FOREST
}

/**
 * A modifier that draws a faint "leaf-litter" texture behind the content.
 * Nature Design Mandate: organic, layered feel.
 */
fun Modifier.leafLitter(color: Color, density: Int = 150): Modifier = this.drawBehind {
    val random = Random(42) // Fixed seed for stable texture
    repeat(density) {
        val x = random.nextFloat() * size.width
        val y = random.nextFloat() * size.height
        val radius = random.nextFloat() * 3f + 1f
        val alpha = random.nextFloat() * 0.08f + 0.02f

        // Draw leaf-like organic shapes instead of just circles
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y),
            alpha = alpha
        )
    }
}

/**
 * Leaf particle trail for drag and drop operations.
 * Nature Design Mandate: drag micro-animations.
 */
fun Modifier.leafTrail(
    isDragging: Boolean,
    offset: Offset,
    particleCount: Int = 8
): Modifier = this.drawWithContent {
    drawContent()
    if (isDragging) {
        val random = Random(offset.hashCode())
        repeat(particleCount) {
            val pOffset = offset + Offset(
                random.nextFloat() * 60f - 30f,
                random.nextFloat() * 60f - 30f
            )
            val size = random.nextFloat() * 10f + 5f

            // Draw a tiny leaf shape
            val path = Path().apply {
                moveTo(pOffset.x, pOffset.y + size)
                quadraticBezierTo(pOffset.x + size, pOffset.y + size, pOffset.x + size, pOffset.y)
                quadraticBezierTo(pOffset.x, pOffset.y - size, pOffset.x - size, pOffset.y)
                quadraticBezierTo(pOffset.x - size, pOffset.y + size, pOffset.x, pOffset.y + size)
            }
            drawPath(
                path = path,
                color = CanopyGreen.copy(alpha = 0.6f),
                style = Fill
            )
        }
    }
}

@Composable
fun NatureFilesTheme(
    natureTheme: NatureTheme = NatureTheme.FOREST_FLOOR,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (natureTheme) {
        NatureTheme.FOREST_FLOOR -> ForestFloorColorScheme
        NatureTheme.AUTUMN_CANOPY -> AutumnCanopyColorScheme
        NatureTheme.MIDNIGHT_GROVE -> MidnightGroveColorScheme
        NatureTheme.ALPINE_SNOW -> AlpineSnowColorScheme
        NatureTheme.VOLCANIC -> VolcanicColorScheme
        NatureTheme.SAHARA_SANDS -> SaharaSandsColorScheme
        NatureTheme.DEEP_SEA -> DeepSeaColorScheme
        NatureTheme.BAMBOO_FOREST -> BambooForestColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            Surface(
                modifier = Modifier.leafLitter(colorScheme.onBackground.copy(alpha = 0.15f)),
                color = colorScheme.background,
                content = content
            )
        }
    )
}
