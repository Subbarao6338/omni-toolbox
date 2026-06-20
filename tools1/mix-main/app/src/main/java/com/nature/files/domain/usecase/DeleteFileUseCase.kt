package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider
import com.nature.files.utils.FileOperationLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteFileUseCase(storageProvider: StorageProvider) : FileOperationUseCase<DeleteFileUseCase.Params, Boolean>(storageProvider) {

    data class Params(val path: String, val secure: Boolean = false)

    override suspend fun execute(param: Params): Boolean = withContext(Dispatchers.IO) {
        FileOperationLock.withLock(param.path) {
            try {
                storageProvider.delete(param.path, param.secure)
            } catch (e: Exception) {
                // Fallback to normal delete if secure delete fails or is not supported
                if (param.secure) {
                    storageProvider.delete(param.path, false)
                } else {
                    false
                }
            }
        }
    }
}
