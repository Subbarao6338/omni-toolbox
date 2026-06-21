package omni.toolbox.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ArchiveUtils {
    fun zip(files: List<File>, outputZip: File) {
        ZipOutputStream(FileOutputStream(outputZip)).use { zos ->
            files.forEach { file ->
                if (file.isDirectory) {
                    zipDirectory(file, file.name, zos)
                } else {
                    zipFile(file, "", zos)
                }
            }
        }
    }

    private fun zipFile(file: File, parentPath: String, zos: ZipOutputStream) {
        val entryName = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
        val entry = ZipEntry(entryName)
        zos.putNextEntry(entry)
        FileInputStream(file).use { fis ->
            fis.copyTo(zos)
        }
        zos.closeEntry()
    }

    private fun zipDirectory(dir: File, parentPath: String, zos: ZipOutputStream) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                zipDirectory(file, "$parentPath/${file.name}", zos)
            } else {
                zipFile(file, parentPath, zos)
            }
        }
    }

    fun unzip(zipFile: File, outputDir: File) {
        if (!outputDir.exists()) outputDir.mkdirs()
        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outFile = File(outputDir, entry.name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }
}
