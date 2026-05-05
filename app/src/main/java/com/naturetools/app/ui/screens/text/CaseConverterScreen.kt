package com.naturetools.app.ui.screens.text

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.Locale

@Composable
fun CaseConverterScreen(navController: NavHostController) {
    var text by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    ToolScreen(
        title = "Case Converter",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { clipboardManager.setText(AnnotatedString(text)) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
            }
        }
    ) { padding ->
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { text = text.uppercase(Locale.getDefault()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("UPPER", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = { text = text.lowercase(Locale.getDefault()) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("lower", style = MaterialTheme.typography.labelSmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        text = text.split(" ").joinToString(" ") { word ->
                            word.lowercase(Locale.getDefault())
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Title Case", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = {
                        text = text.lowercase(Locale.getDefault()).replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sentence", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
