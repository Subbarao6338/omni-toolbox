package com.naturetools.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntry(
    @PrimaryKey val id: String,
    val title: String,
    val status: String // "TODO", "DOING", "DONE"
)
