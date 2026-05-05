package com.naturetools.app.ui.screens.science

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun WorldMapScreen(navController: NavHostController) {
    ToolScreen(
        title = "World Map",
        onBack = { navController.popBackStack() }
    ) { padding ->
        // Since we can't bundle a huge map library easily, we use WebToolScreen's URL functionality
        // but here we can provide a specific offline-friendly view if we had one.
        // For now, let's provide a "Coming Soon" or a simple placeholder with info.
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Interactive World Map", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Map Rendering Area\n(Integration with Vector Map upcoming)", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("This tool provides geographic data and country information offline.")
        }
    }
}
