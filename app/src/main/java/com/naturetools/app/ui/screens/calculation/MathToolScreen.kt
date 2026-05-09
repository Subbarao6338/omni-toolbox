package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun MathToolScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "Matrix Calc" -> MatrixCalculator()
                "Equation Solver" -> EquationSolver()
                "Fraction Calc" -> FractionCalculator()
                else -> Text("Math Utility for $title")
            }
        }
    }
}

@Composable
fun MatrixCalculator() {
    var a11 by remember { mutableStateOf("1") }
    var a12 by remember { mutableStateOf("0") }
    var a21 by remember { mutableStateOf("0") }
    var a22 by remember { mutableStateOf("1") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("2x2 Determinant", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = a11, onValueChange = { a11 = it }, modifier = Modifier.weight(1f), label = { Text("a11") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = a12, onValueChange = { a12 = it }, modifier = Modifier.weight(1f), label = { Text("a12") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = a21, onValueChange = { a21 = it }, modifier = Modifier.weight(1f), label = { Text("a21") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = a22, onValueChange = { a22 = it }, modifier = Modifier.weight(1f), label = { Text("a22") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }

        Spacer(modifier = Modifier.height(16.dp))

        val val11 = a11.toDoubleOrNull() ?: 0.0
        val val12 = a12.toDoubleOrNull() ?: 0.0
        val val21 = a21.toDoubleOrNull() ?: 0.0
        val val22 = a22.toDoubleOrNull() ?: 0.0
        val det = (val11 * val22) - (val12 * val21)

        Card(modifier = Modifier.fillMaxWidth()) {
            Text("Determinant = $det", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun EquationSolver() {
    // ax + b = c
    var a by remember { mutableStateOf("1") }
    var b by remember { mutableStateOf("0") }
    var c by remember { mutableStateOf("0") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Linear Equation (ax + b = c)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("a") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = b, onValueChange = { b = it }, label = { Text("b") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = c, onValueChange = { c = it }, label = { Text("c") }, modifier = Modifier.fillMaxWidth())

        val valA = a.toDoubleOrNull() ?: 1.0
        val valB = b.toDoubleOrNull() ?: 0.0
        val valC = c.toDoubleOrNull() ?: 0.0

        Spacer(modifier = Modifier.height(16.dp))
        if (valA != 0.0) {
            val x = (valC - valB) / valA
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("x = $x", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            Text("Invalid Equation (a cannot be 0)")
        }
    }
}

@Composable
fun FractionCalculator() {
    var n1 by remember { mutableStateOf("1") }
    var d1 by remember { mutableStateOf("2") }
    var n2 by remember { mutableStateOf("1") }
    var d2 by remember { mutableStateOf("3") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Fraction Addition", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.width(60.dp)) {
                OutlinedTextField(value = n1, onValueChange = { n1 = it })
                Divider()
                OutlinedTextField(value = d1, onValueChange = { d1 = it })
            }
            Text("+", modifier = Modifier.padding(horizontal = 8.dp))
            Column(modifier = Modifier.width(60.dp)) {
                OutlinedTextField(value = n2, onValueChange = { n2 = it })
                Divider()
                OutlinedTextField(value = d2, onValueChange = { d2 = it })
            }
        }

        val num1 = n1.toLongOrNull() ?: 1L
        val den1 = d1.toLongOrNull() ?: 2L
        val num2 = n2.toLongOrNull() ?: 1L
        val den2 = d2.toLongOrNull() ?: 3L

        if (den1 != 0L && den2 != 0L) {
            val resNum = (num1 * den2) + (num2 * den1)
            val resDen = den1 * den2
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Text("Result = $resNum / $resDen", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}
