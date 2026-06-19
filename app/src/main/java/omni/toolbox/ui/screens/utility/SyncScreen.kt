package omni.toolbox.ui.screens.utility

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.OmniViewModel
import omni.toolbox.viewmodel.CloudAccount
import kotlinx.coroutines.launch

@Composable
fun SyncScreen(navController: NavHostController, viewModel: OmniViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Accounts", "Backups", "Logs")

    ToolScreen(
        title = "Cloud Sync & Data Hub",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> AccountsTab(viewModel)
                    1 -> BackupsTab()
                    2 -> LogsTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun AccountsTab(viewModel: OmniViewModel) {
    val scope = rememberCoroutineScope()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Connected Profiles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Button(
                onClick = { scope.launch { viewModel.startSyncAll() } },
                enabled = !viewModel.isSyncing.value
            ) {
                if (viewModel.isSyncing.value) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Sync All")
            }
        }

        viewModel.accounts.forEach { account ->
            AccountCard(account) { viewModel.removeAccount(account.id) }
        }

        OutlinedButton(
            onClick = { /* Add account */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Cloud Service")
        }
    }
}

@Composable
fun AccountCard(account: CloudAccount, onDelete: () -> Unit) {
    val icon = when (account.type) {
        "GDrive" -> Icons.Default.Cloud
        "Mega" -> Icons.Default.VpnKey
        "OneDrive" -> Icons.Default.FolderShared
        else -> Icons.Default.Storage
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(account.type, fontWeight = FontWeight.Bold)
                    Text(account.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Account", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { 0.7f }, // Hardcoded for demo
                    modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("${account.storageUsed} / ${account.storageTotal}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun BackupsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Local Data Backups", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        BackupItem("Call Logs", "2,450 entries", Icons.Default.Call)
        BackupItem("SMS Messages", "14,201 messages", Icons.AutoMirrored.Filled.Message)
        BackupItem("App Contacts", "412 contacts", Icons.Default.Contacts)
        BackupItem("System Logs", "1.2 MB archived", Icons.Default.History)

        Spacer(modifier = Modifier.height(8.dp))
        Text("Scheduled Snapshots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Daily Automated Snapshot") },
                supportingContent = { Text("Next run: 02:00 AM") },
                trailingContent = { Switch(checked = true, onCheckedChange = {}) }
            )
        }
    }
}

@Composable
fun BackupItem(label: String, stats: String, icon: ImageVector) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold)
                Text(stats, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = { /* Backup now */ }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) {
                Text("Backup", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun LogsTab(viewModel: OmniViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Synchronization Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = { /* Clear */ }) { Text("Clear") }
        }

        Card(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState())
            ) {
                if (viewModel.syncLogs.isEmpty()) {
                    Text("No logs available.", color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                } else {
                    viewModel.syncLogs.forEach { log ->
                        Text(
                            text = log.message,
                            color = if (log.message.contains("[OK]")) Color(0xFF39FF14) else Color(0xFF00BCD4),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
