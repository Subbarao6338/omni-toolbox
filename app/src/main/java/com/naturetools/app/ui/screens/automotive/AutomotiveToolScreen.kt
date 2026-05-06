package com.naturetools.app.ui.screens.automotive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                else -> Text("Utility for $title")
            }
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
