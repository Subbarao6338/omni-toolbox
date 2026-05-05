package com.naturetools.app.ui.screens.utility

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
import java.security.MessageDigest

@Composable
fun HashGeneratorScreen(navController: NavHostController) {
    var input by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val algorithms = listOf("MD5", "SHA-1", "SHA-256")

    ToolScreen(
        title = "Hash Generator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Input Text") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            algorithms.forEach { algo ->
                val hash = try {
                    if (input.isEmpty()) ""
                    else {
                        val digest = MessageDigest.getInstance(algo)
                        val bytes = digest.digest(input.toByteArray())
                        bytes.joinToString("") { "%02x".format(it) }
                    }
                } catch (e: Exception) {
                    "Error"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(algo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            if (hash.isNotEmpty()) {
                                IconButton(onClick = { clipboardManager.setText(AnnotatedString(hash)) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                }
                            }
                        }
                        Text(
                            text = if (hash.isEmpty()) "..." else hash,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
