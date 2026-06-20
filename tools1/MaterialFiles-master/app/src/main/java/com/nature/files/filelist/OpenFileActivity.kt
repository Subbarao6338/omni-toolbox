/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.filelist

import android.content.Intent
import android.os.Bundle
import java8.nio.file.Path
import com.nature.files.app.AppActivity
import com.nature.files.app.application
import com.nature.files.file.MimeType
import com.nature.files.file.asMimeTypeOrNull
import com.nature.files.file.fileProviderUri
import com.nature.files.filejob.FileJobService
import com.nature.files.provider.archive.isArchivePath
import com.nature.files.util.createViewIntent
import com.nature.files.util.extraPath
import com.nature.files.util.startActivitySafe

class OpenFileActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val path = intent.extraPath
        val mimeType = intent.type?.asMimeTypeOrNull()
        if (path != null && mimeType != null) {
            openFile(path, mimeType)
        }
        finish()
    }

    private fun openFile(path: Path, mimeType: MimeType) {
        if (path.isArchivePath) {
            FileJobService.open(path, mimeType, false, this)
        } else {
            val intent = path.fileProviderUri.createViewIntent(mimeType)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                .apply { extraPath = path }
            startActivitySafe(intent)
        }
    }

    companion object {
        private const val ACTION_OPEN_FILE = "com.nature.files.intent.action.OPEN_FILE"

        fun createIntent(path: Path, mimeType: MimeType): Intent =
            Intent(ACTION_OPEN_FILE)
                .setPackage(application.packageName)
                .setType(mimeType.value)
                .apply { extraPath = path }
    }
}
