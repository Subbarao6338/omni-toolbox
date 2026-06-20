package com.nature.docs

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.nature.docs.data.image.PdfPageFetcher

class NatureApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(PdfPageFetcher.Factory(this@NatureApplication))
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("pdf_previews"))
                    .maxSizeBytes(150 * 1024 * 1024)
                    .build()
            }
            .crossfade(false)
            .build()
    }
}
