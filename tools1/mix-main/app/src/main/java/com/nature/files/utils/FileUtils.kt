package com.nature.files.utils

import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object FileUtils {
    fun formatSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format(java.util.Locale.US, "%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    fun getMimeType(file: File): String? {
        if (file.isDirectory) return null
        return getMimeTypeFromName(file.name)
    }

    fun getMimeTypeFromName(name: String): String? {
        val extension = name.substringAfterLast('.', "").lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    fun calculateChecksum(file: File, algorithm: String = "SHA-256"): String? {
        if (file.isDirectory) return null
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            val buffer = ByteArray(8192)
            FileInputStream(file).use { fis ->
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            null
        }
    }

    fun getExifData(file: File): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        try {
            val exif = ExifInterface(file.absolutePath)
            val tags = arrayOf(
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_IMAGE_WIDTH,
                ExifInterface.TAG_IMAGE_LENGTH,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL
            )
            tags.forEach { tag ->
                exif.getAttribute(tag)?.let { metadata[tag] = it }
            }
        } catch (e: Exception) {}
        return metadata
    }

    fun getMediaMetadata(file: File): Map<String, String> {
        val metadata = mutableMapOf<String, String>()
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(file.absolutePath)
            metadata["Title"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""
            metadata["Artist"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""
            metadata["Duration"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: ""
            metadata["Bitrate"] = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE) ?: ""
        } catch (e: Exception) {} finally {
            retriever.release()
        }
        return metadata
    }
}
