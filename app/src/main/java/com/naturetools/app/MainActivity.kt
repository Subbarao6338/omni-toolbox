package com.naturetools.app

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NatureToolsApp()
        }
    }
}

data class Tool(
    val name: String,
    val icon: ImageVector,
    val route: String
)

val tools = listOf(
    Tool("Unit Converter", Icons.Default.Refresh, "converter"),
    Tool("Calculator", Icons.Default.Add, "calculator"),
    Tool("Web Search", Icons.Default.Search, "web"),
    Tool("Compass", Icons.Default.LocationOn, "compass"),
    Tool("Light Meter", Icons.Default.Info, "light"),
    Tool("Note Pad", Icons.Default.Edit, "note")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NatureToolsApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nature Tools") })
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("converter") { UnitConverterScreen() }
            composable("calculator") { CalculatorScreen() }
            composable("web") { WebToolScreen() }
            composable("compass") { CompassScreen() }
            composable("light") { LightMeterScreen() }
            composable("note") { NotePadScreen() }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tools) { tool ->
            Card(
                onClick = { navController.navigate(tool.route) },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(tool.icon, contentDescription = tool.name)
                    Text(tool.name)
                }
            }
        }
    }
}

@Composable
fun UnitConverterScreen() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Length", "Weight")

    Column(modifier = Modifier.padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index; input = ""; result = "" }, text = { Text(title) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = input,
            onValueChange = {
                input = it
                val value = it.toDoubleOrNull() ?: 0.0
                result = if (selectedTab == 0) {
                    "${(value * 0.621371).format(2)} Miles"
                } else {
                    "${(value * 2.20462).format(2)} Pounds"
                }
            },
            label = { Text(if (selectedTab == 0) "Kilometers" else "Kilograms") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Result: $result", style = MaterialTheme.typography.headlineMedium)
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun CalculatorScreen() {
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = num1, onValueChange = { num1 = it }, label = { Text("Number 1") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = num2, onValueChange = { num2 = it }, label = { Text("Number 2") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val ops = listOf("+", "-", "*", "/")
            ops.forEach { op ->
                Button(onClick = {
                    val n1 = num1.toDoubleOrNull() ?: 0.0
                    val n2 = num2.toDoubleOrNull() ?: 0.0
                    result = when (op) {
                        "+" -> (n1 + n2).toString()
                        "-" -> (n1 - n2).toString()
                        "*" -> (n1 * n2).toString()
                        "/" -> if (n2 != 0.0) (n1 / n2).toString() else "Error"
                        else -> ""
                    }
                }) {
                    Text(op)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Result: $result", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun WebToolScreen() {
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var urlToLoad by remember { mutableStateOf("https://www.google.com") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("URL") }
            )
            Button(onClick = {
                urlToLoad = if (urlInput.startsWith("http")) urlInput else "https://$urlInput"
            }) {
                Text("Go")
            }
        }
        AndroidView(factory = {
            WebView(it).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(urlToLoad)
            }
        }, update = {
            it.loadUrl(urlToLoad)
        }, modifier = Modifier.fillMaxSize().weight(1f))
    }
}

@Composable
fun CompassScreen() {
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

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Compass", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Canvas(modifier = Modifier.size(200.dp)) {
            rotate(rotation) {
                drawLine(Color.Red, center, center.copy(y = center.y - 100.dp.toPx()), strokeWidth = 5.dp.toPx())
                drawLine(Color.Black, center, center.copy(y = center.y + 100.dp.toPx()), strokeWidth = 5.dp.toPx())
            }
        }
        Text("${azimuth.toInt()}°")
    }
}

@Composable
fun LightMeterScreen() {
    val context = LocalContext.current
    var lux by remember { mutableFloatStateOf(0f) }

    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val lightSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                    lux = event.values[0]
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Light Meter", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(100.dp))
        Text("${lux.toInt()} lx", style = MaterialTheme.typography.displayMedium)
        Text(
            when {
                lux < 10 -> "Dark"
                lux < 100 -> "Dim"
                lux < 1000 -> "Normal"
                lux < 10000 -> "Bright"
                else -> "Very Bright"
            }
        )
    }
}

@Composable
fun NotePadScreen() {
    val context = LocalContext.current
    val fileName = "notes.txt"
    var text by remember {
        mutableStateOf(
            try {
                context.openFileInput(fileName).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                ""
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Note Pad", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = text,
            onValueChange = {
                text = it
                try {
                    context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                        output.write(it.toByteArray())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("Write your notes here...") }
        )
    }
}
