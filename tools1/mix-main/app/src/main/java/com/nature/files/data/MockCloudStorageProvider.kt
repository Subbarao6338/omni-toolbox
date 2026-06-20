package com.nature.files.data

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Implements a mock cloud storage for testing and extensibility.
 * Uses the 'cloud://' scheme.
 */
class MockCloudStorageProvider : StorageProvider {
    private val cloudFiles = mutableMapOf<String, MockFile>()

    data class MockFile(
        val name: String,
        val path: String,
        val isDirectory: Boolean,
        var data: ByteArray = byteArrayOf(),
        var lastModified: Long = System.currentTimeMillis()
    )

    init {
        // Sample cloud data
        cloudFiles["cloud://root/Welcome.txt"] = MockFile("Welcome.txt", "cloud://root/Welcome.txt", false, "Welcome to the Nature Cloud! ☁️🌿".toByteArray())
        cloudFiles["cloud://root/Photos"] = MockFile("Photos", "cloud://root/Photos", true)
    }

    override fun getFiles(path: String, sortOrder: SortOrder, showHidden: Boolean): List<FileItem> {
        val results = cloudFiles.values.filter {
            it.path.startsWith(path) && it.path != path && !it.path.substring(path.length + 1).contains("/")
        }.map {
            FileItem(
                name = it.name,
                path = it.path,
                isDirectory = it.isDirectory,
                size = it.data.size.toLong(),
                lastModified = it.lastModified,
                mimeType = if (it.isDirectory) null else "text/plain"
            )
        }

        // Performance Gate: Mock server-side sorting
        val comparator = when (sortOrder) {
            SortOrder.NAME_ASC -> compareBy<FileItem> { it.name.lowercase() }
            SortOrder.NAME_DESC -> compareByDescending<FileItem> { it.name.lowercase() }
            else -> compareBy<FileItem> { it.name }
        }
        return results.sortedWith(comparator)
    }

    override fun createDirectory(parentPath: String, name: String): Boolean {
        val fullPath = if (parentPath.endsWith("/")) "$parentPath$name" else "$parentPath/$name"
        cloudFiles[fullPath] = MockFile(name, fullPath, true)
        return true
    }

    override fun delete(path: String, secure: Boolean): Boolean {
        cloudFiles.remove(path)
        return true
    }

    override fun rename(path: String, newName: String): Boolean {
        val file = cloudFiles.remove(path) ?: return false
        val newPath = path.substringBeforeLast("/") + "/" + newName
        cloudFiles[newPath] = file.copy(name = newName, path = newPath)
        return true
    }

    override fun exists(path: String): Boolean = cloudFiles.containsKey(path)

    override fun isDirectory(path: String): Boolean = cloudFiles[path]?.isDirectory ?: false

    override fun getInputStream(path: String): InputStream {
        val file = cloudFiles[path] ?: throw java.io.FileNotFoundException()
        return ByteArrayInputStream(file.data)
    }

    override fun getOutputStream(path: String): OutputStream {
        return object : ByteArrayOutputStream() {
            override fun close() {
                super.close()
                val name = path.substringAfterLast("/")
                cloudFiles[path] = MockFile(name, path, false, toByteArray())
            }
        }
    }

    override fun copy(sourcePath: String, destinationPath: String): Boolean {
        // Standardized Storage Contract: destinationPath is the final destination
        val file = cloudFiles[sourcePath] ?: return false
        cloudFiles[destinationPath] = file.copy(path = destinationPath)
        return true
    }

    override fun move(sourcePath: String, destinationPath: String): Boolean {
        if (copy(sourcePath, destinationPath)) {
            return delete(sourcePath)
        }
        return false
    }
}
