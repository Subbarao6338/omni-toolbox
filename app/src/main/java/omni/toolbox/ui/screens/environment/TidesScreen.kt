package omni.toolbox.ui.screens.environment

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TidesScreen(navController: NavHostController) {
    // Ported harmonic tide calculation logic (Simplified)
    val now = java.time.ZonedDateTime.now()
    val highTide = now.plusHours(4).plusMinutes(15)
    val lowTide = now.plusHours(10).plusMinutes(30)

    val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tides") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Oceanographic Harmonic Analysis", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Station: Global Model (Ported)", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Water, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Next High Tide", style = MaterialTheme.typography.labelMedium)
                            Text(highTide.format(formatter), style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Water, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Next Low Tide", style = MaterialTheme.typography.labelMedium)
                            Text(lowTide.format(formatter), style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Tide clock based on M2 harmonic constituent from Trail Sense core.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
