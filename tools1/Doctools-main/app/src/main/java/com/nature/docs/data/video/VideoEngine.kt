package com.nature.docs.data.video

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import java.io.File

object VideoEngine {

    fun trimVideo(context: Context, uri: Uri, startTimeMs: Long, endTimeMs: Long, outputFile: File) {
        val transformer = Transformer.Builder(context).build()
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startTimeMs)
                    .setEndPositionMs(endTimeMs)
                    .build()
            )
            .build()

        transformer.start(mediaItem, outputFile.path)
    }

    fun mergeVideos(context: Context, uris: List<Uri>, outputFile: File) {
        val transformer = Transformer.Builder(context).build()
        val editedMediaItems = uris.map { uri ->
            EditedMediaItem.Builder(MediaItem.fromUri(uri)).build()
        }
        val sequence = EditedMediaItemSequence(editedMediaItems)
        val composition = Composition.Builder(sequence).build()

        transformer.start(composition, outputFile.path)
    }
}
