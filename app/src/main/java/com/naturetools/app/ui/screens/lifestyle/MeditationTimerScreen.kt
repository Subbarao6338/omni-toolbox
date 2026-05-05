package com.naturetools.app.ui.screens.lifestyle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun MeditationTimerScreen(navController: NavHostController) {
    var durationMinutes by remember { mutableFloatStateOf(10f) }
    var timeLeft by remember { mutableIntStateOf(10 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var intervalMinutes by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--

            // Interval bell logic could go here
        } else if (timeLeft == 0) {
            isRunning = false
        }
    }

    ToolScreen(
        title = "Meditation Timer",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!isRunning) {
                Text("Set Duration: ${durationMinutes.toInt()} min")
                Slider(
                    value = durationMinutes,
                    onValueChange = {
                        durationMinutes = it
                        timeLeft = it.toInt() * 60
                    },
                    valueRange = 1f..60f,
                    steps = 59
                )

                Text("Interval Bell: ${if (intervalMinutes > 0) intervalMinutes.toInt().toString() + " min" else "Off"}")
                Slider(
                    value = intervalMinutes,
                    onValueChange = { intervalMinutes = it },
                    valueRange = 0f..30f,
                    steps = 30
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            val minutes = timeLeft / 60
            val seconds = timeLeft % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        isRunning = !isRunning
                    },
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "Pause" else "Start")
                }

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        timeLeft = durationMinutes.toInt() * 60
                    },
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
            }
        }
    }
}
