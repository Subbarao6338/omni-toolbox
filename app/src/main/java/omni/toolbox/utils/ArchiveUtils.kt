package omni.toolbox.utils

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
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

    fun create7z(files: List<File>, output7z: File) {
        SevenZOutputFile(output7z).use { out ->
            files.forEach { file ->
                val entry = out.createArchiveEntry(file, file.name)
                out.putArchiveEntry(entry)
                if (!file.isDirectory) {
                    FileInputStream(file).use { fis ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (fis.read(buffer).also { bytesRead = it } != -1) {
                            out.write(buffer, 0, bytesRead)
                        }
                    }
                }
                out.closeArchiveEntry()
            }
        }
    }

    fun extract7z(archive: File, destination: File) {
        if (!destination.exists()) destination.mkdirs()
        SevenZFile(archive).use { sevenZFile ->
            var entry = sevenZFile.nextEntry
            while (entry != null) {
                val curFile = File(destination, entry.name)
                if (entry.isDirectory) {
                    curFile.mkdirs()
                } else {
                    curFile.parentFile?.mkdirs()
                    FileOutputStream(curFile).use { out ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        while (sevenZFile.read(buffer).also { bytesRead = it } != -1) {
                            out.write(buffer, 0, bytesRead)
                        }
                    }
                }
                entry = sevenZFile.nextEntry
            }
        }
    }
}
