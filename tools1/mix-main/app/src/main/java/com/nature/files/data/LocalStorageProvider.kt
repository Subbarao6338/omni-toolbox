package com.nature.files.data

import com.nature.files.utils.FileOperationLock
import com.nature.files.utils.FileUtils
import java.io.*
import java.util.*

class LocalStorageProvider : StorageProvider {
    override fun getFiles(path: String, sortOrder: SortOrder, showHidden: Boolean): List<FileItem> {
        val directory = File(path)
        if (!directory.exists() || !directory.isDirectory) return emptyList()

        val files = directory.listFiles()?.filter {
            showHidden || !it.name.startsWith(".")
        } ?: return emptyList()

        val fileItems = files.map {
            FileItem(
                name = it.name,
                path = it.absolutePath,
                isDirectory = it.isDirectory,
                size = if (it.isDirectory) 0 else it.length(),
                lastModified = it.lastModified(),
                mimeType = FileUtils.getMimeType(it)
            )
        }

        val comparator = when (sortOrder) {
            SortOrder.NAME_ASC -> compareBy<FileItem> { it.name.lowercase() }
            SortOrder.NAME_DESC -> compareByDescending<FileItem> { it.name.lowercase() }
            SortOrder.SIZE_ASC -> compareBy<FileItem> { it.size }
            SortOrder.SIZE_DESC -> compareByDescending<FileItem> { it.size }
            SortOrder.DATE_ASC -> compareBy<FileItem> { it.lastModified }
            SortOrder.DATE_DESC -> compareByDescending<FileItem> { it.lastModified }
        }

        return fileItems.sortedWith(compareBy<FileItem>({ !it.isDirectory }).thenComparing(comparator))
    }

    override fun createDirectory(parentPath: String, name: String): Boolean {
        return File(parentPath, name).mkdirs()
    }

    override fun delete(path: String, secure: Boolean): Boolean {
        val file = File(path)
        if (secure) {
            secureDelete(file)
        }
        return if (file.isDirectory) {
            file.deleteRecursively()
        } else {
            file.delete()
        }
    }

    private fun secureDelete(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { secureDelete(it) }
        } else {
            // Quality Gate: 3-pass overwrite
            val length = file.length()
            if (length > 0) {
                val random = Random()
                RandomAccessFile(file, "rws").use { raf ->
                    repeat(3) {
                        val buffer = ByteArray(8192)
                        var written = 0L
                        while (written < length) {
                            random.nextBytes(buffer)
                            val toWrite = minOf(buffer.size.toLong(), length - written).toInt()
                            raf.write(buffer, 0, toWrite)
                            written += toWrite
                        }
                        raf.seek(0)
                    }
                }
            }
        }
    }

    override fun rename(path: String, newName: String): Boolean {
        val file = File(path)
        val newFile = File(file.parent, newName)
        return file.renameTo(newFile)
    }

    override fun exists(path: String): Boolean = File(path).exists()

    override fun isDirectory(path: String): Boolean = File(path).isDirectory

    override fun getInputStream(path: String): InputStream = FileInputStream(path)

    override fun getOutputStream(path: String): OutputStream {
        // For atomic writes, we'll return a FilterOutputStream that writes to a temp file and renames on close
        // But the interface returns a raw OutputStream. We'll handle atomicity in the copy/move methods.
        return FileOutputStream(path)
    }

    override fun copy(sourcePath: String, destinationPath: String): Boolean {
        val source = File(sourcePath)
        val dest = File(destinationPath, source.name)
        return if (source.isDirectory) {
            copyDirectory(source, dest)
        } else {
            copySingleFile(source, dest)
        }
    }

    private fun copySingleFile(source: File, dest: File): Boolean {
        val tempFile = File(dest.parent, ".tmp_${UUID.randomUUID()}_${dest.name}")
        return try {
            source.inputStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (dest.exists()) dest.delete()
            tempFile.renameTo(dest)
        } catch (e: IOException) {
            if (tempFile.exists()) tempFile.delete()
            false
        }
    }

    private fun copyDirectory(source: File, dest: File): Boolean {
        if (!dest.exists() && !dest.mkdirs()) return false
        source.listFiles()?.forEach { file ->
            val target = File(dest, file.name)
            if (file.isDirectory) {
                if (!copyDirectory(file, target)) return false
            } else {
                if (!copySingleFile(file, target)) return false
            }
        }
        return true
    }

    override fun move(sourcePath: String, destinationPath: String): Boolean {
        val source = File(sourcePath)
        val dest = File(destinationPath, source.name)
        if (source.renameTo(dest)) return true

        // Fallback to copy and delete
        if (copy(sourcePath, destinationPath)) {
            return delete(sourcePath)
        }
        return false
    }
}
