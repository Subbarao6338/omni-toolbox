package com.nature.docs.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.*

@Composable
fun VineProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "VineGrowth"
    )

    Box(modifier = modifier
        .height(12.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(InkBrown.copy(alpha = 0.05f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width * animatedProgress.value
            val path = Path().apply {
                moveTo(0f, size.height / 2)
                var x = 0f
                while (x < width) {
                    val nextX = (x + 30f).coerceAtMost(width)
                    val controlX = x + (nextX - x) / 2
                    val controlY = size.height / 2 + (if (x.toInt() % 60 == 0) 8f else -8f)
                    quadraticBezierTo(controlX, controlY, nextX, size.height / 2)

                    // Draw tiny leaves along the vine
                    if (x % 60f == 0f && x > 0) {
                        val leafPath = Path().apply {
                            moveTo(x, size.height / 2)
                            quadraticBezierTo(x - 5f, size.height / 2 - 10f, x, size.height / 2 - 15f)
                            quadraticBezierTo(x + 5f, size.height / 2 - 10f, x, size.height / 2)
                        }
                        drawPath(leafPath, color = FreshLeaf)
                    }

                    x = nextX
                }
            }
            drawPath(
                path = path,
                color = BotanicalGreen,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}

@Composable
fun BotanicalCorner(modifier: Modifier = Modifier, color: Color = BotanicalGreen.copy(alpha = 0.2f)) {
    Canvas(modifier = modifier.size(40.dp)) {
        val path = Path().apply {
            moveTo(size.width, 0f)
            quadraticBezierTo(size.width * 0.5f, size.height * 0.1f, size.width * 0.2f, size.height * 0.8f)
            // Leaf 1
            moveTo(size.width * 0.4f, size.height * 0.3f)
            quadraticBezierTo(size.width * 0.1f, size.height * 0.2f, 0f, size.height * 0.4f)
            quadraticBezierTo(size.width * 0.2f, size.height * 0.5f, size.width * 0.4f, size.height * 0.3f)
            // Leaf 2
            moveTo(size.width * 0.3f, size.height * 0.6f)
            quadraticBezierTo(size.width * 0.05f, size.height * 0.7f, size.width * 0.1f, size.height * 0.9f)
            quadraticBezierTo(size.width * 0.35f, size.height * 0.8f, size.width * 0.3f, size.height * 0.6f)
        }
        drawPath(path, color = color, style = Stroke(width = 1.5.dp.toPx()))
    }
}

@Composable
fun AgedPaperCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.padding(8.dp),
        color = ParchmentBg,
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 1.dp,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, InkBrown.copy(alpha = 0.15f))
    ) {
        Box {
            // Botanical corner decorations
            BotanicalCorner(
                modifier = Modifier.align(Alignment.TopStart).padding(4.dp).alpha(0.3f)
            )
            BotanicalCorner(
                modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).alpha(0.3f).graphicsLayer { rotationZ = 180f }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun BotanicalBorder(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(InkBrown.copy(alpha = 0.2f), style = Stroke(width = 0.5.dp.toPx()))

            // Corner leaf accents
            val leafSize = 12.dp.toPx()
            drawArc(
                color = BotanicalGreen.copy(alpha = 0.4f),
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = true,
                topLeft = Offset(-leafSize/2, -leafSize/2),
                size = Size(leafSize, leafSize)
            )
            drawArc(
                color = BotanicalGreen.copy(alpha = 0.4f),
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = true,
                topLeft = Offset(size.width - leafSize/2, size.height - leafSize/2),
                size = Size(leafSize, leafSize)
            )
        }
    }
}

@Composable
fun LinenCanvas(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.background(ParchmentBg)) {
        Canvas(modifier = Modifier.matchParentSize().alpha(0.03f)) {
            // Subtle linen texture
            val step = 3.dp.toPx()
            for (i in 0..size.width.toInt() step step.toInt()) {
                drawLine(InkBrown, Offset(i.toFloat(), 0f), Offset(i.toFloat(), size.height), strokeWidth = 0.5.dp.toPx())
            }
            for (i in 0..size.height.toInt() step step.toInt()) {
                drawLine(InkBrown, Offset(0f, i.toFloat()), Offset(size.width, i.toFloat()), strokeWidth = 0.5.dp.toPx())
            }
        }
        content()
    }
}
