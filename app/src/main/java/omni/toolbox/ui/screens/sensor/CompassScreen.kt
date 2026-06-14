package omni.toolbox.ui.screens.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassScreen(navController: NavHostController) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }

    DisposableEffect(sensorManager, rotationSensor) {
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
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val rotation by animateFloatAsState(targetValue = -azimuth)

    val directions = remember(azimuth) {
        val normalized = (azimuth % 360 + 360) % 360
        getDirections(normalized.toDouble())
    }

    ToolScreen(title = "Compass", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(320.dp)) {
                // Fixed Heading Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(4.dp, 20.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )

                // Rotating Dial
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .graphicsLayer { rotationZ = rotation }
                ) {
                    val labels = listOf(
                        "N\nఉత్తరం" to 0f,
                        "NE\nఈశాన్యం" to 45f,
                        "E\nతూర్పు" to 90f,
                        "SE\nఆగ్నేయం" to 135f,
                        "S\nదక్షిణం" to 180f,
                        "SW\nనైరుతి" to 225f,
                        "W\nపడమర" to 270f,
                        "NW\nవాయవ్యం" to 315f
                    )

                    labels.forEach { (text, angle) ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationZ = angle },
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = if (angle % 90f == 0f) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (angle == 0f) Color.Red else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color.Gray.copy(alpha = 0.3f),
                            radius = size.minDimension / 2,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // Center Point
                Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
            }

            Spacer(modifier = Modifier.height(32.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${((azimuth % 360 + 360) % 360).toInt()}°", style = MaterialTheme.typography.displayMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(directions.first, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                Text(directions.second, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

fun getDirections(azimuth: Double): Pair<String, String> {
    return when (azimuth) {
        in 337.5..360.0, in 0.0..22.5 -> "North" to "ఉత్తరం (Uttaram)"
        in 22.5..67.5 -> "North-East" to "ఈశాన్యం (Ishanyam)"
        in 67.5..112.5 -> "East" to "తూర్పు (Toorpu)"
        in 112.5..157.5 -> "South-East" to "ఆగ్నేయం (Agneyam)"
        in 157.5..202.5 -> "South" to "దక్షిణం (Dakshinam)"
        in 202.5..247.5 -> "South-West" to "నైరుతి (Nairuthi)"
        in 247.5..292.5 -> "West" to "పడమర (Padamara)"
        in 292.5..337.5 -> "North-West" to "వాయవ్యం (Vayuvyam)"
        else -> "North" to "ఉత్తరం (Uttaram)"
    }
}
