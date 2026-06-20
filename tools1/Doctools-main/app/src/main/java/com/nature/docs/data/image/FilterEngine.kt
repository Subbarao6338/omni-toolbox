package com.nature.docs.data.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

object FilterEngine {

    fun applyVintage(bitmap: Bitmap): Bitmap {
        val cm = ColorMatrix().apply {
            set(floatArrayOf(
                0.9f, 0.1f, 0.1f, 0f, 0f,
                0.1f, 0.8f, 0.1f, 0f, 0f,
                0.1f, 0.1f, 0.7f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        return applyMatrix(bitmap, cm)
    }

    fun applySepia(bitmap: Bitmap): Bitmap {
        val cm = ColorMatrix().apply {
            set(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
        }
        return applyMatrix(bitmap, cm)
    }

    private fun applyMatrix(bitmap: Bitmap, matrix: ColorMatrix): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(output)
        val paint = Paint().apply { colorFilter = ColorMatrixColorFilter(matrix) }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }
}
