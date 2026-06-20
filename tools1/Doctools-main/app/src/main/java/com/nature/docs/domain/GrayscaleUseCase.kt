package com.nature.docs.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import com.nature.docs.ui.components.saveAndFlush
import com.nature.docs.ui.components.toGrayscaleBitmap
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UseCase for converting a color PDF to grayscale by flattening pages into B&W images.
 */
class GrayscaleUseCase {
    /**
     * Converts a PDF to grayscale.
     *
     * @param context Android context for content resolution and file I/O.
     * @param inputUri URI of the source PDF.
     * @param outputUri URI where the grayscale PDF will be saved.
     * @param password Optional password for encrypted PDFs.
     * @param onProgress Callback for reporting conversion progress (current page, total pages).
     * @throws Exception If processing or I/O fails.
     */
    suspend fun execute(
        context: Context,
        inputUri: Uri,
        outputUri: Uri,
        password: String?,
        onProgress: (Int, Int) -> Unit
    ) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
            val sourceDoc = if (password != null) PDDocument.load(inputStream, password) else PDDocument.load(inputStream)
            if (sourceDoc.isEncrypted) sourceDoc.isAllSecurityToBeRemoved = true
            val targetDoc = PDDocument()
            val renderer = PDFRenderer(sourceDoc)
            val total = sourceDoc.numberOfPages

            for (i in 0 until total) {
                onProgress(i + 1, total)
                val scale = 1.2f
                val bitmap = renderer.renderImage(i, scale, ImageType.RGB)
                val grayBitmap = toGrayscaleBitmap(bitmap)
                bitmap.recycle()

                val pdImage = JPEGFactory.createFromImage(targetDoc, grayBitmap, 0.7f)
                val page = PDPage(PDRectangle(pdImage.width.toFloat() / scale, pdImage.height.toFloat() / scale))
                targetDoc.addPage(page)
                PDPageContentStream(targetDoc, page).use {
                    it.drawImage(pdImage, 0f, 0f, page.mediaBox.width, page.mediaBox.height)
                }
                grayBitmap.recycle()
            }
            saveAndFlush(context, targetDoc, outputUri)
            sourceDoc.close()
        }
    }
}
