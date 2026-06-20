package com.nature.docs.data.archive

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ArchiveEngine {

    fun createZip(files: List<File>, outputZip: File) {
        ZipOutputStream(FileOutputStream(outputZip)).use { zipOut ->
            files.forEach { file ->
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(file.name)
                    zipOut.putNextEntry(zipEntry)
                    fis.copyTo(zipOut)
                    zipOut.closeEntry()
                }
            }
        }
    }
}
