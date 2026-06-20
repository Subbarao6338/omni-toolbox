/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.sftp.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.nature.files.provider.common.UriAuthority
import com.nature.files.util.takeIfNotEmpty
import net.schmizz.sshj.SSHClient

@Parcelize
data class Authority(
    val host: String,
    val port: Int,
    val username: String
) : Parcelable {
    fun toUriAuthority(): UriAuthority {
        val userInfo = username.takeIfNotEmpty()
        val uriPort = port.takeIf { it != DEFAULT_PORT }
        return UriAuthority(userInfo, host, uriPort)
    }

    override fun toString(): String = toUriAuthority().toString()

    companion object {
        const val DEFAULT_PORT = SSHClient.DEFAULT_PORT
    }
}
