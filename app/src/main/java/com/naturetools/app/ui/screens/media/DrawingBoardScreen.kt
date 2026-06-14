package com.naturetools.app.ui.screens.media

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Line(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DrawingBoardScreen(navController: NavHostController, title: String) {
    var lines by remember { mutableStateOf(listOf<Line>()) }
    var currentLinePoints by remember { mutableStateOf(listOf<Offset>()) }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var strokeWidth by remember { mutableFloatStateOf(5f) }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { lines = emptyList() }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear")
            }
            IconButton(onClick = { /* Save logic */ }) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Color & Size Controls
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(Color.Black, Color.Red, Color.Blue, Color.Green).forEach { color ->
                    IconButton(onClick = { selectedColor = color }) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            color = color,
                            shape = androidx.compose.foundation.shape.CircleShape,
                            border = if (selectedColor == color) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {}
                    }
                }
                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    valueRange = 1f..50f,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )
            }

            // Canvas
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentLinePoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                currentLinePoints = currentLinePoints + change.position
                            },
                            onDragEnd = {
                                lines = lines + Line(currentLinePoints, selectedColor, strokeWidth)
                                currentLinePoints = emptyList()
                            }
                        )
                    },
                color = Color.White
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    lines.forEach { line ->
                        val path = Path()
                        if (line.points.isNotEmpty()) {
                            path.moveTo(line.points[0].x, line.points[0].y)
                            line.points.drop(1).forEach { point ->
                                path.lineTo(point.x, point.y)
                            }
                            drawPath(
                                path = path,
                                color = line.color,
                                style = Stroke(width = line.strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Draw current line
                    if (currentLinePoints.isNotEmpty()) {
                        val path = Path()
                        path.moveTo(currentLinePoints[0].x, currentLinePoints[0].y)
                        currentLinePoints.drop(1).forEach { point ->
                            path.lineTo(point.x, point.y)
                        }
                        drawPath(
                            path = path,
                            color = selectedColor,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}
