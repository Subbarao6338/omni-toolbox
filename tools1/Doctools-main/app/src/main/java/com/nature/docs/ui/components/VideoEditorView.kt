package com.nature.docs.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.*

@Composable
fun VideoEditorView(uri: Uri, onBack: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }

    Scaffold(
        bottomBar = {
            AgedPaperCard(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Timeline", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(8.dp))

                    // Bamboo-styled scrubber
                    Box(modifier = Modifier.fillMaxWidth().height(40.dp).background(InkBrown.copy(0.05f), RoundedCornerShape(20.dp)), contentAlignment = Alignment.CenterStart) {
                        Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(BotanicalGreen.copy(0.3f), RoundedCornerShape(20.dp)))
                        Slider(
                            value = progress,
                            onValueChange = { progress = it },
                            colors = SliderDefaults.colors(thumbColor = BotanicalGreen, activeTrackColor = Color.Transparent, inactiveTrackColor = Color.Transparent)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = BotanicalGreen, modifier = Modifier.size(48.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        LinenCanvas(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AgedPaperCard(modifier = Modifier.size(300.dp, 200.dp)) {
                    Text("Video Player Placeholder")
                }
            }
        }
    }
}
