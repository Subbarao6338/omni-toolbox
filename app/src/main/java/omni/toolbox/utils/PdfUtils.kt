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
}
