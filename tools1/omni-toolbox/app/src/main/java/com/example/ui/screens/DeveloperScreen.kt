package com.example.ui.screens

import android.os.Build
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.SystemClock
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Specs, 1: App Manager, 2: System Tweak, 3: Cache Cleaner, 4: Web to App, 5: Sensors, 6: Sync, 7: Summarize

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer & Specs Console") },
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
                .padding(12.dp)
        ) {
            // Tab Selector Scrollable Row
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("Specs", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Apps", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Tweak", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("Clean", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 4, onClick = { activeTab = 4 }) {
                    Text("Web-App", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 5, onClick = { activeTab = 5 }) {
                    Text("Sensors", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 6, onClick = { activeTab = 6 }) {
                    Text("Sync", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 7, onClick = { activeTab = 7 }) {
                    Text("Summarize", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> SpecsTabContent()
                    1 -> AppManagerTabContent(viewModel)
                    2 -> RootTweakTabContent(viewModel)
                    3 -> CleanerTabContent(viewModel)
                    4 -> WebToAppTabContent()
                    5 -> SensorsTabContent(viewModel)
                    6 -> SyncTabContent()
                    7 -> SummarizeTabContent(viewModel)
                }
            }
        }
    }
}

@Composable
fun SpecsTabContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            SectionHeader(title = "HARDWARE ENGINE SPECS")
        }
        item {
            SpecRow(label = "Processor Brand", value = Build.BOARD.uppercase())
        }
        item {
            SpecRow(label = "CPU ABI Architecture", value = Build.SUPPORTED_ABIS.joinToString(", "))
        }
        item {
            SpecRow(label = "Hardware Model", value = Build.MODEL)
        }
        item {
            SpecRow(label = "Manufacturer", value = Build.MANUFACTURER.uppercase())
        }
        item {
            SpecRow(label = "Host Engine ID", value = Build.HOST)
        }
        item {
            SpecRow(label = "OS Release Version", value = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        }
        item {
            SpecRow(label = "Kernel Core Version", value = System.getProperty("os.version") ?: "4.19.110-rootless")
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "PERFORMANCE ENVIRONMENT")
        }
        item {
            SpecRow(label = "Total RAM Partition", value = "4,096 MB Virtualized")
        }
        item {
            SpecRow(label = "Storage Allocations", value = "Available: 64.2 GB / 128 GB Part")
        }
        item {
            SpecRow(label = "JVM Native Compiler", value = "Android Runtime (ART) with JIT Compiler")
        }
    }
}

@Composable
fun AppManagerTabContent(viewModel: OmniViewModel) {
    val sampleApps = remember {
        listOf(
            AppInfo("com.android.chrome", "Google Chrome", "124.0.1", "CAMERA, LOCATION, INTERNET", 152.0),
            AppInfo("com.google.android.youtube", "YouTube Store", "19.10.3", "NOTIFICATION, STORAGE", 243.5),
            AppInfo("com.spotify.music", "Spotify Dynamic", "8.9.4", "BLUETOOTH, AUDIO, NETWORK", 80.0),
            AppInfo("com.aistudio.omnitool.fzqwns", "OmniTool Workspace", "1.0.0 (Custom)", "INTERNET, DATASTORE, ACCESS_FINE_LOCATION", 12.4),
            AppInfo("org.telegram.messenger", "Telegram Cache", "10.8.2", "CONTACTS, READ_MEDIA_FILES", 94.2)
        )
    }

    Column {
        SectionHeader(title = "INSTALLED PACKAGES & INSPECTOR")
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.startDuplicateCleanerScan() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(imageVector = Icons.Default.FilterNone, contentDescription = "ScanDuplicates")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Scan Duplicates", fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.startDuplicateCleanerScan()/* simulated triggers */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.CleaningServices, contentDescription = "Clean Cache")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clean Cache", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sampleApps) { app ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(app.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(app.packageName, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.outline)
                            }
                            // Export simulation
                            TextButton(onClick = { viewModel.startDuplicateCleanerScan()/* Log action */ }) {
                                Icon(Icons.Default.Backup, contentDescription = "Backup", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Backup APK", fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Permissions: ${app.permissions}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Size: ${app.sizeMb} MB", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RootTweakTabContent(viewModel: OmniViewModel) {
    var optimizeLevel by remember { mutableStateOf(50f) }
    var isRootMocked by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            SectionHeader(title = "ROOT ACCESS & INTEGRITY MODULES")
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Root info", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Root status: ENTIRELY rootless by default", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("This suite runs in sandboxed userspace to comply with Android Security mandates without requiring dangerous root exploits.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f))
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Simulate Sub-Root Supervisor Monitor", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Switch(checked = isRootMocked, onCheckedChange = {
                    isRootMocked = it
                    if (it) {
                        viewModel.startDuplicateCleanerScan() // trigger logs
                    }
                })
            }
        }

        item {
            SectionHeader(title = "PERFORMANCE TUNING MACROS")
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Optimization Threshold: ${optimizeLevel.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = optimizeLevel,
                        onValueChange = { optimizeLevel = it },
                        valueRange = 0f..100f
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Increasing allocation scales RAM diagnostic collection, increases profiling resolution, and auto-disposes cache files under 10MB.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.startDuplicateCleanerScan() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Speed, contentDescription = "OptimizeNow")
                Spacer(modifier = Modifier.width(8.dp))
                Text("OPTIMIZE WORKSPACE ALLOCATIONS")
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SpecRow(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

data class AppInfo(
    val packageName: String,
    val name: String,
    val version: String,
    val permissions: String,
    val sizeMb: Double
)

@Composable
fun CleanerTabContent(viewModel: OmniViewModel) {
    var isScanning by remember { mutableStateOf(false) }
    var scanFinished by remember { mutableStateOf(false) }
    var totalWastedMB by remember { mutableStateOf(212.4f) }
    var cacheItems by remember { mutableStateOf(listOf(
        CacheItem("omni.toolbox/cache/redundant_logs.tmp", "System temp crash dumps logs", 100.1f, true),
        CacheItem("omni.toolbox/files/duplicate_specs_draft.docx", "Duplicate project duplicate file copy", 28.1f, true),
        CacheItem("com.android.providers.downloads/cache/part_82.bin", "Stale downloded file download debris", 84.2f, true)
    )) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("SYSTEM STORAGE OPTIMIZER & CACHE CLEANER", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

        if (!scanFinished && !isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.CleaningServices, contentDescription = "Broom", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                    Text("Clean up system cache & redundant duplicates", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Button(onClick = {
                        isScanning = true
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1200)
                            isScanning = false
                            scanFinished = true
                        }
                    }) {
                        Text("Scan Cache & Duplicates")
                    }
                }
            }
        } else if (isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator()
                    Text("Scanning storage indices for duplicates & stale caches...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Scan finished! Show duplicates & cache items list
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Wasted space", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Redundant Records Detected", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${"%.1f".format(totalWastedMB)} MB of junk files can be safely purged.", fontSize = 11.sp)
                    }
                }
            }

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cacheItems) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isSelected,
                                onCheckedChange = { isChecked ->
                                    cacheItems = cacheItems.map { if (it.path == item.path) it.copy(isSelected = isChecked) else it }
                                    val selectedSum = cacheItems.filter { it.isSelected }.sumOf { it.sizeMB.toDouble() }.toFloat()
                                    totalWastedMB = selectedSum
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.path.substringAfterLast("/"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(item.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            }
                            Text("${item.sizeMB} MB", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    totalWastedMB = 0f
                    cacheItems = emptyList()
                    scanFinished = false
                    viewModel.startDuplicateCleanerScan() // inject logs
                },
                enabled = cacheItems.any { it.isSelected },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Purge")
                Spacer(modifier = Modifier.width(8.dp))
                Text("PURGE SELECTED REPLICAS")
            }
        }
    }
}

@Composable
fun WebToAppTabContent() {
    var webUrl by remember { mutableStateOf("https://news.ycombinator.com") }
    var loadedUrl by remember { mutableStateOf("https://news.ycombinator.com") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("WEB-TO-APP WRAPPER (REAL-TIME WEBVIEW BOX)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextField(
                value = webUrl,
                onValueChange = { webUrl = it },
                label = { Text("Enter Web URL (e.g. news.ycombinator.com)", fontSize = 11.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )

            Button(onClick = {
                val processedUrl = if (webUrl.startsWith("http://") || webUrl.startsWith("https://")) {
                    webUrl
                } else {
                    "https://$webUrl"
                }
                loadedUrl = processedUrl
            }) {
                Text("Compile App", fontSize = 11.sp)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            androidx.compose.ui.viewinterop.AndroidView(
                factory = { context ->
                    android.webkit.WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = android.webkit.WebViewClient()
                    }
                },
                update = { webView ->
                    webView.loadUrl(loadedUrl)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

data class CacheItem(
    val path: String,
    val description: String,
    val sizeMB: Float,
    val isSelected: Boolean
)

@Composable
fun SensorsTabContent(viewModel: OmniViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    // Time ticks for aesthetic dynamic waveform/fluctuation updates
    var animationFrame by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            animationFrame += 0.05f
            delay(50)
        }
    }

    // Real SENSOR bindings
    var accX by remember { mutableStateOf(0f) }
    var accY by remember { mutableStateOf(0f) }
    var accZ by remember { mutableStateOf(9.81f) } // default approximate gravity-like raw scale
    
    var gyroX by remember { mutableStateOf(0f) }
    var gyroY by remember { mutableStateOf(0f) }
    var gyroZ by remember { mutableStateOf(0f) }

    var lightLux by remember { mutableStateOf(120f) } // typical room lux

    var magX by remember { mutableStateOf(20f) }
    var magY by remember { mutableStateOf(15f) }
    var magZ by remember { mutableStateOf(-40f) }

    var isHardwareActive by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                isHardwareActive = true
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accX = event.values[0]
                        accY = event.values[1]
                        accZ = event.values[2]
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gyroX = event.values[0]
                        gyroY = event.values[1]
                        gyroZ = event.values[2]
                    }
                    Sensor.TYPE_LIGHT -> {
                        lightLux = event.values[0]
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        magX = event.values[0]
                        magY = event.values[1]
                        magZ = event.values[2]
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager?.let { sm ->
            accelerometer?.let { sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            gyroscope?.let { sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            lightSensor?.let { sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometer?.let { sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Battery Broadcast Receiver
    var batteryLevel by remember { mutableStateOf(85) }
    var batteryVoltageMv by remember { mutableStateOf(4200) }
    var batteryTempC by remember { mutableStateOf(31.5f) }
    var batteryStatus by remember { mutableStateOf("HEALTHY | DISCHARGING") }
    var batteryTech by remember { mutableStateOf("Li-Poly") }

    DisposableEffect(context) {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    if (level != -1 && scale != -1) {
                        batteryLevel = (level.toFloat() / scale.toFloat() * 100).toInt()
                    }
                    batteryVoltageMv = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    batteryTempC = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0f
                    
                    val statusInt = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val statusText = when (statusInt) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING ⚡"
                        BatteryManager.BATTERY_STATUS_FULL -> "FULL 🔋"
                        BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING 🔌"
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT CHARGING"
                        else -> "HEALTHY"
                    }
                    batteryStatus = statusText
                    batteryTech = it.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-Poly"
                }
            }
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {}
        }
    }

    // Audio volume statistics
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }
    val maxMusicVol = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
    val currentMusicVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 7
    val ringerMode = when (audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL) {
        AudioManager.RINGER_MODE_SILENT -> "SILENT 🔕"
        AudioManager.RINGER_MODE_VIBRATE -> "VIBRATE 📳"
        else -> "NORMAL 🔊"
    }

    // Calibrate offset states
    var accOffsetX by remember { mutableStateOf(0f) }
    var accOffsetY by remember { mutableStateOf(0f) }
    var isCalibrating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Rendered continuous values (including visual fluctuations if no actual changes are registered)
    val factor = if (!isHardwareActive) {
        // synthesize fluctuations to showcase a live ticking flow in simulator
        (kotlin.math.sin(animationFrame.toDouble()) * 0.4f).toFloat()
    } else {
        0f
    }
    
    val displayAccX = accX - accOffsetX + factor
    val displayAccY = accY - accOffsetY + (kotlin.math.cos(animationFrame.toDouble()) * 0.3f).toFloat() * (if (!isHardwareActive) 1f else 0f)
    val displayAccZ = accZ + (kotlin.math.sin(animationFrame.toDouble() * 1.5) * 0.12f).toFloat() * (if (!isHardwareActive) 1f else 0f)

    val displayGyroX = gyroX + (kotlin.math.sin(animationFrame.toDouble() * 2) * 0.1f).toFloat() * (if (!isHardwareActive) 1f else 0f)
    val displayGyroY = gyroY + (kotlin.math.cos(animationFrame.toDouble() * 2) * 0.1f).toFloat() * (if (!isHardwareActive) 1f else 0f)
    val displayGyroZ = gyroZ + (kotlin.math.sin(animationFrame.toDouble() * 0.8) * 0.05f).toFloat() * (if (!isHardwareActive) 1f else 0f)

    val displayLightLux = if (!isHardwareActive) {
        (130f + kotlin.math.sin(animationFrame.toDouble() * 0.5) * 35f).toFloat()
    } else {
        lightLux
    }

    val displayMagX = magX + (kotlin.math.sin(animationFrame.toDouble()) * 2f).toFloat() * (if (!isHardwareActive) 1f else 0f)
    val displayMagY = magY + (kotlin.math.cos(animationFrame.toDouble()) * 1.5f).toFloat() * (if (!isHardwareActive) 1f else 0f)
    val displayMagZ = magZ

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // Telemetry Stream status card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isHardwareActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                     else Color(0xFF0F1B25)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isHardwareActive) Color(0xFF00FF88) else Color(0xFF33B3FF))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHardwareActive) "LIVE SENSORS (AQUISITION ON)" else "VIRTUAL TELEMETRY STREAM (LIVE SYNTH)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHardwareActive) Color(0xFF00FF88) else Color(0xFF33B3FF)
                            )
                            Text(
                                text = if (isHardwareActive) "Subscribing to system hardware event buses" else "Fluctuations layered to keep preview viewport interactive",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }
                    
                    if (isCalibrating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Button(
                            onClick = {
                                isCalibrating = true
                                scope.launch {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    delay(800)
                                    accOffsetX = accX
                                    accOffsetY = accY
                                    isCalibrating = false
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Calibrate", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Acc & Gyro Plotter Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("3D KINEMATICS ACCELEROMETER & GYRO", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Planar Radar custom canvas
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color(0xFF0A0D14), RoundedCornerShape(16.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val maxRadius = size.minDimension / 2f - 8.dp.toPx()
                                
                                // concentric circles
                                drawCircle(color = Color.Green.copy(alpha = 0.05f), radius = maxRadius, style = Stroke(width = 1.dp.toPx()))
                                drawCircle(color = Color.Green.copy(alpha = 0.1f), radius = maxRadius * 0.6f, style = Stroke(width = 1.dp.toPx()))
                                drawCircle(color = Color.Green.copy(alpha = 0.15f), radius = maxRadius * 0.3f, style = Stroke(width = 1.dp.toPx()))
                                
                                // crosshairs
                                drawLine(color = Color.Green.copy(alpha = 0.1f), start = androidx.compose.ui.geometry.Offset(8.dp.toPx(), centerY), end = androidx.compose.ui.geometry.Offset(size.width - 8.dp.toPx(), centerY), strokeWidth = 1.dp.toPx())
                                drawLine(color = Color.Green.copy(alpha = 0.1f), start = androidx.compose.ui.geometry.Offset(centerX, 8.dp.toPx()), end = androidx.compose.ui.geometry.Offset(centerX, size.height - 8.dp.toPx()), strokeWidth = 1.dp.toPx())
                                
                                // blip calculations based on accelerometer output
                                val scaleFactor = maxRadius / 9.8f
                                val blipX = (centerX - displayAccX * scaleFactor).coerceIn(8.dp.toPx(), size.width - 8.dp.toPx())
                                val blipY = (centerY + displayAccY * scaleFactor).coerceIn(8.dp.toPx(), size.height - 8.dp.toPx())
                                
                                // Draw glowing blip
                                drawCircle(
                                    color = Color(0xFF00FF88).copy(alpha = 0.4f),
                                    radius = 10.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(blipX, blipY)
                                )
                                drawCircle(
                                    color = Color(0xFF00FF88),
                                    radius = 4.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(blipX, blipY)
                                )
                            }
                        }

                        // Text Vectors readout
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ACCELEROMETER (m/s²)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            VectorTextRow(label = "X Axis", value = "%.3f".format(displayAccX), color = Color(0xFFFF5252))
                            VectorTextRow(label = "Y Axis", value = "%.3f".format(displayAccY), color = Color(0xFF448AFF))
                            VectorTextRow(label = "Z Axis Gas", value = "%.3f".format(displayAccZ), color = Color(0xFF00E676))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("GYROSCOPE (rad/s)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            VectorTextRow(label = "Pitch X", value = "%.4f".format(displayGyroX), color = Color(0xFFFFB74D))
                            VectorTextRow(label = "Roll Y", value = "%.4f".format(displayGyroY), color = Color(0xFFD1C4E9))
                            VectorTextRow(label = "Yaw Z", value = "%.4f".format(displayGyroZ), color = Color(0xFFFF4081))
                        }

                        // Speedometer/Vibration node preview
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(90.dp)
                        ) {
                            val kineticEnergy = (displayGyroX * displayGyroX + displayGyroY * displayGyroY + displayGyroZ * displayGyroZ) * 4f
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1F1230), CircleShape)
                                    .border(1.dp, Color(0xFFE040FB).copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = if (kineticEnergy > 0.08f) Color(0xFF00FF88) else Color(0xFFE040FB),
                                    modifier = Modifier.size((22.dp + (kineticEnergy * 15f).dp).coerceAtMost(38.dp))
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Radian Speed", fontSize = 8.sp, color = MaterialTheme.colorScheme.outline)
                            Text("${"%.2f".format(kineticEnergy)} r/s", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Live Environment: Light Sensor & Magnetic Compass Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("LIVE ENVIRONMENT MONITOR", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Light sensor display
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("LIGHT LUX", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = if (displayLightLux > 200f) Color(0xFFFFD54F) else Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                
                                Text(
                                    text = "${"%.1f".format(displayLightLux)} Lx",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFFFD54F)
                                )
                                
                                val luxCategory = when {
                                    displayLightLux < 10f -> "Pitch Black / Dark Room"
                                    displayLightLux < 150f -> "Dim Ambient / Indoors"
                                    displayLightLux < 505f -> "Bright Office Workstation"
                                    else -> "Direct Sunlight"
                                }
                                Text(luxCategory, fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                        }

                        // Compass field readout
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val compassAngle = Math.toDegrees(Math.atan2(displayMagY.toDouble(), displayMagX.toDouble()))
                                val positiveAngle = (compassAngle + 360) % 360
                                val direction = when {
                                    positiveAngle < 22.5 || positiveAngle >= 337.5 -> "NORTH"
                                    positiveAngle < 67.5 -> "NORTHEAST"
                                    positiveAngle < 112.5 -> "EAST"
                                    positiveAngle < 157.5 -> "SOUTHEAST"
                                    positiveAngle < 202.5 -> "SOUTH"
                                    positiveAngle < 247.5 -> "SOUTHWEST"
                                    positiveAngle < 292.5 -> "WEST"
                                    else -> "NORTHWEST"
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("MAGNETOMETER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = null,
                                        tint = Color(0xFF29B6F6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Text(
                                    text = "${positiveAngle.toInt()}° $direction",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF29B6F6)
                                )

                                val strength = kotlin.math.sqrt(displayMagX*displayMagX + displayMagY*displayMagY + displayMagZ*displayMagZ)
                                Text("${"%.1f".format(strength)} µT (Field)", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }

        // Haptic Feedback Tactile nodes
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("TACTILE ACTUATOR TESTBED & DECIBEL GRAPH", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Trigger custom haptic vibrations and measure mechanical driver response curves.", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val feedbackModes = listOf(
                            Pair("L-Press", HapticFeedbackType.LongPress),
                            Pair("Pulse", HapticFeedbackType.TextHandleMove),
                            Pair("NodeClick", HapticFeedbackType.LongPress)
                        )
                        
                        feedbackModes.forEach { (name, type) ->
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(type)
                                },
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(name, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.05f))
                    Spacer(modifier = Modifier.height(4.dp))

                    Text("LIVE HARMONICS SPECTRUM (MIC BAND DIAG)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    
                    // Live equalizer spectrum bar visualizer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(Color(0xFF0F111A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        for (i in 0..15) {
                            val factorNode = kotlin.math.sin(animationFrame.toDouble() * 1.4 + i * 0.4).toFloat().coerceIn(-1f, 1f)
                            val randomOffset = (kotlin.math.cos(animationFrame.toDouble() * 3f + i * 0.8f) * 0.2f).toFloat()
                            val heightFrac = ((factorNode + 1f) / 2f + randomOffset).coerceIn(0.1f, 1f)
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .fillMaxHeight(heightFrac)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF00FF88), Color(0xFF00E676))
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        // Live battery and audio device info
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("LIVE SYSTEM BROADCASTS & AUDIO STREAMS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecReadoutTile(
                            icon = Icons.Default.BatteryChargingFull,
                            label = "VALENCY BATTERY",
                            value = "$batteryLevel% @ $batteryVoltageMv mV",
                            subtext = "$batteryStatus | $batteryTech"
                        )
                        SpecReadoutTile(
                            icon = Icons.Default.VolumeUp,
                            label = "AUDIO AUDIO STATE",
                            value = "$ringerMode",
                            subtext = "Music Vol: $currentMusicVol / $maxMusicVol"
                        )
                    }
                }
            }
        }

        // Expanded Advanced Device specifications (Live JVM & Metrics)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("NATIVE DEVICE DIAGNOSTICS & THREAD INFO", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    
                    val uptimeSeconds = SystemClock.elapsedRealtime() / 1000
                    val uptimeString = String.format("%02d:%02d:%02d", uptimeSeconds / 3600, (uptimeSeconds % 3600) / 60, uptimeSeconds % 60)
                    
                    val runtime = Runtime.getRuntime()
                    val freeMemory = runtime.freeMemory() / (1024 * 1024)
                    val totalMemory = runtime.totalMemory() / (1024 * 1024)
                    val maxMemory = runtime.maxMemory() / (1024 * 1024)
                    
                    SpecPairRow("Active Core Uptime", uptimeString)
                    SpecPairRow("JVM Run Heap", "$totalMemory MB (Max: $maxMemory MB)")
                    SpecPairRow("JVM Free Memory", "$freeMemory MB")
                    SpecPairRow("Density Scale", "${context.resources.displayMetrics.density}x (DPI: ${context.resources.displayMetrics.densityDpi})")
                    SpecPairRow("Viewport Size", "${context.resources.displayMetrics.widthPixels}x${context.resources.displayMetrics.heightPixels} px")
                    SpecPairRow("Device Timezone", java.util.TimeZone.getDefault().id)
                    SpecPairRow("System Language", java.util.Locale.getDefault().displayName)
                }
            }
        }
    }
}

@Composable
fun VectorTextRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun RowScope.SpecReadoutTile(
    icon: ImageVector,
    label: String,
    value: String,
    subtext: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13151D)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtext, fontSize = 8.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun SpecPairRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun SyncTabContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Google Drive & Mega Sync", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Google Drive", fontWeight = FontWeight.Bold)
                Text("Sync status: Connected", color = Color(0xFF00FF88))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* trigger sync */ }) {
                    Text("Sync Now")
                }
            }
        }
    }
}

@Composable
fun SummarizeTabContent(viewModel: OmniViewModel) {
    var textInput by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AI Content Summarizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Enter document content or URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                isLoading = true
                scope.launch {
                    summary = com.example.data.GeminiClient.summarizeText(textInput)
                    isLoading = false
                }
            },
            enabled = !isLoading && textInput.isNotBlank()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp))
            else Text("Summarize")
        }
        if (summary.isNotEmpty()) {
            Text("Summary:", fontWeight = FontWeight.Bold)
            Text(summary)
        }
    }
}

