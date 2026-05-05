package com.naturetools.app.ui.screens.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun GradientGeneratorScreen(navController: NavHostController) {
    var color1 by remember { mutableStateOf(Color(0xFF4CAF50)) }
    var color2 by remember { mutableStateOf(Color(0xFF2196F3)) }
    val clipboardManager = LocalClipboardManager.current

    val gradientBrush = Brush.linearGradient(
        colors = listOf(color1, color2)
    )

    val cssCode = "background: linear-gradient(to right, #${Integer.toHexString(color1.toArgb()).substring(2)}, #${Integer.toHexString(color2.toArgb()).substring(2)});"

    ToolScreen(title = "Gradient Generator", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(gradientBrush)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ColorSlider("Color 1 Red", color1.red) { color1 = color1.copy(red = it) }
                    ColorSlider("Color 1 Green", color1.green) { color1 = color1.copy(green = it) }
                    ColorSlider("Color 1 Blue", color1.blue) { color1 = color1.copy(blue = it) }

                    Divider()

                    ColorSlider("Color 2 Red", color2.red) { color2 = color2.copy(red = it) }
                    ColorSlider("Color 2 Green", color2.green) { color2 = color2.copy(green = it) }
                    ColorSlider("Color 2 Blue", color2.blue) { color2 = color2.copy(blue = it) }
                }
            }

            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        cssCode,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(cssCode)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy CSS")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Text("$label: ${(value * 255).toInt()}", style = MaterialTheme.typography.labelSmall)
        Slider(value = value, onValueChange = onValueChange)
    }
}
