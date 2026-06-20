package com.nature.docs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.*

@Composable
fun HomeView(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onToolClick: (String) -> Unit,
    onOpenPreview: (android.net.Uri, String, Int) -> Unit
) {
    LinenCanvas(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Field Guide",
                    style = MaterialTheme.typography.displayLarge,
                    color = InkBrown
                )
                Text(
                    text = "Your nature-inspired toolkit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBrown.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(24.dp))
            }
            
            item {
                AgedPaperCard(modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onToolClick("scanner") }) {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = BotanicalGreen, modifier = Modifier.size(56.dp))
                        }
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text("Quick Scan", style = MaterialTheme.typography.headlineMedium, color = InkBrown)
                            Text("Capture specimens with AI", style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.6f))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            
            item {
                Text(
                    "RECENT DISCOVERIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkBrown.copy(0.4f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(listOf(
                Triple("PDF Field Notes", "pdf_editor", Icons.Default.Eco),
                Triple("Botanical Studio", "image_editor", Icons.Default.GridView),
                Triple("Acoustic Room", "audio_tools", Icons.Default.History)
            )) { (label, id, icon) ->
                AgedPaperCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onToolClick(id) }) {
                    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, null, tint = BotanicalGreen, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge, color = InkBrown)
                        Spacer(Modifier.weight(1f))
                        Text("STUDY", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen)
                    }
                }
            }
        }
    }
}
