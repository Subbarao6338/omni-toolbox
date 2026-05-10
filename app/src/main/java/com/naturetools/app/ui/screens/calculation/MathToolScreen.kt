package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.screens.audio.AdjustmentSlider

@Composable
fun MathToolScreen(navController: NavHostController, title: String) {
    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Advanced Calculation: $title",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = input1,
                onValueChange = { input1 = it },
                label = { Text("Primary Input / Equation") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            if (title != "Equation Solver" && title != "Stats Pro" && title != "Truth Table Gen") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = input2,
                    onValueChange = { input2 = it },
                    label = { Text("Secondary Input / Constraint") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Calculation Parameters", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    when (title) {
                        "Matrix Calc" -> {
                            AdjustmentSlider("Matrix Size (N x N)", valueRange = 2f..10f, initialValue = 3f)
                            AdjustmentSlider("Precision (Decimals)", valueRange = 0f..8f, initialValue = 2f)
                        }
                        "Equation Solver" -> {
                            AdjustmentSlider("Iteration Limit", valueRange = 100f..5000f, initialValue = 1000f)
                            AdjustmentSlider("Tolerance (1e-X)", valueRange = 1f..12f, initialValue = 6f)
                        }
                        "Fraction Calc" -> {
                            Text("Mode: Simplification / Addition / Division")
                            AdjustmentSlider("Mixed Number Support (0/1)", initialValue = 1f)
                        }
                        "Truth Table Gen" -> {
                            AdjustmentSlider("Variables Count", valueRange = 1f..6f, initialValue = 3f)
                            Text("Logic: AND, OR, NOT, XOR, NAND")
                        }
                        "Statistics Pro" -> {
                            AdjustmentSlider("Confidence Interval (%)", valueRange = 80f..99.9f, initialValue = 95f)
                            Text("Functions: Mean, Median, StdDev, Regression")
                        }
                        "Binary Calc" -> {
                            AdjustmentSlider("Bit Length", valueRange = 8f..64f, initialValue = 32f)
                            Text("Mode: Two's Complement / Unsigned")
                        }
                        else -> {
                            AdjustmentSlider("Algorithm Complexity")
                            AdjustmentSlider("Precision")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    result = "Computed result for $title based on inputs. (Simulated)"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solve")
            }

            if (result != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Result", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result!!,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            TextButton(onClick = { /* Copy */ }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy")
                            }
                            TextButton(onClick = { /* Share */ }) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }
    }
}
