package omni.toolbox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beacons")
data class BeaconEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Float? = null,
    val color: Int? = null,
    val icon: String? = null
)

@Entity(tableName = "paths")
data class PathEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Int? = null,
    val isVisible: Boolean = true
)

@Entity(tableName = "waypoints")
data class WaypointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pathId: Long,
    val latitude: Double,
    val longitude: Double,
    val elevation: Float? = null,
    val time: Long = System.currentTimeMillis()
)
