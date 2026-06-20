package com.nature.files.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_index")
data class FileEntity(
    @PrimaryKey val path: String,
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String?,
    val isBookmarked: Boolean = false,
    val isInRecycleBin: Boolean = false,
    val originalPath: String? = null,
    val tags: String? = null // Comma-separated tags: Moss, Amber, Sky, Clay
)
