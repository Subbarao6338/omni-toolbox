package com.nature.docs.data.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Matrix

object ImageEditorEngine {

    fun applyAdjustments(
        bitmap: Bitmap,
        brightness: Float = 0f,
        contrast: Float = 1f,
        saturation: Float = 1f
    ): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(output)
        val paint = Paint()

        val cm = ColorMatrix().apply {
            // Brightness
            val b = brightness * 255
            set(floatArrayOf(
                1f, 0f, 0f, 0f, b,
                0f, 1f, 0f, 0f, b,
                0f, 0f, 1f, 0f, b,
                0f, 0f, 0f, 1f, 0f
            ))

            // Contrast
            val postContrast = ColorMatrix().apply {
                val s = contrast
                val o = 128f * (1f - s)
                set(floatArrayOf(
                    s, 0f, 0f, 0f, o,
                    0f, s, 0f, 0f, o,
                    0f, 0f, s, 0f, o,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            postConcat(postContrast)

            // Saturation
            val postSaturation = ColorMatrix().apply { setSaturation(saturation) }
            postConcat(postSaturation)
        }

        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun crop(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }
}
