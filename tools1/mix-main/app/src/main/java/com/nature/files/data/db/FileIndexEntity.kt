package com.nature.files.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_index")
data class FileIndexEntity(
    @PrimaryKey val path: String,
    val name: String,
    val isDirectory: Boolean,
    val lastModified: Long,
    val size: Long
)
