package omni.toolbox.ui.screens.comm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class CallLogEntry(
    val name: String,
    val number: String,
    val type: CallType,
    val timestamp: String
)

enum class CallType { INCOMING, OUTGOING, MISSED }

@Composable
fun HistoryScreen(navController: NavHostController) {
    val callLogs = listOf(
        CallLogEntry("John Doe", "+1 234 567 890", CallType.INCOMING, "10:30 AM"),
        CallLogEntry("Jane Smith", "+1 987 654 321", CallType.OUTGOING, "Yesterday"),
        CallLogEntry("Unknown", "+1 555 000 111", CallType.MISSED, "Monday")
    )

    ToolScreen(title = "Call History", onBack = { navController.popBackStack() }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            items(callLogs) { entry ->
                ListItem(
                    headlineContent = { Text(entry.name) },
                    supportingContent = {
                        Column {
                            Text(entry.number)
                            // Ported Call Tags and Notes from nature-dailer-main
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Business", style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = { Icon(Icons.Default.Label, null, modifier = Modifier.size(12.dp)) }
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Project Alpha", style = MaterialTheme.typography.labelSmall) },
                                    leadingIcon = { Icon(Icons.Default.NoteAdd, null, modifier = Modifier.size(12.dp)) }
                                )
                            }
                        }
                    },
                    trailingContent = { Text(entry.timestamp, style = MaterialTheme.typography.bodySmall) },
                    leadingContent = {
                        val icon = when (entry.type) {
                            CallType.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                            CallType.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                            CallType.MISSED -> Icons.AutoMirrored.Filled.CallMissed
                        }
                        val tint = when (entry.type) {
                            CallType.INCOMING -> Color(0xFF4CAF50)
                            CallType.OUTGOING -> MaterialTheme.colorScheme.primary
                            CallType.MISSED -> MaterialTheme.colorScheme.error
                        }
                        Icon(icon, contentDescription = null, tint = tint)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}
