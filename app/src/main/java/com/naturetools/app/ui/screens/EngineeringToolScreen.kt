package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.math.pow

@Composable
fun EngineeringToolScreen(navController: NavHostController, title: String) {
    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (title == "Resistor Color Code") {
                ResistorColorCodeTool()
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$title is coming soon!")
                }
            }
        }
    }
}

data class ColorBand(val name: String, val color: Color, val value: Int, val multiplier: Double, val tolerance: Double?)

val resistorColors = listOf(
    ColorBand("Black", Color.Black, 0, 1.0, null),
    ColorBand("Brown", Color(0xFF8B4513), 1, 10.0, 1.0),
    ColorBand("Red", Color.Red, 2, 100.0, 2.0),
    ColorBand("Orange", Color(0xFFFF8C00), 3, 1000.0, null),
    ColorBand("Yellow", Color.Yellow, 4, 10000.0, null),
    ColorBand("Green", Color.Green, 5, 100000.0, 0.5),
    ColorBand("Blue", Color.Blue, 6, 1000000.0, 0.25),
    ColorBand("Violet", Color(0xFFEE82EE), 7, 10000000.0, 0.1),
    ColorBand("Grey", Color.Gray, 8, 100000000.0, 0.05),
    ColorBand("White", Color.White, 9, 1000000000.0, null),
    ColorBand("Gold", Color(0xFFFFD700), -1, 0.1, 5.0),
    ColorBand("Silver", Color(0xFFC0C0C0), -1, 0.01, 10.0)
)

@Composable
fun ResistorColorCodeTool() {
    var band1 by remember { mutableStateOf(resistorColors[1]) } // Brown
    var band2 by remember { mutableStateOf(resistorColors[0]) } // Black
    var band3 by remember { mutableStateOf(resistorColors[2]) } // Red (Multiplier)
    var band4 by remember { mutableStateOf(resistorColors[10]) } // Gold (Tolerance)

    val resistanceValue = (band1.value * 10 + band2.value) * band3.multiplier
    val formattedResistance = when {
        resistanceValue >= 1000000 -> "${resistanceValue / 1000000} MΩ"
        resistanceValue >= 1000 -> "${resistanceValue / 1000} kΩ"
        else -> "$resistanceValue Ω"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Resistor Visual
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Wire
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.Gray))

                // Resistor Body
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDEB887)) // BurlyWood
                        .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.fillMaxHeight().width(12.dp).background(band1.color))
                        Box(modifier = Modifier.fillMaxHeight().width(12.dp).background(band2.color))
                        Box(modifier = Modifier.fillMaxHeight().width(12.dp).background(band3.color))
                        Spacer(modifier = Modifier.width(20.dp))
                        Box(modifier = Modifier.fillMaxHeight().width(12.dp).background(band4.color))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result
        Text(
            text = formattedResistance,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tolerance: ±${band4.tolerance}%",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Selectors
        BandSelector("Band 1 (Digit)", resistorColors.filter { it.value >= 0 }, band1) { band1 = it }
        BandSelector("Band 2 (Digit)", resistorColors.filter { it.value >= 0 }, band2) { band2 = it }
        BandSelector("Band 3 (Multiplier)", resistorColors, band3) { band3 = it }
        BandSelector("Band 4 (Tolerance)", resistorColors.filter { it.tolerance != null }, band4) { band4 = it }
    }
}

@Composable
fun BandSelector(label: String, options: List<ColorBand>, selected: ColorBand, onSelect: (ColorBand) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(options, key = { it.name + label }) { band ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(band.color)
                        .border(
                            width = if (selected == band) 3.dp else 1.dp,
                            color = if (selected == band) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { onSelect(band) },
                    contentAlignment = Alignment.Center
                ) {
                    if (band.color == Color.Black) {
                        Text(if (band.value >= 0) band.value.toString() else "", color = Color.White, fontSize = 12.sp)
                    } else if (band.color == Color.White) {
                        Text(if (band.value >= 0) band.value.toString() else "", color = Color.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
