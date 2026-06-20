package com.nature.docs.data.office

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream

object OfficeToPdfConverter {

    fun convertWordToPdf(inputStream: InputStream): PDDocument {
        val wordDoc = XWPFDocument(inputStream)
        val pdfDoc = PDDocument()

        wordDoc.paragraphs.forEach { paragraph ->
            val page = PDPage()
            pdfDoc.addPage(page)
            PDPageContentStream(pdfDoc, page).use { contentStream ->
                contentStream.beginText()
                contentStream.setFont(PDType1Font.HELVETICA, 12f)
                contentStream.newLineAtOffset(50f, 750f)
                contentStream.showText(paragraph.text)
                contentStream.endText()
            }
        }
        return pdfDoc
    }
}
