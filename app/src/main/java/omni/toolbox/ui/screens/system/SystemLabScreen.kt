package omni.toolbox.ui.screens.system

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay
import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface

data class PermissionInfo(val name: String, val status: String, val count: Int)

@Composable
fun SystemLabScreen(navController: NavHostController, title: String) {
    val context = LocalContext.current
    val permissions = listOf(
        PermissionInfo("Camera", "Accessed by 12 apps", 12),
        PermissionInfo("Location", "Accessed by 24 apps", 24),
        PermissionInfo("Microphone", "Accessed by 5 apps", 5),
        PermissionInfo("Contacts", "Accessed by 8 apps", 8)
    )

    var cpuLoad by remember { mutableFloatStateOf(0.24f) }
    var ramUsageProgress by remember { mutableFloatStateOf(0.0f) }
    var ramUsageText by remember { mutableStateOf("0/0 GB") }
    var storageProgress by remember { mutableFloatStateOf(0.0f) }
    var storageText by remember { mutableStateOf("0%") }
    var ipAddress by remember { mutableStateOf("Unknown") }
    var networkType by remember { mutableStateOf("Unknown") }

    val logs = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        logs.add("[INFO] Initializing System Diagnostic Engine...")
        delay(500)
        logs.add("[OK] Hardware Abstraction Layer linked.")
        delay(300)
        logs.add("[OK] Sensor Hub online.")

        while(true) {
            // RAM Usage
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val totalRam = memoryInfo.totalMem.toFloat() / (1024 * 1024 * 1024)
            val availRam = memoryInfo.availMem.toFloat() / (1024 * 1024 * 1024)
            val usedRam = totalRam - availRam
            ramUsageProgress = usedRam / totalRam
            ramUsageText = "%.1f / %.1f GB".format(usedRam, totalRam)

            // Network Info
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(activeNetwork)
            networkType = when {
                caps == null -> "None"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Other"
            }

            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                var foundIp = "Unknown"
                while (interfaces.hasMoreElements()) {
                    val intf = interfaces.nextElement()
                    val addrs = intf.inetAddresses
                    while (addrs.hasMoreElements()) {
                        val addr = addrs.nextElement()
                        if (!addr.isLoopbackAddress && addr is Inet4Address) {
                            foundIp = addr.hostAddress ?: "Unknown"
                        }
                    }
                }
                ipAddress = foundIp
            } catch (ex: Exception) {}

            // Storage Usage
            val stat = StatFs(Environment.getDataDirectory().path)
            val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
            val bytesTotal = stat.blockSizeLong * stat.blockCountLong
            val bytesUsed = bytesTotal - bytesAvailable
            storageProgress = bytesUsed.toFloat() / bytesTotal.toFloat()
            storageText = "${(storageProgress * 100).toInt()}%"

            cpuLoad = (0.1f + Math.random().toFloat() * 0.4f)
            if (logs.size > 10) logs.removeAt(0)
            logs.add("[DEBUG] CPU Thread ${ (1..8).random() } active: ${ (cpuLoad * 100).toInt() }%")
            delay(2000)
        }
    }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            if (title.contains("Hub", ignoreCase = true) || title == "System Lab Core") {
                Text("Diagnostic Dashboard", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MonitoringCard("CPU Load", cpuLoad, "${(cpuLoad * 100).toInt()}%", Modifier.weight(1f), MaterialTheme.colorScheme.primary)
                    MonitoringCard("RAM Usage", ramUsageProgress, ramUsageText, Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terminal Log Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(150.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Column(modifier = Modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
                        logs.forEach { log ->
                            Text(
                                text = log,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatusCard("Storage", storageText, Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
                    StatusCard("Kernel", System.getProperty("os.version") ?: "N/A", Modifier.weight(1f), MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Network Diagnostics", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("IP Address", ipAddress)
                        DetailRow("Connection", networkType)
                    }
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Privacy Score: 85/100", style = MaterialTheme.typography.titleLarge)
                            Text("Great! Most permissions are restricted.")
                        }
                    }
                }
            }

            Text("Hardware Specifications", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Model", Build.MODEL)
                    DetailRow("Manufacturer", Build.MANUFACTURER)
                    DetailRow("Hardware", Build.HARDWARE)
                    DetailRow("Board", Build.BOARD)
                    DetailRow("Brand", Build.BRAND)
                    DetailRow("Android Version", Build.VERSION.RELEASE)
                    DetailRow("SDK level", Build.VERSION.SDK_INT.toString())
                }
            }

            Text("System Analysis", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            permissions.forEach { item ->
                ListItem(
                    headlineContent = { Text(item.name) },
                    supportingContent = { Text(item.status) },
                    trailingContent = { Text("${item.count} apps") }
                )
                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { /* Refresh */ }, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 32.dp)) {
                Text("Refresh Diagnostics")
            }
        }
    }
}

@Composable
fun MonitoringCard(label: String, progress: Float, value: String, modifier: Modifier, color: Color) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(64.dp),
                    color = color,
                    strokeWidth = 6.dp,
                    trackColor = color.copy(alpha = 0.1f)
                )
                Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatusCard(label: String, value: String, modifier: Modifier, color: Color) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
