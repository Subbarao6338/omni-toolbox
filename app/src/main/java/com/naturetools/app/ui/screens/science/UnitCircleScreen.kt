package com.naturetools.app.ui.screens.science

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun UnitCircleScreen(navController: NavHostController) {
    var angleDeg by remember { mutableFloatStateOf(45f) }
    val angleRad = Math.toRadians(angleDeg.toDouble())

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    ToolScreen(title = "Unit Circle", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Visual Trigonometry", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(280.dp)) {
                    val center = this.center
                    val radius = size.minDimension / 2 - 20.dp.toPx()

                    // Axes
                    drawLine(Color.Gray, Offset(0f, center.y), Offset(size.width, center.y), 2f)
                    drawLine(Color.Gray, Offset(center.x, 0f), Offset(center.x, size.height), 2f)

                    // Circle
                    drawCircle(Color.LightGray, radius, center, style = Stroke(4f))

                    // Angle Line
                    val x = center.x + radius * cos(angleRad).toFloat()
                    val y = center.y - radius * sin(angleRad).toFloat()
                    drawLine(primaryColor, center, Offset(x, y), 8f)
                    drawCircle(secondaryColor, 12f, Offset(x, y))

                    // Projections
                    drawLine(Color.Red, Offset(x, y), Offset(x, center.y), 4f) // sin
                    drawLine(Color.Blue, center, Offset(x, center.y), 4f) // cos
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Slider(value = angleDeg, onValueChange = { angleDeg = it }, valueRange = 0f..360f)
            Text("Angle: ${angleDeg.toInt()}°", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("sin(θ)", color = Color.Red, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("%.4f".format(sin(angleRad)))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("cos(θ)", color = Color.Blue, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("%.4f".format(cos(angleRad)))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("tan(θ)", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(if (cos(angleRad) != 0.0) "%.4f".format(sin(angleRad)/cos(angleRad)) else "∞")
                    }
                }
            }
        }
    }
}
