/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.root

import com.nature.files.provider.common.PosixFileAttributeView
import com.nature.files.provider.remote.RemoteInterface
import com.nature.files.provider.remote.RemotePosixFileAttributeView

open class RootPosixFileAttributeView(
    attributeView: PosixFileAttributeView
) : RemotePosixFileAttributeView(
    RemoteInterface { RootFileService.getRemotePosixFileAttributeViewInterface(attributeView) }
) {
    override fun name(): String {
        throw AssertionError()
    }
}
