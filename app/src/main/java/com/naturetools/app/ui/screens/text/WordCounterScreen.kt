package com.naturetools.app.ui.screens.text

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun WordCounterScreen(navController: NavHostController, title: String = "Word Counter", initialText: String = "") {
    var text by remember { mutableStateOf(initialText) }

    val wordCount = remember(text) {
        if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
    }
    val charCount = text.length
    val charCountNoSpaces = text.replace(" ", "").length
    val sentenceCount = remember(text) {
        if (text.isBlank()) 0 else text.split("[.!?]+".toRegex()).filter { it.isNotBlank() }.size
    }
    val paragraphCount = remember(text) {
        if (text.isBlank()) 0 else text.split("\n+".toRegex()).filter { it.isNotBlank() }.size
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Input Text") },
                placeholder = { Text("Paste or type your text here...") }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CounterRow("Words", wordCount)
                    CounterRow("Characters", charCount)
                    CounterRow("Characters (no spaces)", charCountNoSpaces)
                    CounterRow("Sentences", sentenceCount)
                    CounterRow("Paragraphs", paragraphCount)
                }
            }
        }
    }
}

@Composable
private fun CounterRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(count.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
