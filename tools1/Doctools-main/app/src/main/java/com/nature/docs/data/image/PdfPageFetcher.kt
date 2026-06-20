package com.nature.docs.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import com.nature.docs.data.pdf.NativeRendererPool
import com.nature.docs.data.pdf.PdDocumentPool
import com.nature.docs.ui.components.BitmapPool
import com.tom_roush.pdfbox.rendering.ImageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

data class PdfPageRequest(
    val uri: Uri,
    val pageIndex: Int,
    val password: String? = null,
    val scale: Float = 1.0f,
    val rotation: Int = 0,
    val priority: Int = 0 // 0: Prefetch, 1: High (Current View)
)

// Global Concurrency Controller (Native Engine Throttle)
private val renderSemaphore = Semaphore(4)
private val highPriorityDispatcher = Dispatchers.IO
private val lowPriorityDispatcher = Dispatchers.Default

class PdfPageFetcher(
    private val context: Context,
    private val data: PdfPageRequest
) : Fetcher {

    private fun applyRotation(source: Bitmap, rotation: Int): Bitmap {
        if (rotation % 360 == 0) return source
        
        val matrix = android.graphics.Matrix()
        matrix.postRotate(rotation.toFloat())
        
        val newWidth = if (rotation % 180 != 0) source.height else source.width
        val newHeight = if (rotation % 180 != 0) source.width else source.height
        
        val rotated = BitmapPool.get(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(rotated)
        
        // Center rotation logic
        val cx = source.width / 2f
        val cy = source.height / 2f
        
        // Move to origin, rotate, move to new center
        matrix.preTranslate(-cx, -cy)
        matrix.postTranslate(newWidth / 2f, newHeight / 2f)
        
        canvas.drawBitmap(source, matrix, null)
        BitmapPool.put(source) // Recycle source
        return rotated
    }

    override suspend fun fetch(): FetchResult? = withContext(if (data.priority > 0) highPriorityDispatcher else lowPriorityDispatcher) {
        try {
            // 1. Try Native Renderer (Turbo Path)
            if (data.password == null) {
                val renderer = NativeRendererPool.acquire(context, data.uri)
                if (renderer != null && isActive) {
                    try {
                        renderSemaphore.withPermit {
                            if (!isActive) return@withContext null
                            if (data.pageIndex < renderer.pageCount) {
                                val page = renderer.openPage(data.pageIndex)
                                try {
                                    val width = (page.width * data.scale).toInt().coerceAtLeast(1)
                                    val height = (page.height * data.scale).toInt().coerceAtLeast(1)
                                    
                                    val bitmap = BitmapPool.get(width, height, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(bitmap)
                                    canvas.drawColor(Color.WHITE)
                                    
                                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                                    
                                    val finalBitmap = if (data.rotation != 0) applyRotation(bitmap, data.rotation) else bitmap
                                    
                                    return@withContext DrawableResult(
                                        drawable = android.graphics.drawable.BitmapDrawable(context.resources, finalBitmap),
                                        isSampled = data.scale < 1.0f,
                                        dataSource = DataSource.MEMORY
                                    )
                                } finally {
                                    try { page.close() } catch (e: Exception) {}
                                }
                            }
                        }
                    } finally {
                        NativeRendererPool.release(renderer)
                    }
                }
            }

            // 2. Try Pooled PDFBox (Fallback / Protected)
            if (!isActive) return@withContext null
            val document = PdDocumentPool.acquire(context, data.uri, data.password)
            if (document != null && isActive) {
                renderSemaphore.withPermit {
                    if (!isActive) return@withContext null
                    val renderer = com.tom_roush.pdfbox.rendering.PDFRenderer(document)
                    val bitmap = renderer.renderImage(data.pageIndex, data.scale, ImageType.RGB)
                    
                    val finalBitmap = if (data.rotation != 0) applyRotation(bitmap, data.rotation) else bitmap
                    
                    return@withContext DrawableResult(
                        drawable = android.graphics.drawable.BitmapDrawable(context.resources, finalBitmap),
                        isSampled = data.scale < 1.0f,
                        dataSource = DataSource.MEMORY
                    )
                }
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
        null
    }

    class Factory(private val context: Context) : Fetcher.Factory<PdfPageRequest> {
        override fun create(data: PdfPageRequest, options: Options, imageLoader: ImageLoader): Fetcher {
            return PdfPageFetcher(context, data)
        }
    }
}
