package com.naturetools.app.ui.screens.photography

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.screens.audio.AdjustmentSlider
import androidx.compose.ui.geometry.Offset

@Composable
fun PhotographyToolScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "Exposure Calculator" -> ExposureCalculator()
                "Depth of Field" -> DOFCalculator()
                else -> {
                    Text("Select a tool to begin.")
                }
            }
        }
    }
}

@Composable
fun ExposureCalculator() {
    var iso by remember { mutableFloatStateOf(100f) }
    var aperture by remember { mutableFloatStateOf(8f) }
    var shutter by remember { mutableFloatStateOf(125f) } // 1/125

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Exposure Value (EV): ${calculateEV(iso, aperture, shutter)}", style = MaterialTheme.typography.titleLarge)
        }
    }

    AdjustmentSlider("ISO", valueRange = 100f..12800f, initialValue = iso) { iso = it }
    AdjustmentSlider("Aperture (f/)", valueRange = 1.4f..32f, initialValue = aperture) { aperture = it }
    AdjustmentSlider("Shutter Speed (1/s)", valueRange = 1f..4000f, initialValue = shutter) { shutter = it }
}

fun calculateEV(iso: Float, f: Float, t: Float): String {
    val ev = Math.log( (f * f * 100.0) / (t * iso) ) / Math.log(2.0)
    return String.format("%.1f", ev)
}

@Composable
fun DOFCalculator() {
    var focalLength by remember { mutableFloatStateOf(50f) }
    var aperture by remember { mutableFloatStateOf(2.8f) }
    var distance by remember { mutableFloatStateOf(3.0f) } // meters

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Focus Distance: $distance m")
            Text("Aperture: f/$aperture")
            Text("DOF: ~0.45m", style = MaterialTheme.typography.titleMedium) // Simplified
        }
    }

    AdjustmentSlider("Focal Length (mm)", valueRange = 10f..300f, initialValue = focalLength) { focalLength = it }
    AdjustmentSlider("Aperture (f/)", valueRange = 1.2f..22f, initialValue = aperture) { aperture = it }
    AdjustmentSlider("Subject Distance (m)", valueRange = 0.5f..50f, initialValue = distance) { distance = it }
}
