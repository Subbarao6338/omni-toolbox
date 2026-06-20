package com.nature.files.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.*

/**
 * Visualizes storage usage using tree-branch metaphors (treemap).
 * Nature Design Mandate: organic, layered.
 */
@Composable
fun StorageAnalyzerTreemap(
    files: List<FileItem>,
    modifier: Modifier = Modifier
) {
    val totalSize = files.sumOf { it.size }
    if (totalSize == 0L) return

    Column(modifier = modifier.padding(16.dp).leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))) {
        Text(
            "Forest Canopy Analysis",
            style = MaterialTheme.typography.titleMedium,
            color = BarkBrown
        )
        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var currentX = 0f
                val width = size.width
                val height = size.height

                files.sortedByDescending { it.size }.take(10).forEach { file ->
                    val ratio = file.size.toFloat() / totalSize
                    val itemWidth = width * ratio

                    val extension = if (file.isDirectory) "" else file.name.substringAfterLast('.', "").lowercase()
                    // Nature metaphor: tree species per file category
                    val (color, species) = when {
                        file.size > 100 * 1024 * 1024 -> com.nature.files.ui.theme.BarkBrown.copy(alpha = 0.9f) to "Sequoia (Large)"
                        file.isDirectory -> CanopyGreen.copy(alpha = 0.8f) to "Oak (Folder)"
                        extension in listOf("jpg", "png", "webp") -> ImageAutumnLeaf to "Maple (Image)"
                        extension in listOf("mp4", "mkv") -> VideoClearWater to "Willow (Video)"
                        extension in listOf("zip", "7z") -> ArchiveStoneMoss to "Baobab (Archive)"
                        else -> LichenGrey to "Birch (Other)"
                    }

                    drawRect(
                        color = color,
                        topLeft = Offset(currentX, 0f),
                        size = Size(itemWidth, height)
                    )

                    // Draw organic "bark" grain
                    for (i in 0 until (itemWidth / 5).toInt()) {
                        drawLine(
                            color = BarkBrown.copy(alpha = 0.1f),
                            start = Offset(currentX + i * 5f, 0f),
                            end = Offset(currentX + i * 5f, height),
                            strokeWidth = 1f
                        )
                    }

                    // Branch detail
                    drawRect(
                        color = BarkBrown.copy(alpha = 0.3f),
                        topLeft = Offset(currentX, 0f),
                        size = Size(itemWidth, height),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    currentX += itemWidth
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "Visualizing the largest branches in your grove.",
            style = MaterialTheme.typography.bodySmall,
            color = CanopyGreen
        )
    }
}
