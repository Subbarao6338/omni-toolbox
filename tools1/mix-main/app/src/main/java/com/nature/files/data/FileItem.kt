package com.nature.files.data

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String? = null,
    val checksum: String? = null,
    val extension: String = if (isDirectory) "" else name.substringAfterLast('.', "").lowercase(),
    val isHidden: Boolean = name.startsWith("."),
    val tags: List<String> = emptyList(),
    val customColor: String? = null
)
