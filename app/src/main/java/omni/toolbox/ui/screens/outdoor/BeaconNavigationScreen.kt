package omni.toolbox.ui.screens.outdoor

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.data.local.AppDatabase
import omni.toolbox.data.local.entity.BeaconEntity
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.utils.navigation.GeoUtils
import kotlinx.coroutines.launch
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeaconNavigationScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val beacons by db.navigationDao().getAllBeacons().collectAsState(initial = emptyList())
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var showAddBeacon by remember { mutableStateOf(false) }
    var newBeaconName by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    DisposableEffect(context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                userLocation = location
            }
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, 1f, listener)
        } catch (e: SecurityException) {}

        onDispose {
            locationManager.removeUpdates(listener)
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
                    val distance = userLocation?.let { GeoUtils.calculateDistance(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f
                    val bearing = userLocation?.let { GeoUtils.calculateBearing(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f

                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        ListItem(
                            headlineContent = { Text(beacon.name, fontWeight = FontWeight.Bold) },
                            supportingContent = {
                                Text("${String.format("%.2f", distance)}m • ${bearing.toInt()}°")
                            },
                            leadingContent = { Icon(Icons.Default.Place, null, tint = MaterialTheme.colorScheme.primary) },
                            trailingContent = {
                                IconButton(onClick = {
                                    scope.launch { db.navigationDao().deleteBeacon(beacon) }
                                }) {
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
                        scope.launch {
                            db.navigationDao().insertBeacon(BeaconEntity(
                                name = newBeaconName,
                                latitude = userLocation?.latitude ?: 0.0,
                                longitude = userLocation?.longitude ?: 0.0
                            ))
                        }
                        newBeaconName = ""
                        showAddBeacon = false
                    }) { Text("Save") }
                }
            )
        }
    }
}

@Composable
fun NavigationRadar(beacons: List<BeaconEntity>, userLocation: Location?) {
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
            val bearing = userLocation?.let { GeoUtils.calculateBearing(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f
            val distance = userLocation?.let { GeoUtils.calculateDistance(it.latitude, it.longitude, beacon.latitude, beacon.longitude) } ?: 0f

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
