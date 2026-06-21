package omni.toolbox.ui.screens.outdoor

import android.location.Location
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.*

data class Beacon(val name: String, val latitude: Double, val longitude: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeaconNavigationScreen(navController: NavHostController) {
    var beacons by remember { mutableStateOf(listOf(Beacon("Base Camp", 37.7749, -122.4194))) }
    var userLocation by remember { mutableStateOf<Location?>(Location("simulated").apply { latitude = 37.7740; longitude = -122.4190 }) }
    var showAddBeacon by remember { mutableStateOf(false) }
    var newBeaconName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(2000)
            userLocation = Location("simulated").apply {
                latitude = (userLocation?.latitude ?: 37.7740) + (kotlin.random.Random.nextDouble() - 0.5) * 0.0001
                longitude = (userLocation?.longitude ?: -122.4190) + (kotlin.random.Random.nextDouble() - 0.5) * 0.0001
            }
        }
    }

    ToolScreen(
        title = "Beacon Navigation",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { showAddBeacon = true }) {
                Icon(Icons.Default.AddLocation, contentDescription = "Add Beacon")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Radar UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                NavigationRadar(beacons, userLocation)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Active Beacons", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(beacons) { beacon ->
                    val distance = userLocation?.let { calculateDistance(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f
                    val bearing = userLocation?.let { calculateBearing(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f

                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        ListItem(
                            headlineContent = { Text(beacon.name, fontWeight = FontWeight.Bold) },
                            supportingContent = {
                                Text("${String.format("%.2f", distance)}m • ${bearing.toInt()}°")
                            },
                            leadingContent = { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.primary) },
                            trailingContent = {
                                IconButton(onClick = { beacons = beacons.filter { it != beacon } }) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showAddBeacon) {
            AlertDialog(
                onDismissRequest = { showAddBeacon = false },
                title = { Text("Add Beacon") },
                text = {
                    Column {
                        OutlinedTextField(value = newBeaconName, onValueChange = { newBeaconName = it }, label = { Text("Name") })
                        Text("Current coordinates will be used.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        beacons = beacons + Beacon(newBeaconName, userLocation?.latitude ?: 0.0, userLocation?.longitude ?: 0.0)
                        newBeaconName = ""
                        showAddBeacon = false
                    }) { Text("Save") }
                }
            )
        }
    }
}

@Composable
fun NavigationRadar(beacons: List<Beacon>, userLocation: Location?) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2

        // Draw circles
        drawCircle(color = primaryColor.copy(alpha = 0.2f), radius = radius, center = center, style = Stroke(2f))
        drawCircle(color = primaryColor.copy(alpha = 0.1f), radius = radius * 0.66f, center = center, style = Stroke(2f))
        drawCircle(color = primaryColor.copy(alpha = 0.05f), radius = radius * 0.33f, center = center, style = Stroke(2f))

        // Draw crosshair
        drawLine(color = primaryColor.copy(alpha = 0.2f), start = Offset(center.x - radius, center.y), end = Offset(center.x + radius, center.y))
        drawLine(color = primaryColor.copy(alpha = 0.2f), start = Offset(center.x, center.y - radius), end = Offset(center.x, center.y + radius))

        // Draw beacons
        beacons.forEach { beacon ->
            val bearing = userLocation?.let { calculateBearing(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f
            val distance = userLocation?.let { calculateDistance(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f

            val normDistance = (distance / 1000f).coerceIn(0.1f, 1.0f) * radius
            val angleRad = Math.toRadians((bearing - 90).toDouble())

            val beaconPos = Offset(
                center.x + normDistance.toFloat() * cos(angleRad).toFloat(),
                center.y + normDistance.toFloat() * sin(angleRad).toFloat()
            )

            drawCircle(color = secondaryColor, radius = 8f, center = beaconPos)
        }

        // Draw user
        drawCircle(color = primaryColor, radius = 6f, center = center)
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0]
}

fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val startLocation = Location("").apply { latitude = lat1; longitude = lon1 }
    val endLocation = Location("").apply { latitude = lat2; longitude = lon2 }
    return startLocation.bearingTo(endLocation)
}
