package omni.toolbox.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MedicalToolsScreen(navController: NavHostController, title: String) {
    val scope = rememberCoroutineScope()
    var isMeasuring by remember { mutableStateOf(false) }
    var measurementValue by remember { mutableStateOf<String?>(null) }
    val history = remember { mutableStateListOf<Pair<String, String>>() }

    // Heart Rate Tapper State
    var tapTimes by remember { mutableStateOf(listOf<Long>()) }
    val currentBpm = remember(tapTimes) {
        if (tapTimes.size < 2) 0
        else {
            val intervals = tapTimes.zipWithNext { a, b -> b - a }
            val avgInterval = intervals.average()
            (60000 / avgInterval).toInt()
        }
    }

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
            Icon(
                when (title) {
                    "Heart Rate Monitor" -> Icons.Default.Favorite
                    "Blood Pressure" -> Icons.Default.MonitorHeart
                    "Blood Sugar" -> Icons.Default.Bloodtype
                    else -> Icons.Default.MedicalServices
                },
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            if (title == "Heart Rate Monitor") {
                Text(
                    text = if (currentBpm > 0) currentBpm.toString() else "--",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("BPM (Pulse Tapper)", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val now = System.currentTimeMillis()
                        tapTimes = (tapTimes + now).takeLast(10)
                    },
                    modifier = Modifier.size(120.dp).padding(8.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(48.dp))
                }
                Text("Tap in sync with your pulse", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (currentBpm > 0) {
                            val newVal = currentBpm.toString()
                            measurementValue = newVal
                            history.add(0, newVal to java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm")))
                            tapTimes = emptyList()
                        }
                    }, enabled = currentBpm > 0) {
                        Text("Save Reading")
                    }
                    OutlinedButton(onClick = { tapTimes = emptyList() }) {
                        Text("Reset")
                    }
                }
            } else {
                if (isMeasuring) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp), strokeWidth = 8.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing... Please wait", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(
                        text = measurementValue ?: "--",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = when (title) {
                            "Blood Pressure" -> "mmHg (Est.)"
                            "Blood Sugar" -> "mg/dL (Est.)"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        scope.launch {
                            isMeasuring = true
                            delay(2000)
                            val newVal = when (title) {
                                "Blood Pressure" -> "${(110..130).random()}/${(70..90).random()}"
                                "Blood Sugar" -> (80..120).random().toString()
                                else -> "N/A"
                            }
                            measurementValue = newVal
                            history.add(0, newVal to java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm")))
                            isMeasuring = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isMeasuring
                ) {
                    Text(if (measurementValue == null) "Log Current Estimate" else "Estimate Again")
                }

                Card(
                    modifier = Modifier.padding(top = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "These values are estimates for manual logging purposes and not direct sensor measurements.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("History", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))

            if (history.isEmpty()) {
                Text("No data available", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            } else {
                history.forEach { item ->
                    ListItem(
                        headlineContent = { Text(item.first) },
                        supportingContent = { Text(item.second) },
                        trailingContent = { Icon(Icons.Default.History, contentDescription = null) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
