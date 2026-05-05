package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.*
import kotlin.math.pow

@Composable
fun MortgageCalculatorScreen(navController: NavHostController) {
    var loanAmount by remember { mutableStateOf("250000") }
    var interestRate by remember { mutableStateOf("5.5") }
    var loanTerm by remember { mutableStateOf("30") }

    val p = loanAmount.toDoubleOrNull() ?: 0.0
    val r = (interestRate.toDoubleOrNull() ?: 0.0) / 100.0 / 12.0
    val n = (loanTerm.toDoubleOrNull() ?: 0.0) * 12.0

    val monthlyPayment = if (r > 0) {
        p * (r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
    } else {
        if (n > 0) p / n else 0.0
    }

    val totalPayment = monthlyPayment * n
    val totalInterest = totalPayment - p

    ToolScreen(
        title = "Mortgage Calculator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = { Text("Loan Amount ($)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = interestRate,
                onValueChange = { interestRate = it },
                label = { Text("Annual Interest Rate (%)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = loanTerm,
                onValueChange = { loanTerm = it },
                label = { Text("Loan Term (Years)") },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Monthly Payment", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = String.format(Locale.US, "$%.2f", monthlyPayment),
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Payment:")
                        Text(String.format(Locale.US, "$%.2f", totalPayment))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Interest:")
                        Text(String.format(Locale.US, "$%.2f", totalInterest))
                    }
                }
            }
        }
    }
}
