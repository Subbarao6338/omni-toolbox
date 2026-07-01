package omni.toolbox.ui.screens.astronomy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun MoonPhaseScreen(navController: NavHostController) {
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    // Simplified Moon Phase Calculation (Conway's method)
    val r = year % 100
    val g = (r % 19) + 1
    val c = ((g * 11) - 14) % 30
    val d = (day + month + c + (if (year > 2000) -4 else 0)) % 30
    val age = if (d < 0) d + 30 else d

    val phaseName = when (age) {
        0, 1 -> "New Moon"
        in 2..6 -> "Waxing Crescent"
        7, 8 -> "First Quarter"
        in 9..13 -> "Waxing Gibbous"
        14, 15 -> "Full Moon"
        in 16..21 -> "Waning Gibbous"
        22, 23 -> "Last Quarter"
        else -> "Waning Crescent"
    }

    val illumination = if (age <= 15) (age / 15.0 * 100).toInt() else ((30 - age) / 15.0 * 100).toInt()

    ToolScreen(
        title = "Moon Phase",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(phaseName, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCircle(Color.DarkGray)

                val sweep = (age / 30.0 * 360.0).toFloat()
                if (age <= 15) {
                    // Waxing
                    drawArc(
                        color = Color.Yellow,
                        startAngle = -90f,
                        sweepAngle = 180f,
                        useCenter = true
                    )
                    val ratio = (age / 15.0).toFloat()
                    val width = size.width * (1f - ratio)
                    drawOval(
                        color = if (age < 7.5) Color.DarkGray else Color.Yellow,
                        topLeft = androidx.compose.ui.geometry.Offset((size.width - width) / 2f, 0f),
                        size = androidx.compose.ui.geometry.Size(width, size.height)
                    )
                } else {
                    // Waning
                    drawArc(
                        color = Color.Yellow,
                        startAngle = 90f,
                        sweepAngle = 180f,
                        useCenter = true
                    )
                    val ratio = ((age - 15) / 15.0).toFloat()
                    val width = size.width * ratio
                    drawOval(
                        color = if (age < 22.5) Color.Yellow else Color.DarkGray,
                        topLeft = androidx.compose.ui.geometry.Offset((size.width - width) / 2f, 0f),
                        size = androidx.compose.ui.geometry.Size(width, size.height)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Illumination: $illumination%", style = MaterialTheme.typography.bodyLarge)
            Text("Moon Age: $age days", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
