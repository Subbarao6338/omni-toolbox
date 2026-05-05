package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*

@Composable
fun ScientificCalculatorScreen(navController: NavHostController) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var history by remember { mutableStateOf(listOf<String>()) }

    val buttons = listOf(
        "sin", "cos", "tan", "log",
        "ln", "log10", "!", "(",
        ")", "sqrt", "^", "π",
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "−",
        "C", "0", "=", "+"
    )

    ToolScreen(
        title = "Scientific Calculator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        maxLines = 2
                    )
                    Text(
                        text = result,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isNotEmpty()) {
                Text(
                    "History: ${history.takeLast(3).joinToString(" | ")}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(2.5f)
            ) {
                items(buttons) { btn ->
                    Button(
                        onClick = {
                            when (btn) {
                                "C" -> {
                                    expression = ""
                                    result = "0"
                                }
                                "=" -> {
                                    try {
                                        var evalExpr = expression
                                            .replace("×", "*")
                                            .replace("÷", "/")
                                            .replace("−", "-")
                                            .replace("π", "PI")
                                            .replace("e", "E")

                                        // Handle factorial (very basic implementation for positive integers)
                                        if (evalExpr.contains("!")) {
                                            val regex = "(\\d+)!".toRegex()
                                            evalExpr = regex.replace(evalExpr) { match ->
                                                val n = match.groupValues[1].toLong()
                                                var fact = 1L
                                                for (i in 1..n) fact *= i
                                                fact.toString()
                                            }
                                        }

                                        val e = ExpressionBuilder(evalExpr).build()
                                        val res = e.evaluate()
                                        val formattedResult = if (res % 1 == 0.0) res.toLong().toString() else String.format(Locale.US, "%.4f", res)
                                        history = (history + "$expression = $formattedResult").takeLast(10)
                                        result = formattedResult
                                    } catch (e: Exception) {
                                        result = "Error"
                                    }
                                }
                                else -> {
                                    expression += btn
                                }
                            }
                        },
                        modifier = Modifier.aspectRatio(1.2f),
                        colors = if (btn == "=") ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        else if (btn in listOf("C", "÷", "×", "−", "+")) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(btn, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
