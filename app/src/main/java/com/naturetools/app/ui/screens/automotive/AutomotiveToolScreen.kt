package com.naturetools.app.ui.screens.automotive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun AutomotiveToolScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
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
            when (title) {
                "Fuel Consumption" -> FuelConsumptionCalculator()
                "Speedometer" -> SpeedometerScreen()
                "Car Maintenance" -> CarMaintenanceScreen()
                else -> Text("Utility for $title")
            }
        }
    }
}

@Composable
fun SpeedometerScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(120.dp), tint = MaterialTheme.colorScheme.primary)
        Text("0", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)
        Text("km/h", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(32.dp))
        Text("Location & Sensor permission required for real-time tracking.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun CarMaintenanceScreen() {
    val items = listOf("Oil Change", "Tire Rotation", "Brake Inspection", "Air Filter", "Spark Plugs", "Battery Check")
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Recommended Maintenance Schedule", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item) },
                supportingContent = { Text("Every 5,000 - 10,000 km") },
                trailingContent = { Checkbox(checked = false, onCheckedChange = {}) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun FuelConsumptionCalculator() {
    var distance by remember { mutableStateOf("") }
    var fuelUsed by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = distance,
            onValueChange = { distance = it },
            label = { Text("Distance (km)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = fuelUsed,
            onValueChange = { fuelUsed = it },
            label = { Text("Fuel Used (L)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val d = distance.toDoubleOrNull()
        val f = fuelUsed.toDoubleOrNull()

        if (d != null && f != null && d > 0) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Result:", style = MaterialTheme.typography.titleMedium)
                    Text("${(f / d) * 100} L/100km")
                }
            }
        }
    }
}
