package com.naturetools.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_logs")
data class TimeLogEntry(
    @PrimaryKey val id: String,
    val category: String,
    val startTime: Long,
    val endTime: Long?
)
