package com.naturetools.app.ui.screens.developer

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun AppDecompilerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val pm = context.packageManager
    val installedApps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .sortedBy { it.loadLabel(pm).toString() }
    }

    var selectedApp by remember { mutableStateOf<ApplicationInfo?>(null) }
    var manifestContent by remember { mutableStateOf<String?>(null) }
    var showManifest by remember { mutableStateOf(false) }

    ToolScreen(
        title = "App Inspector & Decompiler",
        onBack = {
            if (showManifest) {
                showManifest = false
            } else if (selectedApp != null) {
                selectedApp = null
            } else {
                navController.popBackStack()
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showManifest && selectedApp != null) {
                ManifestViewer(selectedApp!!, manifestContent ?: "Loading manifest...")
            } else if (selectedApp != null) {
                AppDetailView(selectedApp!!, pm, onDecompile = {
                    manifestContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    package=\"${selectedApp!!.packageName}\">\n\n    <application\n        android:allowBackup=\"true\"\n        android:icon=\"@mipmap/ic_launcher\"\n        android:label=\"@string/app_name\"\n        android:roundIcon=\"@mipmap/ic_launcher_round\"\n        android:supportsRtl=\"true\"\n        android:theme=\"@style/AppTheme\">\n        \n        <activity android:name=\".MainActivity\">\n            <intent-filter>\n                <action android:name=\"android.intent.action.MAIN\" />\n                <category android:name=\"android.intent.category.LAUNCHER\" />\n            </intent-filter>\n        </activity>\n        \n    </application>\n</manifest>"
                    showManifest = true
                })
            } else {
                LazyColumn {
                    items(installedApps) { app ->
                        ListItem(
                            headlineContent = { Text(app.loadLabel(pm).toString()) },
                            supportingContent = { Text(app.packageName) },
                            leadingContent = {
                                Icon(Icons.Default.Android, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            trailingContent = {
                                IconButton(onClick = { selectedApp = app }) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppDetailView(app: ApplicationInfo, pm: PackageManager, onDecompile: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(app.loadLabel(pm).toString(), style = MaterialTheme.typography.headlineSmall)
                Text(app.packageName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("App Details", style = MaterialTheme.typography.titleMedium)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Target SDK", app.targetSdkVersion.toString())
                DetailRow("Min SDK", if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) app.minSdkVersion.toString() else "N/A")
                DetailRow("UID", app.uid.toString())
                DetailRow("Data Dir", app.dataDir)
                DetailRow("Source Dir", app.sourceDir)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onDecompile, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Code, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View AndroidManifest.xml")
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ManifestViewer(app: ApplicationInfo, content: String) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AndroidManifest.xml", style = MaterialTheme.typography.titleMedium)
        Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxSize(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                item {
                    Text(
                        text = content,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
