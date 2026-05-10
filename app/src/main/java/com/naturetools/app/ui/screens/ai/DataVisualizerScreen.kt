package com.naturetools.app.ui.screens.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun DataVisualizerScreen(navController: NavHostController) {
    var inputData by remember { mutableStateOf("10, 25, 45, 30, 60, 35") }
    val dataPoints = remember(inputData) {
        inputData.split(",").mapNotNull { it.trim().toFloatOrNull() }
    }

    ToolScreen(
        title = "Data Visualizer",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = inputData,
                onValueChange = { inputData = it },
                label = { Text("Comma separated values") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (dataPoints.isNotEmpty()) {
                val maxValue = dataPoints.maxOrNull() ?: 1f

                Card(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barWidth = canvasWidth / (dataPoints.size * 1.5f)
                        val spacing = (canvasWidth - (barWidth * dataPoints.size)) / (dataPoints.size + 1)

                        dataPoints.forEachIndexed { index, value ->
                            val barHeight = (value / maxValue) * canvasHeight
                            drawRect(
                                color = Color(0xFF4CAF50),
                                topLeft = Offset(spacing + index * (barWidth + spacing), canvasHeight - barHeight),
                                size = Size(barWidth, barHeight)
                            )
                        }
                    }
                }
            } else {
                Text("Enter numbers to see visualization")
            }
        }
    }
}
