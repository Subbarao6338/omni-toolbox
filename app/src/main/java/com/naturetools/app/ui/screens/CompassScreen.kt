package com.naturetools.app.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun CompassScreen(navController: NavHostController) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    val rotation by animateFloatAsState(targetValue = -azimuth)

    ToolScreen(title = "Compass", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(240.dp)) {
                    val center = this.center
                    // Circle
                    drawCircle(Color.Gray, radius = size.minDimension / 2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx()))

                    rotate(rotation) {
                        // North
                        drawLine(Color.Red, center, center.copy(y = center.y - 100.dp.toPx()), strokeWidth = 8.dp.toPx())
                        // South
                        drawLine(Color.Black, center, center.copy(y = center.y + 100.dp.toPx()), strokeWidth = 8.dp.toPx())
                    }
                }
                Text("N", modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp), style = MaterialTheme.typography.titleLarge, color = Color.Red)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("${azimuth.toInt()}°", style = MaterialTheme.typography.displayMedium)
        }
    }
}
