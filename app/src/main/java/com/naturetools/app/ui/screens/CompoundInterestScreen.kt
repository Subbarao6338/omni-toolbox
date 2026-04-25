package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.Locale
import kotlin.math.pow

@Composable
fun CompoundInterestScreen(navController: NavHostController) {
    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("12") }

    val total = try {
        val p = principal.toDoubleOrNull() ?: 0.0
        val r = (rate.toDoubleOrNull() ?: 0.0) / 100.0
        val t = time.toDoubleOrNull() ?: 0.0
        val n = frequency.toDoubleOrNull() ?: 1.0
        p * (1 + r / n).pow(n * t)
    } catch (e: Exception) {
        0.0
    }

    ToolScreen(title = "Compound Interest", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = principal,
                onValueChange = { principal = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Principal Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = rate,
                onValueChange = { rate = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Annual Rate (%)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = time,
                onValueChange = { time = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Time (Years)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Total Amount", style = MaterialTheme.typography.labelLarge)
                    Text(
                        String.format(Locale.US, "%.2f", total),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text("Interest Earned: " + String.format(Locale.US, "%.2f", total - (principal.toDoubleOrNull() ?: 0.0)))
                }
            }
        }
    }
}
