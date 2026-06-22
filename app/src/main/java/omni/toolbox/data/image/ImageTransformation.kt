package omni.toolbox.data.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

interface ImageTransformation {
    fun transform(bitmap: Bitmap): Bitmap
}

class ColorMatrixTransformation(private val matrix: ColorMatrix) : ImageTransformation {
    override fun transform(bitmap: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }
}

object ImageFilters {
    val Sepia = ColorMatrix().apply {
        set(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
    }

    val GrayScale = ColorMatrix().apply {
        setSaturation(0f)
    }

    val Invert = ColorMatrix().apply {
        set(floatArrayOf(
            -1f,  0f,  0f, 0f, 255f,
             0f, -1f,  0f, 0f, 255f,
             0f,  0f, -1f, 0f, 255f,
             0f,  0f,  0f, 1f, 0f
        ))
    }

    val Vintage = ColorMatrix().apply {
        set(floatArrayOf(
            0.9f, 0f, 0f, 0f, 0f,
            0f, 0.7f, 0f, 0f, 0f,
            0f, 0f, 0.5f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))
    }
}
