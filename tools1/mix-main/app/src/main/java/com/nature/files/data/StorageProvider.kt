package com.nature.files.data

import java.io.InputStream
import java.io.OutputStream

interface StorageProvider {
    fun getFiles(path: String, sortOrder: SortOrder, showHidden: Boolean): List<FileItem>
    fun createDirectory(parentPath: String, name: String): Boolean
    fun delete(path: String, secure: Boolean = false): Boolean
    fun rename(path: String, newName: String): Boolean
    fun exists(path: String): Boolean
    fun isDirectory(path: String): Boolean

    fun getInputStream(path: String): InputStream
    fun getOutputStream(path: String): OutputStream

    fun copy(sourcePath: String, destinationPath: String): Boolean
    fun move(sourcePath: String, destinationPath: String): Boolean
}
