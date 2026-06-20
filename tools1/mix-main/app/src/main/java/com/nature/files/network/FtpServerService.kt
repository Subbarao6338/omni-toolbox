package com.nature.files.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.ftplet.UserManager
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.WritePermission
import java.io.File

/**
 * Provides FTP access to the device's storage over LAN.
 */
class FtpServerService : Service() {
    private var server: FtpServer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startServer()
            ACTION_STOP -> stopServer()
        }
        return START_NOT_STICKY
    }

    private fun startServer() {
        if (server != null) return

        val serverFactory = FtpServerFactory()
        val listenerFactory = ListenerFactory()
        listenerFactory.port = 2121
        serverFactory.addListener("default", listenerFactory.createListener())

        val userManagerFactory = PropertiesUserManagerFactory()
        val userManager = userManagerFactory.createUserManager()

        val user = BaseUser()
        user.name = "nature"
        user.password = "files"
        // Quality Gate: Avoid hardcoded /sdcard
        user.homeDirectory = Environment.getExternalStorageDirectory().absolutePath
        val authorities = mutableListOf<Authority>()
        authorities.add(WritePermission())
        user.authorities = authorities
        userManager.save(user)

        serverFactory.userManager = userManager
        server = serverFactory.createServer()
        server?.start()

        startForegroundService()
    }

    private fun stopServer() {
        server?.stop()
        server = null
        @Suppress("DEPRECATION")
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService() {
        val channelId = "ftp_server"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "FTP Server", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Forest LAN Access Active")
            .setContentText("FTP server running on port 2121")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        stopServer()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"
    }
}
