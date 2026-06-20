package com.nature.files.domain.usecase

import com.nature.files.data.FileItem
import com.nature.files.data.StorageProvider
import com.nature.files.utils.ChecksumUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Finds duplicate files based on size and SHA-256 checksum.
 */
class FindDuplicatesUseCase(storageProvider: StorageProvider) : FileOperationUseCase<FindDuplicatesUseCase.Params, List<List<FileItem>>>(storageProvider) {

    data class Params(val rootPath: String, val recursive: Boolean = true)

    override suspend fun execute(param: Params): List<List<FileItem>> = withContext(Dispatchers.IO) {
        val allFiles = mutableListOf<File>()
        scanDirectory(File(param.rootPath), allFiles, param.recursive)

        // Group by size first (fastest)
        val sizeGroups = allFiles.filter { it.isFile }
            .groupBy { it.length() }
            .filter { it.value.size > 1 }

        val duplicateGroups = mutableListOf<List<FileItem>>()

        sizeGroups.values.forEach { group ->
            // For each size group, group by SHA-256
            val hashGroups = group.groupBy { ChecksumUtils.calculateSHA256(it) }
                .filter { it.value.size > 1 }

            hashGroups.values.forEach { files ->
                duplicateGroups.add(files.map {
                    FileItem(
                        name = it.name,
                        path = it.absolutePath,
                        isDirectory = false,
                        size = it.length(),
                        lastModified = it.lastModified(),
                        mimeType = com.nature.files.utils.FileUtils.getMimeType(it)
                    )
                })
            }
        }

        duplicateGroups
    }

    private fun scanDirectory(directory: File, fileList: MutableList<File>, recursive: Boolean) {
        directory.listFiles()?.forEach { file ->
            if (file.isFile) {
                fileList.add(file)
            } else if (file.isDirectory && recursive) {
                scanDirectory(file, fileList, recursive)
            }
        }
    }
}
