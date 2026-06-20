package com.nature.files.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.SunbeamHighlight
import com.nature.files.ui.theme.leafTrail
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItemGrid(
    fileItem: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .leafTrail(isDragging, dragOffset),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SunbeamHighlight else Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                if (fileItem.isDirectory) {
                    NatureFolderIcon(fileItem.name, modifier = Modifier.size(48.dp))
                } else {
                    Icon(
                        imageVector = getFileIcon(fileItem),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    LeafBadge(
                        extension = fileItem.extension,
                        modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp)
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                    ) {
                        LeafCheckmark()
                    }
                }

                // Drag handle overlay for grid items
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopStart)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { isDragging = true },
                                onDragEnd = { isDragging = false; dragOffset = Offset.Zero },
                                onDragCancel = { isDragging = false; dragOffset = Offset.Zero },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffset += dragAmount
                                }
                            )
                        }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = fileItem.name,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun getFileIcon(fileItem: FileItem): ImageVector {
    val extension = fileItem.extension
    return when (extension) {
        "jpg", "jpeg", "png", "gif", "webp", "bmp" -> Icons.Default.Image
        "mp4", "mkv", "avi", "mov", "wmv", "flv" -> Icons.Default.VideoFile
        "mp3", "wav", "ogg", "flac", "m4a" -> Icons.Default.AudioFile
        "pdf" -> Icons.Default.PictureAsPdf
        else -> Icons.Default.Description
    }
}
