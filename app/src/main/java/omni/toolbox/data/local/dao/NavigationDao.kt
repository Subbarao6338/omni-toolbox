package omni.toolbox.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import omni.toolbox.data.local.entity.*

@Dao
interface NavigationDao {
    @Query("SELECT * FROM beacons")
    fun getAllBeacons(): Flow<List<BeaconEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeacon(beacon: BeaconEntity)

    @Delete
    suspend fun deleteBeacon(beacon: BeaconEntity)

    @Query("SELECT * FROM paths")
    fun getAllPaths(): Flow<List<PathEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPath(path: PathEntity): Long

    @Query("SELECT * FROM waypoints WHERE pathId = :pathId ORDER BY time ASC")
    fun getWaypointsForPath(pathId: Long): Flow<List<WaypointEntity>>

    @Insert
    suspend fun insertWaypoint(waypoint: WaypointEntity)

    @Query("DELETE FROM waypoints WHERE pathId = :pathId")
    suspend fun deleteWaypointsForPath(pathId: Long)

    @Delete
    suspend fun deletePath(path: PathEntity)
}
