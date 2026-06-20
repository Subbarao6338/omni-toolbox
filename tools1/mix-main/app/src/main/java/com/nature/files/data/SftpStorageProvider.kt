package com.nature.files.data

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.nature.files.data.db.AppDatabase
import com.nature.files.utils.FileUtils
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class SftpStorageProvider(private val context: android.content.Context) : StorageProvider {
    private val jsch = JSch()
    private var session: Session? = null
    private var channel: ChannelSftp? = null
    private var currentConnectionId: String? = null

    private suspend fun ensureConnected(path: String): String {
        val uri = java.net.URI(path)
        val connectionId = uri.host ?: throw IllegalArgumentException("Invalid SFTP host (connection ID)")
        val relativePath = uri.path.ifEmpty { "/" }

        if (channel == null || !channel!!.isConnected || currentConnectionId != connectionId) {
            disconnect()
            val db = AppDatabase.getDatabase(context)
            val connection = db.cloudConnectionDao().getConnection(connectionId)
                ?: throw IllegalStateException("Cloud connection '$connectionId' not found in the clearing.")

            session = jsch.getSession(connection.username, connection.host, connection.port)
            session!!.setPassword(connection.password)
            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            session!!.setConfig(config)
            session!!.connect()

            channel = session!!.openChannel("sftp") as ChannelSftp
            channel!!.connect()
            currentConnectionId = connectionId
        }
        return relativePath
    }

    private fun disconnect() {
        channel?.disconnect()
        session?.disconnect()
        channel = null
        session = null
        currentConnectionId = null
    }

    override fun getFiles(path: String, sortOrder: SortOrder, showHidden: Boolean): List<FileItem> {
        return try {
            kotlinx.coroutines.runBlocking {
                val relPath = ensureConnected(path)
                val vector = channel!!.ls(relPath)
                val fileItems = mutableListOf<FileItem>()

                for (obj in vector) {
                    val entry = obj as ChannelSftp.LsEntry
                    if (entry.filename == "." || entry.filename == "..") continue
                    if (!showHidden && entry.filename.startsWith(".")) continue

                    val isDir = entry.attrs.isDir
                    // Fix URI construction:
                    val normalizedRelPath = if (relPath.endsWith("/")) relPath else "$relPath/"
                    val itemPath = "sftp://$currentConnectionId$normalizedRelPath${entry.filename}"

                    fileItems.add(
                        FileItem(
                            name = entry.filename,
                            path = itemPath,
                            isDirectory = isDir,
                            size = if (isDir) 0L else entry.attrs.size,
                            lastModified = entry.attrs.mTime.toLong() * 1000,
                            mimeType = if (isDir) null else FileUtils.getMimeTypeFromName(entry.filename)
                        )
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
                fileItems.sortedWith(compareBy<FileItem>({ !it.isDirectory }).thenComparing(comparator))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun createDirectory(parentPath: String, name: String): Boolean {
        return try {
            kotlinx.coroutines.runBlocking {
                val relPath = ensureConnected(parentPath)
                val normalizedRelPath = if (relPath.endsWith("/")) relPath else "$relPath/"
                channel!!.mkdir("$normalizedRelPath$name")
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun delete(path: String, secure: Boolean): Boolean {
        return try {
            kotlinx.coroutines.runBlocking {
                val relPath = ensureConnected(path)
                if (isDirectory(path)) {
                    deleteRecursive(relPath)
                } else {
                    channel!!.rm(relPath)
                }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun deleteRecursive(relPath: String) {
        val vector = channel!!.ls(relPath)
        for (obj in vector) {
            val entry = obj as ChannelSftp.LsEntry
            if (entry.filename == "." || entry.filename == "..") continue
            val childPath = if (relPath.endsWith("/")) "$relPath${entry.filename}" else "$relPath/${entry.filename}"
            if (entry.attrs.isDir) {
                deleteRecursive(childPath)
            } else {
                channel!!.rm(childPath)
            }
        }
        channel!!.rmdir(relPath)
    }

    override fun rename(path: String, newName: String): Boolean {
        return try {
            kotlinx.coroutines.runBlocking {
                val relPath = ensureConnected(path)
                val parentPath = relPath.substringBeforeLast("/", "/")
                val normalizedParentPath = if (parentPath.endsWith("/")) parentPath else "$parentPath/"
                channel!!.rename(relPath, "$normalizedParentPath$newName")
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun exists(path: String): Boolean {
        return try {
            kotlinx.coroutines.runBlocking {
                val relPath = ensureConnected(path)
                try {
                    channel!!.stat(relPath)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun isDirectory(path: String): Boolean {
        return try {
            kotlinx.coroutines.runBlocking {
                val relPath = ensureConnected(path)
                channel!!.stat(relPath).isDir
            }
        } catch (e: Exception) {
            false
        }
    }

    override fun getInputStream(path: String): InputStream {
        return kotlinx.coroutines.runBlocking {
            val relPath = ensureConnected(path)
            channel!!.get(relPath)
        }
    }

    override fun getOutputStream(path: String): OutputStream {
        return kotlinx.coroutines.runBlocking {
            val relPath = ensureConnected(path)
            channel!!.put(relPath)
        }
    }

    override fun copy(sourcePath: String, destinationPath: String): Boolean {
        return try {
            val input = getInputStream(sourcePath)
            val output = getOutputStream(destinationPath)
            input.use { i -> output.use { o -> i.copyTo(o) } }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun move(sourcePath: String, destinationPath: String): Boolean {
        return if (copy(sourcePath, destinationPath)) {
            delete(sourcePath)
        } else {
            false
        }
    }
}
