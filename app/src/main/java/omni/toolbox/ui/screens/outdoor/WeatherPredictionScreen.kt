package omni.toolbox.ui.screens.outdoor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.util.*
import kotlin.math.*

data class PressureReading(val value: Float, val timestamp: Long)

@Composable
fun WeatherPredictionScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val pressureSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) }

    var currentPressure by remember { mutableFloatStateOf(1013.25f) }
    var pressureHistory by remember { mutableStateOf(mutableListOf<PressureReading>()) }

    DisposableEffect(context) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    currentPressure = event.values[0]
                    val now = System.currentTimeMillis()
                    if (pressureHistory.isEmpty() || abs(currentPressure - pressureHistory.last().value) > 0.05f) {
                        pressureHistory.add(PressureReading(currentPressure, now))
                        // Keep 24 hours of data roughly
                        if (pressureHistory.size > 1000) pressureHistory.removeAt(0)
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

    // 3-hour pressure tendency calculation
    val threeHoursAgo = System.currentTimeMillis() - 3 * 60 * 60 * 1000
    val startReading = pressureHistory.findLast { it.timestamp <= threeHoursAgo } ?: pressureHistory.firstOrNull()
    val pressureChange = if (startReading != null) currentPressure - startReading.value else 0f

    val prediction = when {
        pressureChange <= -6.0 -> "Storm Warning (Rapid Fall)"
        pressureChange <= -3.0 -> "Gale Warning (Fast Fall)"
        pressureChange <= -1.5 -> "Deteriorating (Steady Fall)"
        pressureChange <= -0.5 -> "Slightly Unsettled"
        pressureChange >= 6.0 -> "Rapid Rise (Fair Weather)"
        pressureChange >= 3.0 -> "Improving (Fast Rise)"
        pressureChange >= 1.5 -> "Fair (Steady Rise)"
        pressureChange >= 0.5 -> "Slightly Improving"
        else -> "Stable / No Change"
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            if (pressureSensor == null) {
                Text("Barometer sensor not detected on this device.", color = MaterialTheme.colorScheme.error)
            }

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Barometric Pressure", style = MaterialTheme.typography.labelLarge)
                    Text("${String.format("%.2f", currentPressure)} hPa", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("3-Hour Tendency Forecast", style = MaterialTheme.typography.labelLarge)
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "ΔP (3h): ${String.format("%.2f", pressureChange)} hPa",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (abs(pressureChange) > 1.5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Meteorological standard used for tendency analysis.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("History (Last ${pressureHistory.size} readings)", style = MaterialTheme.typography.titleSmall)
            // Here you could add a chart
        }
    }
}
