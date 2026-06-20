/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.smb

import com.nature.files.provider.common.AbstractWatchKey

internal class SmbWatchKey(
    watchService: SmbWatchService,
    path: SmbPath
) : AbstractWatchKey<SmbWatchKey, SmbPath>(watchService, path)
