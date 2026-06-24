package omni.toolbox.ui.screens.audio

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.sin

@Composable
fun AudioGeneratorScreen(navController: NavHostController, title: String) {
    var frequency by remember { mutableFloatStateOf(440f) }
    var isMuted by remember { mutableStateOf(true) }
    var waveType by remember { mutableStateOf("Sine") }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tone Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Frequency: ${frequency.toInt()} Hz", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                text = when {
                                    frequency < 250f -> "Bass Range"
                                    frequency < 2000f -> "Mid Range"
                                    else -> "High Range"
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Button(
                            onClick = { isMuted = !isMuted },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMuted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(if (isMuted) "Start" else "Stop")
                        }
                    }

                    Slider(
                        value = frequency,
                        onValueChange = { frequency = it },
                        valueRange = 20f..5000f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Sine", "Square", "Sawtooth").forEach { type ->
                            FilterChip(
                                selected = waveType == type,
                                onClick = { waveType = type },
                                label = { Text(type) }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F1218)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pathPoints = size.width.toInt()
                    val waveAmplitude = size.height / 3f
                    val waveFrequencyScale = frequency / 200.0f

                    for (x in 0 until pathPoints step 2) {
                        val angle = (x.toDouble() / size.width.toDouble()) * waveFrequencyScale * Math.PI * 4.0
                        val y = (size.height / 2f) + sin(angle).toFloat() * waveAmplitude

                        if (x > 0) {
                            val prevAngle = ((x - 2).toDouble() / size.width.toDouble()) * waveFrequencyScale * Math.PI * 4.0
                            val prevY = (size.height / 2f) + sin(prevAngle).toFloat() * waveAmplitude
                            drawLine(
                                color = if (isMuted) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF00FF88),
                                start = Offset((x - 2).toFloat(), prevY),
                                end = Offset(x.toFloat(), y),
                                strokeWidth = if (isMuted) 2f else 3f
                            )
                        }
                    }
                }
                Text(
                    text = if (isMuted) "Muted" else "Playing: ${frequency.toInt()}Hz $waveType",
                    color = if (isMuted) Color.Gray else Color(0xFF00FF88),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                )
            }

            Text(
                "Warning: High frequencies or high volume can damage hearing or equipment. Use with caution.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
