/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.navigation

import androidx.lifecycle.MediatorLiveData
import com.nature.files.settings.Settings

object StandardDirectoriesLiveData : MediatorLiveData<List<StandardDirectory>>() {
    init {
        // Initialize value before we have any active observer.
        loadValue()
        addSource(Settings.STANDARD_DIRECTORY_SETTINGS) { loadValue() }
    }

    private fun loadValue() {
        value = standardDirectories
    }
}
