/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.fileproperties.permission

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import java8.nio.file.Path
import com.nature.files.R
import com.nature.files.file.FileItem
import com.nature.files.filejob.FileJobService
import com.nature.files.provider.common.PosixFileAttributes
import com.nature.files.provider.common.PosixPrincipal
import com.nature.files.provider.common.PosixUser
import com.nature.files.provider.common.toByteString
import com.nature.files.util.SelectionLiveData
import com.nature.files.util.putArgs
import com.nature.files.util.show
import com.nature.files.util.viewModels

class SetOwnerDialogFragment : SetPrincipalDialogFragment() {
    override val viewModel: SetPrincipalViewModel by viewModels { { SetOwnerViewModel() } }

    @StringRes
    override val titleRes: Int = R.string.file_properties_permission_set_owner_title

    override fun createAdapter(selectionLiveData: SelectionLiveData<Int>): PrincipalListAdapter =
        UserListAdapter(selectionLiveData)

    override val PosixFileAttributes.principal: PosixPrincipal
        get() = owner()!!

    override fun setPrincipal(path: Path, principal: PrincipalItem, recursive: Boolean) {
        val owner = PosixUser(principal.id, principal.name?.toByteString())
        FileJobService.setOwner(path, owner, recursive, requireContext())
    }

    companion object {
        fun show(file: FileItem, fragment: Fragment) {
            SetOwnerDialogFragment().putArgs(Args(file)).show(fragment)
        }
    }
}
