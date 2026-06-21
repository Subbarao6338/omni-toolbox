package omni.toolbox.ui.screens.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.compose.ui.graphics.ColorMatrix
import androidx.palette.graphics.Palette

object ImageProcessor {
    fun extractPalette(bitmap: Bitmap, onGenerated: (Palette?) -> Unit) {
        Palette.from(bitmap).generate { onGenerated(it) }
    }

    fun applyFilters(
        original: Bitmap,
        brightness: Float = 0f,
        contrast: Float = 1f,
        saturation: Float = 1f,
        hue: Float = 0f,
        sepia: Boolean = false,
        invert: Boolean = false,
        grayscale: Boolean = false
    ): Bitmap {
        val result = Bitmap.createBitmap(original.width, original.height, original.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()

        val cm = ColorMatrix()
        cm.reset()

        // Contrast & Brightness
        val t = (1.0f - contrast) / 2.0f * 255.0f
        val contrastMatrix = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, t + brightness * 255f,
            0f, contrast, 0f, 0f, t + brightness * 255f,
            0f, 0f, contrast, 0f, t + brightness * 255f,
            0f, 0f, 0f, 1f, 0f
        ))
        cm.set(contrastMatrix)

        // Saturation
        val invSat = 1 - saturation
        val R = 0.213f * invSat
        val G = 0.715f * invSat
        val B = 0.072f * invSat
        val satMatrix = ColorMatrix(floatArrayOf(
            R + saturation, G, B, 0f, 0f,
            R, G + saturation, B, 0f, 0f,
            R, G, B + saturation, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
        cm.timesAssign(satMatrix)

        if (sepia) {
            val sepiaMatrix = ColorMatrix(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
            cm.timesAssign(sepiaMatrix)
        }

        if (invert) {
            val invertMatrix = ColorMatrix(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            ))
            cm.timesAssign(invertMatrix)
        }

        if (grayscale) {
            val grayMatrix = ColorMatrix(floatArrayOf(
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0.33f, 0.33f, 0.33f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
            cm.timesAssign(grayMatrix)
        }

        paint.colorFilter = ColorMatrixColorFilter(cm.values)
        canvas.drawBitmap(original, 0f, 0f, paint)
        return result
    }
}
