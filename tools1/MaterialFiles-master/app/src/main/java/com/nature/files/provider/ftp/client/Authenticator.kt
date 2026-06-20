/*
 * Copyright (c) 2022 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.ftp.client

interface Authenticator {
    fun getPassword(authority: Authority): String?
}
