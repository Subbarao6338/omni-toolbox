package com.nature.files.util

import android.os.storage.StorageVolume
import com.nature.files.compat.directoryCompat

val StorageVolume.isMounted: Boolean
    get() = directoryCompat != null
