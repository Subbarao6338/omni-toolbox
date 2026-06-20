/*
 * Copyright (c) 2022 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java8.nio.file.Path
import com.nature.files.compat.getPackageArchiveInfoCompat
import com.nature.files.provider.document.isDocumentPath
import com.nature.files.provider.document.resolver.DocumentResolver
import com.nature.files.provider.linux.isLinuxPath
import java.io.Closeable

val Path.isGetPackageArchiveInfoCompatible: Boolean
    get() = isLinuxPath || isDocumentPath

fun PackageManager.getPackageArchiveInfoCompat(
    path: Path,
    flags: Int
): Pair<PackageInfo?, Closeable?> {
    val archiveFilePath: String
    val closeable: Closeable?
    when {
        path.isLinuxPath -> {
            archiveFilePath = path.toFile().path
            closeable = null
        }
        path.isDocumentPath -> {
            val pfd = DocumentResolver.openParcelFileDescriptor(path as DocumentResolver.Path, "r")
            archiveFilePath = "/proc/self/fd/${pfd.fd}"
            closeable = pfd
        }
        else -> throw IllegalArgumentException(path.toString())
    }
    var successful = false
    val packageInfo: PackageInfo?
    try {
        packageInfo = getPackageArchiveInfoCompat(archiveFilePath, flags)?.apply {
            applicationInfo?.apply {
                sourceDir = archiveFilePath
                publicSourceDir = archiveFilePath
            }
        }
        successful = true
    } finally {
        if (!successful) {
            closeable?.close()
        }
    }
    return packageInfo to closeable
}
