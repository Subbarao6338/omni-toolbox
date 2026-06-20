package com.nature.files.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.LichenGrey
import com.nature.files.ui.theme.Spectral
import com.nature.files.ui.theme.leafLitter

data class PermissionInfo(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val isGranted: Boolean,
    val requiredFor: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionAuditScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val permissions = listOf(
        PermissionInfo(
            name = "All Files Access",
            description = "Required to manage files across internal storage.",
            icon = Icons.Default.Storage,
            isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager() else true,
            requiredFor = "Core File Operations"
        ),
        PermissionInfo(
            name = "Storage (Legacy)",
            description = "Access to media and files on older Android versions.",
            icon = Icons.Default.SdStorage,
            isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED,
            requiredFor = "Compatibility"
        ),
        PermissionInfo(
            name = "Network Access",
            description = "Required for SMB and FTP server functionality.",
            icon = Icons.Default.NetworkCheck,
            isGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED,
            requiredFor = "LAN Features"
        ),
        PermissionInfo(
            name = "Biometric",
            description = "Used to secure the Vault clearing.",
            icon = Icons.Default.Fingerprint,
            isGranted = true, // Usually handled at runtime, but check if hardware is available could go here
            requiredFor = "Vault Security"
        ),
        PermissionInfo(
            name = "Install Packages",
            description = "Required for the APK Batch Installer.",
            icon = Icons.Default.SystemUpdate,
            isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.packageManager.canRequestPackageInstalls() else true,
            requiredFor = "App Management"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permission Grove Audit", style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Transparency in the Forest",
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = Spectral),
                    color = BarkBrown
                )
                Text(
                    "Behold the roots of our access. We only ask for what is needed to let your grove flourish.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LichenGrey,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            items(permissions) { permission ->
                PermissionItem(permission)
            }
        }
    }
}

@Composable
fun PermissionItem(permission: PermissionInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                permission.icon,
                contentDescription = null,
                tint = if (permission.isGranted) CanopyGreen else BarkBrown,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    permission.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = BarkBrown
                )
                Text(
                    permission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = LichenGrey
                )
                Spacer(Modifier.height(4.dp))
                SuggestionChip(
                    onClick = { },
                    label = { Text(permission.requiredFor) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        labelColor = CanopyGreen
                    )
                )
            }
            if (permission.isGranted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = CanopyGreen
                )
            } else {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Not Granted",
                    tint = BarkBrown
                )
            }
        }
    }
}
