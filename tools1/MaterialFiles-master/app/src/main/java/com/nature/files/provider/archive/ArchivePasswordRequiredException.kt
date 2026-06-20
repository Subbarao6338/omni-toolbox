/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.archive

import android.content.Context
import java8.nio.file.Path
import com.nature.files.fileaction.ArchivePasswordDialogActivity
import com.nature.files.fileaction.ArchivePasswordDialogFragment
import com.nature.files.provider.common.UserAction
import com.nature.files.provider.common.UserActionRequiredException
import com.nature.files.util.createIntent
import com.nature.files.util.putArgs
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class ArchivePasswordRequiredException(
    private val file: Path,
    reason: String?
) :
    UserActionRequiredException(file.toString(), null, reason) {

    override fun getUserAction(continuation: Continuation<Boolean>, context: Context): UserAction {
        return UserAction(
            ArchivePasswordDialogActivity::class.createIntent().putArgs(
                ArchivePasswordDialogFragment.Args(file) { continuation.resume(it) }
            ), ArchivePasswordDialogFragment.getTitle(context),
            ArchivePasswordDialogFragment.getMessage(file, context)
        )
    }
}
