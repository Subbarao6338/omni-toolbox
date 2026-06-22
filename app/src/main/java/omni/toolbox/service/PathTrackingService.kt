package omni.toolbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import omni.toolbox.data.local.AppDatabase
import omni.toolbox.data.local.entity.WaypointEntity

class PathTrackingService : Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private var currentPathId: Long = -1
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentPathId = intent?.getLongExtra("pathId", -1) ?: -1
        if (currentPathId != -1L) {
            startForegroundService()
            requestLocationUpdates()
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "path_tracking_channel"
        val channelName = "Path Tracking"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking Path")
            .setContentText("Omni Toolbox is recording your journey.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
        startForeground(101, notification)
    }

    private fun requestLocationUpdates() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, this)
        } catch (e: SecurityException) {
            stopSelf()
        }
    }

    override fun onLocationChanged(location: Location) {
        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.navigationDao().insertWaypoint(WaypointEntity(
                pathId = currentPathId,
                latitude = location.latitude,
                longitude = location.longitude,
                elevation = location.altitude.toFloat()
            ))
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
        serviceScope.cancel()
    }
}
