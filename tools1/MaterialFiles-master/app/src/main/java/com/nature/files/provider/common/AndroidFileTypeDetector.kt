/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.common

import java8.nio.file.Path
import java8.nio.file.attribute.BasicFileAttributes
import java8.nio.file.spi.FileTypeDetector
import com.nature.files.file.MimeType
import com.nature.files.file.forSpecialPosixFileType
import com.nature.files.file.guessFromPath
import java.io.IOException

object AndroidFileTypeDetector : FileTypeDetector() {
    @Throws(IOException::class)
    override fun probeContentType(path: Path): String {
        val attributes = path.readAttributes(BasicFileAttributes::class.java)
        return getMimeType(path, attributes)
    }

    fun getMimeType(path: Path, attributes: BasicFileAttributes): String {
        MimeType.forSpecialPosixFileType(attributes.posixFileType)?.let { return it.value }
        if (attributes.isDirectory) {
            return MimeType.DIRECTORY.value
        }
        if (attributes is ContentProviderFileAttributes) {
            attributes.mimeType()?.let { return it }
        }
        return MimeType.guessFromPath(path.toString()).value
    }
}
