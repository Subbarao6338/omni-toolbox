package com.nature.files.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.*

@Composable
fun DuplicateFinderScreen(
    duplicateGroups: List<List<FileItem>>,
    onDeleteFile: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (duplicateGroups.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize().leafLitter(CanopyGreen.copy(alpha = 0.05f)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "No duplicate branches found in the grove.",
                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Spectral),
                color = CanopyGreen
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(duplicateGroups) { group ->
                DuplicateGroupItem(group, onDeleteFile)
            }
        }
    }
}

@Composable
fun DuplicateGroupItem(
    group: List<FileItem>,
    onDeleteFile: (FileItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .leafLitter(LichenGrey.copy(alpha = 0.1f), density = 20),
        colors = CardDefaults.cardColors(
            containerColor = ForestFloorBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Duplicate Set (${group.size} files)",
                style = MaterialTheme.typography.titleSmall,
                color = BarkBrown
            )
            Spacer(Modifier.height(8.dp))
            group.forEach { file ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BarkBrown
                        )
                        Text(
                            text = file.path,
                            style = MaterialTheme.typography.bodySmall,
                            color = CanopyGreen,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { onDeleteFile(file) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Duplicate",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
