/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.fileproperties.apk

import android.content.pm.PermissionInfo

class PermissionItem(
    val name: String,
    val permissionInfo: PermissionInfo?,
    val label: String?,
    val description: String?
)
