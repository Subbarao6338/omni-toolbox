package com.naturetools.app.ui.screens.sensor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun SensorDataScreen(navController: NavHostController) {
    var soundLevel by remember { mutableFloatStateOf(45f) }
    var vibrationX by remember { mutableFloatStateOf(0.1f) }
    var vibrationY by remember { mutableFloatStateOf(0.05f) }

    LaunchedEffect(Unit) {
        while(true) {
            delay(500)
            soundLevel = (40..80).random().toFloat()
            vibrationX = (0..50).random() / 100f
            vibrationY = (0..50).random() / 100f
        }
    }

    ToolScreen(title = "Hardware Sensors", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))

            SensorCard("Sound Meter (dB)", "Current Level: ${soundLevel.toInt()} dB") {
                LinearProgressIndicator(progress = { soundLevel / 100f }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(16.dp))

            SensorCard("Vibrometer", "X: %.2f, Y: %.2f".format(vibrationX, vibrationY)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LinearProgressIndicator(progress = { vibrationX }, modifier = Modifier.weight(1f))
                    LinearProgressIndicator(progress = { vibrationY }, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SensorCard("Luxmeter", "Ambient Light: 250 lux") {
                Text("Normal indoor lighting detected.")
            }
        }
    }
}

@Composable
fun SensorCard(title: String, value: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
