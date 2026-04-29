package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.*

@Composable
fun TaxCalculatorScreen(navController: NavHostController) {
    var incomeInput by remember { mutableStateOf("") }
    var taxRateInput by remember { mutableStateOf("15") }

    val income = incomeInput.toDoubleOrNull() ?: 0.0
    val taxRate = (taxRateInput.toDoubleOrNull() ?: 0.0) / 100.0
    val taxAmount = income * taxRate
    val netIncome = income - taxAmount

    ToolScreen(
        title = "Tax Calculator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = incomeInput,
                onValueChange = { incomeInput = it },
                label = { Text("Annual Income") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = taxRateInput,
                onValueChange = { taxRateInput = it },
                label = { Text("Tax Rate (%)") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            ResultRow("Total Income", income)
            ResultRow("Tax Amount", taxAmount)
            ResultRow("Net Income", netIncome)
        }
    }
}

@Composable
private fun ResultRow(label: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(String.format(Locale.US, "$%.2f", value), style = MaterialTheme.typography.titleLarge)
    }
}
