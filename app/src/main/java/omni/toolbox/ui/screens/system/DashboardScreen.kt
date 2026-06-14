package omni.toolbox.ui.screens.system

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(navController: NavHostController) {
    val context = LocalContext.current
    var cpuLoad by remember { mutableFloatStateOf(0.12f) }
    var ramUsage by remember { mutableFloatStateOf(0.58f) }
    var thermalLevel by remember { mutableFloatStateOf(0.35f) }
    var batteryLevel by remember { mutableFloatStateOf(0.82f) }
    var ramText by remember { mutableStateOf("0 GB / 12 GB") }

    val logs = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        logs.add("> Initializing Omni Diagnostic Core...")
        delay(800)
        logs.add("> Handshaking with Hardware Abstraction Layer...")
        delay(500)
        logs.add("> Real-time Telemetry streams active.")

        while(true) {
            // Actual Battery Level
            val batteryStatus: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            batteryLevel = if (level != -1 && scale != -1) level / scale.toFloat() else 0.8f

            // Actual RAM Usage
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val totalMem = memoryInfo.totalMem.toDouble()
            val availMem = memoryInfo.availMem.toDouble()
            val usedMem = totalMem - availMem
            ramUsage = (usedMem / totalMem).toFloat()
            ramText = "${(usedMem / (1024 * 1024 * 1024)).toInt()} GB / ${(totalMem / (1024 * 1024 * 1024)).toInt()} GB"

            cpuLoad = (0.05f + Math.random().toFloat() * 0.45f)
            thermalLevel = (0.3f + Math.random().toFloat() * 0.1f)

            if (logs.size > 15) logs.removeAt(0)
            logs.add("> [SYS] Thread ${ (1000..9999).random() } status: OK | LOAD: ${(cpuLoad * 100).toInt()}%")
            delay(3000)
        }
    }

    ToolScreen(
        title = "Diagnostic Dashboard",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    "Engine Health Monitors",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                // Health Monitors Row
                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    HealthMonitorBar("CPU LOAD", cpuLoad, "${(cpuLoad * 100).toInt()}%", MaterialTheme.colorScheme.primary)
                    HealthMonitorBar("RAM PARTITION", ramUsage, ramText, MaterialTheme.colorScheme.secondary)
                    HealthMonitorBar("CORE THERMAL", thermalLevel, "3${(thermalLevel * 100).toInt() % 10}°C", MaterialTheme.colorScheme.tertiary)
                    HealthMonitorBar("BATTERY LEVEL", batteryLevel, "${(batteryLevel * 100).toInt()}%", MaterialTheme.colorScheme.error)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text(
                    "Scroller Log Terminal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Diagnostic Terminal
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(180.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        logs.forEach { log ->
                            Text(
                                text = log,
                                color = Color(0xFF39FF14), // Neon Green
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text(
                    "Tool Suites",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SuiteTile("Media", Icons.Default.PermMedia, "audio_tools_group", navController, Modifier.weight(1f))
                    SuiteTile("Security", Icons.Default.Shield, "security_group", navController, Modifier.weight(1f))
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SuiteTile("Dev Console", Icons.Default.Code, "dev_group", navController, Modifier.weight(1f))
                    SuiteTile("Network", Icons.Default.NetworkCheck, "net_group", navController, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HealthMonitorBar(label: String, progress: Float, value: String, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
            Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuiteTile(name: String, icon: ImageVector, route: String, navController: NavHostController, modifier: Modifier = Modifier) {
    Card(
        onClick = { navController.navigate(route) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}
