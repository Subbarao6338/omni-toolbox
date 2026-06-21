package omni.toolbox.utils

import java.io.File

object NotionChunker {
    /**
     * Splits a list of items into chunks of 10 for Notion API compliance.
     */
    fun <T> chunk(items: List<T>, chunkSize: Int = 10): List<List<T>> {
        return items.chunked(chunkSize)
    }

    /**
     * Recursively scans a directory and returns all files.
     */
    fun scanDirectory(directory: File): List<File> {
        val files = mutableListOf<File>()
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                files.addAll(scanDirectory(file))
            } else {
                files.add(file)
            }
        }
        return files
    }
}
