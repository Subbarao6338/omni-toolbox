package com.nature.files.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.CanopyGreen
import com.nature.files.ui.theme.SunbeamHighlight
import com.nature.files.ui.theme.leafTrail
import com.nature.files.utils.FileUtils.formatSize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FileItemRow(
    fileItem: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit,
    onTag: () -> Unit,
    leftSwipeAction: String = "DELETE",
    rightSwipeAction: String = "TAG",
    modifier: Modifier = Modifier
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    // Quality Gate: Configurable swipe actions per user preference and MIME category
    val dismissState = rememberDismissState(
        confirmValueChange = {
            val isMedia = fileItem.mimeType?.startsWith("image/") == true || fileItem.mimeType?.startsWith("video/") == true
            val isArchive = fileItem.extension in listOf("zip", "rar", "7z")

            if (it == DismissValue.DismissedToEnd) {
                // Right swipe (StartToEnd)
                when {
                    isMedia && rightSwipeAction == "TAG" -> onTag() // Preview/Tag metaphor
                    isArchive && rightSwipeAction == "TAG" -> onTag() // Extract/Tag metaphor
                    rightSwipeAction == "DELETE" -> onDelete()
                    else -> onTag()
                }
                false
            } else if (it == DismissValue.DismissedToStart) {
                // Left swipe (EndToStart)
                if (leftSwipeAction == "DELETE") onDelete() else onTag()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val isMedia = fileItem.mimeType?.startsWith("image/") == true || fileItem.mimeType?.startsWith("video/") == true

            val (color, icon) = when (direction) {
                DismissDirection.StartToEnd -> {
                    if (isMedia) com.nature.files.ui.theme.SunbeamYellow.copy(alpha = 0.8f) to Icons.Default.Delete
                    else com.nature.files.ui.theme.MossTag.copy(alpha = 0.8f) to Icons.Default.Label
                }
                DismissDirection.EndToStart -> com.nature.files.ui.theme.Clay.copy(alpha = 0.8f) to Icons.Default.Delete
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            FileItemRowContent(
                fileItem = fileItem,
                isSelected = isSelected,
                onClick = onClick,
                onLongClick = onLongClick,
                isDragging = isDragging,
                dragOffset = dragOffset,
                onDragStart = { isDragging = true },
                onDragEnd = { isDragging = false; dragOffset = Offset.Zero },
                onDrag = { dragAmount -> dragOffset += dragAmount },
                modifier = modifier
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileItemRowContent(
    fileItem: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    isDragging: Boolean,
    dragOffset: Offset,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .then(if (isSelected) Modifier.drawBehind { drawRect(SunbeamHighlight) } else Modifier)
            .then(if (isDragging) Modifier.drawBehind {
                // Quality Gate: Moss-green glow for drop targets metaphor
                drawRect(
                    color = com.nature.files.ui.theme.MossTag.copy(alpha = 0.4f),
                    style = Stroke(width = 6.dp.toPx())
                )
            } else Modifier)
            .leafTrail(isDragging, dragOffset),
        headlineContent = { Text(fileItem.name) },
        supportingContent = {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(fileItem.lastModified))
            val size = if (fileItem.isDirectory) "Folder" else formatSize(fileItem.size)
            Text("$date | $size", style = MaterialTheme.typography.bodySmall)
        },
        leadingContent = {
            if (fileItem.isDirectory) {
                NatureFolderIcon(fileItem.name, customColor = fileItem.customColor, modifier = Modifier.size(40.dp))
            } else {
                LeafBadge(fileItem.extension)
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    LeafCheckmark()
                }
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.DragHandle,
                    contentDescription = "Drag Handle",
                    modifier = Modifier
                        .padding(8.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { onDragStart() },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount)
                                }
                            )
                        }
                )
            }
        }
    )

}

@Composable
fun LeafCheckmark() {
    Canvas(modifier = Modifier.size(24.dp)) {
        val path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.75f)
            lineTo(size.width * 0.8f, size.height * 0.25f)
        }
        drawPath(
            path,
            color = CanopyGreen,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun LeafParticleTrail(offset: Offset) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val random = Random(offset.hashCode())
        repeat(5) {
            val pOffset = offset + Offset(random.nextFloat() * 50 - 25, random.nextFloat() * 50 - 25)
            drawCircle(
                color = CanopyGreen.copy(alpha = 0.4f),
                radius = 5f,
                center = pOffset
            )
        }
    }
}

