/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.navigation

import android.os.Parcelable
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import com.nature.files.filelist.name
import com.nature.files.util.ParcelableParceler
import com.nature.files.util.takeIfNotEmpty
import java.util.Random

@Parcelize
// @see https://youtrack.jetbrains.com/issue/KT-24842
// @Parcelize throws IllegalAccessError if the primary constructor is private.
data class BookmarkDirectory internal constructor(
    val id: Long,
    val customName: String?,
    val path: @WriteWith<ParcelableParceler> Path
) : Parcelable {
    // We cannot simply use path.hashCode() as ID because different bookmark directories may have
    // the same path.
    constructor(customName: String?, path: Path) : this(Random().nextLong(), customName, path)

    val defaultName: String
        get() = path.name

    val name: String
        get() = customName?.takeIfNotEmpty() ?: defaultName
}
