package com.naturetools.app.ui.screens.electronics

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
fun ElectronicsToolScreen(navController: NavHostController, title: String) {
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
                "Ohm's Law" -> OhmsLawCalculator()
                else -> Text("Utility for $title")
            }
        }
    }
}

@Composable
fun OhmsLawCalculator() {
    var voltage by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    var resistance by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = voltage,
            onValueChange = { voltage = it },
            label = { Text("Voltage (V)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = current,
            onValueChange = { current = it },
            label = { Text("Current (A)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = resistance,
            onValueChange = { resistance = it },
            label = { Text("Resistance (Ω)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val v = voltage.toDoubleOrNull()
        val i = current.toDoubleOrNull()
        val r = resistance.toDoubleOrNull()

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calculated Results:", style = MaterialTheme.typography.titleMedium)
                if (v != null && i != null) Text("Resistance: ${v / i} Ω")
                else if (v != null && r != null) Text("Current: ${v / r} A")
                else if (i != null && r != null) Text("Voltage: ${i * r} V")
                else Text("Enter two values to calculate the third.")
            }
        }
    }
}
