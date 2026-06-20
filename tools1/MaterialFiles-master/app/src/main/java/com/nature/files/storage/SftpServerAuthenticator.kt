/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.storage

import com.nature.files.provider.sftp.client.Authentication
import com.nature.files.provider.sftp.client.Authenticator
import com.nature.files.provider.sftp.client.Authority
import com.nature.files.settings.Settings
import com.nature.files.util.valueCompat

object SftpServerAuthenticator : Authenticator {
    private val transientServers = mutableSetOf<SftpServer>()

    override fun getAuthentication(authority: Authority): Authentication? {
        val server = synchronized(transientServers) {
            transientServers.find { it.authority == authority }
        } ?: Settings.STORAGES.valueCompat.find {
            it is SftpServer && it.authority == authority
        } as SftpServer?
        return server?.authentication
    }

    fun addTransientServer(server: SftpServer) {
        synchronized(transientServers) { transientServers += server }
    }

    fun removeTransientServer(server: SftpServer) {
        synchronized(transientServers) { transientServers -= server }
    }
}
