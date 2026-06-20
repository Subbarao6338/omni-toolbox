package com.nature.files.analyzer

import java.io.File

class StorageAnalyzer {
    fun analyze(root: File): List<FolderSize> {
        val result = mutableListOf<FolderSize>()
        root.listFiles()?.filter { it.isDirectory }?.forEach { folder ->
            result.add(FolderSize(folder.name, calculateSize(folder)))
        }
        return result.sortedByDescending { it.size }
    }

    private fun calculateSize(file: File): Long {
        if (file.isFile) return file.length()
        return file.listFiles()?.sumOf { calculateSize(it) } ?: 0L
    }

    data class FolderSize(val name: String, val size: Long)
}
