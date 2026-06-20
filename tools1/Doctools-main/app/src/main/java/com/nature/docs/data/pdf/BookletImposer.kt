package com.nature.docs.data.pdf

import android.content.Context
import android.net.Uri
import com.nature.docs.ui.components.saveAndFlush
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility for creating a booklet from a PDF.
 * Rearranges pages for printing 4 pages per sheet (front and back).
 */
object BookletImposer {
    /**
     * Rearranges pages into booklet order.
     * @param context Android context.
     * @param inputUri Source PDF.
     * @param outputUri Destination PDF.
     * @param password Optional password.
     */
    suspend fun impose(
        context: Context,
        inputUri: Uri,
        outputUri: Uri,
        password: String? = null
    ) = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
            val sourceDoc = if (password != null) PDDocument.load(inputStream, password) else PDDocument.load(inputStream)
            if (sourceDoc.isEncrypted) sourceDoc.isAllSecurityToBeRemoved = true

            val targetDoc = PDDocument()
            val pageCount = sourceDoc.numberOfPages

            // Pad to multiple of 4
            val paddedCount = ((pageCount + 3) / 4) * 4
            val sourcePages = (0 until pageCount).map { sourceDoc.getPage(it) }.toMutableList()
            while (sourcePages.size < paddedCount) {
                sourcePages.add(PDPage(sourceDoc.getPage(0).mediaBox))
            }

            // Rearrange: [Last, First, Second, SecondLast] etc.
            var left = 0
            var right = paddedCount - 1

            while (left < right) {
                // Front sheet
                targetDoc.addPage(targetDoc.importPage(sourcePages[right]))
                targetDoc.addPage(targetDoc.importPage(sourcePages[left]))
                left++
                right--

                // Back sheet
                targetDoc.addPage(targetDoc.importPage(sourcePages[left]))
                targetDoc.addPage(targetDoc.importPage(sourcePages[right]))
                left++
                right--
            }

            saveAndFlush(context, targetDoc, outputUri)
            sourceDoc.close()
        }
    }
}
