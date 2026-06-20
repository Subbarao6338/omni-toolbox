/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.filelist

import java8.nio.file.Path
import com.nature.files.file.MimeType
import com.nature.files.file.isSupportedArchive
import com.nature.files.provider.archive.archiveFile
import com.nature.files.provider.archive.isArchivePath
import com.nature.files.provider.document.isDocumentPath
import com.nature.files.provider.document.resolver.DocumentResolver
import com.nature.files.provider.linux.isLinuxPath

val Path.name: String
    get() = fileName?.toString() ?: if (isArchivePath) archiveFile.fileName.toString() else "/"

fun Path.toUserFriendlyString(): String = if (isLinuxPath) toFile().path else toUri().toString()

fun Path.isArchiveFile(mimeType: MimeType): Boolean = !isArchivePath && mimeType.isSupportedArchive

val Path.isLocalPath: Boolean
    get() =
        isLinuxPath || (isDocumentPath && DocumentResolver.isLocal(this as DocumentResolver.Path))

val Path.isRemotePath: Boolean
    get() = !isLocalPath
