/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.compat

import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import com.nature.files.hiddenapi.RestrictedHiddenApi
import com.nature.files.util.lazyReflectedMethod

@RestrictedHiddenApi
private val isTransformedTouchPointInViewMethod by lazyReflectedMethod(
    ViewGroup::class.java, "isTransformedTouchPointInView", Float::class.java, Float::class.java,
    View::class.java, PointF::class.java
)

fun ViewGroup.isTransformedTouchPointInViewCompat(
    x: Float,
    y: Float,
    child: View,
    outLocalPoint: PointF?
): Boolean =
    isTransformedTouchPointInViewMethod.invoke(this, x, y, child, outLocalPoint) as Boolean
