package omni.toolbox.ui.screens.utility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.os.SystemClock
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun StopwatchScreen(navController: NavHostController) {
    var timeMillis by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    val laps = remember { mutableStateListOf<Long>() }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val startTime = SystemClock.elapsedRealtime() - timeMillis
            while (isRunning) {
                timeMillis = SystemClock.elapsedRealtime() - startTime
                delay(10)
            }
        }
    }

    ToolScreen(title = "Stopwatch", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                formatTime(timeMillis),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(vertical = 48.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isRunning) "Pause" else "Start")
                }

                Button(
                    onClick = {
                        if (isRunning) {
                            laps.add(0, timeMillis)
                        } else {
                            timeMillis = 0L
                            laps.clear()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Text(if (isRunning) "Lap" else "Reset")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                itemsIndexed(laps) { index, lapTime ->
                    ListItem(
                        headlineContent = { Text("Lap ${laps.size - index}") },
                        trailingContent = { Text(formatTime(lapTime)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val hundredths = (millis / 10) % 100
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 60000) % 60
    return "%02d:%02d.%02d".format(minutes, seconds, hundredths)
}
