package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider
import com.nature.files.utils.FileOperationLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RenameFileUseCase(storageProvider: StorageProvider) : FileOperationUseCase<RenameFileUseCase.Params, Boolean>(storageProvider) {

    data class Params(val path: String, val newName: String)

    override suspend fun execute(param: Params): Boolean = withContext(Dispatchers.IO) {
        // Quality Gate: Ensure atomic rename and lock usage
        FileOperationLock.withLock(param.path) {
            storageProvider.rename(param.path, param.newName)
        }
    }
}
