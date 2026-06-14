package omni.toolbox.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun TipCalculatorScreen(navController: NavHostController) {
    var billAmount by remember { mutableStateOf("") }
    var tipPercentage by remember { mutableFloatStateOf(15f) }
    var splitCount by remember { mutableFloatStateOf(1f) }

    val bill = billAmount.toDoubleOrNull() ?: 0.0
    val tipAmount = bill * (tipPercentage / 100)
    val totalAmount = bill + tipAmount
    val amountPerPerson = if (splitCount > 0) totalAmount / splitCount else totalAmount

    ToolScreen(title = "Tip Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = billAmount,
                onValueChange = { billAmount = it },
                label = { Text("Bill Amount") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Tip: ${tipPercentage.toInt()}%", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = tipPercentage,
                onValueChange = { tipPercentage = it },
                valueRange = 0f..50f,
                steps = 10
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Split: ${splitCount.toInt()} person(s)", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = splitCount,
                onValueChange = { splitCount = it },
                valueRange = 1f..20f,
                steps = 19
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    ResultRow("Tip Amount", "$${"%.2f".format(tipAmount)}")
                    ResultRow("Total Amount", "$${"%.2f".format(totalAmount)}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text("Per Person", style = MaterialTheme.typography.labelLarge)
                    Text("$${"%.2f".format(amountPerPerson)}", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}
