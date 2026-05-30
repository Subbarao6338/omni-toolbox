package com.naturetools.app.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun MacroSplitterScreen(navController: NavHostController) {
    var calories by remember { mutableStateOf("2000") }
    val cals = calories.toDoubleOrNull() ?: 2000.0

    ToolScreen(
        title = "Macro Splitter",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Macro Splitter (40/30/30)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Daily Calories") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            val macros = listOf(
                "Carbs (40%)" to (cals * 0.4 / 4),
                "Protein (30%)" to (cals * 0.3 / 4),
                "Fats (30%)" to (cals * 0.3 / 9)
            )

            macros.forEach { (label, grams) ->
                ListItem(
                    headlineContent = { Text(label) },
                    trailingContent = { Text("${grams.toInt()}g") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "This 40/30/30 split is a common ratio for balanced nutrition.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
