package com.example.naturedialer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturedialer.ui.theme.DialerTheme
import com.example.naturedialer.ui.theme.getNatureColors

data class CallLog(val id: String, val name: String, val number: String, val time: String, val type: String)

val MOCK_CALLS = listOf(
    CallLog("1", "Arjuna Smith", "+1 (555) 123-4567", "2 mins ago", "incoming"),
    CallLog("2", "Leila Moss", "+1 (555) 987-6543", "45 mins ago", "missed"),
    CallLog("3", "Unknown", "+1 (555) 000-0000", "2 hours ago", "outgoing")
)

@Composable
fun HistoryScreen(
    theme: DialerTheme,
    onCall: (String) -> Unit
) {
    val natureColors = getNatureColors(theme)

    Column(modifier = Modifier.fillMaxSize().background(natureColors.bg).padding(16.dp)) {
        Text(
            text = "Recent Calls",
            color = natureColors.text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(MOCK_CALLS) { log ->
                HistoryItem(log = log, theme = theme, onCall = onCall)
            }
        }
    }
}

@Composable
fun HistoryItem(log: CallLog, theme: DialerTheme, onCall: (String) -> Unit) {
    val natureColors = getNatureColors(theme)
    val typeColor = when (log.type) {
        "missed" -> Color.Red
        "incoming" -> natureColors.accent
        else -> natureColors.text.copy(alpha = 0.6f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(natureColors.surface)
            .clickable { onCall(log.number) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = log.name, color = natureColors.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = typeColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${log.number} • ${log.time}", color = natureColors.text.copy(alpha = 0.6f), fontSize = 12.sp)
            }
        }
        IconButton(onClick = { onCall(log.number) }) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = natureColors.accent)
        }
    }
}
