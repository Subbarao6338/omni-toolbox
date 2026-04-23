package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.random.Random

@Composable
fun RandomGeneratorScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Number", "Dice", "Coin", "Password")

    ToolScreen(title = "Random Generator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                0 -> NumberGenerator()
                1 -> DiceRoller()
                2 -> CoinFlipper()
                3 -> PasswordGenerator()
            }
        }
    }
}

@Composable
fun NumberGenerator() {
    var min by remember { mutableStateOf("1") }
    var max by remember { mutableStateOf("100") }
    var result by remember { mutableStateOf<String?>(null) }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = min,
                onValueChange = { min = it.filter { c -> c.isDigit() || c == '-' } },
                label = { Text("Min") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) }
            )
            OutlinedTextField(
                value = max,
                onValueChange = { max = it.filter { c -> c.isDigit() || c == '-' } },
                label = { Text("Max") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) }
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
            Text("Generate Number")
        }

        result?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(it, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DiceRoller() {
    var result by remember { mutableStateOf<Int?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = Random.nextInt(1, 7) }) {
            Text("Roll Dice")
        }

        result?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Text(it.toString(), style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun CoinFlipper() {
    var result by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = if (Random.nextBoolean()) "Heads" else "Tails" }) {
            Text("Flip Coin")
        }

        result?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Text(it, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PasswordGenerator() {
    var length by remember { mutableFloatStateOf(12f) }
    var result by remember { mutableStateOf("") }

    Column {
        Text("Password Length: ${length.toInt()}", style = MaterialTheme.typography.labelLarge)
        Slider(
            value = length,
            onValueChange = { length = it },
            valueRange = 4f..32f,
            steps = 27
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-="
                result = (1..length.toInt())
                    .map { chars[Random.nextInt(chars.length)] }
                    .joinToString("")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Password")
        }

        if (result.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    result,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}
