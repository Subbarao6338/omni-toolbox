package com.nature.files.data

import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import com.nature.files.utils.FileUtils
import java.io.InputStream
import java.io.OutputStream
import java.util.EnumSet

class SmbStorageProvider : StorageProvider {
    private val client = SMBClient()
    private var diskShare: DiskShare? = null
    private var currentHost: String? = null
    private var currentShare: String? = null

    private fun ensureConnected(path: String): String {
        val uri = java.net.URI(path)
        val host = uri.host ?: throw IllegalArgumentException("Invalid SMB host")
        val shareName = uri.path.split("/").filter { it.isNotEmpty() }.firstOrNull() ?: throw IllegalArgumentException("Invalid SMB share")
        val relativePath = uri.path.split("/").filter { it.isNotEmpty() }.drop(1).joinToString("/")

        if (diskShare == null || currentHost != host || currentShare != shareName) {
            diskShare?.close()
            val connection = client.connect(host)
            val auth = AuthenticationContext.guest()
            val session = connection.authenticate(auth)
            diskShare = session.connectShare(shareName) as DiskShare
            currentHost = host
            currentShare = shareName
        }
        return relativePath
    }

    override fun getFiles(path: String, sortOrder: SortOrder, showHidden: Boolean): List<FileItem> {
        val relPath = ensureConnected(path)
        val share = diskShare ?: return emptyList()

        val files = share.list(relPath)
        val fileItems = files.filter {
            showHidden || !it.fileName.startsWith(".")
        }.map {
            val fullPath = if (relPath.isEmpty()) it.fileName else "$relPath/${it.fileName}"
            val isDir = (it.fileAttributes and 0x00000010L) != 0L

            FileItem(
                name = it.fileName,
                path = "smb://$currentHost/$currentShare/$fullPath",
                isDirectory = isDir,
                size = if (isDir) 0L else it.endOfFile,
                lastModified = it.changeTime.toEpochMillis(),
                mimeType = if (isDir) null else FileUtils.getMimeTypeFromName(it.fileName)
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
        val relPath = ensureConnected(parentPath)
        val fullPath = if (relPath.isEmpty()) name else "$relPath/$name"
        diskShare?.mkdir(fullPath)
        return true
    }

    override fun delete(path: String, secure: Boolean): Boolean {
        val relPath = ensureConnected(path)
        val share = diskShare ?: return false
        if (share.folderExists(relPath)) {
            deleteRecursive(relPath)
        } else {
            share.rm(relPath)
        }
        return true
    }

    private fun deleteRecursive(relPath: String) {
        val share = diskShare ?: return
        share.list(relPath).forEach {
            val childPath = "$relPath/${it.fileName}"
            if ((it.fileAttributes and 0x00000010L) != 0L) {
                deleteRecursive(childPath)
            } else {
                share.rm(childPath)
            }
        }
        share.rm(relPath)
    }

    override fun rename(path: String, newName: String): Boolean {
        val relPath = ensureConnected(path)
        val share = diskShare ?: return false
        val parentPath = if (relPath.contains("/")) relPath.substringBeforeLast("/") else ""
        val newRelPath = if (newName.contains("/")) newName else if (parentPath.isEmpty()) newName else "$parentPath/$newName"

        return try {
            if (share.folderExists(relPath)) {
                share.openDirectory(
                    relPath,
                    EnumSet.of(AccessMask.DELETE, AccessMask.GENERIC_READ),
                    null,
                    EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ, SMB2ShareAccess.FILE_SHARE_DELETE, SMB2ShareAccess.FILE_SHARE_WRITE),
                    SMB2CreateDisposition.FILE_OPEN,
                    null
                ).use { it.rename(newRelPath) }
            } else {
                share.openFile(
                    relPath,
                    EnumSet.of(AccessMask.DELETE, AccessMask.GENERIC_READ),
                    null,
                    EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ, SMB2ShareAccess.FILE_SHARE_DELETE, SMB2ShareAccess.FILE_SHARE_WRITE),
                    SMB2CreateDisposition.FILE_OPEN,
                    null
                ).use { it.rename(newRelPath) }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun exists(path: String): Boolean {
        val relPath = ensureConnected(path)
        return diskShare?.fileExists(relPath) == true || diskShare?.folderExists(relPath) == true
    }

    override fun isDirectory(path: String): Boolean {
        val relPath = ensureConnected(path)
        return diskShare?.folderExists(relPath) == true
    }

    override fun getInputStream(path: String): InputStream {
        val relPath = ensureConnected(path)
        val share = diskShare ?: throw IllegalStateException("Not connected")
        val file = share.openFile(
            relPath,
            EnumSet.of(AccessMask.GENERIC_READ),
            null,
            EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ),
            SMB2CreateDisposition.FILE_OPEN,
            null
        )
        return file.inputStream
    }

    override fun getOutputStream(path: String): OutputStream {
        val relPath = ensureConnected(path)
        val share = diskShare ?: throw IllegalStateException("Not connected")
        val file = share.openFile(
            relPath,
            EnumSet.of(AccessMask.GENERIC_WRITE, AccessMask.GENERIC_READ),
            null,
            EnumSet.of(SMB2ShareAccess.FILE_SHARE_WRITE),
            SMB2CreateDisposition.FILE_OVERWRITE_IF,
            null
        )
        return file.outputStream
    }

    override fun copy(sourcePath: String, destinationPath: String): Boolean {
        val srcRel = ensureConnected(sourcePath)
        val destRelDir = ensureConnected(destinationPath)
        val share = diskShare ?: return false
        val name = srcRel.substringAfterLast("/")
        val destRel = if (destRelDir.isEmpty()) name else "$destRelDir/$name"
        val tempDestRel = if (destRelDir.isEmpty()) ".tmp_${java.util.UUID.randomUUID()}_$name" else "$destRelDir/.tmp_${java.util.UUID.randomUUID()}_$name"

        return try {
            if (share.folderExists(srcRel)) {
                share.mkdir(destRel)
                share.list(srcRel).forEach {
                    if (it.fileName != "." && it.fileName != "..") {
                        val childSrc = if (sourcePath.endsWith("/")) "$sourcePath${it.fileName}" else "$sourcePath/${it.fileName}"
                        val destBase = "smb://$currentHost/$currentShare/$destRel"
                        copy(childSrc, destBase)
                    }
                }
            } else {
                // Quality Gate: Atomic copy using temp then rename
                getInputStream(sourcePath).use { input ->
                    getOutputStream("smb://$currentHost/$currentShare/$tempDestRel").use { output ->
                        input.copyTo(output)
                    }
                }
                rename("smb://$currentHost/$currentShare/$tempDestRel", name)
            }
            true
        } catch (e: Exception) {
            // Clean up temp if it exists
            try { share.rm(tempDestRel) } catch (ignored: Exception) {}
            false
        }
    }

    override fun move(sourcePath: String, destinationPath: String): Boolean {
        val srcRel = ensureConnected(sourcePath)
        val destRelDir = ensureConnected(destinationPath)
        val name = srcRel.substringAfterLast("/")
        val destRel = if (destRelDir.isEmpty()) name else "$destRelDir/$name"

        return try {
            if (rename(sourcePath, destRel)) {
                true
            } else {
                throw Exception("Rename failed")
            }
        } catch (e: Exception) {
            // Fallback to copy and delete if rename fails (e.g., across shares)
            if (copy(sourcePath, destinationPath)) {
                delete(sourcePath)
            } else {
                false
            }
        }
    }
}
