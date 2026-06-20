package com.nature.files.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nature.files.data.SortOrder

@Entity(tableName = "folder_preferences")
data class FolderPreferenceEntity(
    @PrimaryKey val path: String,
    val isGridView: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val customColor: String? = null
)
