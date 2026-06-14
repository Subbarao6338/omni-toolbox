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
    var history = remember { mutableStateListOf<Pair<String, String>>() }

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

            if (isMeasuring) {
                CircularProgressIndicator(modifier = Modifier.size(100.dp), strokeWidth = 8.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Measuring... Keep your finger steady", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(
                    text = measurementValue ?: "--",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (title) {
                        "Heart Rate Monitor" -> "BPM"
                        "Blood Pressure" -> "mmHg"
                        "Blood Sugar" -> "mg/dL"
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
                        delay(3000)
                        val newVal = when (title) {
                            "Heart Rate Monitor" -> (60..100).random().toString()
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
                Text(if (measurementValue == null) "Start Measurement" else "Measure Again")
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
