package com.nature.docs.ui.components

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.LruCache
import androidx.compose.material.icons.outlined.FolderZip
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.pdmodel.interactive.action.PDActionURI
import com.tom_roush.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import com.nature.docs.data.pdf.PdDocumentPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

data class UriDetails(
    val name: String,
    val size: String,
    val sizeBytes: Long = 0
)

data class PdfLink(
    val pageIndex: Int,
    val rect: PDRectangle,
    val url: String
)

object PreferencesManager {
    fun setDefaultAuthor(context: Context, name: String) {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("default_author", name).apply()
    }
    fun getDefaultAuthor(context: Context): String {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        return prefs.getString("default_author", "") ?: ""
    }

    fun setDarkMode(context: Context, mode: Int) {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("dark_mode", mode).apply()
    }
    fun getDarkMode(context: Context): Int {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("dark_mode", 0) // 0: System, 1: Light, 2: Dark
    }

    fun setThemeId(context: Context, id: Int) {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("theme_id", id).apply()
    }
    fun getThemeId(context: Context): Int {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("theme_id", 0)
    }

    fun setHistoryRetention(context: Context, days: Int) {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("history_retention", days).apply()
    }
    fun getHistoryRetention(context: Context): Int {
        val prefs = context.getSharedPreferences("pk_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("history_retention", 0)
    }
}

object LayoutMath {
    fun getFitRect(containerWidth: Float, containerHeight: Float, pageRatio: Float): android.graphics.RectF {
        val containerRatio = containerWidth / containerHeight
        return if (pageRatio > containerRatio) {
            val h = containerWidth / pageRatio
            val top = (containerHeight - h) / 2
            android.graphics.RectF(0f, top, containerWidth, top + h)
        } else {
            val w = containerHeight * pageRatio
            val left = (containerWidth - w) / 2
            android.graphics.RectF(left, 0f, left + w, containerHeight)
        }
    }
}

object BitmapCache {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = if (maxMemory > 512 * 1024) maxMemory / 5 else maxMemory / 8
    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
        override fun entryRemoved(evicted: Boolean, key: String, oldValue: Bitmap, newValue: Bitmap?) {
            if (oldValue.isMutable) BitmapPool.put(oldValue)
        }
    }

    fun getBitmap(key: String): Bitmap? = synchronized(this) { memoryCache.get(key) }
    fun putBitmap(key: String, bitmap: Bitmap) {
        synchronized(this) {
            if (getBitmap(key) == null) memoryCache.put(key, bitmap)
        }
    }
    fun clear() = synchronized(this) { memoryCache.evictAll() }
}

object BitmapPool {
    private val pool = mutableMapOf<Triple<Int, Int, Bitmap.Config>, MutableList<Bitmap>>()
    private val maxPoolCount: Int by lazy {
        val memoryClass = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt()
        when {
            memoryClass > 1024 -> 32
            memoryClass > 512 -> 16
            else -> 6
        }
    }
    private var currentCount = 0

    fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        synchronized(this) {
            val key = Triple(width, height, config)
            val list = pool[key]
            if (list != null && list.isNotEmpty()) {
                val bitmap = list.removeAt(list.size - 1)
                currentCount--
                return bitmap
            }
        }
        return Bitmap.createBitmap(width, height, config)
    }

    fun put(bitmap: Bitmap) {
        if (!bitmap.isMutable) return
        synchronized(this) {
            if (currentCount >= maxPoolCount) {
                val randomKey = pool.keys.firstOrNull() ?: return
                pool[randomKey]?.removeFirstOrNull()?.recycle()
                currentCount--
            }
            val key = Triple(bitmap.width, bitmap.height, bitmap.config)
            pool.getOrPut(key) { mutableListOf() }.add(bitmap)
            currentCount++
        }
    }
}

@SuppressLint("Range")
fun getUriDetails(context: Context, uri: Uri): UriDetails {
    var name = "Document.pdf"
    var size = "Unknown size"
    var bytes: Long = 0
    
    try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                
                if (nameIndex != -1) {
                    val rawName = cursor.getString(nameIndex)
                    if (rawName != null) name = rawName
                }
                if (sizeIndex != -1) {
                    bytes = cursor.getLong(sizeIndex)
                    size = formatSize(bytes)
                }
            }
        }
    } catch (e: Exception) {
        uri.lastPathSegment?.let { name = it }
    }
    
    if (!name.contains(".")) name += ".pdf"
    return UriDetails(name, size, bytes)
}

fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

/**
 * Checks if a PDF is encrypted.
 * @param context Context for content resolution.
 * @param uri URI of the PDF.
 * @return True if encrypted or failed to read.
 */
suspend fun checkIsEncryptedLocal(context: Context, uri: Uri): Boolean = withContext(Dispatchers.IO) {
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val doc = PDDocument.load(inputStream)
            val isEnc = doc.isEncrypted
            doc.close()
            isEnc
        } ?: false
    } catch (e: Exception) { true }
}

/**
 * Verifies if the provided password can unlock the PDF.
 * @param context Context for content resolution.
 * @param uri URI of the PDF.
 * @param password Password to test.
 * @return True if password is correct.
 */
suspend fun verifyPasswordLocal(context: Context, uri: Uri, password: String): Boolean = withContext(Dispatchers.IO) {
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            PDDocument.load(inputStream, password).use { true }
        } ?: false
    } catch (e: Exception) { false }
}

suspend fun loadPreview(context: Context, uri: Uri, password: String?): Bitmap? = withContext(Dispatchers.IO) {
    renderPageToBitmap(context, uri, 0, password, 1.0f)
}

suspend fun getPageCount(context: Context, uri: Uri, password: String?): Int = withContext(Dispatchers.IO) {
    try {
        val document = PdDocumentPool.acquire(context, uri, password)
        document?.numberOfPages ?: 0
    } catch (e: Exception) { 0 }
}

/**
 * Renders a PDF page to a Bitmap using Native Renderer with PDFBox fallback.
 * @param context Context for content resolution.
 * @param uri URI of the PDF.
 * @param pageIndex Index of the page to render.
 * @param password Optional password.
 * @param scale Rendering scale factor.
 * @return Rendered Bitmap or null.
 */
suspend fun renderPageToBitmap(context: Context, uri: Uri, pageIndex: Int, password: String?, scale: Float = 1f): Bitmap? = withContext(Dispatchers.IO) {
    val key = "$uri-$pageIndex-$scale"
    BitmapCache.getBitmap(key)?.let { return@withContext it }

    if (password == null) {
        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val renderer = android.graphics.pdf.PdfRenderer(pfd)
                if (pageIndex < renderer.pageCount) {
                    val page = renderer.openPage(pageIndex)
                    val width = (page.width * scale).toInt().coerceAtLeast(1)
                    val height = (page.height * scale).toInt().coerceAtLeast(1)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    
                    page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                    
                    page.close()
                    renderer.close()
                    BitmapCache.putBitmap(key, bitmap)
                    return@withContext bitmap
                }
                renderer.close()
            }
        } catch (e: Exception) { }
    }

    try {
        val document = PdDocumentPool.acquire(context, uri, password)
        if (document != null) {
            val renderer = PDFRenderer(document)
            val bitmap = renderer.renderImage(pageIndex, scale, ImageType.RGB)
            BitmapCache.putBitmap(key, bitmap)
            return@withContext bitmap
        }
    } catch (e: Exception) { }
    null
}

/**
 * Converts a Bitmap to grayscale.
 * @param src Source Bitmap.
 * @return Grayscale Bitmap.
 */
fun toGrayscaleBitmap(src: Bitmap): Bitmap {
    val bmpGrayscale = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmpGrayscale)
    canvas.drawColor(android.graphics.Color.WHITE)
    val paint = Paint()
    val cm = ColorMatrix().apply { setSaturation(0f) }
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(src, 0f, 0f, paint)
    return bmpGrayscale
}

suspend fun performGrayscaleRewrite(context: Context, inputUri: Uri, outputUri: Uri, password: String?, onProgress: (Int, Int) -> Unit) = withContext(Dispatchers.IO) {
    com.nature.docs.domain.GrayscaleUseCase().execute(context, inputUri, outputUri, password, onProgress)
}

suspend fun compressPdf(context: Context, inputUri: Uri, outputUri: Uri, password: String?, level: String, onProgress: (Int, Int) -> Unit) = withContext(Dispatchers.IO) {
    com.nature.docs.domain.CompressUseCase().execute(context, inputUri, outputUri, password, level, onProgress)
}

/**
 * Converts multiple images into a single PDF.
 * @param context Context for content resolution.
 * @param imageUris List of image URIs.
 * @param outputUri Destination URI for the PDF.
 * @param pageSize Paper size (e.g., "A4").
 * @param onProgress Progress callback.
 */
suspend fun convertImagesToPdf(context: Context, imageUris: List<Uri>, outputUri: Uri, pageSize: String, onProgress: (Int, Int) -> Unit) = withContext(Dispatchers.IO) {
    val document = PDDocument()
    imageUris.forEachIndexed { index, uri ->
        onProgress(index + 1, imageUris.size)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@use
            val pdImage = JPEGFactory.createFromImage(document, bitmap, 0.8f)
            val rect = if (pageSize == "A4") PDRectangle.A4 else PDRectangle(pdImage.width.toFloat(), pdImage.height.toFloat())
            val page = PDPage(rect)
            document.addPage(page)
            PDPageContentStream(document, page).use { cs ->
                if (pageSize == "A4") {
                    val sc = Math.min(rect.width / pdImage.width, rect.height / pdImage.height)
                    val x = (rect.width - pdImage.width * sc) / 2
                    val y = (rect.height - pdImage.height * sc) / 2
                    cs.drawImage(pdImage, x, y, pdImage.width * sc, pdImage.height * sc)
                } else cs.drawImage(pdImage, 0f, 0f)
            }
            bitmap.recycle()
        }
    }
    saveAndFlush(context, document, outputUri)
}

/**
 * Extracts PDF pages as images into a ZIP archive.
 * @param context Context for content resolution.
 * @param pdfUri Source PDF URI.
 * @param outputUri Destination ZIP URI.
 * @param password Optional password.
 * @param selectedPages List of page indices to extract.
 * @param format Image format ("JPG", "PNG", "WebP").
 * @param quality Resolution quality.
 * @param onProgress Progress callback.
 */
suspend fun convertPdfToImages(context: Context, pdfUri: Uri, outputUri: Uri, password: String?, selectedPages: List<Int>, format: String, quality: String, onProgress: (Int, Int) -> Unit) = withContext(Dispatchers.IO) {
    context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
        ZipOutputStream(outputStream).use { zipOut ->
            var workingUri = pdfUri
            if (password != null) {
                decryptToCache(context, pdfUri, password)?.let { workingUri = it }
            }

            context.contentResolver.openFileDescriptor(workingUri, "r")?.use { pfd ->
                val renderer = android.graphics.pdf.PdfRenderer(pfd)
                val scale = when(quality) { "Ultra HD" -> 3.0f; "HD" -> 2.0f; "Standard" -> 1.5f; else -> 1.0f }
                
                val cf = when(format) {
                    "PNG" -> Bitmap.CompressFormat.PNG
                    "WebP" -> if (Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.JPEG
                }
                val ext = format.lowercase()
                
                selectedPages.forEachIndexed { index, pageIdx ->
                    if (pageIdx < renderer.pageCount) {
                        onProgress(index + 1, selectedPages.size)
                        val page = renderer.openPage(pageIdx)
                        val width = (page.width * scale).toInt().coerceAtLeast(1)
                        val height = (page.height * scale).toInt().coerceAtLeast(1)
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)
                        canvas.drawColor(android.graphics.Color.WHITE)
                        page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                        zipOut.putNextEntry(ZipEntry("page_${pageIdx + 1}.$ext"))
                        bitmap.compress(cf, if (format == "PNG") 100 else 90, zipOut)
                        zipOut.closeEntry()
                        bitmap.recycle()
                        page.close()
                    }
                }
                renderer.close()
            }
            if (workingUri != pdfUri) {
                try { File(workingUri.path!!).delete() } catch (e: Exception) {}
            }
            zipOut.finish()
        }
    }
}

/**
 * Saves a PDDocument to a URI atomically using a temporary file.
 * @param context Context for content resolution.
 * @param document PDDocument to save.
 * @param outputUri Destination URI.
 */
fun saveAndFlush(context: Context, document: PDDocument, outputUri: Uri) {
    val info = document.documentInformation
    if (info.creator.isNullOrBlank()) info.creator = "Nature Tools"
    if (info.producer.isNullOrBlank()) info.producer = "Nature Tools Studio Engine"
    val autoAuthor = PreferencesManager.getDefaultAuthor(context)
    if (autoAuthor.isNotEmpty() && info.author.isNullOrBlank()) info.author = autoAuthor
    
    // Atomic Write Implementation
    val tempFile = File(context.cacheDir, "save_temp_${System.currentTimeMillis()}.pdf")
    try {
        FileOutputStream(tempFile).use { os ->
            document.save(os)
            os.flush()
            try { os.fd.sync() } catch (e: Exception) {}
        }
        document.close()

        context.contentResolver.openOutputStream(outputUri, "w")?.use { os ->
            tempFile.inputStream().use { input -> input.copyTo(os) }
            os.flush()
        }
    } finally {
        if (tempFile.exists()) tempFile.delete()
    }
    
    try {
        val values = ContentValues().apply { put(MediaStore.MediaColumns.IS_PENDING, 0) }
        context.contentResolver.update(outputUri, values, null, null)
    } catch (e: Exception) { }
}

fun saveToTemp(context: Context, document: PDDocument): Uri {
    val tempFile = File(context.cacheDir, "preview_${System.currentTimeMillis()}.pdf")
    val info = document.documentInformation
    info.creator = "Nature Tools"
    info.producer = "Nature Tools Preview Engine"
    FileOutputStream(tempFile).use { os ->
        document.save(os)
        os.flush()
    }
    document.close()
    return Uri.fromFile(tempFile)
}

/**
 * Attempts to repair a PDF by re-saving it with PDFBox.
 * @param context Context for content resolution.
 * @param inputUri Source PDF URI.
 * @param outputUri Destination PDF URI.
 * @param password Optional password.
 */
suspend fun repairPdf(context: Context, inputUri: Uri, outputUri: Uri, password: String?) = withContext(Dispatchers.IO) {
    context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
        val document = if (password != null) PDDocument.load(inputStream, password) else PDDocument.load(inputStream)
        document.isAllSecurityToBeRemoved = true 
        saveAndFlush(context, document, outputUri)
    }
}

/**
 * Decrypts a PDF and saves it to a temporary file in the cache directory.
 * @param context Context for content resolution.
 * @param uri Source PDF URI.
 * @param password Password to unlock.
 * @return URI of the decrypted temporary file.
 */
suspend fun decryptToCache(context: Context, uri: Uri, password: String): Uri? = withContext(Dispatchers.IO) {
    try {
        val tempFile = File(context.cacheDir, "decrypted_${System.currentTimeMillis()}.pdf")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = PDDocument.load(inputStream, password)
            document.isAllSecurityToBeRemoved = true
            FileOutputStream(tempFile).use { os ->
                document.save(os)
                os.flush()
            }
            document.close()
        }
        Uri.fromFile(tempFile)
    } catch (e: Exception) { null }
}

data class TextMatch(
    val pageIndex: Int,
    val rect: PDRectangle
)

/**
 * Searches for text within a PDF and returns its coordinates.
 * @param context Context for content resolution.
 * @param uri Source PDF URI.
 * @param password Optional password.
 * @param query Text to search for.
 * @param onProgress Progress callback.
 * @return List of text matches with page index and coordinates.
 */
suspend fun findAllTextMatches(context: Context, uri: Uri, password: String?, query: String, onProgress: ((Int, Int) -> Unit)? = null): List<TextMatch> = withContext(Dispatchers.IO) {
    val matches = mutableListOf<TextMatch>()
    if (query.isBlank()) return@withContext matches
    
    try {
        val document = PdDocumentPool.acquire(context, uri, password)
        if (document != null) {
            val totalPages = document.numberOfPages
            val stripper = object : PDFTextStripper() {
                override fun writeString(text: String?, textPositions: MutableList<TextPosition>?) {
                    if (text == null || textPositions == null) return
                    var index = text.indexOf(query, 0, ignoreCase = true)
                    while (index != -1) {
                        val first = textPositions[index]
                        val last = textPositions[index + query.length - 1]
                        val page = document.getPage(currentPageNo - 1)
                        val pageWidth = page.mediaBox.width
                        val pageHeight = page.mediaBox.height
                        val x = first.xDirAdj / pageWidth
                        val y = first.yDirAdj / pageHeight
                        val w = ((last.xDirAdj + last.widthDirAdj) - first.xDirAdj) / pageWidth
                        val h = first.heightDir / pageHeight
                        matches.add(TextMatch(currentPageNo - 1, PDRectangle(x, y, x + w, y + h)))
                        index = text.indexOf(query, index + 1, ignoreCase = true)
                    }
                }
                override fun processPage(page: PDPage?) {
                    super.processPage(page)
                    onProgress?.invoke(currentPageNo, totalPages)
                }
            }
            stripper.sortByPosition = true
            stripper.getText(document)
        }
    } catch (e: Exception) {}
    matches
}

/**
 * Extracts links (URIs) from a PDF.
 * @param context Context for content resolution.
 * @param uri Source PDF URI.
 * @param password Optional password.
 * @return List of extracted links.
 */
suspend fun getLinksFromPdf(context: Context, uri: Uri, password: String?): List<PdfLink> = withContext(Dispatchers.IO) {
    val links = mutableListOf<PdfLink>()
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val document = if (password != null) PDDocument.load(inputStream, password) else PDDocument.load(inputStream)
            document.pages.forEachIndexed { index, page ->
                page.annotations.filterIsInstance<PDAnnotationLink>().forEach { link ->
                    val action = link.action
                    if (action is PDActionURI) {
                        links.add(PdfLink(index, link.rectangle, action.uri))
                    }
                }
            }
            document.close()
        }
    } catch (e: Exception) { }
    links
}
