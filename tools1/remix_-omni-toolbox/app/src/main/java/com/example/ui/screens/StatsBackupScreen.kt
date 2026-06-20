package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import com.example.ui.CallLogItem
import com.example.ui.SmsItem
import com.example.ui.RestoreLog
import com.example.ui.AppScreenTime
import com.example.ui.AppDataUsage
import com.example.ui.UnlockLogEvent
import com.example.ui.NotificationLogEvent
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsBackupScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: System & Data, 1: Screen & Locks, 2: Notifications Log, 3: Backup Recovery

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Diagnostic Stats Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Telemetry & Logs Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            // Horizontal scrollable Tab row or custom chips for 4 tabs of premium looks
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp,
                divider = { Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)) }
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Dns, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📡 System & Data", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EdgesensorHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📱 Screen & Unlocks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🔔 Notification Log", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("💾 Backup Recovery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

// ==========================================
// TAB 0: SYSTEM & DATA TAB
// ==========================================
@Composable
fun SystemDataTab(viewModel: OmniViewModel) {
    val haptic = LocalHapticFeedback.current
    val systemHealth by viewModel.systemHealth.collectAsState()
    val wifiUsed by viewModel.wifiDataUsedMb.collectAsState()
    val mobileUsed by viewModel.mobileDataUsedMb.collectAsState()
    val mobileLimit by viewModel.mobileDataLimitMb.collectAsState()
    val topAppsData by viewModel.topAppsDataUsage.collectAsState()

    var systemStatsHistory by remember { mutableStateOf(List(12) { Random.nextFloat() * 30f + 15f }) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            systemStatsHistory = (systemStatsHistory.drop(1) + (Random.nextFloat() * 35f + 15f))
        }
    }

    val isLimitExceeded = mobileUsed >= mobileLimit

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        // System Diagnostical load chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F121C)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "PROCESSOR DYNAMIC BANDWIDTH LOAD",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF88)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00E676).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("LIVE INTERPRETATION", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathPoints = systemStatsHistory.size
                            val stepX = size.width / (pathPoints - 1)
                            val maxVal = 100f

                            // Draw horizontal grid lines
                            for (i in 1..3) {
                                val yLine = size.height * (i / 4f)
                                drawLine(
                                    color = Color.White.copy(alpha = 0.05f),
                                    start = Offset(0f, yLine),
                                    end = Offset(size.width, yLine)
                                )
                            }

                            // Plot dynamic lines
                            for (i in 0 until pathPoints - 1) {
                                val x1 = i * stepX
                                val y1 = size.height - (systemStatsHistory[i] / maxVal) * size.height
                                val x2 = (i + 1) * stepX
                                val y2 = size.height - (systemStatsHistory[i + 1] / maxVal) * size.height

                                drawLine(
                                    color = Color(0xFF00FF88),
                                    start = Offset(x1, y1),
                                    end = Offset(x2, y2),
                                    strokeWidth = 3f
                                )

                                drawCircle(
                                    color = Color(0xFF00FF88),
                                    radius = 3.5f,
                                    center = Offset(x2, y2)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SYSTEM TEMP: ${String.format("%.1f", systemHealth.temperatureC)}°C", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        Text("RAM: ${systemHealth.memoryUsedMb}MB / ${systemHealth.memoryMaxMb}MB", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        Text("CPU: ${String.format("%.1f", systemHealth.cpuLoad)}%", fontSize = 9.sp, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Warning trigger badge
        if (isLimitExceeded) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Alert", tint = MaterialTheme.colorScheme.error)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("MOBILE DATA LIMIT BREACHED", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("Your cumulative simulated cellular traffic exceeds the configured daily budget constraint of ${mobileLimit.toInt()} MB.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }

        // Live Data usage stats summary cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Wi-Fi Stats card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = "WiFi", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text("Wi-Fi DATA", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val formattedWifi = if (wifiUsed > 1024) "${String.format("%.2f", wifiUsed / 1024f)} GB" else "${wifiUsed.toInt()} MB"
                        Text(formattedWifi, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Limit: Uncapped", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Mobile Stats card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isLimitExceeded) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.SignalCellularAlt, contentDescription = "Mobile Data", tint = if (isLimitExceeded) MaterialTheme.colorScheme.error else Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                            Text("MOBILE TRAFFIC", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${mobileUsed.toInt()} MB", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (isLimitExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                        Text("Limit budget: ${mobileLimit.toInt()} MB", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Set limits slider configuration
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily Mobile Data Budget Limit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${mobileLimit.toInt()} MB", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = mobileLimit,
                        onValueChange = { viewModel.updateMobileDataLimit(it) },
                        valueRange = 100f..1000f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text("Configuring cellular limits flags safe boundary triggers in background tasks.", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }

        // Simulator inputs buttons row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.simulatedDataConsumption(15f, 120f)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Consume Data (+120M Wi-Fi / +15M Mob)", fontSize = 10.sp)
                }

                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.simulatedDataConsumption(110f, 0f) // Instantly breach budget
                    },
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Breach cellular budget", fontSize = 10.sp)
                }
            }
        }

        // Top Apps data list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.AlignVerticalBottom, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text("INDIVIDUAL APP TRAFFIC BREAKDOWN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
        }

        // List item components for app data
        items(topAppsData) { app ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (app.appName) {
                                "Chrome" -> Icons.Default.Language
                                "YouTube" -> Icons.Default.PlayCircleOutline
                                "Instagram" -> Icons.Default.CameraAlt
                                "WhatsApp" -> Icons.Default.Forum
                                else -> Icons.Default.Android
                            }
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(app.appName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Package: com.android.${app.appName.lowercase()}", fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Wi-Fi: ${app.wifiUsedMb.toInt()} MB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Mobile: ${app.mobileUsedMb.toInt()} MB", fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: SCREEN & UNLOCKS TAB
// ==========================================
@Composable
fun ScreenUnlocksTab(viewModel: OmniViewModel) {
    val haptic = LocalHapticFeedback.current
    val screenMinutes by viewModel.screenTimeTodayMinutes.collectAsState()
    val screenLimit by viewModel.screenLimitMinutes.collectAsState()
    val topAppsScreen by viewModel.topAppsScreenTime.collectAsState()
    val unlocksToday by viewModel.screenUnlocksToday.collectAsState()
    val unlockEvents by viewModel.unlockEvents.collectAsState()
    val hourlyUnlocks by viewModel.unlocksByHour.collectAsState()

    val isScreenExceeded = screenMinutes >= screenLimit

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        // High polish circular Screentime chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("CUMULATIVE SCREEN OCCUPANCY TODAY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)

                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background circle
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Color.Gray.copy(alpha = 0.12f),
                                startAngle = 135f,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // Progress circle
                        val progressSweep = (screenMinutes.toFloat() / screenLimit.toFloat()).coerceAtMost(1f) * 270f
                        val circleColor = if (isScreenExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = circleColor,
                                startAngle = 135f,
                                sweepAngle = progressSweep,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val hrs = screenMinutes / 60
                            val mins = screenMinutes % 60
                            Text(
                                text = "${hrs}h ${mins}m",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = if (isScreenExceeded) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                            Text("Goal: ${screenLimit / 60} hours", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    if (isScreenExceeded) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Text(
                                "⚠️ ALERT: DAILY SCREEN TIME BUDGET SURPASSED!",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Slider to change screentime limit
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Screentime Alert Safety Trigger", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${screenLimit / 60} Hour (${screenLimit}m)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = screenLimit.toFloat(),
                        onValueChange = { viewModel.updateScreenLimit(it.toInt()) },
                        valueRange = 60f..480f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Screen unlocks stats big number and custom hourly locks canvas chart
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SCREEN UNLOCKS COUNTER", fontSize = 10.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                            Text("$unlocksToday unlocks today", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }

                    // Hourly unlocks premium Canvas graph chart visualization
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxCount = maxOf(3, hourlyUnlocks.maxOrNull() ?: 3).toFloat()
                            val barWidth = (size.width / 24f) * 0.75f
                            val barSpacing = (size.width / 24f) * 0.25f

                            for (i in 0..23) {
                                val currentHourCount = hourlyUnlocks[i]
                                val barHeight = (currentHourCount.toFloat() / maxCount) * size.height
                                val x = i * (barWidth + barSpacing) + (barSpacing / 2)
                                val y = size.height - barHeight

                                drawRect(
                                    color = if (currentHourCount > 0) Color(0xFF00E676) else Color.White.copy(alpha = 0.08f),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, maxOf(2f, barHeight))
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("AM (00h-11h)", fontSize = 8.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        Text("PM (12h-23h)", fontSize = 8.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Action Simulation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.incrementScreenTime(15)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add 15m screentime", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.simulateScreenUnlock()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate screen unlock", fontSize = 11.sp)
                }
            }
        }

        // Section header top apps minutes
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text("MOST USED APPLICATIONS TRACKING", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
        }

        // Apps screen list items
        items(topAppsScreen) { app ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val icon = when (app.iconName) {
                                "youtube" -> Icons.Default.PlayArrow
                                "instagram" -> Icons.Default.CameraAlt
                                "whatsapp" -> Icons.Default.Forum
                                "chrome" -> Icons.Default.Language
                                else -> Icons.Default.Android
                            }
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(app.appName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(app.packageName, fontSize = 9.sp, color = Color.Gray)
                        }
                    }

                    Text("${app.usageMinutes}m", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Unlock historical timestamps logs list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Text("HISTORICAL LOGS: TERMINAL SECURITY LOCK", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            }
        }

        if (unlockEvents.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    Text("No security unlock history event logged.", color = Color.Gray, fontSize = 11.sp)
                }
            }
        } else {
            items(unlockEvents.take(5)) { ev ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Decrypted via: ${ev.unlockMethod}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(ev.timestamp, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: NOTIFICATION LOG TAB
// ==========================================
@Composable
fun NotificationLogTab(viewModel: OmniViewModel) {
    val haptic = LocalHapticFeedback.current
    val totalCount by viewModel.notificationsCountToday.collectAsState()
    val notificationLogs by viewModel.notificationLogs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    // Interceptor inputs
    var inputAppName by remember { mutableStateOf("WhatsApp") }
    var inputSender by remember { mutableStateOf("") }
    var inputBody by remember { mutableStateOf("") }
    var inputCategory by remember { mutableStateOf("Communication") }
    var isInsertingSim by remember { mutableStateOf(false) }

    val filteredLogs = remember(notificationLogs, searchQuery, selectedCategoryFilter) {
        notificationLogs.filter { log ->
            val matchQuery = log.appName.contains(searchQuery, ignoreCase = true) ||
                             log.sender.contains(searchQuery, ignoreCase = true) ||
                             log.content.contains(searchQuery, ignoreCase = true)
            val matchCategory = if (selectedCategoryFilter == "All") true else log.category == selectedCategoryFilter
            matchQuery && matchCategory
        }
    }

    // Pie chart Arc calculations
    val categoryCounts = remember(notificationLogs) {
        val countMap = mutableMapOf(
            "Communication" to 0,
            "Social" to 0,
            "System" to 0,
            "Productivity" to 0
        )
        notificationLogs.forEach { log ->
            countMap[log.category] = (countMap[log.category] ?: 0) + 1
        }
        countMap
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
    ) {
        // Notification Stats doughnut / arc chart card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("INTERCEPTED ALERTS DISTRIBUTION", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        Text("$totalCount alerts captured today", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        
                        Divider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))
                        
                        // Category legend markers
                        val categoriesList = listOf("Communication", "Social", "System", "Productivity")
                        val colorsList = listOf(Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF4CAF50), Color(0xFFFF9800))
                        
                        categoriesList.forEachIndexed { idx, cat ->
                            val count = categoryCounts[cat] ?: 0
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colorsList[idx]))
                                Text("$cat: $count items", fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    // Circular arc canvas
                    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val total = notificationLogs.size.toFloat()
                            if (total == 0f) {
                                drawArc(
                                    color = Color.White.copy(alpha = 0.1f),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx())
                                )
                            } else {
                                var startingAngle = 0f
                                val categoriesList = listOf("Communication", "Social", "System", "Productivity")
                                val colorsList = listOf(Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF4CAF50), Color(0xFFFF9800))

                                categoriesList.forEachIndexed { idx, cat ->
                                    val catCount = (categoryCounts[cat] ?: 0).toFloat()
                                    if (catCount > 0f) {
                                        val sweep = (catCount / total) * 360f
                                        drawArc(
                                            color = colorsList[idx],
                                            startAngle = startingAngle,
                                            sweepAngle = sweep,
                                            useCenter = false,
                                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                        startingAngle += sweep
                                    }
                                }
                            }
                        }
                        Icon(Icons.Default.LabelImportant, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Custom simulated input form expand trigger
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulate Notification Interception", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        IconButton(onClick = { isInsertingSim = !isInsertingSim }) {
                            Icon(imageVector = if (isInsertingSim) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                        }
                    }

                    if (isInsertingSim) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Row options app
                            Text("Select Triggering App Network Node", fontSize = 9.sp, color = Color.Gray)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(listOf("WhatsApp", "Instagram", "System", "Slack", "Gmail")) { app ->
                                    FilterChip(
                                        selected = inputAppName == app,
                                        onClick = {
                                            inputAppName = app
                                            inputCategory = when (app) {
                                                "WhatsApp" -> "Communication"
                                                "Instagram" -> "Social"
                                                "System" -> "System"
                                                "Slack" -> "Productivity"
                                                else -> "Communication"
                                            }
                                        },
                                        label = { Text(app, fontSize = 9.sp) }
                                    )
                                }
                            }

                            // Inputs
                            TextField(
                                value = inputSender,
                                onValueChange = { inputSender = it },
                                placeholder = { Text("Sender contact name...", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                singleLine = true,
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                            )

                            TextField(
                                value = inputBody,
                                onValueChange = { inputBody = it },
                                placeholder = { Text("Notification body string...", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                textStyle = MaterialTheme.typography.bodySmall,
                                singleLine = true,
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                            )

                            Button(
                                onClick = {
                                    val finalSender = inputSender.ifEmpty { "System Broadcast" }
                                    val finalBody = inputBody.ifEmpty { "Test diagnostic rule logs verified successfully." }
                                    viewModel.simulateNotificationReceived(inputAppName, finalSender, finalBody, inputCategory)
                                    inputSender = ""
                                    inputBody = ""
                                    isInsertingSim = false
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Dispatch Notification Event Trigger", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Search and Category filtering row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search intercepted notifications body...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(listOf("All", "Communication", "Social", "System", "Productivity")) { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = {
                                selectedCategoryFilter = cat
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            label = { Text(cat, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }

        // Header and Clear buttons logs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "INTERCEPTED REALTIME DATABASE LOGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.clearNotificationLogs()
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Clear captured logs", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.NotInterested, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Text("No notifications matching filtering constraints.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text("Use helper form above to manually inject dynamic warnings.", fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(filteredLogs) { log ->
                val cardBorderColor = when (log.category) {
                    "Communication" -> Color(0xFF2196F3).copy(alpha = 0.3f)
                    "Social" -> Color(0xFFE91E63).copy(alpha = 0.3f)
                    "System" -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                    "Productivity" -> Color(0xFFFF9800).copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, cardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (log.category) {
                                                "Communication" -> Color(0xFF2196F3)
                                                "Social" -> Color(0xFFE91E63)
                                                "System" -> Color(0xFF4CAF50)
                                                else -> Color(0xFFFF9800)
                                            }
                                        )
                                )
                                Text(
                                    text = log.appName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(log.timestamp, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.Gray)
                        }

                        Text("From: ${log.sender}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(log.content, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .align(Alignment.End)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(log.category.uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: COMPLETE BACKUP RECOVERY FLOWS (PRESERVED MODIFIED CODES)
// ==========================================
@Composable
fun CallSmsBackupTab(viewModel: OmniViewModel) {
    var toolSubTab by remember { mutableStateOf(0) } // 0: Calls, 1: SMS Inbox, 2: GDrive Setup, 3: Core Snapshots Restore
    val callLogs by viewModel.callLogs.collectAsState()
    val smsMessages by viewModel.smsMessages.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val restoreLogs by viewModel.restoreLogs.collectAsState()
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Interactive simulator inputs
    var showCallForm by remember { mutableStateOf(false) }
    var inputCallName by remember { mutableStateOf("") }
    var inputCallNumber by remember { mutableStateOf("") }
    var inputCallType by remember { mutableStateOf("INCOMING") }
    var inputCallDuration by remember { mutableStateOf("2m 45s") }

    var showSmsForm by remember { mutableStateOf(false) }
    var inputSmsSender by remember { mutableStateOf("") }
    var inputSmsContent by remember { mutableStateOf("") }
    var inputSmsType by remember { mutableStateOf("INBOX") }

    // Backup actions logs
    var backupProgress by remember { mutableStateOf<Float?>(null) }
    var backupStatusText by remember { mutableStateOf("") }
    var connectedEmail by remember { mutableStateOf("subbu.edu.68@gmail.com") }

    // Search and Filter States for Calls & SMS
    var callSearchQuery by remember { mutableStateOf("") }
    var callFilterMode by remember { mutableStateOf("ALL") }

    var smsSearchQuery by remember { mutableStateOf("") }
    var smsFilterMode by remember { mutableStateOf("ALL") }

    val filteredCallLogs = remember(callLogs, callSearchQuery, callFilterMode) {
        callLogs.filter { log ->
            val matchQuery = log.name.contains(callSearchQuery, ignoreCase = true) || log.number.contains(callSearchQuery, ignoreCase = true)
            val matchFilter = when (callFilterMode) {
                "INCOMING" -> log.type == "INCOMING"
                "OUTGOING" -> log.type == "OUTGOING"
                "MISSED" -> log.type == "MISSED"
                "SELECTED" -> log.isSelected
                else -> true
            }
            matchQuery && matchFilter
        }
    }

    val filteredSmsMessages = remember(smsMessages, smsSearchQuery, smsFilterMode) {
        smsMessages.filter { msg ->
            val matchQuery = msg.sender.contains(smsSearchQuery, ignoreCase = true) || msg.content.contains(smsSearchQuery, ignoreCase = true)
            val matchFilter = when (smsFilterMode) {
                "INBOX" -> msg.type == "INBOX"
                "SENT" -> msg.type == "SENT"
                "SELECTED" -> msg.isSelected
                else -> true
            }
            matchQuery && matchFilter
        }
    }

    // Tab items nested inside Call/SMS Backup
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("📞 Calls List", "💬 SMS Records", "☁️ GDrive Sync", "♻️ Restore Points").forEachIndexed { index, title ->
                FilterChip(
                    selected = toolSubTab == index,
                    onClick = { 
                        toolSubTab = index 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    label = { Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (toolSubTab) {
            0 -> {
                // Call logs list inner tab code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val selectedCalls = callLogs.filter { it.isSelected }
                    Text(
                        text = "CALL LOG HISTORY (${selectedCalls.size}/${callLogs.size} selected)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { viewModel.toggleAllCallLogs(true) }) { Text("All", fontSize = 10.sp) }
                        TextButton(onClick = { viewModel.toggleAllCallLogs(false) }) { Text("None", fontSize = 10.sp) }
                        IconButton(onClick = { showCallForm = !showCallForm }) {
                            Icon(imageVector = if (showCallForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                TextField(
                    value = callSearchQuery,
                    onValueChange = { callSearchQuery = it },
                    placeholder = { Text("Search by contact name or number...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )

                if (showCallForm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Simulate Input Call Log", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextField(value = inputCallName, onValueChange = { inputCallName = it }, label = { Text("Name", fontSize = 9.sp) }, modifier = Modifier.weight(1f))
                                TextField(value = inputCallNumber, onValueChange = { inputCallNumber = it }, label = { Text("Number", fontSize = 9.sp) }, modifier = Modifier.weight(1f))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                listOf("INCOMING", "OUTGOING", "MISSED").forEach { t ->
                                    FilterChip(selected = inputCallType == t, onClick = { inputCallType = t }, label = { Text(t, fontSize = 8.sp) })
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                            Button(
                                onClick = {
                                    val name = inputCallName.ifEmpty { "Unknown Caller" }
                                    val num = inputCallNumber.ifEmpty { "+91 99000 11223" }
                                    viewModel.addCallLog(name, num, inputCallType, inputCallDuration)
                                    inputCallName = ""
                                    inputCallNumber = ""
                                    showCallForm = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Inject Call History Instance", fontSize = 11.sp)
                            }
                        }
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredCallLogs) { call ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (call.isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val iconColor = when (call.type) {
                                    "INCOMING" -> Color(0xFF4CAF50)
                                    "OUTGOING" -> Color(0xFF2196F3)
                                    else -> Color(0xFFF44336)
                                }
                                Box(
                                    modifier = Modifier.size(28.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (call.type == "INCOMING") Icons.Default.CallReceived else if (call.type == "OUTGOING") Icons.Default.CallMade else Icons.Default.CallMissed,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(call.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("${call.number} • ${call.duration}", fontSize = 10.sp, color = Color.Gray)
                                }
                                Checkbox(checked = call.isSelected, onCheckedChange = { viewModel.toggleCallLogSelected(call.id) })
                            }
                        }
                    }
                }

                val selectedCallsCount = callLogs.count { it.isSelected }
                Button(
                    onClick = {
                        if (selectedCallsCount == 0) {
                            backupStatusText = "Error: Please check at least one call log to backup."
                            return@Button
                        }
                        backupProgress = 0.0f
                        backupStatusText = "Initiating cloud handshakes..."
                        coroutineScope.launch {
                            delay(600)
                            backupProgress = 0.4f
                            backupStatusText = "Deploying calls backup dump to GDrive sandbox..."
                            delay(800)
                            val textBuilder = StringBuilder()
                            textBuilder.append("=== GOOGLE DRIVE CALL LOGS BACKUP ===\n")
                            textBuilder.append("AccountEmail: $connectedEmail\n")
                            textBuilder.append("Timestamp: ${System.currentTimeMillis()}\n")
                            textBuilder.append("ItemsCount: $selectedCallsCount\n")
                            textBuilder.append("===\n")
                            callLogs.filter { it.isSelected }.forEach { c ->
                                textBuilder.append("Call|${c.name}|${c.number}|${c.type}|${c.timestamp}|${c.duration}\n")
                            }
                            val timestampSuffix = Random.nextInt(1000, 9999)
                            viewModel.addDocument("OMNITOOL_GD_CALLS_BACKUP_$timestampSuffix.json", "TXT", textBuilder.toString())
                            backupProgress = null
                            backupStatusText = "SUCCESS: Uploaded $selectedCallsCount logs to Drive folder My Drive > OmniToolbox."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupProgress == null
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back Up Selected Calls to Google Drive", fontSize = 11.sp)
                }
            }

            1 -> {
                // SMS Records tab code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val checkedSms = smsMessages.filter { it.isSelected }
                    Text(
                        text = "SMS MESSAGES INBOX (${checkedSms.size}/${smsMessages.size} selected)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { viewModel.toggleAllSms(true) }) { Text("All", fontSize = 10.sp) }
                        TextButton(onClick = { viewModel.toggleAllSms(false) }) { Text("None", fontSize = 10.sp) }
                        IconButton(onClick = { showSmsForm = !showSmsForm }) {
                            Icon(imageVector = if (showSmsForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                TextField(
                    value = smsSearchQuery,
                    onValueChange = { smsSearchQuery = it },
                    placeholder = { Text("Search by sender or body...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )

                if (showSmsForm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Simulate Input SMS Message", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextField(value = inputSmsSender, onValueChange = { inputSmsSender = it }, label = { Text("Sender", fontSize = 9.sp) }, modifier = Modifier.weight(1f))
                                Row(modifier = Modifier.weight(1.5f)) {
                                    listOf("INBOX", "SENT").forEach { t ->
                                        FilterChip(selected = inputSmsType == t, onClick = { inputSmsType = t }, label = { Text(t, fontSize = 8.sp) })
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                }
                            }
                            TextField(value = inputSmsContent, onValueChange = { inputSmsContent = it }, label = { Text("Content", fontSize = 9.sp) }, modifier = Modifier.fillMaxWidth())
                            Button(
                                onClick = {
                                    val sender = inputSmsSender.ifEmpty { "98420-ALRT" }
                                    val content = inputSmsContent.ifEmpty { "Test message body placeholder logs." }
                                    viewModel.addSmsMessage(sender, content, inputSmsType)
                                    inputSmsSender = ""
                                    inputSmsContent = ""
                                    showSmsForm = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Inject SMS Message Record", fontSize = 11.sp)
                            }
                        }
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredSmsMessages) { sms ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (sms.isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(28.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (sms.type == "INBOX") Icons.Default.Sms else Icons.Default.Send,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sms.sender, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(sms.content, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                                }
                                Checkbox(checked = sms.isSelected, onCheckedChange = { viewModel.toggleSmsSelected(sms.id) })
                            }
                        }
                    }
                }

                val selectedSmsCount = smsMessages.count { it.isSelected }
                Button(
                    onClick = {
                        if (selectedSmsCount == 0) {
                            backupStatusText = "Error: Please check at least one SMS to backup."
                            return@Button
                        }
                        backupProgress = 0.0f
                        backupStatusText = "Preparing payload pack..."
                        coroutineScope.launch {
                            delay(600)
                            backupProgress = 0.5f
                            backupStatusText = "Pipes writing to GDrive securely..."
                            delay(800)
                            val textBuilder = StringBuilder()
                            textBuilder.append("=== GOOGLE DRIVE SMS BACKUP ===\n")
                            textBuilder.append("AccountEmail: $connectedEmail\n")
                            textBuilder.append("Timestamp: ${System.currentTimeMillis()}\n")
                            textBuilder.append("ItemsCount: $selectedSmsCount\n")
                            textBuilder.append("===\n")
                            smsMessages.filter { it.isSelected }.forEach { s ->
                                textBuilder.append("Sms|${s.sender}|${s.content}|${s.type}|${s.timestamp}\n")
                            }
                            val timestampSuffix = Random.nextInt(1000, 9999)
                            viewModel.addDocument("OMNITOOL_GD_SMS_BACKUP_$timestampSuffix.json", "TXT", textBuilder.toString())
                            backupProgress = null
                            backupStatusText = "SUCCESS: Synced $selectedSmsCount SMS securely with Google cloud sandbox."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupProgress == null
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back Up Selected SMS to Google Drive", fontSize = 11.sp)
                }
            }

            2 -> {
                // Cloud Integration Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF0F9D58)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Drive Cloud Sandbox", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Active connected nodes", fontSize = 9.sp, color = Color(0xFF00E676))
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Text("Active Connected Identity: $connectedEmail", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text("Workspace Root: /My Drive/OmniToolbox/Phone_Backups/", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        
                        Button(
                            onClick = {
                                connectedEmail = if (connectedEmail == "subbu.edu.68@gmail.com") "sandbox.agent@ai.terminal.com" else "subbu.edu.68@gmail.com"
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Switch Sandbox User Account Node", fontSize = 11.sp)
                        }
                    }
                }
            }

            3 -> {
                // Snapshot restore inner tab code
                BackupsRestoreSubTab(viewModel)
            }
        }

        // Action Status Banner row
        if (backupProgress != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("DRIVE DISPATCH CORE PIPELINE INSTRUCTIONS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontFamily = FontFamily.Monospace)
                    LinearProgressIndicator(progress = backupProgress!!, modifier = Modifier.fillMaxWidth())
                    Text(backupStatusText, fontSize = 9.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontFamily = FontFamily.Monospace)
                }
            }
        } else if (backupStatusText.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(backupStatusText, modifier = Modifier.weight(1f), fontSize = 10.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    IconButton(onClick = { backupStatusText = "" }) { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp)) }
                }
            }
        }
    }
}

// Inner helper component for JSON restore logs list logic
@Composable
fun BackupsRestoreSubTab(viewModel: OmniViewModel) {
    var expandedBackupId by remember { mutableStateOf<Int?>(null) }
    var restoreProgress by remember { mutableStateOf<Float?>(null) }
    var isRestoring by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("") }

    val documents by viewModel.documents.collectAsState()
    val restoreLogs by viewModel.restoreLogs.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val gdriveBackups = documents.filter { 
        it.fileName.contains("BACKUP") || it.fileName.contains("OMNITOOL_GD_")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Create Full Database Core Snapshot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Triggers a manual backup dump of all key local database schemas into local TxT files index instantly.", fontSize = 10.sp, color = Color.Gray)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val idSuffix = Random.nextInt(1000, 9999)
                            viewModel.addDocument("OMNITOOL_MAN_SNAPSHOT_$idSuffix.json", "TXT", "=== OMNI TOOLBOX SQLITE SNAPSHOT ===\nVersion: 2\nStatus: Complete Saved Offline")
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Trigger Database Snapshot", fontSize = 11.sp)
                }
            }
        }

        if (gdriveBackups.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No manual recovery point json files detected on client.", fontSize = 11.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.heightIn(max = 240.dp)
            ) {
                items(gdriveBackups) { b ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.BackupTable, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(b.fileName, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        isRestoring = true
                                        statusText = "Syncing binary packets..."
                                        coroutineScope.launch {
                                            delay(500)
                                            val lines = b.content.split("\n")
                                            if (b.content.contains("=== GOOGLE DRIVE CALL LOGS BACKUP ===")) {
                                                val callItems = lines.filter { it.startsWith("Call|") }.map {
                                                    val parts = it.split("|")
                                                    CallLogItem(
                                                        id = Random.nextInt(10000, 99999).toString(),
                                                        name = parts[1],
                                                        number = parts[2],
                                                        type = parts[3],
                                                        timestamp = parts[4],
                                                        duration = parts[5],
                                                        isSelected = true
                                                    )
                                                }
                                                if (callItems.isNotEmpty()) {
                                                    viewModel.restoreCallLogsAndSms(callItems, viewModel.smsMessages.value)
                                                }
                                                viewModel.logRestoreEvent(b.fileName, "Calls Restore", callItems.size, "subbu.edu.68@gmail.com")
                                            } else if (b.content.contains("=== GOOGLE DRIVE SMS BACKUP ===")) {
                                                val smsItems = lines.filter { it.startsWith("Sms|") }.map {
                                                    val parts = it.split("|")
                                                    SmsItem(
                                                        id = Random.nextInt(10000, 99999).toString(),
                                                        sender = parts[1],
                                                        content = parts[2],
                                                        type = parts[3],
                                                        timestamp = parts[4],
                                                        isSelected = true
                                                    )
                                                }
                                                if (smsItems.isNotEmpty()) {
                                                    viewModel.restoreCallLogsAndSms(viewModel.callLogs.value, smsItems)
                                                }
                                                viewModel.logRestoreEvent(b.fileName, "SMS Restore", smsItems.size, "subbu.edu.68@gmail.com")
                                            }
                                            isRestoring = false
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    },
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Restore", fontSize = 10.sp)
                                }
                            }
                            Text(b.content.take(60), fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Historic Restore Activity log list inside inner tab 3
        if (restoreLogs.isNotEmpty()) {
            Text("RESTORE ACTIVITY HISTORICAL HISTORY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.heightIn(max = 140.dp)
            ) {
                items(restoreLogs) { log ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(log.type, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("File: ${log.fileName} • ${log.itemsCount} rows", fontSize = 9.sp, color = Color.Gray)
                            }
                            Text(log.timestamp, fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
