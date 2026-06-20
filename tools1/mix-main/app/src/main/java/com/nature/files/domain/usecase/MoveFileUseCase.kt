package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider
import com.nature.files.utils.FileOperationLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoveFileUseCase(
    storageProvider: StorageProvider,
    private val copyFileUseCase: CopyFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase
) : FileOperationUseCase<MoveFileUseCase.Params, Boolean>(storageProvider) {

    data class Params(val sourcePath: String, val destinationDirectory: String)

    override suspend fun execute(param: Params): Boolean = withContext(Dispatchers.IO) {
        FileOperationLock.withLocks(listOf(param.sourcePath, param.destinationDirectory)) {
            // Try atomic rename first (most efficient and atomic if on same partition)
            if (storageProvider.move(param.sourcePath, param.destinationDirectory)) {
                return@withLocks true
            }

            // Fallback to copy and delete if rename fails (e.g., across partitions)
            val copySuccess = copyFileUseCase.execute(CopyFileUseCase.Params(param.sourcePath, param.destinationDirectory))
            if (copySuccess) {
                return@withLocks deleteFileUseCase.execute(DeleteFileUseCase.Params(param.sourcePath))
            }
            false
        }
    }
}
