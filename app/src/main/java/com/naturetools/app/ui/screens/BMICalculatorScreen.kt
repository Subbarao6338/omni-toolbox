package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        bmi < 18.5 -> MaterialTheme.colorScheme.tertiary
        bmi < 25 -> MaterialTheme.colorScheme.primary
        bmi < 30 -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your BMI", style = MaterialTheme.typography.titleMedium)
            Text("%.1f".format(bmi), style = MaterialTheme.typography.displayLarge, color = color)
            Text(category, style = MaterialTheme.typography.headlineSmall, color = color)

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = (bmi / 40f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = color
            )
        }
    }
}
