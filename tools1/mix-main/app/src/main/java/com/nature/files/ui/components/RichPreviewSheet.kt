package com.nature.files.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.Spectral
import com.nature.files.ui.theme.leafLitter
import com.nature.files.utils.ChecksumUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichPreviewSheet(
    fileItem: FileItem,
    onDismiss: () -> Unit,
    onUpdateTags: (List<String>) -> Unit,
    currentTags: List<String> = emptyList()
) {
    val availableTags = listOf("Moss", "Amber", "Sky", "Clay")
    val tagColors = mapOf(
        "Moss" to Color(0xFF386641),
        "Amber" to Color(0xFFFBBF24),
        "Sky" to Color(0xFF8ECAE6),
        "Clay" to Color(0xFFBC6C25)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ForestFloorBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = fileItem.name,
                style = MaterialTheme.typography.titleLarge,
                color = BarkBrown
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tags", style = MaterialTheme.typography.titleMedium, color = BarkBrown)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableTags) { tag ->
                    val isSelected = currentTags.contains(tag)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newTags = if (isSelected) {
                                currentTags.filter { it != tag }
                            } else {
                                currentTags + tag
                            }
                            onUpdateTags(newTags)
                        },
                        label = { Text(tag) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tagColors[tag] ?: Color.Gray,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PropertyRow("Path", fileItem.path)
            PropertyRow("Size", "${fileItem.size} bytes")
            PropertyRow("Type", fileItem.mimeType ?: "Unknown")

            if (!fileItem.isDirectory) {
                var checksum by remember { mutableStateOf<String?>(null) }
                val scope = rememberCoroutineScope()

                Spacer(modifier = Modifier.height(8.dp))
                if (checksum == null) {
                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            checksum = ChecksumUtils.calculateSHA256(File(fileItem.path))
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Calculate SHA-256")
                    }
                } else {
                    PropertyRow("SHA-256", checksum!!)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun PropertyRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = Spectral),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Spectral),
            color = BarkBrown
        )
    }
}
