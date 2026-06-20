/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.storage

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import com.nature.files.R
import com.nature.files.app.packageManager
import com.nature.files.file.ExternalStorageUri
import com.nature.files.util.ParcelableArgs
import com.nature.files.util.args
import com.nature.files.util.createDocumentsUiViewDirectoryIntent
import com.nature.files.util.finish
import com.nature.files.util.showToast

class AddExternalStorageShortcutFragment : Fragment() {
    private val args by args<Args>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val uri = args.uri
        val hasDocumentsUi = uri.value.createDocumentsUiViewDirectoryIntent()
            .resolveActivity(packageManager) != null
        if (hasDocumentsUi) {
            val externalStorageShortcut = ExternalStorageShortcut(
                null, args.customNameRes?.let { getString(it) }, uri
            )
            Storages.addOrReplace(externalStorageShortcut)
        } else {
            showToast(R.string.activity_not_found)
        }
        finish()
    }

    @Parcelize
    class Args(
        @StringRes val customNameRes: Int?,
        val uri: ExternalStorageUri
    ) : ParcelableArgs
}
