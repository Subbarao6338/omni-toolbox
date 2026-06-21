package omni.toolbox.ui.screens.utility

import android.content.Context
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.EnumSet

object NASManager {
    suspend fun connectSMB(
        context: Context,
        server: String,
        shareName: String,
        user: String,
        pass: String,
        remotePath: String,
        localDest: File
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val client = SMBClient()
            client.connect(server).use { connection ->
                val auth = AuthenticationContext(user, pass.toCharArray(), "")
                val session = connection.authenticate(auth)
                session.connectShare(shareName).use { share ->
                    if (share is DiskShare) {
                        val remoteFile = share.openFile(
                            remotePath,
                            EnumSet.of(AccessMask.GENERIC_READ),
                            EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                            EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ),
                            SMB2CreateDisposition.FILE_OPEN,
                            null
                        )
                        remoteFile.inputStream.use { input ->
                            FileOutputStream(localDest).use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
