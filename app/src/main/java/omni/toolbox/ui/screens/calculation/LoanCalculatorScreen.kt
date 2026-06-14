package omni.toolbox.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.util.Locale
import kotlin.math.pow

@Composable
fun LoanCalculatorScreen(navController: NavHostController) {
    var amount by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }
    var years by remember { mutableStateOf("") }

    val monthlyPayment = try {
        val p = amount.toDoubleOrNull() ?: 0.0
        val r = (interest.toDoubleOrNull() ?: 0.0) / 100.0 / 12.0
        val n = (years.toDoubleOrNull() ?: 0.0) * 12.0
        if (r > 0 && n > 0) {
            (p * r * (1 + r).pow(n)) / ((1 + r).pow(n) - 1)
        } else if (n > 0) {
            p / n
        } else {
            0.0
        }
    } catch (e: Exception) {
        0.0
    }

    ToolScreen(title = "Loan Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Loan Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = interest,
                onValueChange = { interest = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Annual Interest Rate (%)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = years,
                onValueChange = { years = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Loan Term (Years)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Monthly Payment", style = MaterialTheme.typography.labelLarge)
                    Text(
                        String.format(Locale.US, "%.2f", monthlyPayment),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
