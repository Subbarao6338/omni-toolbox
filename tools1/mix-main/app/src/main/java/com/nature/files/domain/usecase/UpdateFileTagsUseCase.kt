package com.nature.files.domain.usecase

import com.nature.files.data.StorageProvider
import com.nature.files.data.db.FileDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateFileTagsUseCase(
    storageProvider: StorageProvider,
    private val fileDao: FileDao
) : FileOperationUseCase<UpdateFileTagsUseCase.Params, Unit>(storageProvider) {

    data class Params(val path: String, val tags: List<String>)

    override suspend fun execute(param: Params) = withContext(Dispatchers.IO) {
        val tagsString = if (param.tags.isEmpty()) null else param.tags.joinToString(",")
        fileDao.updateTags(param.path, tagsString)
    }
}
