package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun CalorieCalculatorScreen(navController: NavHostController) {
    var weight by remember { mutableStateOf("70") }
    var height by remember { mutableStateOf("175") }
    var age by remember { mutableStateOf("25") }
    var isMale by remember { mutableStateOf(true) }

    val bmr = if (isMale) {
        88.362 + (13.397 * (weight.toDoubleOrNull() ?: 0.0)) + (4.799 * (height.toDoubleOrNull() ?: 0.0)) - (5.677 * (age.toDoubleOrNull() ?: 0.0))
    } else {
        447.593 + (9.247 * (weight.toDoubleOrNull() ?: 0.0)) + (3.098 * (height.toDoubleOrNull() ?: 0.0)) - (4.330 * (age.toDoubleOrNull() ?: 0.0))
    }

    ToolScreen(
        title = "Calorie Calculator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = isMale,
                    onClick = { isMale = true },
                    label = { Text("Male") },
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
                FilterChip(
                    selected = !isMale,
                    onClick = { isMale = false },
                    label = { Text("Female") },
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
            }

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Your BMR", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${bmr.toInt()} kcal/day",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        "Basal Metabolic Rate is the number of calories your body needs at rest.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text("Calorie Needs Estimation", style = MaterialTheme.typography.titleMedium)
            val levels = listOf(
                "Sedentary" to 1.2,
                "Lightly Active" to 1.375,
                "Moderately Active" to 1.55,
                "Very Active" to 1.725,
                "Extra Active" to 1.9
            )
            levels.forEach { (label, multiplier) ->
                ListItem(
                    headlineContent = { Text(label) },
                    trailingContent = { Text("${(bmr * multiplier).toInt()} kcal") },
                    supportingContent = { Text("Multiplier: $multiplier") }
                )
            }
        }
    }
}
