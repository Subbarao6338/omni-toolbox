package com.nature.files.data

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.nature.files.utils.FileOperationLock
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

enum class SortOrder {
    NAME_ASC, NAME_DESC, SIZE_ASC, SIZE_DESC, DATE_ASC, DATE_DESC
}

class FileRepository(private val context: Context? = null) {
    private val localProvider = LocalStorageProvider()
    private val smbProvider = SmbStorageProvider()
    private val mockCloudProvider = MockCloudStorageProvider()
    private val megaProvider = MegaStorageProvider(context)
    private val gdriveProvider = GDriveStorageProvider(context)
    private val onedriveProvider = OneDriveStorageProvider(context)
    private val telegramProvider = TelegramStorageProvider(context)
    private val sftpProvider = context?.let { SftpStorageProvider(it) }

    fun getStorageProvider(path: String = ""): StorageProvider {
        return when {
            path.startsWith("smb://") -> smbProvider
            path.startsWith("cloud://") -> mockCloudProvider
            path.startsWith("mega://") -> megaProvider
            path.startsWith("gdrive://") -> gdriveProvider
            path.startsWith("onedrive://") -> onedriveProvider
            path.startsWith("telegram://") -> telegramProvider
            path.startsWith("sftp://") -> sftpProvider ?: localProvider
            else -> localProvider
        }
    }

    fun getFiles(
        directoryPath: String,
        sortOrder: SortOrder = SortOrder.NAME_ASC,
        showHidden: Boolean = false
    ): List<FileItem> {
        if (directoryPath.startsWith("content://")) {
            return getFilesFromSaf(directoryPath, sortOrder, showHidden)
        }

        // Dynamic routing based on path scheme
        return getStorageProvider(directoryPath).getFiles(directoryPath, sortOrder, showHidden)
    }

    private fun getFilesFromSaf(
        uriString: String,
        sortOrder: SortOrder,
        showHidden: Boolean
    ): List<FileItem> {
        val context = context ?: return emptyList()
        val rootDoc = DocumentFile.fromTreeUri(context, Uri.parse(uriString)) ?: return emptyList()
        val files = rootDoc.listFiles().filter {
            showHidden || !it.name.orEmpty().startsWith(".")
        }

        val fileItems = files.map {
            FileItem(
                name = it.name.orEmpty(),
                path = it.uri.toString(),
                isDirectory = it.isDirectory,
                size = it.length(),
                lastModified = it.lastModified(),
                mimeType = it.type
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

    suspend fun deleteFile(path: String, permanent: Boolean = false, secure: Boolean = false): Boolean = FileOperationLock.withLock(path) {
        if (permanent || secure) {
            getStorageProvider(path).delete(path, secure)
        } else {
            moveToRecycleBin(File(path))
        }
    }

    private fun moveToRecycleBin(file: File): Boolean {
        val recycleBin = File(context?.getExternalFilesDir(null), ".recycle_bin")
        if (!recycleBin.exists()) recycleBin.mkdirs()

        val target = File(recycleBin, "${System.currentTimeMillis()}_${file.name}")
        return file.renameTo(target)
    }

    fun emptyRecycleBin() {
        val recycleBin = File(context?.getExternalFilesDir(null), ".recycle_bin")
        if (recycleBin.exists()) {
            recycleBin.deleteRecursively()
        }
    }

    fun cleanOldRecycleBinFiles() {
        val recycleBin = File(context?.getExternalFilesDir(null), ".recycle_bin")
        if (recycleBin.exists()) {
            val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            recycleBin.listFiles()?.forEach { file ->
                if (file.lastModified() < thirtyDaysAgo) {
                    file.deleteRecursively()
                }
            }
        }
    }

    suspend fun renameFile(path: String, newName: String): Boolean = FileOperationLock.withLock(path) {
        getStorageProvider(path).rename(path, newName)
    }

    suspend fun createDirectory(parentPath: String, name: String): Boolean = FileOperationLock.withLock(parentPath) {
        getStorageProvider(parentPath).createDirectory(parentPath, name)
    }

    @Throws(IOException::class)
    suspend fun copyFile(sourcePath: String, destDirectoryPath: String): Boolean = FileOperationLock.withLocks(listOf(sourcePath, destDirectoryPath)) {
        getStorageProvider(sourcePath).copy(sourcePath, destDirectoryPath)
    }

    @Throws(IOException::class)
    suspend fun moveFile(sourcePath: String, destDirectoryPath: String): Boolean = FileOperationLock.withLocks(listOf(sourcePath, destDirectoryPath)) {
        getStorageProvider(sourcePath).move(sourcePath, destDirectoryPath)
    }

    @Throws(IOException::class)
    suspend fun compressFiles(sourcePaths: List<String>, archivePath: String): Boolean = FileOperationLock.withLocks(sourcePaths + archivePath) {
        when {
            archivePath.endsWith(".zip") -> compressToZip(sourcePaths, archivePath)
            archivePath.endsWith(".7z") -> compressTo7z(sourcePaths, archivePath)
            archivePath.endsWith(".tar.gz") -> compressToTarGz(sourcePaths, archivePath)
            else -> compressToZip(sourcePaths, archivePath)
        }
    }

    private fun compressToZip(sourcePaths: List<String>, zipFilePath: String): Boolean {
        val tempFile = File("$zipFilePath.tmp")
        try {
            FileOutputStream(tempFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    sourcePaths.forEach { path ->
                        val file = File(path)
                        addFileToZip("", file, zos)
                    }
                }
            }
            if (File(zipFilePath).exists()) File(zipFilePath).delete()
            return tempFile.renameTo(File(zipFilePath))
        } catch (e: IOException) {
            if (tempFile.exists()) tempFile.delete()
            throw e
        }
    }

    private fun compressTo7z(sourcePaths: List<String>, filePath: String): Boolean {
        val tempFile = File("$filePath.tmp")
        try {
            SevenZOutputFile(tempFile).use { out ->
                sourcePaths.forEach { path ->
                    val file = File(path)
                    addFileTo7z("", file, out)
                }
            }
            if (File(filePath).exists()) File(filePath).delete()
            return tempFile.renameTo(File(filePath))
        } catch (e: IOException) {
            if (tempFile.exists()) tempFile.delete()
            throw e
        }
    }

    private fun addFileTo7z(path: String, file: File, out: SevenZOutputFile) {
        val entryName = if (path.isEmpty()) file.name else "$path/${file.name}"
        val entry = out.createArchiveEntry(file, entryName)
        out.putArchiveEntry(entry)
        if (!file.isDirectory) {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var len: Int
                while (fis.read(buffer).also { len = it } > 0) {
                    out.write(buffer, 0, len)
                }
            }
        }
        out.closeArchiveEntry()
        if (file.isDirectory) {
            file.listFiles()?.forEach { child -> addFileTo7z(entryName, child, out) }
        }
    }

    private fun compressToTarGz(sourcePaths: List<String>, filePath: String): Boolean {
        val tempFile = File("$filePath.tmp")
        try {
            FileOutputStream(tempFile).use { fos ->
                BufferedOutputStream(fos).use { bos ->
                    GzipCompressorOutputStream(bos).use { gzos ->
                        TarArchiveOutputStream(gzos).use { tos ->
                            sourcePaths.forEach { path ->
                                val file = File(path)
                                addFileToTar("", file, tos)
                            }
                        }
                    }
                }
            }
            if (File(filePath).exists()) File(filePath).delete()
            return tempFile.renameTo(File(filePath))
        } catch (e: IOException) {
            if (tempFile.exists()) tempFile.delete()
            throw e
        }
    }

    private fun addFileToTar(path: String, file: File, tos: TarArchiveOutputStream) {
        val entryName = if (path.isEmpty()) file.name else "$path/${file.name}"
        val entry = TarArchiveEntry(file, entryName)
        tos.putArchiveEntry(entry)
        if (!file.isDirectory) {
            FileInputStream(file).use { fis -> fis.copyTo(tos) }
        }
        tos.closeArchiveEntry()
        if (file.isDirectory) {
            file.listFiles()?.forEach { child -> addFileToTar(entryName, child, tos) }
        }
    }

    @Throws(IOException::class)
    private fun addFileToZip(path: String, file: File, zos: ZipOutputStream) {
        val entryName = if (path.isEmpty()) file.name else "$path/${file.name}"
        if (file.isDirectory) {
            val entry = ZipEntry("$entryName/")
            zos.putNextEntry(entry)
            zos.closeEntry()
            file.listFiles()?.forEach { child ->
                addFileToZip(entryName, child, zos)
            }
        } else {
            val entry = ZipEntry(entryName)
            zos.putNextEntry(entry)
            FileInputStream(file).use { fis ->
                fis.copyTo(zos)
            }
            zos.closeEntry()
        }
    }

    @Throws(IOException::class)
    suspend fun extractArchive(archivePath: String, destDirectoryPath: String): Boolean = FileOperationLock.withLocks(listOf(archivePath, destDirectoryPath)) {
        when {
            archivePath.endsWith(".zip") -> extractZip(archivePath, destDirectoryPath)
            archivePath.endsWith(".7z") -> extract7z(archivePath, destDirectoryPath)
            archivePath.endsWith(".tar.gz") -> extractTarGz(archivePath, destDirectoryPath)
            else -> extractZip(archivePath, destDirectoryPath)
        }
    }

    @Throws(IOException::class)
    private fun extractZip(zipFilePath: String, destDirectoryPath: String): Boolean {
        val destDir = File(destDirectoryPath)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        ZipInputStream(FileInputStream(zipFilePath)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val newFile = File(destDir, entry.name)
                validateEntryPath(newFile, destDir)
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    // Extract to temp then rename for atomicity
                    val tempFile = File(newFile.parent, ".tmp_${UUID.randomUUID()}_${newFile.name}")
                    try {
                        FileOutputStream(tempFile).use { fos ->
                            zis.copyTo(fos)
                        }
                        if (newFile.exists()) newFile.delete()
                        tempFile.renameTo(newFile)
                    } catch (e: IOException) {
                        if (tempFile.exists()) tempFile.delete()
                        throw e
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
        return true
    }

    private fun extract7z(filePath: String, destPath: String): Boolean {
        val destDir = File(destPath)
        SevenZFile(File(filePath)).use { archive ->
            var entry = archive.nextEntry
            while (entry != null) {
                val newFile = File(destDir, entry.name)
                validateEntryPath(newFile, destDir)
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    val tempFile = File(newFile.parent, ".tmp_${UUID.randomUUID()}_${newFile.name}")
                    try {
                        FileOutputStream(tempFile).use { fos ->
                            val buffer = ByteArray(8192)
                            var len: Int
                            while (archive.read(buffer).also { len = it } > 0) {
                                fos.write(buffer, 0, len)
                            }
                        }
                        if (newFile.exists()) newFile.delete()
                        tempFile.renameTo(newFile)
                    } catch (e: IOException) {
                        if (tempFile.exists()) tempFile.delete()
                        throw e
                    }
                }
                entry = archive.nextEntry
            }
        }
        return true
    }

    private fun extractTarGz(filePath: String, destPath: String): Boolean {
        val destDir = File(destPath)
        TarArchiveInputStream(GzipCompressorInputStream(FileInputStream(filePath))).use { tis ->
            var entry = tis.nextTarEntry
            while (entry != null) {
                val newFile = File(destDir, entry.name)
                validateEntryPath(newFile, destDir)
                if (entry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    newFile.parentFile?.mkdirs()
                    val tempFile = File(newFile.parent, ".tmp_${UUID.randomUUID()}_${newFile.name}")
                    try {
                        FileOutputStream(tempFile).use { fos ->
                            tis.copyTo(fos)
                        }
                        if (newFile.exists()) newFile.delete()
                        tempFile.renameTo(newFile)
                    } catch (e: IOException) {
                        if (tempFile.exists()) tempFile.delete()
                        throw e
                    }
                }
                entry = tis.nextTarEntry
            }
        }
        return true
    }

    private fun validateEntryPath(file: File, destDir: File) {
        val canonicalPath = file.canonicalPath
        if (!canonicalPath.startsWith(destDir.canonicalPath + File.separator)) {
            throw IOException("Archive entry is outside of the destination directory: ${file.name}")
        }
    }

    /**
     * Search inside ZIP without extraction.
     * Quality Gate: Performance - avoid full extraction for simple name search.
     */
    fun searchInsideZip(zipPath: String, query: String): List<String> {
        val results = mutableListOf<String>()
        try {
            ZipInputStream(FileInputStream(zipPath)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (entry.name.contains(query, ignoreCase = true)) {
                        results.add(entry.name)
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return results
    }
}
