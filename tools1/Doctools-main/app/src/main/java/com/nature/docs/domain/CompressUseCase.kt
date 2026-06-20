package com.nature.docs.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.nature.docs.ui.components.saveAndFlush
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UseCase for compressing PDF documents by optimizing embedded images.
 */
class CompressUseCase {
    /**
     * Compresses a PDF by re-encoding images at a lower quality and optionally downscaling them.
     *
     * @param context Android context for content resolution and file I/O.
     * @param inputUri URI of the source PDF.
     * @param outputUri URI where the compressed PDF will be saved.
     * @param password Optional password for encrypted PDFs.
     * @param level Compression level: "Extreme", "Recommended", or "Low".
     * @param onProgress Callback for reporting compression progress (current page, total pages).
     * @throws Exception If processing or I/O fails.
     */
    suspend fun execute(
        context: Context,
        inputUri: Uri,
        outputUri: Uri,
        password: String?,
        level: String,
        onProgress: (Int, Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
            val document = if (password != null) PDDocument.load(inputStream, password) else PDDocument.load(inputStream)
            if (document.isEncrypted) document.isAllSecurityToBeRemoved = true
            val total = document.numberOfPages

            val quality = when(level) { "Extreme" -> 0.4f; "Recommended" -> 0.7f; else -> 0.9f }
            val downscale = when(level) { "Extreme" -> 0.6f; "Recommended" -> 0.8f; else -> 1.0f }

            val processedImages = mutableMapOf<String, PDImageXObject>()

            fun processResources(resources: PDResources?) {
                if (resources == null) return
                for (name in resources.xObjectNames) {
                    try {
                        val xobject = resources.getXObject(name)
                        if (xobject is PDImageXObject) {
                            val key = xobject.cosObject.toString()
                            if (processedImages.containsKey(key)) {
                                resources.put(name, processedImages[key])
                                continue
                            }

                            var bitmap = xobject.image
                            if (bitmap != null) {
                                if (downscale < 1.0f) {
                                    val w = (bitmap.width * downscale).toInt().coerceAtLeast(1)
                                    val h = (bitmap.height * downscale).toInt().coerceAtLeast(1)
                                    val scaled = Bitmap.createScaledBitmap(bitmap, w, h, true)
                                    bitmap.recycle()
                                    bitmap = scaled
                                }
                                val newImage = JPEGFactory.createFromImage(document, bitmap, quality)
                                resources.put(name, newImage)
                                processedImages[key] = newImage
                                bitmap.recycle()
                            }
                        } else if (xobject is com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject) {
                            processResources(xobject.resources)
                        }
                    } catch (e: Exception) {}
                }
            }

            document.pages.forEachIndexed { i, page ->
                onProgress(i + 1, total)
                processResources(page.resources)
            }

            saveAndFlush(context, document, outputUri)
            document.close()
        }
    }
}
