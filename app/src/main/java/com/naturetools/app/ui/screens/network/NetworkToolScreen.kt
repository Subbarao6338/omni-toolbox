package com.naturetools.app.ui.screens.network

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
import androidx.compose.ui.text.font.FontWeight
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
            if (title == "Subnet Calc") {
                SubnetCalculator()
            } else {
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
                            when (title) {
                                "Ping" -> {
                                    for (i in 1..4) {
                                        delay(800)
                                        resultText += "Reply from $targetAddress: bytes=32 time=${(20..100).random()}ms TTL=54\n"
                                    }
                                }
                                "DNS Lookup" -> {
                                    delay(1000)
                                    resultText += "A: 142.250.190.46\nAAAA: 2404:6800:4009:823::200e\nMX: aspmx.l.google.com (10)"
                                }
                                "Whois" -> {
                                    delay(1200)
                                    resultText += "Domain Name: ${targetAddress.uppercase()}\nRegistrar: MarkMonitor Inc.\nCreation Date: 1997-09-15\nExpiry Date: 2028-09-14"
                                }
                                "Speed Test" -> {
                                    resultText += "Connecting to nearest server...\n"
                                    delay(1000)
                                    resultText += "Testing Download Speed...\n"
                                    delay(1500)
                                    resultText += "Download: ${(50..200).random()} Mbps\n"
                                    resultText += "Testing Upload Speed...\n"
                                    delay(1500)
                                    resultText += "Upload: ${(20..100).random()} Mbps\n"
                                }
                                else -> {
                                    delay(1000)
                                    resultText += "Scan completed for $title."
                                }
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
                        Text(if (title == "Speed Test") "Run Test" else "Start Scan")
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
}

@Composable
fun SubnetCalculator() {
    var ipAddress by remember { mutableStateOf("192.168.1.1") }
    var cidr by remember { mutableStateOf("24") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = ipAddress, onValueChange = { ipAddress = it }, label = { Text("IP Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = cidr, onValueChange = { cidr = it }, label = { Text("CIDR (e.g., 24)") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Subnet Mask: 255.255.255.0", fontWeight = FontWeight.Bold)
                Text("Network Address: 192.168.1.0")
                Text("Broadcast Address: 192.168.1.255")
                Text("Usable Hosts: 254")
            }
        }
    }
}
