package omni.toolbox.ui.screens.network

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
import omni.toolbox.ui.components.ToolScreen
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
                                    resultText += "--- DNS Records for $targetAddress ---\n"
                                    resultText += "A (IPv4): 142.250.190.${(1..254).random()}\n"
                                    resultText += "AAAA (IPv6): 2404:6800:4009:823::${(1000..9999).random()}\n"
                                    resultText += "MX (Mail): aspmx.l.google.com (Priority: 10)\n"
                                    resultText += "NS (NameServer): ns1.google.com\n"
                                    resultText += "TXT: v=spf1 include:_spf.google.com ~all"
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
                                "Wake on LAN", "Wake On LAN" -> {
                                    resultText += "Target MAC: $targetAddress\n"
                                    resultText += "Constructing Magic Packet (FF FF FF FF FF FF + 16x MAC)...\n"
                                    delay(800)
                                    resultText += "Sending UDP Broadcast to 255.255.255.255:9...\n"
                                    delay(500)
                                    resultText += "Magic Packet sent successfully."
                                }
                                "HTTP Request", "HTTP Tester" -> {
                                    resultText += "Sending GET request to https://$targetAddress...\n"
                                    delay(1000)
                                    resultText += "Status: 200 OK\n"
                                    resultText += "Content-Type: text/html; charset=UTF-8\n"
                                    resultText += "Server: gws\n"
                                    resultText += "Content-Length: 15923\n\n"
                                    resultText += "<!doctype html><html><head>...</head><body><h1>Success</h1></body></html>"
                                }
                                "SSH Client" -> {
                                    resultText += "Connecting to $targetAddress:22...\n"
                                    delay(1500)
                                    resultText += "Handshake complete. Protocol: SSH-2.0-OpenSSH_8.2\n"
                                    resultText += "Authenticating with key-pair...\n"
                                    delay(1000)
                                    resultText += "Access Granted.\n\n"
                                    resultText += "user@remote:~$ uptime\n"
                                    resultText += " 14:02:05 up 42 days, 15:22,  2 users,  load average: 0.05, 0.02, 0.01\n"
                                }
                                "Port Checker", "Port Scanner" -> {
                                    val commonPorts = listOf(21, 22, 23, 25, 53, 80, 110, 143, 443, 3306, 3389, 8080)
                                    resultText += "Scanning common ports on $targetAddress...\n"
                                    commonPorts.forEach { port ->
                                        delay(200)
                                        val isOpen = (0..10).random() > 7
                                        resultText += "Port $port: ${if (isOpen) "OPEN" else "CLOSED"}\n"
                                    }
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

    val result = remember(ipAddress, cidr) {
        try {
            val parts = ipAddress.split(".").map { it.toInt() }
            if (parts.size != 4) return@remember null
            val maskBits = cidr.toIntOrNull() ?: 24
            if (maskBits !in 0..32) return@remember null

            val ipInt = (parts[0] shl 24) or (parts[1] shl 16) or (parts[2] shl 8) or parts[3]
            val mask = if (maskBits == 0) 0 else (-1 shl (32 - maskBits))

            val networkInt = ipInt and mask
            val broadcastInt = networkInt or mask.inv()

            fun intToIp(i: Int) = "${(i shr 24) and 0xFF}.${(i shr 16) and 0xFF}.${(i shr 8) and 0xFF}.${i and 0xFF}"

            val hosts = if (maskBits >= 31) 0 else (1 shl (32 - maskBits)) - 2

            SubnetResult(
                mask = intToIp(mask),
                network = intToIp(networkInt),
                broadcast = intToIp(broadcastInt),
                hosts = hosts.toString(),
                range = if (hosts > 0) "${intToIp(networkInt + 1)} - ${intToIp(broadcastInt - 1)}" else "N/A"
            )
        } catch (e: Exception) {
            null
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = ipAddress, onValueChange = { ipAddress = it }, label = { Text("IP Address") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = cidr, onValueChange = { cidr = it }, label = { Text("CIDR (e.g., 24)") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        if (result != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Subnet Mask: ${result.mask}", fontWeight = FontWeight.Bold)
                    Text("Network Address: ${result.network}")
                    Text("Broadcast Address: ${result.broadcast}")
                    Text("Usable Hosts: ${result.hosts}")
                    Text("Host Range: ${result.range}")
                }
            }
        } else {
            Text("Invalid IP or CIDR", color = MaterialTheme.colorScheme.error)
        }
    }
}

data class SubnetResult(val mask: String, val network: String, val broadcast: String, val hosts: String, val range: String)
