package com.nature.files.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.Spectral

@Composable
fun HomeScreen(
    onNavigateToPath: (String) -> Unit,
    onToolClick: (String) -> Unit,
    shortcuts: List<com.nature.files.data.db.SidebarShortcutEntity>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Forest Clearing",
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = Spectral, fontWeight = FontWeight.Bold),
            color = BarkBrown
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Grove Patches (Storage)",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral),
            color = BarkBrown
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            items(shortcuts) { shortcut ->
                GrovePatchCard(
                    name = shortcut.name,
                    icon = when (shortcut.iconName) {
                        "Storage" -> Icons.Default.Storage
                        "Lock" -> Icons.Default.Lock
                        else -> Icons.Default.Cloud
                    },
                    onClick = { onNavigateToPath(shortcut.path) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Forest Tools",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral),
            color = BarkBrown
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ToolIconCard("Timeline", Icons.Default.Timeline, onToolClick) }
            item { ToolIconCard("Duplicates", Icons.Default.CopyAll, onToolClick) }
            item { ToolIconCard("Analyzer", Icons.Default.BarChart, onToolClick) }
            item { ToolIconCard("Audit", Icons.Default.Shield, onToolClick) }
            item { ToolIconCard("FTP", Icons.Default.Router, onToolClick) }
            item { ToolIconCard("Watcher", Icons.Default.Visibility, onToolClick) }
        }
    }
}

@Composable
fun GrovePatchCard(name: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = BarkBrown)
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.labelLarge, color = BarkBrown)
        }
    }
}

@Composable
fun ToolIconCard(name: String, icon: ImageVector, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(name) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = BarkBrown)
            Spacer(modifier = Modifier.height(4.dp))
            Text(name, style = MaterialTheme.typography.labelSmall, color = BarkBrown)
        }
    }
}
