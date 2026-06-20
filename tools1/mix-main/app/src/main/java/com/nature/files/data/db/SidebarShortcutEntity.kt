package com.nature.files.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sidebar_shortcuts")
data class SidebarShortcutEntity(
    @PrimaryKey val id: String,
    val name: String,
    val path: String,
    val order: Int,
    val iconName: String? = null
)
