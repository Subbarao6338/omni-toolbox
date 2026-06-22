package omni.toolbox.utils

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object ExifUtils {
    fun getExifTags(context: Context, uri: Uri): Map<String, String> {
        val tags = mutableMapOf<String, String>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val exif = ExifInterface(inputStream)
            val attributes = listOf(
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.TAG_WHITE_BALANCE,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_IMAGE_WIDTH,
                ExifInterface.TAG_IMAGE_LENGTH
            )
            attributes.forEach { attr ->
                exif.getAttribute(attr)?.let { tags[attr] = it }
            }
        }
        return tags
    }

    fun removeExif(context: Context, uri: Uri, outputFile: File) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            outputFile.writeBytes(bytes)
            val exif = ExifInterface(outputFile.absolutePath)

            // List of tags to clear
            val tagsToClear = listOf(
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_SOFTWARE,
                ExifInterface.TAG_USER_COMMENT
            )

            tagsToClear.forEach { exif.setAttribute(it, null) }
            exif.saveAttributes()
        }
    }

    fun updateExif(context: Context, uri: Uri, outputFile: File, updates: Map<String, String>) {
         context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            outputFile.writeBytes(bytes)
            val exif = ExifInterface(outputFile.absolutePath)
            updates.forEach { (tag, value) ->
                exif.setAttribute(tag, value)
            }
            exif.saveAttributes()
        }
    }
}
