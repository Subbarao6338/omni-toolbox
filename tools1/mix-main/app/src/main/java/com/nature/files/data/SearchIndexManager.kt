package com.nature.files.data

import com.nature.files.data.db.FileDao
import com.nature.files.data.db.FileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SearchIndexManager(private val fileDao: FileDao) {
    suspend fun indexDirectory(directory: File) = withContext(Dispatchers.IO) {
        directory.walkTopDown().forEach { file ->
            val entity = FileEntity(
                path = file.absolutePath,
                name = file.name,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) 0 else file.length(),
                lastModified = file.lastModified(),
                mimeType = ""
            )
            fileDao.insertFiles(listOf(entity))
        }
    }
}
