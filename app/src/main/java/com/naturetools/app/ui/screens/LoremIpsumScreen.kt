package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun LoremIpsumScreen(navController: NavHostController) {
    var paragraphs by remember { mutableFloatStateOf(3f) }
    val clipboardManager = LocalClipboardManager.current

    val baseText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

    val generatedText = remember(paragraphs) {
        List(paragraphs.toInt()) { baseText }.joinToString("\n\n")
    }

    ToolScreen(
        title = "Lorem Ipsum",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { clipboardManager.setText(AnnotatedString(generatedText)) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Paragraphs: ${paragraphs.toInt()}")
            Slider(
                value = paragraphs,
                onValueChange = { paragraphs = it },
                valueRange = 1f..10f,
                steps = 8
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text(generatedText)
                }
            }
        }
    }
}
