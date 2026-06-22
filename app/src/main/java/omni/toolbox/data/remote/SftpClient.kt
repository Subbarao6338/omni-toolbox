package omni.toolbox.data.remote

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.sftp.SFTPClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.xfer.InMemoryDestFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object SftpClient {
    fun downloadFile(
        host: String,
        port: Int,
        user: String,
        pass: String,
        remotePath: String,
        destination: File
    ): Boolean {
        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        return try {
            ssh.connect(host, port)
            ssh.authPassword(user, pass)
            val sftp = ssh.newSFTPClient()
            sftp.use { client ->
                client.get(remotePath, object : InMemoryDestFile() {
                    override fun getOutputStream(): OutputStream = FileOutputStream(destination)
                    override fun getOutputStream(append: Boolean): OutputStream = FileOutputStream(destination, append)
                    override fun getLength(): Long = destination.length()
                })
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            ssh.disconnect()
        }
    }
}
