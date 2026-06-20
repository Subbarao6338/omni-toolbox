package com.nature.docs.data.image

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility for extracting a color palette from a specimen (image).
 */
object PaletteExtractor {
    /**
     * Extracts dominant colors from a bitmap.
     * @param bitmap The source image specimen.
     * @return List of hex color strings.
     */
    suspend fun extract(bitmap: Bitmap): List<String> = withContext(Dispatchers.Default) {
        val palette = Palette.from(bitmap).generate()
        val colors = mutableListOf<String>()

        palette.dominantSwatch?.let { colors.add(formatColor(it.rgb)) }
        palette.vibrantSwatch?.let { colors.add(formatColor(it.rgb)) }
        palette.mutedSwatch?.let { colors.add(formatColor(it.rgb)) }
        palette.lightVibrantSwatch?.let { colors.add(formatColor(it.rgb)) }
        palette.darkVibrantSwatch?.let { colors.add(formatColor(it.rgb)) }

        colors.distinct()
    }

    private fun formatColor(color: Int): String {
        return String.format("#%06X", 0xFFFFFF and color)
    }
}
