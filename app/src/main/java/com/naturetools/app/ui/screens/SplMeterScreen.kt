package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.math.log10
import kotlin.random.Random

@Composable
fun SplMeterScreen(navController: NavHostController) {
    var dbLevel by remember { mutableFloatStateOf(30f) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                // Mocking microphone input
                dbLevel = 30f + Random.nextFloat() * 60f
                kotlinx.coroutines.delay(200)
            }
        }
    }

    ToolScreen(
        title = "SPL Meter",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Sound Pressure Level", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = String.format("%.1f", dbLevel),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                color = when {
                    dbLevel > 85 -> Color.Red
                    dbLevel > 70 -> Color(0xFFFFA000)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            Text("dB", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(64.dp))

            LinearProgressIndicator(
                progress = { (dbLevel / 120f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(16.dp),
                color = if (dbLevel > 85) Color.Red else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isRunning) "Stop Measuring" else "Start Measuring")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Mock data shown. Real-time mic access required for actual measurements.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
