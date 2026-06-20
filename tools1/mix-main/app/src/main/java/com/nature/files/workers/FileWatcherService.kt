package com.nature.files.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.FileObserver
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.File

/**
 * Monitors a specific path and alerts the user on changes.
 */
class FileWatcherService : Service() {
    private var observer: FileObserver? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val path = intent?.getStringExtra("path") ?: return START_NOT_STICKY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            observer = object : FileObserver(File(path)) {
                override fun onEvent(event: Int, path: String?) {
                    if (path != null) {
                        sendNotification("Nature Alert", "Change detected in the grove: $path")
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            observer = object : FileObserver(path, ALL_EVENTS) {
                override fun onEvent(event: Int, path: String?) {
                    if (path != null) {
                        sendNotification("Nature Alert", "Change detected in the grove: $path")
                    }
                }
            }
        }
        observer?.startWatching()

        startForeground(2, createNotification("Monitoring Grove", "Watching $path"))
        return START_NOT_STICKY
    }

    private fun createNotification(title: String, text: String): android.app.Notification {
        val channelId = "file_watcher"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "File Watcher", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    private fun sendNotification(title: String, text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        // Nature Design Mandate: themed leaf emojis in system notifications
        val themedText = "🌿 $text 🍂"
        manager.notify(System.currentTimeMillis().toInt(), createNotification(title, themedText))
    }

    override fun onDestroy() {
        observer?.stopWatching()
        super.onDestroy()
    }
}
