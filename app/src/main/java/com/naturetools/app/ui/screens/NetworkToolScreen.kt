package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NetworkToolScreen(navController: NavHostController, title: String) {
    var targetAddress by remember { mutableStateOf("google.com") }
    var resultText by remember { mutableStateOf("Results will appear here...") }
    var isRunning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = targetAddress,
                onValueChange = { targetAddress = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Target IP or Domain") },
                leadingIcon = { Icon(Icons.Default.Lan, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isRunning = true
                    resultText = "Running $title on $targetAddress...\n"
                    scope.launch {
                        // Simulated network action
                        for (i in 1..4) {
                            delay(1000)
                            resultText += "Reply from $targetAddress: bytes=32 time=${(20..100).random()}ms TTL=54\n"
                        }
                        isRunning = false
                        resultText += "\nDone."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRunning && targetAddress.isNotBlank()
            ) {
                if (isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Scan")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    Text(
                        text = resultText,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}
