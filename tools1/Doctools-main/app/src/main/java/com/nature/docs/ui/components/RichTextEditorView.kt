package com.nature.docs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.BotanicalGreen
import com.nature.docs.ui.theme.InkBrown

@Composable
fun RichTextEditorView() {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        AgedPaperCard(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = {}) { Icon(Icons.Default.FormatBold, contentDescription = "Bold", tint = BotanicalGreen) }
                IconButton(onClick = {}) { Icon(Icons.Default.FormatItalic, contentDescription = "Italic", tint = BotanicalGreen) }
            }
        }

        // Canvas
        LinenCanvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        }
    }
}
