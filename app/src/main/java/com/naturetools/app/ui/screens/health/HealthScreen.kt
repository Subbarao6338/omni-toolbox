package com.naturetools.app.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.screens.audio.AdjustmentSlider

@Composable
fun HealthScreen(navController: NavHostController, title: String) {
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
                Icons.Default.HealthAndSafety,
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
                "Sleep Tracker" -> {
                    Text("Sleep Quality: 85%", style = MaterialTheme.typography.titleMedium)
                    Text("Last night: 7h 45m", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    AdjustmentSlider("Goal (Hours)", valueRange = 4f..12f, initialValue = 8f)
                }
                "Yoga Guide" -> {
                    Text("Current Pose: Mountain Pose", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Focus on your breath and stand tall.", style = MaterialTheme.typography.bodyMedium)
                }
                else -> {
                    Text("Health monitoring for $title")
                    AdjustmentSlider("Reminder Frequency", initialValue = 0.5f)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Specialized action */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Session")
            }
        }
    }
}
