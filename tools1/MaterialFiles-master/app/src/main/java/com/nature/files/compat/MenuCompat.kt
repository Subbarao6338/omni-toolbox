/*
 * Copyright (c) 2023 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.compat

import android.view.Menu
import androidx.core.view.MenuCompat

fun Menu.setGroupDividerEnabledCompat(groupDividerEnabled: Boolean) {
    MenuCompat.setGroupDividerEnabled(this, groupDividerEnabled)
}
