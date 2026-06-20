/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.app

import android.os.AsyncTask
import android.os.Build
import android.webkit.WebView
import jcifs.context.SingletonContext
import com.nature.files.BuildConfig
import com.nature.files.coil.initializeCoil
import com.nature.files.filejob.fileJobNotificationTemplate
import com.nature.files.ftpserver.ftpServerServiceNotificationTemplate
import com.nature.files.hiddenapi.HiddenApi
import com.nature.files.provider.FileSystemProviders
import com.nature.files.settings.Settings
import com.nature.files.storage.FtpServerAuthenticator
import com.nature.files.storage.SftpServerAuthenticator
import com.nature.files.storage.SmbServerAuthenticator
import com.nature.files.storage.StorageVolumeListLiveData
import com.nature.files.storage.WebDavServerAuthenticator
import com.nature.files.theme.custom.CustomThemeHelper
import com.nature.files.theme.night.NightModeHelper
import java.util.Properties
import com.nature.files.provider.ftp.client.Client as FtpClient
import com.nature.files.provider.sftp.client.Client as SftpClient
import com.nature.files.provider.smb.client.Client as SmbClient
import com.nature.files.provider.webdav.client.Client as WebDavClient

val appInitializers = listOf(
    ::disableHiddenApiChecks,
    ::initializeWebViewDebugging,
    ::initializeCoil,
    ::initializeFileSystemProviders,
    ::upgradeApp,
    ::initializeLiveDataObjects,
    ::initializeCustomTheme,
    ::initializeNightMode,
    ::createNotificationChannels
)

private fun disableHiddenApiChecks() {
    HiddenApi.disableHiddenApiChecks()
}

private fun initializeWebViewDebugging() {
    if (BuildConfig.DEBUG) {
        WebView.setWebContentsDebuggingEnabled(true)
    }
}

private fun initializeFileSystemProviders() {
    FileSystemProviders.install()
    FileSystemProviders.overflowWatchEvents = true
    // SingletonContext.init() calls NameServiceClientImpl.initCache() which connects to network.
    AsyncTask.THREAD_POOL_EXECUTOR.execute {
        SingletonContext.init(
            Properties().apply {
                setProperty("jcifs.netbios.cachePolicy", "0")
                setProperty("jcifs.smb.client.maxVersion", "SMB1")
            }
        )
    }
    FtpClient.authenticator = FtpServerAuthenticator
    SftpClient.authenticator = SftpServerAuthenticator
    SmbClient.authenticator = SmbServerAuthenticator
    WebDavClient.authenticator = WebDavServerAuthenticator
}

private fun initializeLiveDataObjects() {
    // Force initialization of LiveData objects so that it won't happen on a background thread.
    StorageVolumeListLiveData.value
    Settings.FILE_LIST_DEFAULT_DIRECTORY.value
}

private fun initializeCustomTheme() {
    CustomThemeHelper.initialize(application)
}

private fun initializeNightMode() {
    NightModeHelper.initialize(application)
}

private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannels(
            listOf(
                backgroundActivityStartNotificationTemplate.channelTemplate,
                fileJobNotificationTemplate.channelTemplate,
                ftpServerServiceNotificationTemplate.channelTemplate
            ).map { it.create(application) }
        )
    }
}
