package com.naturetools.app.ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun JsonFormatterScreen(navController: NavHostController) {
    val clipboardManager = LocalClipboardManager.current
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun formatJson() {
        try {
            val trimmed = input.trim()
            if (trimmed.isEmpty()) {
                output = ""
                error = null
                return
            }
            // Basic manual formatting for offline use without heavy libraries
            var indent = 0
            val sb = StringBuilder()
            var inQuotes = false

            for (char in trimmed) {
                when (char) {
                    '"' -> {
                        inQuotes = !inQuotes
                        sb.append(char)
                    }
                    '{', '[' -> {
                        sb.append(char)
                        if (!inQuotes) {
                            indent++
                            sb.append("\n").append("  ".repeat(indent))
                        }
                    }
                    '}', ']' -> {
                        if (!inQuotes) {
                            indent--
                            sb.append("\n").append("  ".repeat(indent))
                        }
                        sb.append(char)
                    }
                    ',' -> {
                        sb.append(char)
                        if (!inQuotes) {
                            sb.append("\n").append("  ".repeat(indent))
                        }
                    }
                    ':' -> {
                        sb.append(char)
                        if (!inQuotes) sb.append(" ")
                    }
                    else -> if (!char.isWhitespace() || inQuotes) sb.append(char)
                }
            }
            output = sb.toString()
            error = null
        } catch (e: Exception) {
            error = "Invalid JSON"
        }
    }

    ToolScreen(title = "JSON Formatter", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Raw JSON") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { formatJson() }, modifier = Modifier.fillMaxWidth()) {
                Text("Format")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Formatted JSON", style = MaterialTheme.typography.labelLarge)
                if (output.isNotEmpty()) {
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(output)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                }
            }
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    output,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}
