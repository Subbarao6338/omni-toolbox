package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun UrlEncoderScreen(navController: NavHostController) {
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    ToolScreen(title = "URL Encoder/Decoder", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Input") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
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
            Text("Result", style = MaterialTheme.typography.labelLarge)
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
