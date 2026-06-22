package omni.toolbox.data.remote

import android.content.Context
import omni.toolbox.data.remote.SmbClient
import java.io.File

object NASManager {
    fun connectSMB(
        context: Context,
        server: String,
        share: String,
        user: String,
        pass: String,
        path: String,
        dest: File
    ): Boolean {
        return SmbClient.downloadFile(server, share, user, pass, path, dest)
    }
}
