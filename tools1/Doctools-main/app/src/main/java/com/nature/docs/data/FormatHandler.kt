package com.nature.docs.data

import android.content.Context
import android.net.Uri

/**
 * Interface for handling different document and media formats.
 * All format-specific processing logic should implement this interface.
 */
interface FormatHandler {
    /**
     * The file extension this handler supports (e.g., "pdf", "jpg", "mp4").
     */
    val supportedExtension: String

    /**
     * Checks if the given URI is supported by this handler.
     */
    fun canHandle(context: Context, uri: Uri): Boolean
}

/**
 * Base implementation of FormatHandler for PDF documents.
 */
class PdfFormatHandler : FormatHandler {
    override val supportedExtension: String = "pdf"

    override fun canHandle(context: Context, uri: Uri): Boolean {
        val type = context.contentResolver.getType(uri)
        return type == "application/pdf" || uri.toString().lowercase().endsWith(".pdf")
    }
}

/**
 * Base implementation of FormatHandler for Image specimens.
 */
class ImageFormatHandler : FormatHandler {
    override val supportedExtension: String = "image"

    override fun canHandle(context: Context, uri: Uri): Boolean {
        val type = context.contentResolver.getType(uri)
        return type?.startsWith("image/") == true
    }
}
