/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.smb.client

interface Authenticator {
    fun getPassword(authority: Authority): String?
}
