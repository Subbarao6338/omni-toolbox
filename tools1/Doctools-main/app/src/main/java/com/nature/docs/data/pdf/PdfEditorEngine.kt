package com.nature.docs.data.pdf

import android.graphics.Color
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm
import java.io.InputStream

object PdfEditorEngine {

    fun addSignature(doc: PDDocument, pageIndex: Int, signatureStream: InputStream, x: Float, y: Float, width: Float, height: Float) {
        val page = doc.getPage(pageIndex)
        val image = JPEGFactory.createFromStream(doc, signatureStream)
        PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true).use { contentStream ->
            contentStream.drawImage(image, x, y, width, height)
        }
    }

    fun fillFormField(doc: PDDocument, fieldName: String, value: String) {
        val acroForm: PDAcroForm? = doc.documentCatalog.acroForm
        acroForm?.getField(fieldName)?.setValue(value)
    }

    fun flatten(doc: PDDocument) {
        doc.documentCatalog.acroForm?.flatten()
    }
}
