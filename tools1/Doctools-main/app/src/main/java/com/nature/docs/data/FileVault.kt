package com.nature.docs.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

object FileVault {
    private const val TEMP_DIR = "nature_temp"

    fun getTempFile(context: Context, suffix: String): File {
        val dir = File(context.cacheDir, TEMP_DIR)
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "proc_${UUID.randomUUID()}$suffix")
    }

    fun clearTempFiles(context: Context) {
        File(context.cacheDir, TEMP_DIR).deleteRecursively()
    }

    fun copyToTemp(context: Context, uri: Uri): File? {
        return try {
            val extension = getExtension(context, uri)
            val tempFile = getTempFile(context, ".$extension")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    fun getExtension(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "bin"
    }

    fun getFileName(context: Context, uri: Uri): String {
        var name = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) name = cursor.getString(index)
            }
        }
        return name.ifEmpty { uri.lastPathSegment ?: "document" }
    }
}
