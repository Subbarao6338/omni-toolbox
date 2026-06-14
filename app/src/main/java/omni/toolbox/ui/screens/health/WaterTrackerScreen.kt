package omni.toolbox.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.WaterTrackerViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WaterTrackerScreen(navController: NavHostController, viewModel: WaterTrackerViewModel = viewModel()) {
    val todayTotal by viewModel.todayTotal.collectAsState(initial = 0)
    val logs by viewModel.allLogs.collectAsState(initial = emptyList())
    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    ToolScreen(title = "Water Tracker", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Today's Intake", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${todayTotal ?: 0} ml",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    LinearProgressIndicator(
                        progress = { ((todayTotal ?: 0).toFloat() / 2000f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(12.dp).padding(vertical = 8.dp),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text("Goal: 2000 ml", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Quick Add", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(250, 500, 750).forEach { amount ->
                    Button(
                        onClick = { viewModel.addWater(amount) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocalDrink, contentDescription = null)
                            Text("${amount}ml")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Recent Logs", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs) { log ->
                    ListItem(
                        headlineContent = { Text("${log.amountMl} ml") },
                        supportingContent = { Text(dateFormat.format(Date(log.timestamp))) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteLog(log) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
