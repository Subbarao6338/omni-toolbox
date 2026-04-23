package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.random.Random

@Composable
fun RandomGeneratorScreen(navController: NavHostController) {
    var min by remember { mutableStateOf("1") }
    var max by remember { mutableStateOf("100") }
    var result by remember { mutableStateOf<String?>(null) }

    ToolScreen(title = "Random Generator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = min,
                    onValueChange = { min = it.filter { c -> c.isDigit() || c == '-' } },
                    label = { Text("Min") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = max,
                    onValueChange = { max = it.filter { c -> c.isDigit() || c == '-' } },
                    label = { Text("Max") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val start = min.toIntOrNull() ?: 1
                    val end = max.toIntOrNull() ?: 100
                    result = if (start < end) {
                        Random.nextInt(start, end + 1).toString()
                    } else {
                        "Invalid range"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate")
            }

            result?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(it, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
