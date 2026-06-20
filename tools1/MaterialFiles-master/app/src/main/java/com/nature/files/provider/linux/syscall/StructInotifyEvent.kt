/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.linux.syscall

import com.nature.files.provider.common.ByteString

class StructInotifyEvent(
    val wd: Int,
    val mask: Int, /* uint32_t */
    val cookie: Int, /* uint32_t */
    val name: ByteString?
)
