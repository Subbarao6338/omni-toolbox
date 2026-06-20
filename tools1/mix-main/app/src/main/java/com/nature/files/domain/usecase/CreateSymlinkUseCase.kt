package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider
import com.nature.files.utils.FileOperationLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Implements "Path Grafting" (Symlink creation).
 * Metaphor: grafting a branch from one tree to another.
 */
class CreateSymlinkUseCase(storageProvider: StorageProvider) : FileOperationUseCase<CreateSymlinkUseCase.Params, Boolean>(storageProvider) {

    data class Params(val targetPath: String, val linkDirectory: String, val linkName: String)

    override suspend fun execute(param: Params): Boolean = withContext(Dispatchers.IO) {
        FileOperationLock.withLocks(listOf(param.targetPath, param.linkDirectory)) {
            try {
                val target = Paths.get(param.targetPath)
                val link = Paths.get(param.linkDirectory, param.linkName)

                if (Files.exists(link)) {
                    Files.delete(link)
                }

                Files.createSymbolicLink(link, target)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
