package com.naturetools.app.ui.screens

import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.io.File

@Composable
fun StorageScreen(navController: NavHostController) {
    val path = Environment.getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val availableBlocks = stat.availableBlocksLong

    val totalSpace = totalBlocks * blockSize
    val availableSpace = availableBlocks * blockSize
    val usedSpace = totalSpace - availableSpace
    val usedPercentage = if (totalSpace > 0) usedSpace.toFloat() / totalSpace else 0f

    fun formatSize(size: Long): String {
        val gb = size.toDouble() / (1024 * 1024 * 1024)
        return java.lang.String.format(java.util.Locale.US, "%.2f GB", gb)
    }

    ToolScreen(title = "Storage Info", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Internal Storage", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = usedPercentage,
                    modifier = Modifier.size(200.dp),
                    strokeWidth = 16.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(usedPercentage * 100).toInt()}%", style = MaterialTheme.typography.displaySmall)
                    Text("Used", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StorageRow("Total Space", formatSize(totalSpace))
                    StorageRow("Used Space", formatSize(usedSpace))
                    StorageRow("Available Space", formatSize(availableSpace))
                }
            }
        }
    }
}

@Composable
fun StorageRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}
