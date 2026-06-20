package com.nature.files.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineViewScreen(
    files: List<FileItem>,
    onFileClick: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupedFiles = remember(files) {
        files.filter { !it.isDirectory && isMediaFile(it) }
            .groupBy {
                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(it.lastModified))
            }
    }

    if (groupedFiles.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().leafLitter(CanopyGreen.copy(alpha = 0.05f)), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(
                "The rings of time show no media here 🍃",
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Spectral),
                color = CanopyGreen
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
            contentPadding = PaddingValues(16.dp)
        ) {
            groupedFiles.forEach { (month, monthFiles) ->
                stickyHeader {
                    Text(
                        text = month,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = PlusJakartaSans),
                        color = BarkBrown
                    )
                }

                items(monthFiles.chunked(3)) { rowFiles ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowFiles.forEach { file ->
                            Box(modifier = Modifier.weight(1f)) {
                                FileItemGrid(
                                    fileItem = file,
                                    isSelected = false,
                                    onClick = { onFileClick(file) },
                                    onLongClick = {}
                                )
                            }
                        }
                        // Fill empty spaces in the last row
                        repeat(3 - rowFiles.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

private fun isMediaFile(file: FileItem): Boolean {
    val ext = file.extension.lowercase()
    return ext in listOf("jpg", "jpeg", "png", "webp", "mp4", "mkv", "mov", "gif")
}
