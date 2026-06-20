package com.nature.docs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.InkBrown
import com.nature.docs.ui.theme.ParchmentBg
import com.nature.docs.ui.theme.SourceSerif4

@Composable
fun MarkdownEditorView() {
    var text by remember { mutableStateOf("# Nature Document\n\nWrite your field notes here...") }

    Row(modifier = Modifier.fillMaxSize()) {
        // Editor
        LinenCanvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxSize().padding(16.dp),
                textStyle = TextStyle(fontFamily = SourceSerif4, color = InkBrown)
            )
        }

        // Split Divider
        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(InkBrown.copy(0.1f)))

        // Preview
        LinenCanvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Preview", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.5f))
                Spacer(Modifier.height(8.dp))
                Text(text, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
