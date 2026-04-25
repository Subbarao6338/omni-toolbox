package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(navController: NavHostController, viewModel: CalculatorViewModel = viewModel()) {
    ToolScreen(title = "Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Display and History
            Surface(
                modifier = Modifier.fillMaxWidth().weight(0.4f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        items(viewModel.history) { entry ->
                            Text(entry, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxWidth()) {
                        Text(viewModel.display.ifEmpty { "0" }, style = MaterialTheme.typography.displayMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scientific Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("sin", "cos", "tan", "log", "√", "eˣ", "x²", "abs", "π").forEach { op ->
                    FilledTonalButton(
                        onClick = { viewModel.onScientific(op) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(op, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Buttons
            val buttons = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("C", "0", "=", "+"),
                listOf("mod", "pow", ".", "AC")
            )
            buttons.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { label ->
                        Button(
                            onClick = {
                                when (label) {
                                    "C" -> viewModel.clear()
                                    "AC" -> {
                                        viewModel.clear()
                                        viewModel.clearHistory()
                                    }
                                    "=" -> viewModel.calculate()
                                    "+", "-", "*", "/", "mod", "pow" -> viewModel.onOperator(label)
                                    "." -> viewModel.onDigit(".")
                                    else -> viewModel.onDigit(label)
                                }
                            },
                            modifier = Modifier.weight(1f).aspectRatio(if (buttons.size > 4) 1.5f else 1.2f),
                            colors = if (label in "+-*/=modpow") ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                     else if (label == "C" || label == "AC") ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                     else ButtonDefaults.filledTonalButtonColors(),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(label, style = if (label.length > 2) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
