package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun DiscountCalculatorScreen(navController: NavHostController) {
    var originalPrice by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("") }

    val price = originalPrice.toDoubleOrNull() ?: 0.0
    val discount = discountPercent.toDoubleOrNull() ?: 0.0
    val tax = taxPercent.toDoubleOrNull() ?: 0.0

    val savings = price * (discount / 100)
    val discountedPrice = price - savings
    val taxAmount = discountedPrice * (tax / 100)
    val finalPrice = discountedPrice + taxAmount

    ToolScreen(title = "Discount Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = originalPrice,
                onValueChange = { originalPrice = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Original Price") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = discountPercent,
                    onValueChange = { discountPercent = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Discount") },
                    modifier = Modifier.weight(1f),
                    suffix = { Text("%") }
                )
                OutlinedTextField(
                    value = taxPercent,
                    onValueChange = { taxPercent = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Tax") },
                    modifier = Modifier.weight(1f),
                    suffix = { Text("%") }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ResultRow("Savings", "-$${java.lang.String.format(java.util.Locale.US, "%.2f", savings)}")
                    ResultRow("Tax Amount", "+$${java.lang.String.format(java.util.Locale.US, "%.2f", taxAmount)}")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ResultRow("Final Price", "$${java.lang.String.format(java.util.Locale.US, "%.2f", finalPrice)}", isBold = true)
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = if (isBold) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge)
    }
}
