package omni.toolbox.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val status: String, // "Todo", "In Progress", "Done"
    val timestamp: Long = System.currentTimeMillis()
)
