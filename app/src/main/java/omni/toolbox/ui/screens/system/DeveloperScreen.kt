package omni.toolbox.ui.screens.system

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.io.File

@Composable
fun DeveloperScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Specifications", "App Manager", "Integrity")

    ToolScreen(
        title = "Developer & Hardware Console",
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
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> Column(modifier = Modifier.verticalScroll(rememberScrollState())) { SpecificationsTab() }
                    1 -> AppManagerTab()
                    2 -> Column(modifier = Modifier.verticalScroll(rememberScrollState())) { IntegrityTab() }
                }
            }
        }
    }
}

@Composable
fun SpecificationsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SpecCard("Board", Build.BOARD, Icons.Default.Memory)
        SpecCard("Architecture", Build.SUPPORTED_ABIS.joinToString(", "), Icons.Default.Architecture)
        SpecCard("Kernel", System.getProperty("os.version") ?: "Unknown", Icons.Default.SettingsInputComponent)
        SpecCard("Bootloader", Build.BOOTLOADER, Icons.Default.Lock)
        SpecCard("Hardware", Build.HARDWARE, Icons.Default.DeveloperBoard)
        SpecCard("Model", Build.MODEL, Icons.Default.Smartphone)
    }
}

@Composable
fun AppManagerTab() {
    val context = LocalContext.current
    val pm = context.packageManager
    val apps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .sortedBy { it.loadLabel(pm).toString() }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("User Packages (${apps.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apps.size) { index ->
                val app = apps[index]
                ListItem(
                    headlineContent = { Text(app.loadLabel(pm).toString()) },
                    supportingContent = { Text(app.packageName) },
                    leadingContent = {
                        Icon(Icons.Default.Android, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Package Name", app.packageName)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied: ${app.packageName}", Toast.LENGTH_SHORT).show()
                            }) { Icon(Icons.Default.ContentCopy, contentDescription = "Copy Package") }

                            IconButton(onClick = {
                                try {
                                    val sourceFile = File(app.sourceDir)
                                    val destDir = File(context.cacheDir, "apk_backups")
                                    if (!destDir.exists()) destDir.mkdirs()
                                    val destFile = File(destDir, "${app.packageName}.apk")
                                    sourceFile.copyTo(destFile, overwrite = true)
                                    Toast.makeText(context, "APK Backed up: ${destFile.name}", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }) { Icon(Icons.Default.Backup, contentDescription = "Backup APK") }
                        }
                    }
                )
                HorizontalDivider()
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { /* Export logic */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export List")
            }

            Button(
                onClick = {
                    try {
                        val cacheDir = context.cacheDir
                        val files = cacheDir.listFiles()
                        var deletedCount = 0
                        files?.forEach {
                            if (it.deleteRecursively()) deletedCount++
                        }
                        Toast.makeText(context, "Cleaned $deletedCount cache segments", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cleanup failed", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Default.CleaningServices, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Cache")
            }
        }
    }
}

@Composable
fun IntegrityTab() {
    var diagnosticDensity by remember { mutableFloatStateOf(0.5f) }
    val isRooted = remember {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        val hasSuBinary = paths.any { File(it).exists() }
        val hasTestKeys = Build.TAGS?.contains("test-keys") ?: false
        hasSuBinary || hasTestKeys
    }

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text("Sandbox Integrity Controllers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Column {
            Text("Diagnostic Density Filter", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = diagnosticDensity,
                onValueChange = { diagnosticDensity = it },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Level: ${(diagnosticDensity * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }

        ListItem(
            headlineContent = { Text("Device Root Status") },
            supportingContent = { Text(if (isRooted) "Root access detected on this device." else "No root access detected.") },
            trailingContent = {
                Icon(
                    imageVector = if (isRooted) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isRooted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isRooted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isRooted) Icons.Default.ReportProblem else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isRooted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    if (isRooted) "Security Warning: Rooted environment detected. Sandbox integrity may be compromised."
                    else "Integrity check passed. Rootless user-space environment confirmed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isRooted) MaterialTheme.colorScheme.onErrorContainer else Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun SpecCard(label: String, value: String, icon: ImageVector) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}
