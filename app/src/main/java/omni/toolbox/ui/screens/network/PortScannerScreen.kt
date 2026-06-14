package omni.toolbox.ui.screens.network

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

@Composable
fun PortScannerScreen(navController: NavHostController) {
    var host by remember { mutableStateOf("127.0.0.1") }
    var startPort by remember { mutableStateOf("1") }
    var endPort by remember { mutableStateOf("1024") }
    var openPorts by remember { mutableStateOf(listOf<Int>()) }
    var isScanning by remember { mutableStateOf(false) }
    var currentScanningPort by remember { mutableIntStateOf(0) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            openPorts = emptyList()
            val start = startPort.toIntOrNull() ?: 1
            val end = endPort.toIntOrNull() ?: 1024

            withContext(Dispatchers.IO) {
                for (port in start..end) {
                    if (!isScanning) break
                    currentScanningPort = port
                    try {
                        val socket = Socket()
                        socket.connect(InetSocketAddress(host, port), 200)
                        socket.close()
                        withContext(Dispatchers.Main) {
                            openPorts = openPorts + port
                        }
                    } catch (e: Exception) {
                        // Port closed
                    }
                }
                isScanning = false
            }
        }
    }

    ToolScreen(
        title = "Port Scanner",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = host,
                onValueChange = { host = it },
                label = { Text("Target IP/Host") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = startPort,
                    onValueChange = { startPort = it },
                    label = { Text("Start Port") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endPort,
                    onValueChange = { endPort = it },
                    label = { Text("End Port") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isScanning = !isScanning },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isScanning) "Stop Scan (Port $currentScanningPort)" else "Start Scan")
            }

            if (isScanning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
            }

            Text("Open Ports:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(openPorts) { port ->
                    ListItem(
                        headlineContent = { Text("Port $port") },
                        supportingContent = { Text("Open") }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
