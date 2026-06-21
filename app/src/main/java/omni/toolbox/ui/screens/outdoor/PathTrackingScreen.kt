package omni.toolbox.ui.screens.outdoor

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.data.local.AppDatabase
import omni.toolbox.data.local.entity.PathEntity
import omni.toolbox.service.PathTrackingService
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.launch

@Composable
fun PathTrackingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val paths by db.navigationDao().getAllPaths().collectAsState(initial = emptyList())
    var isTracking by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val needsRequest = permissions.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (needsRequest) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    ToolScreen(
        title = "Path Tracking",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = {
                scope.launch {
                    if (!isTracking) {
                        val id = db.navigationDao().insertPath(PathEntity(name = "Path ${paths.size + 1}"))
                        val intent = Intent(context, PathTrackingService::class.java).apply {
                            putExtra("pathId", id)
                        }
                        context.startForegroundService(intent)
                        isTracking = true
                    } else {
                        context.stopService(Intent(context, PathTrackingService::class.java))
                        isTracking = false
                    }
                }
            }) {
                Icon(if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Toggle Tracking")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (isTracking) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Recording journey...")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Recorded Paths", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(paths) { path ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        ListItem(
                            headlineContent = { Text(path.name, fontWeight = FontWeight.Bold) },
                            leadingContent = { Icon(Icons.Default.Route, null) },
                            trailingContent = {
                                IconButton(onClick = {
                                    scope.launch {
                                        db.navigationDao().deletePath(path)
                                        db.navigationDao().deleteWaypointsForPath(path.id)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
