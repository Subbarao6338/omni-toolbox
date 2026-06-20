package com.nature.files.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ThumbnailManager(private val context: Context) {
    private val memoryCache = LruCache<String, Bitmap>(50)
    private val imageLoader = ImageLoader(context)

    suspend fun getThumbnail(file: File): Bitmap? = withContext(Dispatchers.IO) {
        memoryCache.get(file.absolutePath)?.let { return@withContext it }

        val request = ImageRequest.Builder(context)
            .data(file)
            .size(Size(200, 200))
            .build()

        val result = imageLoader.execute(request).drawable
        if (result is android.graphics.drawable.BitmapDrawable) {
            val bitmap = result.bitmap
            memoryCache.put(file.absolutePath, bitmap)
            return@withContext bitmap
        }
        null
    }
}
