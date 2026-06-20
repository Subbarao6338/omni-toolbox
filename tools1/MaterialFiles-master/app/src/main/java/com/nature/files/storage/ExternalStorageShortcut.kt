/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.storage

import android.content.Context
import android.content.Intent
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import com.nature.files.file.ExternalStorageUri
import com.nature.files.file.displayName
import com.nature.files.util.createDocumentsUiViewDirectoryIntent
import com.nature.files.util.createIntent
import com.nature.files.util.putArgs
import kotlin.random.Random

@Parcelize
data class ExternalStorageShortcut(
    override val id: Long,
    override val customName: String?,
    val uri: ExternalStorageUri
) : Storage() {
    constructor(
        id: Long?,
        customName: String?,
        uri: ExternalStorageUri
    ) : this(id ?: Random.nextLong(), customName, uri)

    override fun getDefaultName(context: Context): String = uri.displayName

    override val description: String
        get() = uri.value.toString()

    override val path: Path?
        get() = null

    override fun createIntent(): Intent = uri.value.createDocumentsUiViewDirectoryIntent()

    override fun createEditIntent(): Intent =
        EditExternalStorageShortcutDialogActivity::class.createIntent()
            .putArgs(EditExternalStorageShortcutDialogFragment.Args(this))
}
