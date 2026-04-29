package com.naturetools.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen(navController: NavHostController) {
    var timeLeft by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var isBreak by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0) {
            isRunning = false
            // Logic to switch between work and break
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeDisplay = String.format("%02d:%02d", minutes, seconds)

    ToolScreen(
        title = "Pomodoro Timer",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isBreak) "Break Time" else "Focus Time",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isBreak) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = timeDisplay,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        isRunning = false
                        timeLeft = if (isBreak) 5 * 60 else 25 * 60
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(32.dp))

                FloatingActionButton(
                    onClick = { isRunning = !isRunning },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Start/Pause",
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                IconButton(
                    onClick = {
                        isBreak = !isBreak
                        isRunning = false
                        timeLeft = if (isBreak) 5 * 60 else 25 * 60
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isBreak) Icons.Default.Work else Icons.Default.SelfImprovement,
                        contentDescription = "Switch Mode",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
