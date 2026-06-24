package omni.toolbox.utils

import android.graphics.Bitmap
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.io.File
import java.io.FileOutputStream
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState
import com.tom_roush.pdfbox.pdmodel.graphics.blend.BlendMode
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory

object PdfUtils {
    fun exportToImages(pdfFile: File, outputDir: File): List<File> {
        if (!outputDir.exists()) outputDir.mkdirs()
        val document = PDDocument.load(pdfFile)
        val renderer = PDFRenderer(document)
        val exportedFiles = mutableListOf<File>()

        for (i in 0 until document.numberOfPages) {
            val bitmap = renderer.renderImageWithDPI(i, 300f)
            val outFile = File(outputDir, "page_${i + 1}.jpg")
            FileOutputStream(outFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            exportedFiles.add(outFile)
        }
        document.close()
        return exportedFiles
    }

    fun protect(pdfFile: File, password: String, outFile: File) {
        val document = PDDocument.load(pdfFile)
        val accessPermissions = com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission()
        val standardProtection = com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy(password, password, accessPermissions)
        standardProtection.encryptionKeyLength = 128
        document.protect(standardProtection)
        document.save(outFile)
        document.close()
    }

    fun rotatePages(pdfFile: File, angle: Int, outFile: File) {
        val document = PDDocument.load(pdfFile)
        for (page in document.pages) {
            page.rotation = (page.rotation + angle) % 360
        }
        document.save(outFile)
        document.close()
    }

    fun unlock(pdfFile: File, password: String, outFile: File) {
        val document = PDDocument.load(pdfFile, password)
        document.isAllSecurityToBeRemoved = true
        document.save(outFile)
        document.close()
    }

    fun repair(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        document.save(outFile)
        document.close()
    }

    fun invert(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        for (page in document.pages) {
            PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true).use { contentStream ->
                val graphicsState = PDExtendedGraphicsState()
                graphicsState.blendMode = BlendMode.DIFFERENCE
                contentStream.setGraphicsStateParameters(graphicsState)
                contentStream.setNonStrokingColor(1f, 1f, 1f)
                contentStream.addRect(0f, 0f, page.mediaBox.width, page.mediaBox.height)
                contentStream.fill()
            }
        }
        document.save(outFile)
        document.close()
    }

    fun flatten(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        document.documentCatalog.acroForm?.flatten()
        document.save(outFile)
        document.close()
    }

    fun grayscale(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        val renderer = PDFRenderer(document)
        val newDoc = PDDocument()
        for (i in 0 until document.numberOfPages) {
            val bitmap = renderer.renderImageWithDPI(i, 200f)
            // Convert to grayscale using Android Bitmap logic
            val grayBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(grayBitmap)
            val paint = android.graphics.Paint()
            val colorMatrix = android.graphics.ColorMatrix()
            colorMatrix.setSaturation(0f)
            paint.colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            val page = com.tom_roush.pdfbox.pdmodel.PDPage(PDRectangle(bitmap.width.toFloat(), bitmap.height.toFloat()))
            newDoc.addPage(page)
            val pdImage = LosslessFactory.createFromImage(newDoc, grayBitmap)
            PDPageContentStream(newDoc, page).use { contentStream ->
                contentStream.drawImage(pdImage, 0f, 0f)
            }
        }
        newDoc.save(outFile)
        newDoc.close()
        document.close()
    }

    fun compress(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        val renderer = PDFRenderer(document)
        val newDoc = PDDocument()
        for (i in 0 until document.numberOfPages) {
            val bitmap = renderer.renderImageWithDPI(i, 150f)
            val page = com.tom_roush.pdfbox.pdmodel.PDPage(document.getPage(i).mediaBox)
            newDoc.addPage(page)
            val pdImage = JPEGFactory.createFromImage(newDoc, bitmap, 0.6f)
            PDPageContentStream(newDoc, page).use { contentStream ->
                contentStream.drawImage(pdImage, 0f, 0f, page.mediaBox.width, page.mediaBox.height)
            }
        }
        newDoc.save(outFile)
        newDoc.close()
        document.close()
    }
}
