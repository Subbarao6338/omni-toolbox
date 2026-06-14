package omni.toolbox.ui.screens.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.sqrt

@Composable
fun DataVisualizerScreen(navController: NavHostController) {
    var inputData by remember { mutableStateOf("10, 25, 45, 30, 60, 35") }
    val dataPoints = remember(inputData) {
        inputData.split(",").mapNotNull { it.trim().toFloatOrNull() }
    }

    ToolScreen(
        title = "Data Lab",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = {
                inputData = List(6) { (10..100).random() }.joinToString(", ")
            }) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Mock Data")
            }
        }
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

                Card(modifier = Modifier.fillMaxWidth().height(250.dp)) {
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

                Spacer(modifier = Modifier.height(24.dp))

                DataStatsView(dataPoints)

            } else {
                Text("Enter numbers to see visualization")
            }
        }
    }
}

@Composable
fun DataStatsView(data: List<Float>) {
    val mean = data.average().toFloat()
    val variance = data.map { (it - mean) * (it - mean) }.average().toFloat()
    val stdDev = sqrt(variance)

    // Outliers (Z-score > 1.5 for this simple implementation)
    val outliers = data.filter { kotlin.math.abs(it - mean) > 1.5 * stdDev }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Statistics", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatRow("Mean", "%.2f".format(mean))
                StatRow("Std Dev", "%.2f".format(stdDev))
                StatRow("Regression (Slope)", "Simulated: 0.85")
                StatRow("Outliers Found", outliers.size.toString())
                if (outliers.isNotEmpty()) {
                    Text("Outliers: ${outliers.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}
