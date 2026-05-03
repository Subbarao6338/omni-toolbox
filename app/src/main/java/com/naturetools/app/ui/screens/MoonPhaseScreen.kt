package com.naturetools.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.*
import kotlin.math.cos

@Composable
fun MoonPhaseScreen(navController: NavHostController) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Simplified Moon Phase Calculation (Conway's algorithm)
    val moonAge = remember(year, month, day) {
        var r = year % 100
        r %= 19
        if (r > 9) r -= 19
        r = ((r * 11) % 30) + month + day
        if (month < 3) r += 2
        r -= if (year < 2000) 4 else 8.3.toInt()
        r % 30
    }.toDouble()

    val phaseName = when {
        moonAge < 1.84566 -> "New Moon"
        moonAge < 5.53699 -> "Waxing Crescent"
        moonAge < 9.22831 -> "First Quarter"
        moonAge < 12.91963 -> "Waxing Gibbous"
        moonAge < 16.61096 -> "Full Moon"
        moonAge < 20.30228 -> "Waning Gibbous"
        moonAge < 23.99361 -> "Last Quarter"
        moonAge < 27.68493 -> "Waning Crescent"
        else -> "New Moon"
    }

    val illumination = (1 - cos(2 * Math.PI * moonAge / 30)) / 2 * 100

    ToolScreen(
        title = "Moon Phase",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(phaseName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Moon Age: ${String.format("%.1f", moonAge)} days", style = MaterialTheme.typography.bodyLarge)
            Text("Illumination: ${String.format("%.1f", illumination)}%", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            // Moon Drawing
            Box(
                modifier = Modifier.size(200.dp).background(Color.Black, shape = androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Draw the illuminated part
                    // This is a simplification
                    drawCircle(
                        color = Color(0xFFFFF9C4).copy(alpha = 0.3f),
                        radius = radius,
                        center = center
                    )

                    val fraction = (moonAge / 30.0)
                    // Visualizing phase (Simplified)
                    if (moonAge <= 15) {
                         // Waxing
                         val widthFactor = (moonAge / 7.5 - 1).toFloat()
                         // Path logic for crescent/gibbous would go here
                    }

                    // For now, let's just draw a simple representation
                    drawCircle(
                        color = Color(0xFFFFF9C4),
                        radius = radius * (illumination / 100).toFloat(),
                        center = center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Details", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Next Full Moon: In ${String.format("%.0f", (15 - moonAge + 30) % 30)} days")
                    Text("Next New Moon: In ${String.format("%.0f", (30 - moonAge) % 30)} days")
                }
            }
        }
    }
}
