package omni.toolbox.model.common

import java.io.File

data class FileItem(
    val name: String,
    val file: File,
    val isDirectory: Boolean,
    val sizeLabel: String = "",
    val size: Long = 0L,
    val lastModified: Long = 0L
)
