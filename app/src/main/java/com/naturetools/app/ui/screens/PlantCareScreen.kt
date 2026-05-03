package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Plant(val name: String, val water: String, val light: String, val tips: String)

@Composable
fun PlantCareScreen(navController: NavHostController) {
    val plants = listOf(
        Plant("Monstera Deliciosa", "Every 1-2 weeks", "Bright indirect light", "Wipe leaves to remove dust."),
        Plant("Snake Plant", "Every 2-8 weeks", "Low to bright light", "Thrives on neglect, do not overwater."),
        Plant("Spider Plant", "Weekly", "Bright indirect light", "Produces 'pups' that can be propagated."),
        Plant("Peace Lily", "Weekly", "Low to medium light", "Leaves droop when thirsty."),
        Plant("Aloe Vera", "Every 3 weeks", "Bright direct light", "Use well-draining succulent soil."),
        Plant("Fiddle Leaf Fig", "Weekly", "Bright indirect light", "Sensitive to being moved."),
        Plant("Pothos", "Every 1-2 weeks", "Low to bright light", "Great for hanging baskets."),
        Plant("ZZ Plant", "Every 3-4 weeks", "Low to bright light", "Excellent low-light survivor.")
    )

    ToolScreen(
        title = "Plant Care Guide",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(plants) { plant ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(plant.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Water: ${plant.water}", style = MaterialTheme.typography.bodyMedium)
                        Text("Light: ${plant.light}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pro Tip: ${plant.tips}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
