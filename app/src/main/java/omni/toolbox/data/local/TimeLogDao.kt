package omni.toolbox.data.local

import androidx.room.*
import omni.toolbox.model.TimeLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeLogDao {
    @Query("SELECT * FROM time_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<TimeLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TimeLogEntry)

    @Delete
    suspend fun deleteLog(log: TimeLogEntry)
}
