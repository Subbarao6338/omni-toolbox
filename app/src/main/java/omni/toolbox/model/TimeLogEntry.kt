package omni.toolbox.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_logs")
data class TimeLogEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activity: String,
    val durationMillis: Long,
    val timestamp: Long = System.currentTimeMillis()
)
