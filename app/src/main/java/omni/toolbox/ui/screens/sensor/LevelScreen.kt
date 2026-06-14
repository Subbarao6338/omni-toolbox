package omni.toolbox.ui.screens.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.abs

@Composable
fun LevelScreen(navController: NavHostController) {
    val context = LocalContext.current
    var xTilt by remember { mutableFloatStateOf(0f) }
    var yTilt by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    DisposableEffect(sensorManager, accelSensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    xTilt = event.values[0]
                    yTilt = event.values[1]
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    ToolScreen(title = "Level", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = this.center
                    val radius = size.minDimension / 2

                    // Outer circle
                    drawCircle(Color.Gray, radius = radius, style = androidx.compose.ui.graphics.drawscope.Stroke(4.dp.toPx()))
                    // Inner crosshair
                    drawLine(Color.LightGray, start = center.copy(x = center.x - 20.dp.toPx()), end = center.copy(x = center.x + 20.dp.toPx()), strokeWidth = 2.dp.toPx())
                    drawLine(Color.LightGray, start = center.copy(y = center.y - 20.dp.toPx()), end = center.copy(y = center.y + 20.dp.toPx()), strokeWidth = 2.dp.toPx())

                    // Bubble
                    val bubbleX = center.x - (xTilt * 20.dp.toPx())
                    val bubbleY = center.y + (yTilt * 20.dp.toPx())

                    val isLevel = abs(xTilt) < 0.5f && abs(yTilt) < 0.5f
                    drawCircle(if (isLevel) Color.Green else Color.Red, radius = 20.dp.toPx(), center = androidx.compose.ui.geometry.Offset(bubbleX, bubbleY))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("X: ${"%.1f".format(xTilt)}  Y: ${"%.1f".format(yTilt)}", style = MaterialTheme.typography.headlineMedium)
            if (abs(xTilt) < 0.5f && abs(yTilt) < 0.5f) {
                Text("Level!", color = Color.Green, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
