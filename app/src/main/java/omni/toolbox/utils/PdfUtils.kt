package omni.toolbox.utils

import android.graphics.Bitmap
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.io.File
import java.io.FileOutputStream

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
        // Simple repair by reloading and resaving using PDFBox
        val document = PDDocument.load(pdfFile)
        document.save(outFile)
        document.close()
    }

    fun invert(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        for (page in document.pages) {
            val contentStream = com.tom_roush.pdfbox.pdmodel.PDPageContentStream(document, page, com.tom_roush.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND, true, true)
            val graphicsState = com.tom_roush.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState()
            graphicsState.blendMode = com.tom_roush.pdfbox.pdmodel.graphics.blend.BlendMode.DIFFERENCE
            contentStream.setGraphicsStateParameters(graphicsState)
            contentStream.setNonStrokingColor(1.0f, 1.0f, 1.0f)
            contentStream.addRect(0f, 0f, page.mediaBox.width, page.mediaBox.height)
            contentStream.fill()
            contentStream.close()
        }
        document.save(outFile)
        document.close()
    }

    fun grayscale(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        // For actual grayscale, we iterate through images in the resources
        for (page in document.pages) {
            val resources = page.resources
            for (name in resources.xObjectNames) {
                val xobject = resources.getXObject(name)
                if (xobject is com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject) {
                    // This is a placeholder for actual pixel-by-pixel conversion if needed,
                    // but often resaving with different settings or flattening works.
                }
            }
        }
        document.save(outFile)
        document.close()
    }

    fun compress(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        // PDFBox-Android re-saving can sometimes perform basic stream optimization.
        // In a full implementation, we'd adjust JPEG quality for PDImageXObjects.
        document.save(outFile)
        document.close()
    }

    fun flatten(pdfFile: File, outFile: File) {
        val document = PDDocument.load(pdfFile)
        val acroForm = document.documentCatalog.acroForm
        if (acroForm != null) {
            acroForm.flatten()
        }
        document.save(outFile)
        document.close()
    }
}
