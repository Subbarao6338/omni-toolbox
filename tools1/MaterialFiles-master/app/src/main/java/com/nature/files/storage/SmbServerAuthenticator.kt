/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.storage

import com.nature.files.provider.smb.client.Authenticator
import com.nature.files.provider.smb.client.Authority
import com.nature.files.settings.Settings
import com.nature.files.util.valueCompat

object SmbServerAuthenticator : Authenticator {
    private val transientServers = mutableSetOf<SmbServer>()

    override fun getPassword(authority: Authority): String? {
        val server = synchronized(transientServers) {
            transientServers.find { it.authority == authority }
        } ?: Settings.STORAGES.valueCompat.find {
            it is SmbServer && it.authority == authority
        } as SmbServer?
        return server?.password
    }

    fun addTransientServer(server: SmbServer) {
        synchronized(transientServers) { transientServers += server }
    }

    fun removeTransientServer(server: SmbServer) {
        synchronized(transientServers) { transientServers -= server }
    }
}
