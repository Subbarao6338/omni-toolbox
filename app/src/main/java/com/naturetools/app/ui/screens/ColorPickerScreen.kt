package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun ColorPickerScreen(navController: NavHostController) {
    var red by remember { mutableFloatStateOf(0f) }
    var green by remember { mutableFloatStateOf(0f) }
    var blue by remember { mutableFloatStateOf(0f) }

    val currentColor = Color(red / 255f, green / 255f, blue / 255f)
    val hexCode = String.format("#%02X%02X%02X", red.toInt(), green.toInt(), blue.toInt())

    ToolScreen(title = "Color Picker", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(150.dp),
                color = currentColor,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
            ) {}
            Spacer(modifier = Modifier.height(16.dp))
            Text(hexCode, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            ColorSlider("Red", red, { red = it }, Color.Red)
            ColorSlider("Green", green, { green = it }, Color.Green)
            ColorSlider("Blue", blue, { blue = it }, Color.Blue)
        }
    }
}

@Composable
fun ColorSlider(label: String, value: Float, onValueChange: (Float) -> Unit, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label)
            Text(value.toInt().toString())
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
        )
    }
}
