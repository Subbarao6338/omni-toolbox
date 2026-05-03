package com.naturetools.app.data.local

import androidx.room.*
import com.naturetools.app.model.TaskEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntry)

    @Update
    suspend fun updateTask(task: TaskEntry)

    @Delete
    suspend fun deleteTask(task: TaskEntry)
}
