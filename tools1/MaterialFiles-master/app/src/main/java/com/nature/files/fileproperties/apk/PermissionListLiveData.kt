/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.fileproperties.apk

import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.nature.files.app.packageManager
import com.nature.files.util.Failure
import com.nature.files.util.Loading
import com.nature.files.util.Stateful
import com.nature.files.util.Success
import com.nature.files.util.getPermissionInfoOrNull
import com.nature.files.util.valueCompat

class PermissionListLiveData(
    private val permissionNames: Array<String>
) : MutableLiveData<Stateful<List<PermissionItem>>>() {
    init {
        loadValue()
    }

    private fun loadValue() {
        value = Loading(value?.value)
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val value = try {
                val permissions = permissionNames.map { name ->
                    val packageManager = packageManager
                    val permissionInfo = packageManager.getPermissionInfoOrNull(name, 0)
                    val label = permissionInfo?.loadLabel(packageManager)?.toString()
                        .takeIf { it != name }
                    val description = permissionInfo?.loadDescription(packageManager)?.toString()
                    PermissionItem(name, permissionInfo, label, description)
                }
                Success(permissions)
            } catch (e: Exception) {
                Failure(valueCompat.value, e)
            }
            postValue(value)
        }
    }
}
