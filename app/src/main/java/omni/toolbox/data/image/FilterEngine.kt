package omni.toolbox.data.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

interface ImageTransformation {
    fun transform(input: Bitmap): Bitmap
}

class BrightnessTransformation(val amount: Float) : ImageTransformation {
    override fun transform(input: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val matrix = ColorMatrix().apply {
            set(floatArrayOf(
                1f, 0f, 0f, 0f, amount * 255f,
                0f, 1f, 0f, 0f, amount * 255f,
                0f, 0f, 1f, 0f, amount * 255f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(input, 0f, 0f, paint)
        return output
    }
}

class ContrastTransformation(val amount: Float) : ImageTransformation {
    override fun transform(input: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val t = (1.0f - amount) / 2.0f * 255.0f
        val matrix = ColorMatrix().apply {
            set(floatArrayOf(
                amount, 0f, 0f, 0f, t,
                0f, amount, 0f, 0f, t,
                0f, 0f, amount, 0f, t,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(input, 0f, 0f, paint)
        return output
    }
}

object FilterEngine {
    fun applyTransformations(input: Bitmap, transformations: List<ImageTransformation>): Bitmap {
        var result = input
        transformations.forEach {
            result = it.transform(result)
        }
        return result
    }
}
