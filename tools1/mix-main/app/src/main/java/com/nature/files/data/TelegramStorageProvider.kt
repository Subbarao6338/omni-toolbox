package com.nature.files.data

import com.nature.files.utils.FileUtils
import java.io.*
import java.util.*

/**
 * Persistent simulation for Telegram Cloud Storage.
 * Uses a hidden local directory to persist "cloud" data across app restarts.
 */
class TelegramStorageProvider(private val context: android.content.Context?) : StorageProvider {
    private val rootDir: File by lazy {
        val dir = File(context?.getExternalFilesDir(null), ".telegram_sim")
        if (!dir.exists()) {
            dir.mkdirs()
            // Sample data for first run
            File(dir, "SavedMessages_Log.txt").writeText("Simulated Telegram Saved Messages.")
        }
        dir
    }

    private fun getFileFromPath(path: String): File {
        val relPath = path.removePrefix("telegram://root")
        return File(rootDir, relPath.removePrefix("/"))
    }

    private fun getUriFromFile(file: File): String {
        val relPath = file.absolutePath.removePrefix(rootDir.absolutePath)
        return "telegram://root${if (relPath.startsWith("/")) "" else "/"}$relPath"
    }

    override fun getFiles(path: String, sortOrder: SortOrder, showHidden: Boolean): List<FileItem> {
        val dir = getFileFromPath(path)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val files = dir.listFiles()?.filter {
            showHidden || !it.name.startsWith(".")
        } ?: return emptyList()

        val fileItems = files.map {
            FileItem(
                name = it.name,
                path = getUriFromFile(it),
                isDirectory = it.isDirectory,
                size = if (it.isDirectory) 0 else it.length(),
                lastModified = it.lastModified(),
                mimeType = if (it.isDirectory) null else FileUtils.getMimeTypeFromName(it.name)
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
        return File(getFileFromPath(parentPath), name).mkdirs()
    }

    override fun delete(path: String, secure: Boolean): Boolean {
        val file = getFileFromPath(path)
        return if (file.isDirectory) file.deleteRecursively() else file.delete()
    }

    override fun rename(path: String, newName: String): Boolean {
        val file = getFileFromPath(path)
        val newFile = File(file.parent, newName)
        return file.renameTo(newFile)
    }

    override fun exists(path: String): Boolean = getFileFromPath(path).exists()

    override fun isDirectory(path: String): Boolean = getFileFromPath(path).isDirectory

    override fun getInputStream(path: String): InputStream = FileInputStream(getFileFromPath(path))

    override fun getOutputStream(path: String): OutputStream = FileOutputStream(getFileFromPath(path))

    override fun copy(sourcePath: String, destinationPath: String): Boolean {
        val source = getFileFromPath(sourcePath)
        val dest = getFileFromPath(destinationPath)
        return try {
            if (source.isDirectory) {
                source.copyRecursively(File(dest, source.name), overwrite = true)
            } else {
                source.inputStream().use { input ->
                    File(dest, source.name).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun move(sourcePath: String, destinationPath: String): Boolean {
        val source = getFileFromPath(sourcePath)
        val dest = File(getFileFromPath(destinationPath), source.name)
        return if (source.renameTo(dest)) {
            true
        } else {
            if (copy(sourcePath, destinationPath)) {
                delete(sourcePath)
            } else {
                false
            }
        }
    }
}
