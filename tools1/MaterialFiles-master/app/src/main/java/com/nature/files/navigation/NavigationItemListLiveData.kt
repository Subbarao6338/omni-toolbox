/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.navigation

import androidx.lifecycle.MediatorLiveData
import com.nature.files.settings.Settings
import com.nature.files.storage.StorageVolumeListLiveData

object NavigationItemListLiveData : MediatorLiveData<List<NavigationItem?>>() {
    init {
        // Initialize value before we have any active observer.
        loadValue()
        addSource(Settings.STORAGES) { loadValue() }
        addSource(StorageVolumeListLiveData) { loadValue() }
        addSource(StandardDirectoriesLiveData) { loadValue() }
        addSource(Settings.BOOKMARK_DIRECTORIES) { loadValue() }
    }

    private fun loadValue() {
        value = navigationItems
    }
}
