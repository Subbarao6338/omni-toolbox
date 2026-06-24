package omni.toolbox.ui.screens.system

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.*
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun StatsBackupScreen(navController: NavHostController, viewModel: OmniViewModel) {
    var activeTab by remember { mutableIntStateOf(0) }

    ToolScreen(
        title = "Telemetry & Logs Dashboard",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("System & Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EdgesensorHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Screen & Unlocks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Notification Log", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup Recovery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                when (activeTab) {
                    0 -> SystemDataTab(viewModel)
                    1 -> ScreenUnlocksTab(viewModel)
                    2 -> NotificationLogTab(viewModel)
                    3 -> CallSmsBackupTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun SystemDataTab(viewModel: OmniViewModel) {
    val systemHealth by viewModel.systemHealth.collectAsState()
    val wifiUsed by viewModel.wifiDataUsedMb.collectAsState()
    val mobileUsed by viewModel.mobileDataUsedMb.collectAsState()
    val mobileLimit by viewModel.mobileDataLimitMb.collectAsState()
    val topAppsData by viewModel.topAppsDataUsage.collectAsState()
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F121C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SYSTEM DYNAMIC LOAD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00FF88))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("CPU: ${String.format("%.1f", systemHealth.cpuLoad)}%", fontSize = 14.sp, color = Color.White)
                    Text("RAM: ${systemHealth.memoryUsedMb}MB / ${systemHealth.memoryMaxMb}MB", fontSize = 14.sp, color = Color.White)
                    Text("TEMP: ${String.format("%.1f", systemHealth.temperatureC)}°C", fontSize = 14.sp, color = Color.White)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Wi-Fi", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${wifiUsed.toInt()} MB", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Mobile", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${mobileUsed.toInt()} MB", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Button(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.simulatedDataConsumption(15f, 120f)
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Simulate Data Usage")
            }
        }

        items(topAppsData) { app ->
            ListItem(
                headlineContent = { Text(app.appName) },
                supportingContent = { Text("WiFi: ${app.wifiUsedMb.toInt()}MB, Mob: ${app.mobileUsedMb.toInt()}MB") },
                leadingContent = { Icon(Icons.Default.Android, contentDescription = null) }
            )
        }
    }
}

@Composable
fun ScreenUnlocksTab(viewModel: OmniViewModel) {
    val screenMinutes by viewModel.screenTimeTodayMinutes.collectAsState()
    val screenLimit by viewModel.screenLimitMinutes.collectAsState()
    val topAppsScreen by viewModel.topAppsScreenTime.collectAsState()
    val unlocksToday by viewModel.screenUnlocksToday.collectAsState()
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Screen Time Today", fontWeight = FontWeight.Bold)
                    Text("${screenMinutes / 60}h ${screenMinutes % 60}m", fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Limit: ${screenLimit / 60}h", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Unlocks Today", fontWeight = FontWeight.Bold)
                    Text("$unlocksToday", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.incrementScreenTime(15) }, modifier = Modifier.weight(1f)) { Text("+15m Screen") }
                Button(onClick = { viewModel.simulateScreenUnlock() }, modifier = Modifier.weight(1f)) { Text("Simulate Unlock") }
            }
        }

        items(topAppsScreen) { app ->
            ListItem(
                headlineContent = { Text(app.appName) },
                supportingContent = { Text("${app.usageMinutes} minutes") },
                leadingContent = { Icon(Icons.Default.AccessTime, contentDescription = null) }
            )
        }
    }
}

@Composable
fun NotificationLogTab(viewModel: OmniViewModel) {
    val totalCount by viewModel.notificationsCountToday.collectAsState()
    val notificationLogs by viewModel.notificationLogs.collectAsState()
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Notifications Today", fontWeight = FontWeight.Bold)
                    Text("$totalCount", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Button(onClick = {
                viewModel.simulateNotificationReceived("WhatsApp", "Subbu", "New sync rules ready.", "Communication")
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Simulate Notification")
            }
        }

        items(notificationLogs) { log ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(log.appName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(log.timestamp, fontSize = 10.sp, color = Color.Gray)
                    }
                    Text("From: ${log.sender}", fontWeight = FontWeight.Medium)
                    Text(log.content, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CallSmsBackupTab(viewModel: OmniViewModel) {
    val callLogs by viewModel.callLogs.collectAsState()
    val smsMessages by viewModel.smsMessages.collectAsState()
    val restoreLogs by viewModel.restoreLogs.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isBackingUp by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cloud Backup Status", fontWeight = FontWeight.Bold)
                    Text("Last synced: Just Now", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = {
                        isBackingUp = true
                        coroutineScope.launch {
                            delay(2000)
                            viewModel.logRestoreEvent("backup_auto.json", "Core Snapshot", 120, "user@omni.com")
                            isBackingUp = false
                        }
                    }, enabled = !isBackingUp, modifier = Modifier.fillMaxWidth()) {
                        if (isBackingUp) CircularProgressIndicator(size = 20.dp)
                        else Text("Backup to Google Drive")
                    }
                }
            }
        }

        item { Text("Recent Restore Events", fontWeight = FontWeight.Bold, fontSize = 14.sp) }

        items(restoreLogs) { log ->
            ListItem(
                headlineContent = { Text(log.type) },
                supportingContent = { Text("${log.fileName} • ${log.itemsCount} items") },
                trailingContent = { Text(log.timestamp, fontSize = 10.sp) }
            )
        }
    }
}
