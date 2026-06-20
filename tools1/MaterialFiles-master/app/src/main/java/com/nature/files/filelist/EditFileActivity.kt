/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.filelist

import android.os.Bundle
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import com.nature.files.app.AppActivity
import com.nature.files.file.MimeType
import com.nature.files.file.fileProviderUri
import com.nature.files.util.ParcelableArgs
import com.nature.files.util.ParcelableParceler
import com.nature.files.util.args
import com.nature.files.util.createEditIntent
import com.nature.files.util.startActivitySafe

// Use a trampoline activity so that we can have a proper icon and title.
class EditFileActivity : AppActivity() {
    private val args by args<Args>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivitySafe(args.path.fileProviderUri.createEditIntent(args.mimeType))
        finish()
    }

    @Parcelize
    class Args(
        val path: @WriteWith<ParcelableParceler> Path,
        val mimeType: MimeType
    ) : ParcelableArgs
}
