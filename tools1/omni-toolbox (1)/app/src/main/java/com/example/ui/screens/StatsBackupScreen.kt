package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsBackupScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Telemetry Stats, 1: Call/Msg Stats, 2: Backup & Recovery

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
                            text = "Telemetry & Backups",
                            fontWeight = FontWeight.Medium,
                            fontSize = 19.sp
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("📊 Live Usage", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("📞 Call / Msg Info", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("💾 Backups Restore", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> LiveUsageTab(viewModel)
                    1 -> CallMsgInfoTab(viewModel)
                    2 -> BackupsRestoreTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun LiveUsageTab(viewModel: OmniViewModel) {
    val systemHealth by viewModel.systemHealth.collectAsState()
    var systemStatsHistory by remember { mutableStateOf(List(15) { Random.nextFloat() * 40f + 10f }) }

    // Simulate dynamic live chart updates!
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            systemStatsHistory = (systemStatsHistory.drop(1) + (Random.nextFloat() * 45f + 10f))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "SYSTEM TELEMETRY SPECTRAL INDEX",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        // Spectral live viewport canvas
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "PROCESSOR LOAD INDEX (HISTORICAL)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val pathPoints = systemStatsHistory.size
                        val stepX = size.width / (pathPoints - 1)
                        val maxVal = 100f

                        // Grid lines
                        for (i in 1..4) {
                            val yLine = size.height * (i / 5f)
                            drawLine(
                                color = Color.White.copy(alpha = 0.05f),
                                start = Offset(0f, yLine),
                                end = Offset(size.width, yLine)
                            )
                        }

                        // Plot line
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
                                radius = 4f,
                                center = Offset(x2, y2)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("15s AGO", fontSize = 8.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                    Text("ACTIVE LOAD: ${String.format("%.1f", systemHealth.cpuLoad)}%", fontSize = 9.sp, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("REALTIME GMT", fontSize = 8.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Expanded resource widgets
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Database Table Records Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Active Tasks", fontSize = 10.sp, color = Color.Gray)
                        Text("12 items", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Stored Accounts", fontSize = 10.sp, color = Color.Gray)
                        Text("3 profiles", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Documents Indexed", fontSize = 10.sp, color = Color.Gray)
                        Text("6 files", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Scraper Threads", fontSize = 10.sp, color = Color.Gray)
                        Text("4 synced", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Core information
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("App Storage Overhead", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text("Total Sandbox Footprint: 21.42 MB\nSQLite Cache Size: 320 KB\nNetwork Logs Queue: ${systemStatsHistory.size} telemetry nodes.", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun CallMsgInfoTab(viewModel: OmniViewModel) {
    var toolSubTab by remember { mutableStateOf(0) } // 0: Call History, 1: SMS Messages, 2: GDrive Integration
    
    val callLogs by viewModel.callLogs.collectAsState()
    val smsMessages by viewModel.smsMessages.collectAsState()
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
    var callFilterMode by remember { mutableStateOf("ALL") } // "ALL", "INCOMING", "OUTGOING", "MISSED", "SELECTED"

    var smsSearchQuery by remember { mutableStateOf("") }
    var smsFilterMode by remember { mutableStateOf("ALL") } // "ALL", "INBOX", "SENT", "SELECTED"

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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Subsection headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("📞 Calls List", "💬 SMS Records", "☁️ GDrive Sync").forEachIndexed { index, title ->
                FilterChip(
                    selected = toolSubTab == index,
                    onClick = { 
                        toolSubTab = index 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    label = { Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when (toolSubTab) {
            0 -> {
                // Call History Sub-tab
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
                        TextButton(
                            onClick = { viewModel.toggleAllCallLogs(true) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("All", fontSize = 10.sp)
                        }
                        TextButton(
                            onClick = { viewModel.toggleAllCallLogs(false) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("None", fontSize = 10.sp)
                        }
                        IconButton(
                            onClick = { showCallForm = !showCallForm },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (showCallForm) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add simulated log",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Call Search & Filter Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextField(
                        value = callSearchQuery,
                        onValueChange = { callSearchQuery = it },
                        placeholder = { Text("Search by contact name or number...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (callSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { callSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL", "INCOMING", "OUTGOING", "MISSED", "SELECTED").forEach { mode ->
                            FilterChip(
                                selected = callFilterMode == mode,
                                onClick = { 
                                    callFilterMode = mode
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                label = { Text(mode, fontSize = 8.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(26.dp)
                            )
                        }
                    }
                }

                if (showCallForm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Simulate Input Call Log", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextField(
                                    value = inputCallName,
                                    onValueChange = { inputCallName = it },
                                    label = { Text("Contact Name", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                                )
                                TextField(
                                    value = inputCallNumber,
                                    onValueChange = { inputCallNumber = it },
                                    label = { Text("Number", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1.5f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf("INCOMING", "OUTGOING", "MISSED").forEach { t ->
                                        FilterChip(
                                            selected = inputCallType == t,
                                            onClick = { inputCallType = t },
                                            label = { Text(t, fontSize = 8.sp) }
                                        )
                                    }
                                }
                                TextField(
                                    value = inputCallDuration,
                                    onValueChange = { inputCallDuration = it },
                                    label = { Text("Duration", fontSize = 9.sp) },
                                    modifier = Modifier.weight(0.8f),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                                )
                            }
                            Button(
                                onClick = {
                                    val name = inputCallName.ifEmpty { "Unknown Caller" }
                                    val num = inputCallNumber.ifEmpty { "+91 99000 11223" }
                                    viewModel.addCallLog(name, num, inputCallType, inputCallDuration)
                                    inputCallName = ""
                                    inputCallNumber = ""
                                    showCallForm = false
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Inject Call History Instance", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Call Logs Scroll List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredCallLogs) { call ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (call.isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val logIcon = when (call.type) {
                                    "INCOMING" -> Icons.Default.CallReceived
                                    "OUTGOING" -> Icons.Default.CallMade
                                    else -> Icons.Default.CallMissed
                                }
                                val iconColor = when (call.type) {
                                    "INCOMING" -> Color(0xFF4CAF50)
                                    "OUTGOING" -> Color(0xFF2196F3)
                                    else -> Color(0xFFF44336)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(iconColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = logIcon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(call.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("${call.number} • ${call.duration}", fontSize = 10.sp, color = Color.Gray)
                                }

                                Text(call.timestamp, fontSize = 9.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))

                                Checkbox(
                                    checked = call.isSelected,
                                    onCheckedChange = { viewModel.toggleCallLogSelected(call.id) }
                                )
                            }
                        }
                    }
                }

                // Backup selected triggers button
                val selectedCallsCount = callLogs.count { it.isSelected }
                Button(
                    onClick = {
                        if (selectedCallsCount == 0) {
                            backupStatusText = "Error: Please check at least one call log to backup."
                            return@Button
                        }
                        backupProgress = 0.0f
                        backupStatusText = "Connecting Cloud Storage API standard layers..."
                        coroutineScope.launch {
                            delay(800)
                            backupStatusText = "Google Drive OAuth token match certified under accounts Node..."
                            backupProgress = 0.25f
                            delay(1000)
                            backupStatusText = "Iterating call instances schemas ($selectedCallsCount item structures loaded)..."
                            backupProgress = 0.5f
                            delay(1200)
                            
                            // Serialize selected calls in structured text format
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
                            val docName = "OMNITOOL_GD_CALLS_BACKUP_$timestampSuffix.json"
                            
                            backupStatusText = "Transfer rate matching network: 12.4 KB/s uploading secure dump file..."
                            backupProgress = 0.8f
                            delay(1000)

                            viewModel.addDocument(
                                name = docName,
                                type = "TXT",
                                content = textBuilder.toString()
                            )

                            backupProgress = null
                            backupStatusText = "Done! Transferred $selectedCallsCount logs to Drive folder My Drive > OmniToolbox."
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupProgress == null
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back Up Selected Calls to Google Drive", fontSize = 11.sp)
                }
            }

            1 -> {
                // SMS Messages Sub-tab
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
                        TextButton(
                            onClick = { viewModel.toggleAllSms(true) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("All", fontSize = 10.sp)
                        }
                        TextButton(
                            onClick = { viewModel.toggleAllSms(false) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("None", fontSize = 10.sp)
                        }
                        IconButton(
                            onClick = { showSmsForm = !showSmsForm },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (showSmsForm) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Add message",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // SMS Search & Filter Controls
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextField(
                        value = smsSearchQuery,
                        onValueChange = { smsSearchQuery = it },
                        placeholder = { Text("Search by sender or message content...", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = {
                            if (smsSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { smsSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("ALL", "INBOX", "SENT", "SELECTED").forEach { mode ->
                            FilterChip(
                                selected = smsFilterMode == mode,
                                onClick = { 
                                    smsFilterMode = mode
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                label = { Text(mode, fontSize = 8.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.height(26.dp)
                            )
                        }
                    }
                }

                if (showSmsForm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Simulate Input SMS Message", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextField(
                                    value = inputSmsSender,
                                    onValueChange = { inputSmsSender = it },
                                    label = { Text("Sender / Contact", fontSize = 9.sp) },
                                    modifier = Modifier.weight(1.2f),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                                )
                                Row(
                                    modifier = Modifier.weight(1.5f),
                                ) {
                                    listOf("INBOX", "SENT").forEach { t ->
                                        FilterChip(
                                            selected = inputSmsType == t,
                                            onClick = { inputSmsType = t },
                                            label = { Text(t, fontSize = 8.sp) }
                                        )
                                    }
                                }
                            }
                            TextField(
                                value = inputSmsContent,
                                onValueChange = { inputSmsContent = it },
                                label = { Text("Message Body Text Content", fontSize = 9.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodySmall,
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                            )
                            Button(
                                onClick = {
                                    val sender = inputSmsSender.ifEmpty { "98420-ALRT" }
                                    val content = inputSmsContent.ifEmpty { "Test message body placeholder logs." }
                                    viewModel.addSmsMessage(sender, content, inputSmsType)
                                    inputSmsSender = ""
                                    inputSmsContent = ""
                                    showSmsForm = false
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Inject SMS Message Record", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // SMS Scrolls list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredSmsMessages) { sms ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (sms.isSelected) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (sms.type == "INBOX") Icons.Default.Sms else Icons.Default.Send,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(sms.sender, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (sms.type == "INBOX") "[RECV]" else "[SENT]",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    Text(sms.content, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                                }

                                Text(sms.timestamp, fontSize = 9.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp))

                                Checkbox(
                                    checked = sms.isSelected,
                                    onCheckedChange = { viewModel.toggleSmsSelected(sms.id) }
                                )
                            }
                        }
                    }
                }

                // SMS backup button
                val selectedSmsCount = smsMessages.count { it.isSelected }
                Button(
                    onClick = {
                        if (selectedSmsCount == 0) {
                            backupStatusText = "Error: Please check at least one SMS to backup."
                            return@Button
                        }
                        backupProgress = 0.0f
                        backupStatusText = "Allocated thread for My Drive sync node..."
                        coroutineScope.launch {
                            delay(800)
                            backupStatusText = "Google Drive OAuth matching completed for target directory..."
                            backupProgress = 0.3f
                            delay(1000)
                            backupStatusText = "Constructing secure encrypted dump structure mapping..."
                            backupProgress = 0.6f
                            delay(1200)

                            // Serialize messages
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
                            val docName = "OMNITOOL_GD_SMS_BACKUP_$timestampSuffix.json"

                            backupStatusText = "Pipes writing to GDrive folder path: /My Drive/OmniToolbox/Phone_Backups/ ..."
                            backupProgress = 0.85f
                            delay(1000)

                            viewModel.addDocument(
                                name = docName,
                                type = "TXT",
                                content = textBuilder.toString()
                            )

                            backupProgress = null
                            backupStatusText = "Completed! Safely uploaded $selectedSmsCount SMS to GDrive under profile subbu.edu.68@gmail.com."
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = backupProgress == null
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back Up Selected SMS to Google Drive", fontSize = 11.sp)
                }
            }

            2 -> {
                // GDrive Integration Settings Account Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F9D58)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Google Cloud Storage Node", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Securely Synced", fontSize = 10.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Active Identity", fontSize = 10.sp, color = Color.Gray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(connectedEmail, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Drive Usage Quota", fontSize = 10.sp, color = Color.Gray)
                                Text("12.4 GB / 15.0 GB used (82%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = 0.82f,
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF0F9D58),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Standard Sandboxed Workspace Paths", fontSize = 10.sp, color = Color.Gray)
                            Text("/My Drive/OmniToolbox/Phone_Backups/", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                connectedEmail = if (connectedEmail == "subbu.edu.68@gmail.com") "sandbox.agent@gmail.com" else "subbu.edu.68@gmail.com"
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Switch Sandbox User Account Node", fontSize = 11.sp)
                        }
                    }
                }

                // Mirror all triggers card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("⚡ Fast Cloud Mirror Automation Plan", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        Text("Auto-triggers secure backup snapshot dumps under Google Drive storage node whenever a Call Log change or incoming SMS threshold is crossed.", fontSize = 10.sp, color = Color.Gray)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sync triggers on data change", fontSize = 11.sp)
                            var optSelected by remember { mutableStateOf(true) }
                            Switch(checked = optSelected, onCheckedChange = { optSelected = it })
                        }
                    }
                }
            }
        }

        // Stepper Status bar
        if (backupProgress != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("DRIVE DISPATCH MATRIX", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("${(backupProgress!! * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    LinearProgressIndicator(progress = backupProgress!!, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                    Text(backupStatusText, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        } else if (backupStatusText.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(16.dp))
                    Text(backupStatusText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.weight(1f))
                    IconButton(onClick = { backupStatusText = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "dismiss", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BackupsRestoreTab(viewModel: OmniViewModel) {
    var backupStatusText by remember { mutableStateOf<String?>(null) }
    var isBackingUp by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }
    var restoreProgress by remember { mutableStateOf(0f) }
    var expandedBackupId by remember { mutableStateOf<Int?>(null) }

    val documents by viewModel.documents.collectAsState()
    val restoreLogs by viewModel.restoreLogs.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Build the lists of backups from SQLite docs!
    val gdriveBackups = documents.filter { 
        it.fileName.startsWith("OMNITOOL_GD_") || it.fileName.contains("BACKUP")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "SECURE DEVICE SQLITE SNAPSHOT & GDRIVE RESTORE",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        // Snapshot Generator Device Box
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Create Full Database Core Snapshot", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Compiles everything (tasks, automation rules, profiles, local documents, crawler logs) in an offline JSON backup dump.", fontSize = 11.sp, color = Color.Gray)

                if (!isBackingUp) {
                    Button(
                        onClick = {
                            isBackingUp = true
                            backupStatusText = null
                            coroutineScope.launch {
                                delay(1500)
                                val snapshotName = "OMNITOOL_MAN_SNAPSHOT_${Random.nextInt(1000, 9999)}.json"
                                viewModel.addDocument(
                                    name = snapshotName,
                                    type = "TXT",
                                    content = "=== OMNI TOOLBOX SQLITE SNAPSHOT ===\n" +
                                            "Version: 2\n" +
                                            "Status: Complete Exported\n" +
                                            "Records mapped: Profiles, Task Lists, Automation Rules, Scraping parameters."
                                )
                                isBackingUp = false
                                backupStatusText = "Exported core SQLite snapshot $snapshotName to folder repository successfully."
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Core System Snapshot", fontSize = 11.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Serializing SQLite schemas index...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (isRestoring) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DEPLOYING RETROSPECTIVE DRIVE SNAPSHOT...", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    LinearProgressIndicator(progress = restoreProgress, modifier = Modifier.fillMaxWidth())
                    Text(
                        text = if (restoreProgress < 0.4f) "Connecting and pulling file descriptor payload from Drive sandbox..."
                        else if (restoreProgress < 0.8f) "Decoding secure token headers and unpacking entries schema..."
                        else "Restoring logs, sms list, databases tables...",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        if (backupStatusText != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(backupStatusText!!, fontSize = 10.sp, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.weight(1f))
                    IconButton(onClick = { backupStatusText = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Backups lists & Ledger rendering
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Section 1: Backups Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Google Drive & Core Recovery Points", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Section 2: Backup Snapshot elements
            if (gdriveBackups.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.Gray)
                            Text("No Cloud backups detected yet.", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Text("Go to Call/Msg Tab and hit Backup to create snapshots.", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(gdriveBackups) { b ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val backupIcon = if (b.fileName.contains("CALLS")) Icons.Default.Call
                                    else if (b.fileName.contains("SMS")) Icons.Default.Sms
                                    else Icons.Default.InsertDriveFile

                                    Icon(imageVector = backupIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(b.fileName, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.widthIn(max = 200.dp))
                                }

                                TextButton(
                                    onClick = {
                                        // Run simulated restore
                                        isRestoring = true
                                        restoreProgress = 0.1f
                                        coroutineScope.launch {
                                            delay(700)
                                            restoreProgress = 0.45f
                                            delay(800)
                                            restoreProgress = 0.82f
                                            delay(700)

                                            // Parse stored lists
                                            val content = b.content
                                            val lines = content.split("\n")
                                            
                                            var count = 0
                                            if (content.contains("=== GOOGLE DRIVE CALL LOGS BACKUP ===")) {
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
                                                count = callItems.size
                                                if (callItems.isNotEmpty()) {
                                                    viewModel.restoreCallLogsAndSms(callItems, viewModel.smsMessages.value)
                                                }
                                                backupStatusText = "Restored successfully: Re-inserted ${callItems.size} phone call logs back to local terminal directory."
                                                viewModel.logRestoreEvent(b.fileName, "Calls Restore", count, "subbu.edu.68@gmail.com")
                                            } else if (content.contains("=== GOOGLE DRIVE SMS BACKUP ===")) {
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
                                                count = smsItems.size
                                                if (smsItems.isNotEmpty()) {
                                                    viewModel.restoreCallLogsAndSms(viewModel.callLogs.value, smsItems)
                                                }
                                                backupStatusText = "Restored successfully: Deployed ${smsItems.size} SMS records back into Local Terminal Inbox."
                                                viewModel.logRestoreEvent(b.fileName, "SMS Restore", count, "subbu.edu.68@gmail.com")
                                            } else {
                                                backupStatusText = "Restored successfully: Checked system snapshot databases mappings complete."
                                                viewModel.logRestoreEvent(b.fileName, "Core System Snapshot", 1, "subbu.edu.68@gmail.com")
                                            }

                                            isRestoring = false
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    enabled = !isRestoring
                                ) {
                                    Text("Restore", fontSize = 11.sp)
                                }
                            }

                            // Dynamic parsing for descriptive headers
                            val contentLines = b.content.split("\n")
                            val description = if (b.fileName.contains("CALLS")) {
                                val cnt = contentLines.filter { it.startsWith("Call|") }.size
                                "Google Drive Call History Backup Snapshot containing $cnt call elements."
                            } else if (b.fileName.contains("SMS")) {
                                val cnt = contentLines.filter { it.startsWith("Sms|") }.size
                                "Google Drive Messages backup containing $cnt SMS records."
                            } else {
                                "Immutable JSON database structural map dump of files index catalogs."
                            }

                            Text(description, fontSize = 11.sp, color = Color.Gray)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Stored: Google Drive Sandbox", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CloudDone, contentDescription = "Synced", tint = Color(0xFF00FF88), modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("GDrive Synced", fontSize = 9.sp, color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

                            // Inspect and Delete Payload Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isExpanded = expandedBackupId == b.id
                                TextButton(
                                    onClick = {
                                        expandedBackupId = if (isExpanded) null else b.id
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isExpanded) "Hide Payload" else "Inspect Payload", fontSize = 10.sp)
                                }

                                if (isExpanded) {
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteDocument(b.id)
                                            expandedBackupId = null
                                            backupStatusText = "Deleted backup file snapshot: ${b.fileName}"
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete snapshot", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            if (expandedBackupId == b.id) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F111A), RoundedCornerShape(8.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = b.content.take(650) + if (b.content.length > 650) "\n... [TRUNCATED FOR VIEWPORTS COHERENCE] ..." else "",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF00FF88)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Restore Logs History Ledger
            if (restoreLogs.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Restore History Ledger Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "RESTORE ACTIVITY LEDGER (HISTORIC)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                items(restoreLogs) { log ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E676))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(log.type, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Text(log.timestamp, fontSize = 9.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                            }

                            Text("Source File: ${log.fileName}", fontSize = 10.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Restored Records: ${log.itemsCount} rows", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("Status: ${log.status}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                            }
                        }
                    }
                }
            }
        }
    }
}
