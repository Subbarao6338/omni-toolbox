package com.naturetools.app.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun SensorsListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensorList = remember { sensorManager.getSensorList(Sensor.TYPE_ALL) }

    ToolScreen(
        title = "Available Sensors",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sensorList) { sensor ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(sensor.name, style = MaterialTheme.typography.titleMedium)
                        Text("Vendor: ${sensor.vendor}", style = MaterialTheme.typography.bodySmall)
                        Text("Version: ${sensor.version}", style = MaterialTheme.typography.bodySmall)
                        Text("Power: ${sensor.power} mA", style = MaterialTheme.typography.bodySmall)
                        Text("Resolution: ${sensor.resolution}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
