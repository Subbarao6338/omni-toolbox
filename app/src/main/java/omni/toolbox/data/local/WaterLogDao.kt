package omni.toolbox.data.local

import androidx.room.*
import omni.toolbox.model.WaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE timestamp >= :startOfDay")
    fun getTodayTotal(startOfDay: Long): Flow<Int?>

    @Insert
    suspend fun insertLog(log: WaterLog)

    @Delete
    suspend fun deleteLog(log: WaterLog)

    @Query("DELETE FROM water_logs")
    suspend fun deleteAll()
}
