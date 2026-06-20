package com.nature.files.analyzer

import java.io.File

object FileTools {
    /**
     * Finds duplicate files using SHA-256 content hashing.
     * Quality Gate: Data integrity and efficient detection.
     */
    fun findDuplicates(directory: File): List<DuplicateGroup> {
        val hashToFile = mutableMapOf<String, MutableList<File>>()

        fun walk(file: File) {
            if (file.isDirectory) {
                file.listFiles()?.forEach { walk(it) }
            } else {
                val hash = com.nature.files.utils.FileUtils.calculateChecksum(file, "SHA-256")
                if (hash != null) {
                    hashToFile.getOrPut(hash) { mutableListOf() }.add(file)
                }
            }
        }

        walk(directory)
        return hashToFile.filter { it.value.size > 1 }.map { (hash, files) ->
            DuplicateGroup(hash, files)
        }
    }

    data class DuplicateGroup(val hash: String, val files: List<File>)

    fun findLargeFiles(directory: File, minSize: Long = 100 * 1024 * 1024): List<File> {
        val largeFiles = mutableListOf<File>()

        fun walk(file: File) {
            if (file.isDirectory) {
                file.listFiles()?.forEach { walk(it) }
            } else {
                if (file.length() >= minSize) {
                    largeFiles.add(file)
                }
            }
        }

        walk(directory)
        return largeFiles.sortedByDescending { it.length() }
    }
}
