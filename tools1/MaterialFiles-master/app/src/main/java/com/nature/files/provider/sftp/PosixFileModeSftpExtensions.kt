/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.sftp

import com.nature.files.provider.common.PosixFileModeBit
import com.nature.files.provider.common.toInt
import net.schmizz.sshj.sftp.FileAttributes

fun Set<PosixFileModeBit>.toSftpAttributes(): FileAttributes =
    FileAttributes.Builder().withPermissions(toInt()).build()
