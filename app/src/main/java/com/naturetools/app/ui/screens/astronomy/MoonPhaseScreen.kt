package com.naturetools.app.ui.screens.astronomy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun MoonPhaseScreen(navController: NavHostController) {
    ToolScreen(
        title = "Moon Phase",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Waxing Gibbous", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(Color.DarkGray)
                drawArc(
                    color = Color.Yellow,
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = true
                )
                // Simulated phase shadow
                drawOval(
                    color = Color.Yellow,
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.15f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.7f, size.height)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Illumination: 82%", style = MaterialTheme.typography.bodyLarge)
            Text("Moon Age: 11.2 days", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
