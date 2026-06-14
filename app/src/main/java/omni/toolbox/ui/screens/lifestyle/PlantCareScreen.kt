package omni.toolbox.ui.screens.lifestyle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class Plant(val name: String, val lastWatered: String, val needsWater: Boolean)

@Composable
fun PlantCareScreen(navController: NavHostController) {
    val plants = remember { mutableStateListOf(
        Plant("Monstera", "2 days ago", false),
        Plant("Snake Plant", "10 days ago", true),
        Plant("Aloe Vera", "5 days ago", false)
    ) }

    ToolScreen(
        title = "Plant Care",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(plants) { plant ->
                ListItem(
                    leadingContent = { Icon(Icons.Default.Eco, contentDescription = null, tint = if (plant.needsWater) Color.Red else Color.Green) },
                    headlineContent = { Text(plant.name) },
                    supportingContent = { Text("Last watered: ${plant.lastWatered}") },
                    trailingContent = {
                        if (plant.needsWater) {
                            Button(onClick = { /* Water */ }) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null)
                                Text(" Water")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
