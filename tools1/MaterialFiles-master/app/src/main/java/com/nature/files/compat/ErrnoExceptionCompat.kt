/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.compat

import android.system.ErrnoException
import com.nature.files.hiddenapi.RestrictedHiddenApi
import com.nature.files.util.lazyReflectedField

@RestrictedHiddenApi
private val functionNameField by lazyReflectedField(ErrnoException::class.java, "functionName")

val ErrnoException.functionNameCompat: String
    get() = functionNameField.get(this) as String
