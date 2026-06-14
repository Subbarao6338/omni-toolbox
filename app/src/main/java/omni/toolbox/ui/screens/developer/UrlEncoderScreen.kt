package omni.toolbox.ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun UrlEncoderScreen(navController: NavHostController) {
    val clipboardManager = LocalClipboardManager.current
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    ToolScreen(title = "URL Encoder/Decoder", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Input") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                trailingIcon = {
                    if (input.isNotEmpty()) {
                        IconButton(onClick = { input = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    output = try {
                        URLEncoder.encode(input, StandardCharsets.UTF_8.toString())
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Encode")
                }
                Button(onClick = {
                    output = try {
                        URLDecoder.decode(input, StandardCharsets.UTF_8.toString())
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Decode")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Result", style = MaterialTheme.typography.labelLarge)
                if (output.isNotEmpty() && !output.startsWith("Error")) {
                    IconButton(onClick = { clipboardManager.setText(AnnotatedString(output)) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().weight(1f),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    output,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
