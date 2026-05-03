package com.naturetools.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun DataVisualizerScreen(navController: NavHostController) {
    var dataPoints by remember { mutableStateOf(listOf(10f, 20f, 15f, 30f, 25f)) }
    var newValue by remember { mutableStateOf("") }
    var graphType by remember { mutableStateOf("Bar") } // Bar or Line

    ToolScreen(
        title = "Data Visualizer",
        onBack = { navController.popBackStack() },
        actions = {
            TextButton(onClick = { graphType = if (graphType == "Bar") "Line" else "Bar" }) {
                Text(graphType, color = MaterialTheme.colorScheme.primary)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Graph Area
            Card(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val secondaryColor = MaterialTheme.colorScheme.secondary

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (dataPoints.isEmpty()) return@Canvas

                        val width = size.width
                        val height = size.height
                        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)

                        if (graphType == "Bar") {
                            val barWidth = width / (dataPoints.size * 1.5f)
                            val spacing = barWidth * 0.5f
                            dataPoints.forEachIndexed { index, valAt ->
                                val barHeight = (valAt / maxVal) * height
                                drawRect(
                                    color = if (index % 2 == 0) primaryColor else secondaryColor,
                                    topLeft = Offset(index * (barWidth + spacing), height - barHeight),
                                    size = Size(barWidth, barHeight)
                                )
                            }
                        } else {
                            val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)
                            for (i in 0 until dataPoints.size - 1) {
                                val startX = i * stepX
                                val startY = height - (dataPoints[i] / maxVal) * height
                                val endX = (i + 1) * stepX
                                val endY = height - (dataPoints[i+1] / maxVal) * height
                                drawLine(
                                    color = primaryColor,
                                    start = Offset(startX, startY),
                                    end = Offset(endX, endY),
                                    strokeWidth = 4f
                                )
                                drawCircle(color = secondaryColor, center = Offset(startX, startY), radius = 6f)
                            }
                            drawCircle(color = secondaryColor, center = Offset(width, height - (dataPoints.last() / maxVal) * height), radius = 6f)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data Input
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    label = { Text("Value") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    newValue.toFloatOrNull()?.let {
                        dataPoints = dataPoints + it
                        newValue = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Data List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(dataPoints) { index, value ->
                    ListItem(
                        headlineContent = { Text("Point ${index + 1}: $value") },
                        trailingContent = {
                            IconButton(onClick = {
                                dataPoints = dataPoints.toMutableList().apply { removeAt(index) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
