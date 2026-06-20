package com.nature.docs.data.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.google.mlkit.vision.segmentation.Segmentation
import kotlinx.coroutines.tasks.await

object ImageMLEngine {
    private val segmenter = Segmentation.getClient(
        SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .build()
    )

    suspend fun removeBackground(bitmap: Bitmap): Bitmap {
        val image = InputImage.fromBitmap(bitmap, 0)
        val mask = segmenter.process(image).await()

        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val maskBuffer = mask.buffer
        maskBuffer.rewind()

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val confidence = maskBuffer.float
                if (confidence > 0.5f) {
                    output.setPixel(x, y, bitmap.getPixel(x, y))
                } else {
                    output.setPixel(x, y, 0) // Transparent
                }
            }
        }
        return output
    }
}
