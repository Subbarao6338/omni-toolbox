package omni.toolbox.data.local

import androidx.room.*
import omni.toolbox.model.TaskEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntry)

    @Update
    suspend fun updateTask(task: TaskEntry)

    @Delete
    suspend fun deleteTask(task: TaskEntry)
}
