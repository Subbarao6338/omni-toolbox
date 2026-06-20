package com.nature.files.ui.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen
import java.io.File

@Composable
fun ApkBatchInstaller(
    apkFiles: List<FileItem>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    var currentApkIndex by remember { mutableStateOf(0) }

    val apkInfos = remember(apkFiles) {
        apkFiles.mapNotNull { file ->
            val info = packageManager.getPackageArchiveInfo(file.path, PackageManager.GET_PERMISSIONS)
            if (info != null) {
                file to info
            } else {
                null
            }
        }
    }

    NatureDialog(
        onDismissRequest = onDismiss,
        title = "Forest Batch Installer",
        confirmButton = {
            Button(onClick = {
                apkInfos.forEach { (file, _) ->
                    installApk(context, File(file.path))
                }
                onDismiss()
            }) {
                Text("Install All")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        Column {
            Text("Ready to plant ${apkInfos.size} applications.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(apkInfos) { (file, info) ->
                    ApkInfoItem(file, info)
                }
            }
        }
    }
}

@Composable
private fun ApkInfoItem(file: FileItem, info: PackageInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Android, contentDescription = null, tint = CanopyGreen)
                Spacer(Modifier.width(8.dp))
                Text(file.name, style = MaterialTheme.typography.titleSmall, color = BarkBrown)
            }
            Text("Version: ${info.versionName}", style = MaterialTheme.typography.labelSmall)

            val permissions = info.requestedPermissions ?: emptyArray()
            if (permissions.isNotEmpty()) {
                Text("Permissions: ${permissions.size} requested", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

private fun installApk(context: Context, file: File) {
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
