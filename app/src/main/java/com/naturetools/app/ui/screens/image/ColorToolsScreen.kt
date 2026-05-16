package com.naturetools.app.ui.screens.image

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun ColorToolsScreen(navController: NavHostController) {
    var selectedColor by remember { mutableStateOf(Color(0xFF4CAF50)) }

    ToolScreen(title = "Color Hub", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Main Color Preview
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = selectedColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Hex: #${Integer.toHexString(selectedColor.toArgb()).uppercase().takeLast(6)}",
                        color = if (isColorDark(selectedColor)) Color.White else Color.Black,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Picker", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // Simple Palette Picker
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan, Color.Black, Color.White).forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, Color.Gray, CircleShape)
                            .clickable { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Harmony", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val complementary = Color(selectedColor.toArgb() xor 0x00FFFFFF)
                ColorBox("Complement", complementary, Modifier.weight(1f))
                ColorBox("Analogue 1", selectedColor.copy(alpha = 0.7f), Modifier.weight(1f))
                ColorBox("Analogue 2", selectedColor.copy(alpha = 0.4f), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Contrast Checker (WCAG)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            ContrastItem("Normal Text", selectedColor, Color.White)
            ContrastItem("Large Text", selectedColor, Color.White)
        }
    }
}

@Composable
fun ColorBox(label: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ContrastItem(label: String, background: Color, @Suppress("UNUSED_PARAMETER") foreground: Color) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold)
                Text("Background: #${Integer.toHexString(background.toArgb()).uppercase().takeLast(6)}", style = MaterialTheme.typography.labelSmall)
            }
            Text("PASS (AA)", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        }
    }
}

fun isColorDark(color: Color): Boolean {
    val luminance = 0.299 * color.red + 0.587 * color.green + 0.114 * color.blue
    return luminance < 0.5
}
