package com.naturetools.app.ui.screens.environment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.components.AdjustmentSlider

@Composable
fun EnvironmentToolScreen(navController: NavHostController, title: String) {
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
            Icon(
                Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (title) {
                "Air Quality" -> {
                    Text("Current Air Quality Index (AQI)", style = MaterialTheme.typography.titleMedium)
                    Text("42", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Status: Good", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    AdjustmentSlider("Sensor Sensitivity", initialValue = 0.8f)
                }
                "UV Index" -> {
                    Text("Current UV Radiation Level", style = MaterialTheme.typography.titleMedium)
                    Text("Low (2)", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Sun protection is generally not needed.", style = MaterialTheme.typography.bodyMedium)
                }
                else -> {
                    Text("Environmental monitoring for $title")
                    AdjustmentSlider("Pollution Threshold", initialValue = 0.5f)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Refresh data */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Data")
            }
        }
    }
}
