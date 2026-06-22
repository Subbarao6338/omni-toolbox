package omni.toolbox.ui.screens.environment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.ui.components.AdjustmentSlider

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
                    Text("${(20..150).random()}", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Status: Good", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("PM2.5: 12.4 µg/m³")
                            Text("PM10: 21.0 µg/m³")
                            Text("NO2: 8.5 ppb")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    AdjustmentSlider("Sensor Sensitivity", initialValue = 0.8f)
                }
                "UV Index" -> {
                    val uv = (0..11).random()
                    Text("Current UV Radiation Level", style = MaterialTheme.typography.titleMedium)
                    Text(if (uv < 3) "Low ($uv)" else if (uv < 6) "Moderate ($uv)" else "High ($uv)", style = MaterialTheme.typography.displaySmall, color = if (uv > 5) Color.Red else MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(if (uv < 3) "Sun protection is generally not needed." else "Wear sunscreen and seek shade.", style = MaterialTheme.typography.bodyMedium)
                }
                "Rain Radar" -> {
                    Text("Local Precipitation Map", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.DarkGray, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Text("📡 Radar Stream Active", color = Color.Green)
                    }
                    Text("No significant rain detected in your 50km radius.", modifier = Modifier.padding(16.dp))
                }
                "Moon Phase" -> {
                    Text("Lunar Cycle", style = MaterialTheme.typography.titleMedium)
                    Text("Waxing Gibbous", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(androidx.compose.material.icons.Icons.Default.Brightness3, null, modifier = Modifier.size(80.dp))
                    Text("Illumination: 78%", modifier = Modifier.padding(top = 16.dp))
                    Text("Next Full Moon: 4 days")
                }
                "Tides" -> {
                    Text("Tidal Information", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    listOf("High Tide" to "04:12 AM (1.8m)", "Low Tide" to "10:45 AM (0.2m)", "High Tide " to "04:56 PM (1.6m)").forEach { (t, time) ->
                         ListItem(headlineContent = { Text(t) }, trailingContent = { Text(time) })
                    }
                }
                "Light Pollution" -> {
                    Text("Bortle Scale Class", style = MaterialTheme.typography.titleMedium)
                    Text("Class 4", style = MaterialTheme.typography.displaySmall, color = Color.Yellow)
                    Text("Rural/Suburban Transition Sky", style = MaterialTheme.typography.bodyLarge)
                    Text("SQM: 20.45 mag/arcsec²", modifier = Modifier.padding(top = 8.dp))
                }
                "Weather Forecast", "weather_forecast" -> {
                    Text("Barometric Pressure Trend", style = MaterialTheme.typography.titleMedium)
                    Text("1012 hPa", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trend: Falling slowly", color = MaterialTheme.colorScheme.secondary)

                    Spacer(modifier = Modifier.height(24.dp))
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Trail Sense Prediction:", fontWeight = FontWeight.Bold)
                            Text("Scattered showers possible within 12-24 hours based on pressure changes.")
                        }
                    }
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
