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

class CyberpunkTransformation : ImageTransformation {
    override fun transform(input: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val matrix = ColorMatrix().apply {
            set(floatArrayOf(
                1.2f, 0f, 0f, 0f, 20f,
                0f, 0.9f, 0f, 0f, -10f,
                0f, 0f, 1.5f, 0f, 30f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        paint.colorFilter = ColorMatrixColorFilter(matrix)
        canvas.drawBitmap(input, 0f, 0f, paint)
        return output
    }
}

class GothamTransformation : ImageTransformation {
    override fun transform(input: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(input.width, input.height, input.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val matrix = ColorMatrix().apply {
            setSaturation(0.1f)
            val contrast = 1.2f
            val translate = (-0.5f * contrast + 0.5f) * 255.0f
            val postMatrix = floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate + 10f,
                0f, 0f, 0f, 1f, 0f
            )
            postConcat(ColorMatrix(postMatrix))
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
