package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun FuelCostCalculatorScreen(navController: NavHostController) {
    var distance by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }
    var pricePerLiter by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf<Double?>(null) }

    ToolScreen(title = "Fuel Cost Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = distance,
                onValueChange = { distance = it },
                label = { Text("Distance (km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = efficiency,
                onValueChange = { efficiency = it },
                label = { Text("Efficiency (L/100km)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = pricePerLiter,
                onValueChange = { pricePerLiter = it },
                label = { Text("Price per Liter") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val d = distance.toDoubleOrNull()
                    val e = efficiency.toDoubleOrNull()
                    val p = pricePerLiter.toDoubleOrNull()
                    if (d != null && e != null && p != null) {
                        totalCost = (d / 100.0) * e * p
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LocalGasStation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculate Cost")
            }

            totalCost?.let { cost ->
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Estimated Fuel Cost", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "$%.2f".format(cost),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val fuelNeeded = (distance.toDoubleOrNull() ?: 0.0) / 100.0 * (efficiency.toDoubleOrNull() ?: 0.0)
                        Text("Fuel needed: %.2f L".format(fuelNeeded), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
