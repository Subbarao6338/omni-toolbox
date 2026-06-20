/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.linux

import com.nature.files.provider.common.AbstractWatchKey

internal class LocalLinuxWatchKey(
    watchService: LocalLinuxWatchService,
    path: LinuxPath,
    val watchDescriptor: Int
) : AbstractWatchKey<LocalLinuxWatchKey, LinuxPath>(watchService, path)
