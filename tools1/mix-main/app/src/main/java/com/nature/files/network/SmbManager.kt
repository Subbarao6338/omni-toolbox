package com.nature.files.network

import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmbManager {
    suspend fun listFiles(host: String, shareName: String): List<String> = withContext(Dispatchers.IO) {
        val client = SMBClient()
        client.connect(host).use { connection ->
            val auth = AuthenticationContext.guest()
            val session = connection.authenticate(auth)
            (session.connectShare(shareName) as DiskShare).use { share ->
                share.list("").map { it.fileName }
            }
        }
    }
}
