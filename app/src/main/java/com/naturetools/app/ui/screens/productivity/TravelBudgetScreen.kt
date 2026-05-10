package com.naturetools.app.ui.screens.productivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Expense(val description: String, val amount: Double)

@Composable
fun TravelBudgetScreen(navController: NavHostController) {
    var budget by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expenses by remember { mutableStateOf(listOf<Expense>()) }

    val totalExpenses = expenses.sumOf { it.amount }
    val remaining = (budget.toDoubleOrNull() ?: 0.0) - totalExpenses

    ToolScreen(
        title = "Travel Budgeter",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { expenses = emptyList(); budget = "" }) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = budget,
                onValueChange = { budget = it },
                label = { Text("Total Budget") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Spent", style = MaterialTheme.typography.labelMedium)
                        Text("$${java.lang.String.format("%.2f", totalExpenses)}", style = MaterialTheme.typography.titleLarge)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Remaining", style = MaterialTheme.typography.labelMedium)
                        Text("$${java.lang.String.format("%.2f", remaining)}", style = MaterialTheme.typography.titleLarge, color = if (remaining < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Add Expense", style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.width(100.dp),
                    prefix = { Text("$") }
                )
                IconButton(onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (description.isNotEmpty() && amt != null) {
                        expenses = expenses + Expense(description, amt)
                        description = ""
                        amount = ""
                    }
                }) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(expenses) { expense ->
                    ListItem(
                        headlineContent = { Text(expense.description) },
                        trailingContent = { Text("$${java.lang.String.format("%.2f", expense.amount)}") },
                        leadingContent = { Icon(Icons.Default.ReceiptLong, contentDescription = null) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
