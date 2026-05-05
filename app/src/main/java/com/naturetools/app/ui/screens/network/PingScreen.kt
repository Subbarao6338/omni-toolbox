package com.naturetools.app.ui.screens.network

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

@Composable
fun PingScreen(navController: NavHostController) {
    var host by remember { mutableStateOf("google.com") }
    var results by remember { mutableStateOf(listOf<String>()) }
    var isPinging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ToolScreen(title = "Ping", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Host or IP") },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (!isPinging) {
                                isPinging = true
                                results = emptyList()
                                scope.launch {
                                    for (i in 1..5) {
                                        if (!isPinging) break
                                        val result = ping(host)
                                        results = results + "Seq $i: $result"
                                        delay(1000)
                                    }
                                    isPinging = false
                                }
                            } else {
                                isPinging = false
                            }
                        }
                    ) {
                        Icon(
                            if (isPinging) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPinging) "Stop" else "Start"
                        )
                    }
                }
            )

            if (isPinging) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(results) { result ->
                    Text(result, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (results.isNotEmpty() && !isPinging) {
                Button(onClick = { results = emptyList() }, modifier = Modifier.align(Alignment.End)) {
                    Text("Clear")
                }
            }
        }
    }
}

private suspend fun ping(host: String): String = withContext(Dispatchers.IO) {
    try {
        val start = System.currentTimeMillis()
        val address = InetAddress.getByName(host)
        val reachable = address.isReachable(3000)
        val end = System.currentTimeMillis()
        if (reachable) {
            "${address.hostAddress} - ${end - start}ms"
        } else {
            "Timed out"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
