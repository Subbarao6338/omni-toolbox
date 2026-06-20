/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.root

import com.nature.files.provider.common.PosixFileStore
import com.nature.files.provider.remote.RemoteInterface
import com.nature.files.provider.remote.RemotePosixFileStore

class RootPosixFileStore(fileStore: PosixFileStore) : RemotePosixFileStore(
    RemoteInterface { RootFileService.getRemotePosixFileStoreInterface(fileStore) }
)
