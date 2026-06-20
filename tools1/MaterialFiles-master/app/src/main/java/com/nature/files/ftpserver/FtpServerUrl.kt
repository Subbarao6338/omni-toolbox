/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.ftpserver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.nature.files.settings.Settings
import com.nature.files.util.RuntimeBroadcastReceiver
import com.nature.files.util.getLocalAddress
import com.nature.files.util.valueCompat
import java.net.InetAddress

object FtpServerUrl {
    fun getUrl(): String? {
        val localAddress = InetAddress::class.getLocalAddress() ?: return null
        val username = if (!Settings.FTP_SERVER_ANONYMOUS_LOGIN.valueCompat) {
            Settings.FTP_SERVER_USERNAME.valueCompat
        } else {
            null
        }
        val host = localAddress.hostAddress
        val port = Settings.FTP_SERVER_PORT.valueCompat
        return "ftp://${if (username != null) "$username@" else ""}$host:$port/"
    }

    fun createChangeReceiver(context: Context, onChange: () -> Unit): RuntimeBroadcastReceiver =
        RuntimeBroadcastReceiver(
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    onChange()
                }
            }, context
        )
}
