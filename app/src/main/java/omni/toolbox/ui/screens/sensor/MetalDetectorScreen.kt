package omni.toolbox.ui.screens.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.sqrt

@Composable
fun MetalDetectorScreen(navController: NavHostController) {
    val context = LocalContext.current
    var magneticFieldStrength by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val magnetometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }

    DisposableEffect(sensorManager, magnetometer) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    magneticFieldStrength = sqrt(x * x + y * y + z * z)
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val animatedStrength by animateFloatAsState(targetValue = magneticFieldStrength)

    ToolScreen(title = "Metal Detector", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CompassCalibration,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = if (magneticFieldStrength > 100) Color.Red else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("${magneticFieldStrength.toInt()} µT", style = MaterialTheme.typography.displayLarge)
            Text("Magnetic Field Strength", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(48.dp))

            LinearProgressIndicator(
                progress = { (animatedStrength / 200f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(0.8f).height(16.dp),
                color = if (magneticFieldStrength > 100) Color.Red else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                if (magneticFieldStrength > 100) "Metal Detected!" else "Scanning...",
                style = MaterialTheme.typography.titleLarge,
                color = if (magneticFieldStrength > 100) Color.Red else MaterialTheme.colorScheme.outline
            )
        }
    }
}
