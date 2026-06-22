package omni.toolbox.data.remote

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.File
import java.io.FileOutputStream

object FtpClient {
    fun downloadFile(
        host: String,
        port: Int,
        user: String,
        pass: String,
        remotePath: String,
        destination: File
    ): Boolean {
        val ftp = FTPClient()
        return try {
            ftp.connect(host, port)
            ftp.login(user, pass)
            ftp.enterLocalPassiveMode()
            ftp.setFileType(FTP.BINARY_FILE_TYPE)

            FileOutputStream(destination).use { output ->
                ftp.retrieveFile(remotePath, output)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (ftp.isConnected) {
                ftp.logout()
                ftp.disconnect()
            }
        }
    }
}
