package omni.toolbox.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val username: String,
    val password: String,
    val website: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
