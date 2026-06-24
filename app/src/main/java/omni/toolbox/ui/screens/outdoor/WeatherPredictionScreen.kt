package omni.toolbox.ui.screens.outdoor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.*

@Composable
fun WeatherPredictionScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val pressureSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) }

    var currentPressure by remember { mutableFloatStateOf(1013.25f) }
    var pressureHistory by remember { mutableStateOf(mutableListOf<Pair<Long, Float>>()) }

    DisposableEffect(context) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    currentPressure = event.values[0]
                    val currentTime = System.currentTimeMillis()
                    if (pressureHistory.isEmpty() || abs(currentPressure - pressureHistory.last().second) > 0.05f) {
                        pressureHistory.add(currentTime to currentPressure)
                        // Keep 3 hours of history (3 * 60 * 60 * 1000 ms)
                        val threeHoursAgo = currentTime - (3 * 60 * 60 * 1000)
                        pressureHistory.removeAll { it.first < threeHoursAgo }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val pressureChange = if (pressureHistory.size > 1) {
        currentPressure - pressureHistory.first().second
    } else 0f

    val prediction = when {
        pressureChange < -3.0 -> "Rapidly Falling (Storm)"
        pressureChange < -1.5 -> "Falling (Deteriorating)"
        pressureChange < -0.5 -> "Slowly Falling"
        pressureChange > 3.0 -> "Rapidly Rising (Fair)"
        pressureChange > 1.5 -> "Rising (Improving)"
        pressureChange > 0.5 -> "Slowly Rising"
        else -> "Steady"
    }

    val forecastIcon = when {
        pressureChange < -1.5 -> Icons.Default.Warning
        pressureChange < -0.5 -> Icons.Default.Cloud
        pressureChange > 1.5 -> Icons.Default.WbSunny
        pressureChange > 0.5 -> Icons.Default.WbCloudy
        else -> Icons.Default.CloudQueue
    }

    ToolScreen(
        title = "Weather Prediction",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (pressureSensor == null) {
                Text("Barometer sensor not detected on this device.", color = MaterialTheme.colorScheme.error)
            }

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Pressure", style = MaterialTheme.typography.labelLarge)
                    Text("${String.format("%.2f", currentPressure)} hPa", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Forecast", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(
                            imageVector = forecastIcon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = if (pressureChange < -1.5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(prediction, style = MaterialTheme.typography.headlineSmall)
                    }
                    Text(
                        "Based on ${String.format("%.2f", pressureChange)} hPa change since start.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
