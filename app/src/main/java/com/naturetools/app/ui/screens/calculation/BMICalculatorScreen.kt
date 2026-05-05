package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.math.pow

@Composable
fun BMICalculatorScreen(navController: NavHostController) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<Float?>(null) }

    ToolScreen(title = "BMI Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val w = weight.toFloatOrNull()
                    val h = height.toFloatOrNull()
                    if (w != null && h != null && h > 0) {
                        bmiResult = w / (h / 100).pow(2)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate BMI")
            }

            bmiResult?.let { bmi ->
                Spacer(modifier = Modifier.height(32.dp))
                BMIResultCard(bmi)
            }
        }
    }
}

@Composable
fun BMIResultCard(bmi: Float) {
    val category = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25 -> "Normal"
        bmi < 30 -> "Overweight"
        else -> "Obese"
    }
    val color = when {
        bmi < 18.5 -> Color(0xFF2196F3) // Sky Blue
        bmi < 25 -> Color(0xFF4CAF50) // Leaf Green
        bmi < 30 -> Color(0xFFFFC107) // Earthy Yellow
        else -> Color(0xFFF44336) // Clay Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(2.dp, color)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your BMI", style = MaterialTheme.typography.titleMedium)
            Text("%.1f".format(bmi), style = MaterialTheme.typography.displayLarge, color = color)
            Text(category, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = (bmi / 40f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}
