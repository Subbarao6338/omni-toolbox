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
fun UnitPriceCalculatorScreen(navController: NavHostController) {
    var price1 by remember { mutableStateOf("") }
    var quantity1 by remember { mutableStateOf("") }
    var price2 by remember { mutableStateOf("") }
    var quantity2 by remember { mutableStateOf("") }

    val unitPrice1 = (price1.toDoubleOrNull() ?: 0.0) / (quantity1.toDoubleOrNull() ?: 1.0)
    val unitPrice2 = (price2.toDoubleOrNull() ?: 0.0) / (quantity2.toDoubleOrNull() ?: 1.0)

    ToolScreen(
        title = "Unit Price Calc",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Compare two items to find the better deal.")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Item 1", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(value = price1, onValueChange = { price1 = it }, label = { Text("Price") })
                    OutlinedTextField(value = quantity1, onValueChange = { quantity1 = it }, label = { Text("Quantity") })
                    Text(String.format(Locale.US, "Unit Price: %.4f", unitPrice1))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Item 2", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(value = price2, onValueChange = { price2 = it }, label = { Text("Price") })
                    OutlinedTextField(value = quantity2, onValueChange = { quantity2 = it }, label = { Text("Quantity") })
                    Text(String.format(Locale.US, "Unit Price: %.4f", unitPrice2))
                }
            }

            if (price1.isNotEmpty() && price2.isNotEmpty()) {
                val betterDeal = if (unitPrice1 < unitPrice2) "Item 1 is cheaper!" else if (unitPrice2 < unitPrice1) "Item 2 is cheaper!" else "Both items are the same price."
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(betterDeal, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
