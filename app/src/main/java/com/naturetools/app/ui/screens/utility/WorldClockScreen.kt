package com.naturetools.app.ui.screens.utility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WorldClockScreen(navController: NavHostController) {
    val zones = listOf("UTC", "America/New_York", "Europe/London", "Europe/Paris", "Asia/Tokyo", "Asia/Dubai", "Australia/Sydney")
    var currentTime by remember { mutableStateOf(ZonedDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = ZonedDateTime.now()
            kotlinx.coroutines.delay(1000)
        }
    }

    ToolScreen(title = "World Clock", onBack = { navController.popBackStack() }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(zones) { zone ->
                val zdt = currentTime.withZoneSameInstant(ZoneId.of(zone))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(zone.split("/").last().replace("_", " "), style = MaterialTheme.typography.titleMedium)
                            Text(zone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(zdt.format(DateTimeFormatter.ofPattern("HH:mm:ss")), style = MaterialTheme.typography.headlineSmall)
                            Text(zdt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
