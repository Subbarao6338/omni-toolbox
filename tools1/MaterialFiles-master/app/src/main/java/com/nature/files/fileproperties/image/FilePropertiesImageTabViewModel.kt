/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.fileproperties.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java8.nio.file.Path
import com.nature.files.file.MimeType
import com.nature.files.util.Stateful

class FilePropertiesImageTabViewModel(path: Path, mimeType: MimeType) : ViewModel() {
    private val _imageInfoLiveData = ImageInfoLiveData(path, mimeType)
    val imageInfoLiveData: LiveData<Stateful<ImageInfo>>
        get() = _imageInfoLiveData

    fun reload() {
        _imageInfoLiveData.loadValue()
    }

    override fun onCleared() {
        _imageInfoLiveData.close()
    }
}
