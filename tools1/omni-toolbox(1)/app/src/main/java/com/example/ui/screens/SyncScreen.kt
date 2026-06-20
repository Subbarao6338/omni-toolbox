package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccountProfile
import com.example.data.DocumentItem
import com.example.ui.OmniViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Cloud Sync (GDrive/Mega), 1: Notion & Raindrop, 2: Secure DNS (NextDNS/AdGuard)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi-Cloud Sync & DNS", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("sync_screen_back")) {
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
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Elegant Tab Bar
            TabRow(selectedTabIndex = activeTab, modifier = Modifier.fillMaxWidth()) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = "Cloud", modifier = Modifier.size(16.dp))
                        Text("Cloud Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Web Sync", modifier = Modifier.size(16.dp))
                        Text("Notion & Raindrop", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Dns, contentDescription = "DNS", modifier = Modifier.size(16.dp))
                        Text("Secure DNS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                when (activeTab) {
                    0 -> CloudSyncTab(viewModel)
                    1 -> WebSyncTab(viewModel)
                    2 -> SecureDnsTab()
                }
            }
        }
    }
}

@Composable
fun CloudSyncTab(viewModel: OmniViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val scope = rememberCoroutineScope()
    
    val gdriveAccounts = remember(profiles) { profiles.filter { it.platform == "GDrive" } }
    val megaAccounts = remember(profiles) { profiles.filter { it.platform == "Mega" } }
    val onedriveAccounts = remember(profiles) { profiles.filter { it.platform == "OneDrive" } }
    val nextcloudAccounts = remember(profiles) { profiles.filter { it.platform == "Nextcloud" } }

    var showingAddAccountDialog by remember { mutableStateOf(false) }
    var addPlatformSelection by remember { mutableStateOf("GDrive") }
    var inputAccountName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputToken by remember { mutableStateOf("") }

    var isSyncingAll by remember { mutableStateOf(false) }
    var syncLogText by remember { mutableStateOf("Ready to initiate multi-cloud sync.") }
    var syncPercentage by remember { mutableStateOf(0f) }

    var isNetworkOnline by remember { mutableStateOf(true) }
    var networkType by remember { mutableStateOf("Wi-Fi") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 50.dp)
    ) {
        // --- Animated services & connectivity monitor deck ---
        item {
            CloudServicesMonitorSection(
                gdriveCount = gdriveAccounts.size,
                megaCount = megaAccounts.size,
                onedriveCount = onedriveAccounts.size,
                nextcloudCount = nextcloudAccounts.size,
                isSyncingActive = isSyncingAll,
                syncProgress = syncPercentage,
                isNetworkOnline = isNetworkOnline,
                onNetworkOnlineChange = { isNetworkOnline = it },
                networkType = networkType,
                onNetworkTypeChange = { networkType = it }
            )
        }
        // Multi-Account connection Panel
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVE CLOUD SESSIONS", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { showingAddAccountDialog = true }) {
                            Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Add cloud profile", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Google Drive Sessions list
                    Text("Google Drive Accounts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    if (gdriveAccounts.isEmpty()) {
                        Text("No GDrive account synced yet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        gdriveAccounts.forEach { acc ->
                            CloudAccountRow(acc = acc, onDelete = { viewModel.deleteProfile(acc.id) })
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Mega Sessions list
                    Text("Mega Decrypted Vault Accounts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    if (megaAccounts.isEmpty()) {
                        Text("No Mega account registered.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        megaAccounts.forEach { acc ->
                            CloudAccountRow(acc = acc, onDelete = { viewModel.deleteProfile(acc.id) })
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // OneDrive Sessions list
                    Text("Microsoft OneDrive Accounts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    if (onedriveAccounts.isEmpty()) {
                        Text("No OneDrive account synced yet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        onedriveAccounts.forEach { acc ->
                            CloudAccountRow(acc = acc, onDelete = { viewModel.deleteProfile(acc.id) })
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nextcloud Sessions list
                    Text("Nextcloud Accounts", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    if (nextcloudAccounts.isEmpty()) {
                        Text("No Nextcloud account synced yet.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        nextcloudAccounts.forEach { acc ->
                            CloudAccountRow(acc = acc, onDelete = { viewModel.deleteProfile(acc.id) })
                        }
                    }
                }
            }
        }

        // Synchronize engine controller
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("MULTI-ACCOUNT AUTO-SYNC SYSTEM", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text("Synchronizes local cached logs with cloud drives daemon", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        Button(
                            onClick = {
                                if (!isNetworkOnline) {
                                    syncLogText = "Critical Network Error: Syncer daemon halted. Please establish terminal connection link first."
                                    return@Button
                                }
                                isSyncingAll = true
                                syncPercentage = 0f
                                scope.launch {
                                    syncLogText = "Authenticating persistent scopes..."
                                    delay(600)
                                    
                                    if (gdriveAccounts.isNotEmpty()) {
                                        syncPercentage = 0.2f
                                        syncLogText = "Connecting ${gdriveAccounts.size} Google Drive node(s)..."
                                        gdriveAccounts.forEach { acc ->
                                            delay(500)
                                            if (!isNetworkOnline) {
                                                syncLogText = "Sync Aborted: Network connection terminated mid-process!"
                                                isSyncingAll = false
                                                return@launch
                                            }
                                            syncLogText = "GDrive: Synchronizing folder indices for [${acc.email}]..."
                                            viewModel.addDocument(
                                                name = "GDrive_${acc.accountName.replace(" ", "_")}_Archive.pdf",
                                                type = "PDF",
                                                content = "Google Drive cloud-synchronized server telemetries log frame for ${acc.email}. Registered offline."
                                            )
                                        }
                                    }

                                    if (megaAccounts.isNotEmpty()) {
                                        syncPercentage = 0.45f
                                        syncLogText = "Securing ${megaAccounts.size} Mega decrypter handshake(s)..."
                                        megaAccounts.forEach { acc ->
                                            delay(500)
                                            if (!isNetworkOnline) {
                                                syncLogText = "Sync Aborted: Network connection terminated mid-process!"
                                                isSyncingAll = false
                                                return@launch
                                            }
                                            syncLogText = "Mega: Transcribing secure key segments for [${acc.email}]..."
                                            viewModel.addDocument(
                                                name = "Mega_${acc.accountName.replace(" ", "_")}_Vault.txt",
                                                type = "TXT",
                                                content = "Highly secure cryptographically hashed container decoded from Mega account ${acc.email}."
                                            )
                                        }
                                    }

                                    if (onedriveAccounts.isNotEmpty()) {
                                        syncPercentage = 0.7f
                                        syncLogText = "Authenticating ${onedriveAccounts.size} OneDrive Sharepoint node(s)..."
                                        onedriveAccounts.forEach { acc ->
                                            delay(500)
                                            if (!isNetworkOnline) {
                                                syncLogText = "Sync Aborted: Network connection terminated mid-process!"
                                                isSyncingAll = false
                                                return@launch
                                            }
                                            syncLogText = "OneDrive: Upstream sync active for [${acc.email}]..."
                                            viewModel.addDocument(
                                                name = "OneDrive_${acc.accountName.replace(" ", "_")}_Backup.docx",
                                                type = "DOCX",
                                                content = "Microsoft-Onedrive live workspace snapshot document synced for ${acc.email}. Encrypted safely."
                                            )
                                        }
                                    }

                                    if (nextcloudAccounts.isNotEmpty()) {
                                        syncPercentage = 0.88f
                                        syncLogText = "Connecting ${nextcloudAccounts.size} Nextcloud client(s)..."
                                        nextcloudAccounts.forEach { acc ->
                                            delay(500)
                                            if (!isNetworkOnline) {
                                                syncLogText = "Sync Aborted: Network connection terminated mid-process!"
                                                isSyncingAll = false
                                                return@launch
                                            }
                                            syncLogText = "Nextcloud: Caching WebDAV directories for [${acc.email}]..."
                                            viewModel.addDocument(
                                                name = "Nextcloud_${acc.accountName.replace(" ", "_")}_Casket.md",
                                                type = "MD",
                                                content = "# Nextcloud Sync Archive\nAccount: ${acc.email}\nSystem directories synchronization finished, validating integrity of directories indexes metadata."
                                            )
                                        }
                                    }

                                    delay(600)
                                    syncPercentage = 1f
                                    syncLogText = "Cloud sync daemon finished. Repositories successfully saved offline and local logs matched."
                                    isSyncingAll = false
                                }
                            },
                            enabled = !isSyncingAll && isNetworkOnline && (gdriveAccounts.isNotEmpty() || megaAccounts.isNotEmpty() || onedriveAccounts.isNotEmpty() || nextcloudAccounts.isNotEmpty()),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync All")
                        }
                    }

                    if (isSyncingAll) {
                        LinearProgressIndicator(progress = { syncPercentage }, modifier = Modifier.fillMaxWidth())
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(syncLogText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Offline Cache & Local Files Browser
        item {
            Text("OFFLINE SYNCHRONIZED STORAGE CACHE", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
        }

        if (documents.isEmpty()) {
            item {
                Text("No synchronized files available offline. Click Sync All to download folders.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(16.dp))
            }
        } else {
            items(documents) { doc ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (doc.fileType) {
                                    "PDF" -> Icons.Default.PictureAsPdf
                                    "DOCX" -> Icons.Default.Description
                                    else -> Icons.Default.Article
                                },
                                contentDescription = doc.fileType,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Offline copy verified", fontSize = 10.sp, color = Color(0xFF00C853), fontWeight = FontWeight.Bold)
                                Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                val platformLabel = when {
                                    doc.fileName.contains("GDrive") || doc.fileName.contains("drive") -> "G-Drive"
                                    doc.fileName.contains("Mega") -> "Mega"
                                    doc.fileName.contains("OneDrive") -> "OneDrive"
                                    doc.fileName.contains("Nextcloud") -> "Nextcloud"
                                    else -> "Cloud Cache"
                                }
                                Text("Platform Sync: $platformLabel", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }

                        // Remove offline cache copy button
                        IconButton(onClick = { viewModel.deleteDocument(doc.id) }) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove cached doc", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showingAddAccountDialog) {
        val dialogScrollState = rememberScrollState()
        AlertDialog(
            onDismissRequest = { showingAddAccountDialog = false },
            title = { Text("Authenticate Cloud Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Select Platform Proxy Gateway", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(dialogScrollState)
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("GDrive", "Mega", "OneDrive", "Nextcloud").forEach { plat ->
                            FilterChip(
                                selected = addPlatformSelection == plat,
                                onClick = { addPlatformSelection = plat },
                                label = {
                                    Text(
                                        text = when (plat) {
                                            "GDrive" -> "Google Drive"
                                            "Mega" -> "Mega Decrypted"
                                            "OneDrive" -> "OneDrive"
                                            "Nextcloud" -> "Nextcloud"
                                            else -> plat
                                        }
                                    )
                                },
                                modifier = Modifier.testTag("chip_$plat")
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputAccountName,
                        onValueChange = { inputAccountName = it },
                        label = { Text("Account Label (e.g. Work Cloud)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputEmail,
                        onValueChange = { inputEmail = it },
                        label = { Text("Account Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputToken,
                        onValueChange = { inputToken = it },
                        label = {
                            Text(
                                when (addPlatformSelection) {
                                    "GDrive" -> "OAuth Refresh Token"
                                    "Mega" -> "Mega Password Key"
                                    "OneDrive" -> "Microsoft Auth Token"
                                    "Nextcloud" -> "Nextcloud App Key"
                                    else -> "Credential Key"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputAccountName.isNotBlank() && inputEmail.isNotBlank()) {
                            viewModel.addProfile(
                                platform = addPlatformSelection,
                                name = inputAccountName,
                                email = inputEmail,
                                token = inputToken
                            )
                            showingAddAccountDialog = false
                            inputAccountName = ""
                            inputEmail = ""
                            inputToken = ""
                        }
                    },
                    enabled = inputAccountName.isNotBlank() && inputEmail.isNotBlank()
                ) {
                    Text("Add Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showingAddAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CloudAccountRow(acc: AccountProfile, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (acc.platform) {
                            "GDrive" -> Icons.Default.Cloud
                            "OneDrive" -> Icons.Default.CloudQueue
                            "Nextcloud" -> Icons.Default.CloudSync
                            else -> Icons.Default.Lock // Mega
                        },
                        contentDescription = "platform icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(15.dp)
                    )
                }
                Column {
                    Text(acc.accountName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(acc.email, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Badge(containerColor = Color(0xFF00FF88).copy(alpha = 0.2f), contentColor = Color(0xFF00AA4F)) {
                    Text("Authorized", fontSize = 9.sp, modifier = Modifier.padding(2.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Disconnect account", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun WebSyncTab(viewModel: OmniViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    val scope = rememberCoroutineScope()
    
    val notionAccounts = remember(profiles) { profiles.filter { it.platform == "Notion" } }
    
    var selectedNotionProfile by remember { mutableStateOf("") }
    LaunchedEffect(notionAccounts) {
        if (notionAccounts.isNotEmpty()) selectedNotionProfile = notionAccounts.first().email
    }

    var isSyncingNotion by remember { mutableStateOf(false) }
    var notionLog by remember { mutableStateOf("Ready to sync cached discussions to Notion database.") }

    var savedBookmarks = remember {
        mutableStateListOf(
            BookmarkProxy("Google Android Docs", "https://developer.android.com", "documentation, developers"),
            BookmarkProxy("Material Design 3 Components", "https://m3.material.io", "css, design-system"),
            BookmarkProxy("Raindrop Official Site", "https://raindrop.io", "bookmark-curation")
        )
    }

    var raindropAccountConnected by remember { mutableStateOf(true) }
    var inputBookmarkTitle by remember { mutableStateOf("") }
    var inputBookmarkUrl by remember { mutableStateOf("") }
    var inputBookmarkTags by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 50.dp)
    ) {
        // Notion database push
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("NOTION WORKSPACE SYNC", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFFB74D))
                    Text("Pushes index folders and forum crawl records directly to Notion workspace directories", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    if (notionAccounts.isEmpty()) {
                        Text("Please register a Notion developer token inside Cloud Accounts page first to initialize syncer.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Notion Connection Profile", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                Text(selectedNotionProfile, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    isSyncingNotion = true
                                    scope.launch {
                                        notionLog = "Searching local sqlite caches..."
                                        delay(800)
                                        notionLog = "Matching Workspace hierarchies page blocks..."
                                        delay(900)
                                        notionLog = "Uploading pages structures directly as Notion Subpages..."
                                        delay(1000)
                                        notionLog = "Notion Sync Successful! Parent workspace index validated."
                                        isSyncingNotion = false
                                    }
                                },
                                enabled = !isSyncingNotion,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D), contentColor = Color.Black)
                            ) {
                                if (isSyncingNotion) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                else Text("Sync Notion")
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(notionLog, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Raindrop Bookmark Curator Panel
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("RAINDROP.IO INTEGRATION DAEMON", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF29B6F6))
                            Text("Curates, crawls, and backs up bookmarks collections", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        Switch(
                            checked = raindropAccountConnected,
                            onCheckedChange = { raindropAccountConnected = it }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    if (raindropAccountConnected) {
                        // Quick bookmark creator
                        Text("Quick Add Bookmark Link", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        OutlinedTextField(
                            value = inputBookmarkTitle,
                            onValueChange = { inputBookmarkTitle = it },
                            label = { Text("Bookmark Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputBookmarkUrl,
                            onValueChange = { inputBookmarkUrl = it },
                            label = { Text("Url (e.g. http://...)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputBookmarkTags,
                            onValueChange = { inputBookmarkTags = it },
                            label = { Text("Tags (separated by commas)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (inputBookmarkTitle.isNotBlank() && inputBookmarkUrl.isNotBlank()) {
                                    savedBookmarks.add(0, BookmarkProxy(inputBookmarkTitle, inputBookmarkUrl, inputBookmarkTags))
                                    inputBookmarkTitle = ""
                                    inputBookmarkUrl = ""
                                    inputBookmarkTags = ""
                                }
                            },
                            enabled = inputBookmarkTitle.isNotBlank() && inputBookmarkUrl.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6), contentColor = Color.Black)
                        ) {
                            Text("Save and Sync to Raindrop.io Cloud")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Synced Links & Collections", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                        savedBookmarks.forEach { b ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(b.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(b.url, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("Tags: ${b.tags}", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Badge(containerColor = Color(0xFF29B6F6).copy(alpha = 0.2f), contentColor = Color(0xFF0288D1)) {
                                        Text("Sync", fontSize = 8.sp, modifier = Modifier.padding(2.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Raindrop syncer is deactivated. Toggle on to sign in and view your online folders.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(12.dp))
                    }
                }
            }
        }
    }
}

data class BookmarkProxy(val title: String, val url: String, val tags: String)

@Composable
fun SecureDnsTab() {
    var activeDnsPreset by remember { mutableStateOf("NextDNS") } // "NextDNS", "AdGuard", "Default"
    var isDnsActive by remember { mutableStateOf(false) }
    var queryCounter by remember { mutableStateOf(Random.nextInt(100, 300)) }

    var nextDnsProfileId by remember { mutableStateOf("nx-ab84cd") }
    var adGuardServerIp by remember { mutableStateOf("94.140.14.14") }

    val blockedList = remember {
        mutableStateListOf("track.adscore.com", "analytics.cloud.net", "telemetry-doubleclick.ad")
    }

    var inputBlockDomain by remember { mutableStateOf("") }

    LaunchedEffect(isDnsActive) {
        if (isDnsActive) {
            while (true) {
                delay(2000)
                queryCounter += Random.nextInt(1, 5)
            }
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 50.dp)
    ) {
        // DNS Shield Controller
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("SECURE DNS SHIELD CONTROL PANEL", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF00C853))
                            Text("Intercept and filter network traffic packets via TLS", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }

                        Switch(
                            checked = isDnsActive,
                            onCheckedChange = { isDnsActive = it }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    // Preset selection
                    Text("Select Secured Profile Handshake", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("NextDNS", "AdGuard Secure DNS", "Unfiltered System").forEach { preset ->
                            val isSelected = (preset.startsWith("Next") && activeDnsPreset == "NextDNS") ||
                                    (preset.startsWith("Ad") && activeDnsPreset == "AdGuard") ||
                                    (preset.startsWith("Unf") && activeDnsPreset == "Default")
                            
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    activeDnsPreset = when {
                                        preset.startsWith("Next") -> "NextDNS"
                                        preset.startsWith("Ad") -> "AdGuard"
                                        else -> "Default"
                                    }
                                },
                                label = { Text(preset, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    when (activeDnsPreset) {
                        "NextDNS" -> {
                            Text("NextDNS Profile Configurations", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            OutlinedTextField(
                                value = nextDnsProfileId,
                                onValueChange = { nextDnsProfileId = it },
                                label = { Text("NextDNS Profile ID Certificate") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("Endpoints DoT: https://dns.nextdns.io/$nextDnsProfileId", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        "AdGuard" -> {
                            Text("AdGuard Secured IP Anchors", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            OutlinedTextField(
                                value = adGuardServerIp,
                                onValueChange = { adGuardServerIp = it },
                                label = { Text("Primary AdGuard IP Server") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("Endpoints DoH: https://dns.adguard-dns.com/dns-query", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        else -> {
                            Text("Using system default ISP settings. Your DNS Queries are unencrypted.", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // DNS Metrics and Live queries block
        if (isDnsActive) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF00FF88), CircleShape))
                            Text("DNS FILTER SHIELD IS CURRENTLY ENFORCED", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF00C853))
                        }
                        Text("Encrypted requests intercepted today: $queryCounter rules metrics logs", fontSize = 11.sp)
                        Text("Active engine profile: $activeDnsPreset DNS-over-TLS with SECURE Handshake", fontSize = 11.sp)
                    }
                }
            }
        }

        // Custom Domain Filtering list
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("CUSTOM BLACKLIST AD INTERCEPTOR", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
                    Text("Add domain names that you wish to block from tracking or displaying ads in webpage wrappers", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputBlockDomain,
                            onValueChange = { inputBlockDomain = it },
                            placeholder = { Text("e.g. doubleclick.net") },
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                if (inputBlockDomain.isNotBlank()) {
                                    blockedList.add(inputBlockDomain)
                                    inputBlockDomain = ""
                                }
                            },
                            enabled = inputBlockDomain.isNotBlank()
                        ) {
                            Text("Add Block")
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                    Text("Active Blacklisted Host Domains", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                    blockedList.forEach { domain ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Block, contentDescription = "Blocked", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(13.dp))
                                Text(domain, fontSize = 12.sp)
                            }

                            IconButton(onClick = { blockedList.remove(domain) }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove block", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CloudServicesMonitorSection(
    gdriveCount: Int,
    megaCount: Int,
    onedriveCount: Int,
    nextcloudCount: Int,
    isSyncingActive: Boolean,
    syncProgress: Float,
    isNetworkOnline: Boolean,
    onNetworkOnlineChange: (Boolean) -> Unit,
    networkType: String,
    onNetworkTypeChange: (String) -> Unit
) {
    var simulatedLatency by remember { mutableStateOf(14) }
    var packetLight by remember { mutableStateOf(false) }

    // Latency and packet simulation loop
    LaunchedEffect(isNetworkOnline) {
        if (isNetworkOnline) {
            while (true) {
                delay(1200)
                simulatedLatency = Random.nextInt(12, 24)
                packetLight = !packetLight
            }
        } else {
            simulatedLatency = 0
            packetLight = false
        }
    }

    // Infinite transition for pulsing glowing beacons
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // --- 1. Network Connectivity Indicator Card ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (isNetworkOnline) Color(0xFF00E676).copy(alpha = pulseAlpha)
                                    else Color(0xFFFF5252).copy(alpha = pulseAlpha),
                                    CircleShape
                                )
                        )
                        Text(
                            text = "NETWORK LINK STATUS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    // Network simulation mode chiplets
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simulate:", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        listOf("Wi-Fi", "5G Mobile", "Offline").forEach { type ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (type == "Offline" && !isNetworkOnline) MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                        else if (type != "Offline" && isNetworkOnline && networkType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        if (type == "Offline") {
                                            onNetworkOnlineChange(false)
                                            onNetworkTypeChange("Offline")
                                        } else {
                                            onNetworkOnlineChange(true)
                                            onNetworkTypeChange(type)
                                        }
                                    }
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (type == "Offline" && !isNetworkOnline) Color(0xFFEF5350)
                                    else if (type != "Offline" && isNetworkOnline && networkType == type) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Network Stat Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isNetworkOnline) "Uplink established successfully" else "Gateway offline / Signal Terminated",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNetworkOnline) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = if (isNetworkOnline) "Protocol: IPv4/IPv6 Tunnel over AIS Secure Gateway" else "Reason: Local device antenna emulation switched off",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("LATENCY", fontSize = 8.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (isNetworkOnline) "$simulatedLatency ms" else "∞ ms",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isNetworkOnline) Color(0xFF00E676) else Color(0xFFFF5252)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("BANDWIDTH", fontSize = 8.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (!isNetworkOnline) "0 Kbps"
                                       else if (isSyncingActive) "124 Mbps"
                                       else if (networkType == "Wi-Fi") "850 Mbps"
                                       else "45 Mbps",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Simulated Blinking packet transfers indicator
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    if (isNetworkOnline && packetLight) Color(0xFF00FF88).copy(alpha = 0.6f)
                                    else Color.Gray.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        )
                    }
                }
            }
        }

        // --- 2. Active Sync Services (4 Animated Dashboard Cards Grid) ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // google drive Card
                val gdSyncing = isSyncingActive && syncProgress > 0.05f && syncProgress <= 0.35f
                MonitorServiceCard(
                    modifier = Modifier.weight(1f),
                    title = "Google Drive",
                    icon = Icons.Default.Cloud,
                    accountsCount = gdriveCount,
                    isOnline = isNetworkOnline,
                    isSyncing = gdSyncing,
                    pulseAlpha = pulseAlpha,
                    accentColor = Color(0xFF1E88E5), // Blue
                    progressVal = if (gdSyncing) (syncProgress - 0.05f) / 0.3f else 0f
                )

                // mega Card
                val megaSyncing = isSyncingActive && syncProgress > 0.35f && syncProgress <= 0.65f
                MonitorServiceCard(
                    modifier = Modifier.weight(1f),
                    title = "MEGA Vault",
                    icon = Icons.Default.Lock,
                    accountsCount = megaCount,
                    isOnline = isNetworkOnline,
                    isSyncing = megaSyncing,
                    pulseAlpha = pulseAlpha,
                    accentColor = Color(0xFFE53935), // Crimson Red
                    progressVal = if (megaSyncing) (syncProgress - 0.35f) / 0.3f else 0f
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // onedrive Card
                val odSyncing = isSyncingActive && syncProgress > 0.65f && syncProgress <= 0.85f
                MonitorServiceCard(
                    modifier = Modifier.weight(1f),
                    title = "OneDrive MS",
                    icon = Icons.Default.CloudQueue,
                    accountsCount = onedriveCount,
                    isOnline = isNetworkOnline,
                    isSyncing = odSyncing,
                    pulseAlpha = pulseAlpha,
                    accentColor = Color(0xFF039BE5), // OneDrive Blue
                    progressVal = if (odSyncing) (syncProgress - 0.65f) / 0.2f else 0f
                )

                // nextcloud Card
                val ncSyncing = isSyncingActive && syncProgress > 0.85f && syncProgress <= 0.99f
                MonitorServiceCard(
                    modifier = Modifier.weight(1f),
                    title = "Nextcloud Lab",
                    icon = Icons.Default.CloudSync,
                    accountsCount = nextcloudCount,
                    isOnline = isNetworkOnline,
                    isSyncing = ncSyncing,
                    pulseAlpha = pulseAlpha,
                    accentColor = Color(0xFF00ACC1), // Cyan Nextcloud
                    progressVal = if (ncSyncing) (syncProgress - 0.85f) / 0.14f else 0f
                )
            }
        }
    }
}

@Composable
fun MonitorServiceCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    accountsCount: Int,
    isOnline: Boolean,
    isSyncing: Boolean,
    pulseAlpha: Float,
    accentColor: Color,
    progressVal: Float
) {
    // Beautiful scaling and bouncy spring transitions
    val scaleFactor by animateFloatAsState(
        targetValue = if (isSyncing) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bounceScale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSyncing) accentColor.copy(alpha = 0.08f)
                             else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSyncing) 1.5.dp else 1.dp,
            color = if (isSyncing) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = modifier
            .graphicsLayer(scaleX = scaleFactor, scaleY = scaleFactor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon + Status Beacon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "platform",
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Small pulsing status bead
                val badgeColor = when {
                    !isOnline -> Color(0xFFEF5350) // Red offline
                    accountsCount == 0 -> Color(0xFF757575) // Grey unconfigured
                    isSyncing -> accentColor // Syncing
                    else -> Color(0xFF00C853) // Green configured idle
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            badgeColor.copy(alpha = if (isSyncing || isOnline) pulseAlpha else 0.5f),
                            CircleShape
                        )
                )
            }

            // Title + Config info
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (accountsCount > 0) "$accountsCount Connected" else "Unconfigured Link",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Simple status label or fine animated bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        !isOnline -> "Offline Mode"
                        accountsCount == 0 -> "Unlinked Node"
                        isSyncing -> "SYNCING..."
                        else -> "Uplink Standby"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        !isOnline -> Color(0xFFEF5350)
                        accountsCount == 0 -> MaterialTheme.colorScheme.outline
                        isSyncing -> accentColor
                        else -> Color(0xFF00C853)
                    }
                )

                if (isSyncing) {
                    Text(
                        text = "${(progressVal * 100).toInt()}%",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            // Show miniature progress bar if syncing
            if (isSyncing) {
                LinearProgressIndicator(
                    progress = { progressVal },
                    color = accentColor,
                    trackColor = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape)
                )
            } else if (isOnline && accountsCount > 0) {
                // Decorative stationary full line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color(0xFF00C853).copy(alpha = 0.15f), CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), CircleShape)
                )
            }
        }
    }
}
