package com.nature.files.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen

@Composable
fun ForestPathBreadcrumb(
    path: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val segments = path.split("/").filter { it.isNotEmpty() }
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LaunchedEffect(segments.size) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
        // Initial "Root" segment
        BreadcrumbSegment(
            name = "Forest",
            onClick = { onNavigate("/") },
            isLast = segments.isEmpty()
        )

        segments.forEachIndexed { index, segment ->
            key(segments.take(index + 1).joinToString("/")) {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { visible = true }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(700, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(700)),
                    exit = slideOutHorizontally() + fadeOut()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WindingPathConnector()
                        BreadcrumbSegment(
                            name = segment,
                            onClick = {
                                val targetPath = "/" + segments.take(index + 1).joinToString("/")
                                onNavigate(targetPath)
                            },
                            isLast = index == segments.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WindingPathConnector() {
    Canvas(modifier = Modifier.size(width = 24.dp, height = 16.dp)) {
        val path = Path().apply {
            moveTo(0f, size.height / 2f)
            cubicTo(
                size.width * 0.3f, 0f,
                size.width * 0.7f, size.height,
                size.width, size.height / 2f
            )
        }
        drawPath(
            path = path,
            color = CanopyGreen.copy(alpha = 0.3f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
private fun BreadcrumbSegment(
    name: String,
    onClick: () -> Unit,
    isLast: Boolean
) {
    val color = if (isLast) CanopyGreen else BarkBrown
    val fontWeight = if (isLast) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal

    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = color,
                    fontWeight = fontWeight
                )
            )
            // Visual metaphor: leaf-step marker
            if (isLast) {
                Canvas(modifier = Modifier.size(width = 20.dp, height = 4.dp)) {
                    val path = Path().apply {
                        moveTo(0f, size.height / 2f)
                        quadraticBezierTo(size.width / 2f, 0f, size.width, size.height / 2f)
                        quadraticBezierTo(size.width / 2f, size.height, 0f, size.height / 2f)
                    }
                    drawPath(path, color = CanopyGreen)
                }
            }
        }
    }
}
