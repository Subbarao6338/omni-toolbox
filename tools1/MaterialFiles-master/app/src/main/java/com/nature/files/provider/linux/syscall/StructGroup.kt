/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.linux.syscall

import com.nature.files.provider.common.ByteString

class StructGroup(
    val gr_name: ByteString?,
    val gr_passwd: ByteString?,
    val gr_gid: Int,
    val gr_mem: Array<ByteString>?
)
