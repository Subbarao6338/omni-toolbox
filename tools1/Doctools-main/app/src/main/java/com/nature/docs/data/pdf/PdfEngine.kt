package com.nature.docs.data.pdf

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.io.MemoryUsageSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

object NativeRendererPool {
    private val mutex = Mutex()
    private val pool = mutableMapOf<Uri, MutableList<Pair<PdfRenderer, ParcelFileDescriptor>>>()
    private val inUse = mutableSetOf<PdfRenderer>()

    suspend fun acquire(context: Context, uri: Uri): PdfRenderer? = mutex.withLock {
        val list = pool.getOrPut(uri) { mutableListOf() }
        val idle = list.find { it.first !in inUse }
        if (idle != null) {
            inUse.add(idle.first)
            return idle.first
        }

        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val renderer = PdfRenderer(pfd)
                list.add(renderer to pfd)
                inUse.add(renderer)
                return renderer
            }
        } catch (e: Exception) {}
        return null
    }

    suspend fun release(renderer: PdfRenderer) = mutex.withLock {
        inUse.remove(renderer)
    }
}

object PdDocumentPool {
    private val mutex = Mutex()
    private val docPool = mutableMapOf<Uri, PDDocument>()

    suspend fun acquire(context: Context, uri: Uri, password: String? = null): PDDocument? = mutex.withLock {
        docPool[uri]?.let { return it }
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val mem = MemoryUsageSetting.setupTempFileOnly()
                val doc = if (password != null) PDDocument.load(inputStream, password, mem) else PDDocument.load(inputStream, mem)
                docPool[uri] = doc
                doc
            }
        } catch (e: Exception) { null }
    }

    suspend fun invalidate(uri: Uri) = mutex.withLock {
        docPool.remove(uri)?.close()
    }
}

object PdfEngine {
    suspend fun loadDocument(context: Context, uri: Uri, password: String? = null): PDDocument? = PdDocumentPool.acquire(context, uri, password)

    suspend fun saveDocument(doc: PDDocument, outputFile: File) = withContext(Dispatchers.IO) {
        doc.save(outputFile)
    }

    suspend fun closeDocument(uri: Uri) = PdDocumentPool.invalidate(uri)

    suspend fun withRenderer(context: Context, uri: Uri, block: suspend (PdfRenderer) -> Unit) = withContext(Dispatchers.IO) {
        val renderer = NativeRendererPool.acquire(context, uri)
        if (renderer != null) {
            try {
                block(renderer)
            } finally {
                NativeRendererPool.release(renderer)
            }
        }
    }
}
