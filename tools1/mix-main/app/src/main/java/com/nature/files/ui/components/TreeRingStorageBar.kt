package com.nature.files.ui.components

import androidx.compose.foundation.Canvas
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
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.SunbeamYellow
import com.nature.files.ui.theme.CanopyGreen

@Composable
fun TreeRingStorageBar(
    usedPercentage: Float,
    totalSpace: String,
    usedSpace: String,
    fileTypeDistribution: Map<String, Float> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.minDimension / 2f

                // Draw bark (outer ring)
                drawCircle(
                    color = BarkBrown,
                    radius = maxRadius,
                    center = centerOffset,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Draw annual rings
                for (i in 1..8) {
                    drawCircle(
                        color = BarkBrown.copy(alpha = 0.15f),
                        radius = maxRadius * (i / 8f),
                        center = centerOffset,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Draw heartwood (used space) with segmented growth rings for file types
                if (fileTypeDistribution.isEmpty()) {
                    drawCircle(
                        color = SunbeamYellow.copy(alpha = 0.6f),
                        radius = maxRadius * usedPercentage,
                        center = centerOffset
                    )
                } else {
                    var currentStartAngle = -90f
                    val innerRadius = maxRadius * usedPercentage

                    fileTypeDistribution.forEach { (type, ratio) ->
                        val sweepAngle = 360f * ratio
                        val color = when (type.lowercase()) {
                            "images" -> com.nature.files.ui.theme.ImageAutumnLeaf
                            "videos" -> com.nature.files.ui.theme.VideoClearWater
                            "archives" -> com.nature.files.ui.theme.ArchiveStoneMoss
                            "documents" -> com.nature.files.ui.theme.DocumentFreshLeaf
                            else -> SunbeamYellow
                        }

                        drawArc(
                            color = color.copy(alpha = 0.7f),
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(centerOffset.x - innerRadius, centerOffset.y - innerRadius),
                            size = Size(innerRadius * 2, innerRadius * 2)
                        )
                        currentStartAngle += sweepAngle
                    }
                }
            }
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = "$usedSpace used of $totalSpace",
                style = MaterialTheme.typography.bodyMedium,
                color = BarkBrown
            )
            Text(
                text = "${(usedPercentage * 100).toInt()}% Forest Occupancy",
                style = MaterialTheme.typography.labelSmall,
                color = CanopyGreen
            )
        }
    }
}
