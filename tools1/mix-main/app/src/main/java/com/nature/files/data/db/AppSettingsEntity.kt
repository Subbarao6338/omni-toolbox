package com.nature.files.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: String = "default",
    val natureTheme: String = "FOREST_FLOOR",
    val showHiddenFiles: Boolean = false,
    val animationsEnabled: Boolean = true,
    val metaphorsEnabled: Boolean = true,
    val leftSwipeAction: String = "DELETE",
    val rightSwipeAction: String = "TAG"
)
