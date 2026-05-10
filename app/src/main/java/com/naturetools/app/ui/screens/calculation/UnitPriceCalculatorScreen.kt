package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Product(val name: String, val price: Double, val quantity: Double) {
    val unitPrice: Double get() = if (quantity > 0) price / quantity else 0.0
}

@Composable
fun UnitPriceCalculatorScreen(navController: NavHostController) {
    var nameA by remember { mutableStateOf("Product A") }
    var priceA by remember { mutableStateOf("") }
    var quantityA by remember { mutableStateOf("") }

    var nameB by remember { mutableStateOf("Product B") }
    var priceB by remember { mutableStateOf("") }
    var quantityB by remember { mutableStateOf("") }

    val productA = Product(nameA, priceA.toDoubleOrNull() ?: 0.0, quantityA.toDoubleOrNull() ?: 0.0)
    val productB = Product(nameB, priceB.toDoubleOrNull() ?: 0.0, quantityB.toDoubleOrNull() ?: 0.0)

    val bestValue = when {
        productA.unitPrice == 0.0 && productB.unitPrice == 0.0 -> null
        productA.unitPrice == 0.0 -> productB
        productB.unitPrice == 0.0 -> productA
        productA.unitPrice < productB.unitPrice -> productA
        productA.unitPrice > productB.unitPrice -> productB
        else -> null
    }

    ToolScreen(
        title = "Unit Price Comparison",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProductInputCard(
                title = "Item 1",
                name = nameA,
                onNameChange = { nameA = it },
                price = priceA,
                onPriceChange = { priceA = it },
                quantity = quantityA,
                onQuantityChange = { quantityA = it },
                unitPrice = productA.unitPrice,
                isBest = bestValue == productA && productA.unitPrice != productB.unitPrice
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProductInputCard(
                title = "Item 2",
                name = nameB,
                onNameChange = { nameB = it },
                price = priceB,
                onPriceChange = { priceB = it },
                quantity = quantityB,
                onQuantityChange = { quantityB = it },
                unitPrice = productB.unitPrice,
                isBest = bestValue == productB && productA.unitPrice != productB.unitPrice
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (bestValue != null && productA.unitPrice != productB.unitPrice) {
                val savings = (kotlin.math.abs(productA.unitPrice - productB.unitPrice) / kotlin.math.max(productA.unitPrice, productB.unitPrice)) * 100
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Savings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${bestValue.name} is ${java.lang.String.format("%.1f", savings)}% cheaper per unit!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductInputCard(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    unitPrice: Double,
    isBest: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isBest) CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)) else CardDefaults.elevatedCardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                if (isBest) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Best Value") },
                        icon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = price,
                    onValueChange = onPriceChange,
                    label = { Text("Price") },
                    modifier = Modifier.weight(1f),
                    prefix = { Text("$") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Quantity") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Unit Price: $${java.lang.String.format("%.4f", unitPrice)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
