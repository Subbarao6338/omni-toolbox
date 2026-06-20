/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.fileproperties.permission

import androidx.annotation.DrawableRes
import com.nature.files.R
import com.nature.files.util.SelectionLiveData

class GroupListAdapter(
    selectionLiveData: SelectionLiveData<Int>
) : PrincipalListAdapter(selectionLiveData) {
    @DrawableRes
    override val principalIconRes: Int = R.drawable.people_icon_control_normal_24dp
}
