package com.nature.files.ui

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.nature.files.data.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Mandatory Quality Gate: All thumbnails must be loaded through this repository.
 * Uses DiskLruCache via Coil and avoids regeneration on valid cache.
 */
class ThumbnailRepository(private val context: Context) {
    private val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()

    suspend fun getThumbnail(fileItem: FileItem): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
        if (fileItem.isDirectory) return@withContext null

        val request = ImageRequest.Builder(context)
            .data(File(fileItem.path))
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)
        if (result is SuccessResult) {
            return@withContext (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
        }
        null
    }
}
