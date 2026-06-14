package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun BillingScreen(navController: NavHostController) {
    var clientName by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<Pair<String, Double>>() }
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }

    val total = items.sumOf { it.second }

    ToolScreen(title = "Billing & Invoices", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Client Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Add Item", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it },
                    label = { Text("Price") },
                    modifier = Modifier.weight(0.5f)
                )
            }

            Button(
                onClick = {
                    val price = itemPrice.toDoubleOrNull() ?: 0.0
                    if (itemName.isNotBlank() && price > 0) {
                        items.add(itemName to price)
                        itemName = ""
                        itemPrice = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add to Invoice")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Invoice Items", style = MaterialTheme.typography.titleMedium)
            items.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.first) },
                    trailingContent = { Text("$${item.second}") },
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Amount", fontWeight = FontWeight.Bold)
                    Text("$${java.lang.String.format("%.2f", total)}", fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { /* Export logic */ },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate PDF Invoice")
            }
        }
    }
}
