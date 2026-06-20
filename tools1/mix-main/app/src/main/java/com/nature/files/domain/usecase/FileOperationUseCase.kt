package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider

abstract class FileOperationUseCase<in T, out R>(protected val storageProvider: StorageProvider) {
    abstract suspend fun execute(param: T): R
}
