package omni.toolbox.data.remote

import android.content.Context
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import java.io.File
import java.io.FileOutputStream

object SmbClient {
    fun downloadFile(
        server: String,
        shareName: String,
        user: String,
        pass: String,
        remotePath: String,
        destination: File
    ): Boolean {
        val client = SMBClient()
        return try {
            client.connect(server).use { connection ->
                val authContext = AuthenticationContext(user, pass.toCharArray(), "")
                val session = connection.authenticate(authContext)
                val share = session.connectShare(shareName) as DiskShare

                val remoteFile = share.openFile(
                    remotePath,
                    setOf(AccessMask.GENERIC_READ),
                    null,
                    setOf(SMB2ShareAccess.FILE_SHARE_READ),
                    SMB2CreateDisposition.FILE_OPEN,
                    null
                )

                remoteFile.inputStream.use { input ->
                    FileOutputStream(destination).use { output ->
                        input.copyTo(output)
                    }
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            client.close()
        }
    }
}
