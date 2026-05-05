package com.naturetools.app.ui.screens.developer

import android.util.Base64
import androidx.compose.foundation.layout.*
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
fun Base64Screen(navController: NavHostController) {
    val clipboardManager = LocalClipboardManager.current
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    ToolScreen(title = "Base64 Tool", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Input Text", style = MaterialTheme.typography.labelLarge)
                if (input.isNotEmpty()) {
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(input)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Input")
                    }
                }
            }
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        output = try {
                            Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
                        } catch (e: Exception) {
                            "Error encoding"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Encode")
                }
                Button(
                    onClick = {
                        output = try {
                            String(Base64.decode(input, Base64.DEFAULT))
                        } catch (e: Exception) {
                            "Error decoding (Invalid Base64)"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decode")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Output", style = MaterialTheme.typography.labelLarge)
                if (output.isNotEmpty() && !output.startsWith("Error")) {
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(output)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Output")
                    }
                }
            }
            OutlinedTextField(
                value = output,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    input = ""
                    output = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All")
            }
        }
    }
}
