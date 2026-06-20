package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider
import com.nature.files.utils.FileOperationLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CopyFileUseCase(storageProvider: StorageProvider) : FileOperationUseCase<CopyFileUseCase.Params, Boolean>(storageProvider) {

    data class Params(val sourcePath: String, val destinationDirectory: String)

    override suspend fun execute(param: Params): Boolean = withContext(Dispatchers.IO) {
        FileOperationLock.withLocks(listOf(param.sourcePath, param.destinationDirectory)) {
            val sourceName = param.sourcePath.substringAfterLast("/")
            val destinationPath = if (param.destinationDirectory.endsWith("/")) {
                "${param.destinationDirectory}$sourceName"
            } else {
                "${param.destinationDirectory}/$sourceName"
            }
            val tempPath = if (param.destinationDirectory.endsWith("/")) {
                "${param.destinationDirectory}.tmp_${UUID.randomUUID()}_$sourceName"
            } else {
                "${param.destinationDirectory}/.tmp_${UUID.randomUUID()}_$sourceName"
            }

            try {
                // Quality Gate: Atomic copy using temp then rename
                val success = if (storageProvider.isDirectory(param.sourcePath)) {
                    copyDirectoryAtomic(param.sourcePath, tempPath)
                } else {
                    storageProvider.getInputStream(param.sourcePath).use { input ->
                        storageProvider.getOutputStream(tempPath).use { output ->
                            input.copyTo(output)
                        }
                    }
                    true
                }

                if (success) {
                    if (storageProvider.exists(destinationPath)) {
                        storageProvider.delete(destinationPath)
                    }
                    return@withLocks storageProvider.rename(tempPath, sourceName)
                } else {
                    storageProvider.delete(tempPath)
                    false
                }
            } catch (e: Exception) {
                try {
                    storageProvider.delete(tempPath)
                } catch (ignored: Exception) {
                }
                false
            }
        }
    }

    private fun copyDirectoryAtomic(sourcePath: String, tempDestPath: String): Boolean {
        val parentPath = tempDestPath.substringBeforeLast("/", "")
        val dirName = tempDestPath.substringAfterLast("/", tempDestPath)

        if (!storageProvider.createDirectory(parentPath, dirName)) return false

        val files = storageProvider.getFiles(sourcePath, com.nature.files.data.SortOrder.NAME_ASC, true)
        for (file in files) {
            val childTempPath = if (tempDestPath.endsWith("/")) "$tempDestPath${file.name}" else "$tempDestPath/${file.name}"
            val success = if (file.isDirectory) {
                copyDirectoryAtomic(file.path, childTempPath)
            } else {
                try {
                    storageProvider.getInputStream(file.path).use { input ->
                        storageProvider.getOutputStream(childTempPath).use { output ->
                            input.copyTo(output)
                        }
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }
            if (!success) return false
        }
        return true
    }
}
