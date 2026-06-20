package com.nature.toolbox.ultimate

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var pressureSensor: Sensor? = null
    private var rotationSensor: Sensor? = null

    private var _pressure = mutableStateOf(1013.25f)
    private var _azimuth = mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        setContent {
            NatureToolboxTheme {
                MainScreen(_pressure.value, _azimuth.value)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pressureSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        rotationSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_PRESSURE -> _pressure.value = event.values[0]
            Sensor.TYPE_ROTATION_VECTOR -> {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                val orientationValues = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationValues)
                _azimuth.value = Math.toDegrees(orientationValues[0].toDouble()).toFloat()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun MainScreen(pressure: Float, azimuth: Float) {
    Scaffold(
        containerColor = Color(0xFFF8FAF8),
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                contentColor = Color(0xFF2D5A27)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {}) { Icon(Icons.Default.Home, "Home") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Explore, "Explore") }
                    IconButton(onClick = {}) { Icon(Icons.Default.CameraAlt, "NatureDex") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Settings") }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                "Nature Toolbox",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3C18),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "v5.0.0-Native-Core",
                fontSize = 10.sp,
                color = Color(0xFF2D5A27).copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SensorCard(
                        title = "Pressure",
                        value = "${pressure.roundToInt()} hPa",
                        sub = "Stable Air",
                        icon = Icons.Default.CloudQueue,
                        color = Color(0xFF67B26F)
                    )
                }
                item {
                    SensorCard(
                        title = "Compass",
                        value = "${(if (azimuth < 0) azimuth + 360 else azimuth).roundToInt()}°",
                        sub = "Magnetic North",
                        icon = Icons.Default.Explore,
                        color = Color(0xFF4CA2CD)
                    )
                }
                item(span = { androidx.compose.lazy.grid.GridItemSpan(2) }) {
                    BigVisualCard("Orientation") {
                        CompassDial(azimuth)
                    }
                }
            }
        }
    }
}

@Composable
fun SensorCard(title: String, value: String, sub: String, icon: ImageVector, color: Color) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.height(140.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3C18))
                Text(title, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
                Text(sub, fontSize = 8.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BigVisualCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                title.uppercase(),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray
            )
            content()
        }
    }
}

@Composable
fun CompassDial(azimuth: Float) {
    val rotation by animateFloatAsState(targetValue = -azimuth, animationSpec = spring(stiffness = Spring.StiffnessLow))
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(200.dp).rotate(rotation)) {
            val center = size / 2.0f
            val radius = size.minDimension / 2.0f
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFF0F4F0), Color.White)),
                radius = radius
            )
            // Compass markers
            for (i in 0 until 360 step 30) {
                rotate(i.toFloat()) {
                    drawLine(
                        color = if (i % 90 == 0) Color(0xFF2D5A27) else Color.LightGray,
                        start = center.copy(y = 10f),
                        end = center.copy(y = if (i % 90 == 0) 40f else 25f),
                        strokeWidth = if (i % 90 == 0) 4f else 2f
                    )
                }
            }
        }
        // Needle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(Color(0xFFE53935), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(Color(0xFF1B3C18), RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
            )
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color.White, RoundedCornerShape(50))
                .padding(2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2D5A27), RoundedCornerShape(50)))
        }
    }
}

@Composable
fun NatureToolboxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2D5A27),
            secondary = Color(0xFF67B26F),
            tertiary = Color(0xFF4CA2CD)
        ),
        content = content
    )
}
