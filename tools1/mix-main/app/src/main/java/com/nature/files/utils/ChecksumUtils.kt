package com.nature.files.utils

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

import java.io.InputStream

object ChecksumUtils {
    fun calculateSHA256(file: File): String {
        if (file.isDirectory) return ""
        return calculateSHA256(FileInputStream(file))
    }

    /**
     * Calculate SHA-256 from an InputStream.
     * Quality Gate: Universal storage support (local, SMB, Cloud).
     */
    fun calculateSHA256(inputStream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        inputStream.use { fis ->
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
