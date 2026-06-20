package com.nature.docs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.*

@Composable
fun ToolsView(onToolClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    LinenCanvas(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AgedPaperCard(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = InkBrown.copy(0.4f))
                    Spacer(Modifier.width(12.dp))
                    Text("Search the studio archives...", style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.4f))
                }
            }
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "STUDIO COLLECTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkBrown.copy(0.4f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(listOf(
                    "PDF Field Guide" to "pdf_editor",
                    "Image Specimen" to "image_editor",
                    "Natural Video" to "video_editor",
                    "Forest Sounds" to "audio_tools",
                    "Office Desk" to "word2pdf",
                    "Utility Kit" to "merge"
                )) { (category, id) ->
                    AgedPaperCard(modifier = Modifier.fillMaxWidth().height(120.dp).clickable { onToolClick(id) }) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(category, style = MaterialTheme.typography.titleLarge, color = InkBrown)
                        }
                    }
                }
            }
        }
    }
}
