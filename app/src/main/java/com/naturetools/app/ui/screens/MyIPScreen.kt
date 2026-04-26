package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URL
import java.util.Collections

@Composable
fun MyIPScreen(navController: NavHostController) {
    var publicIp by remember { mutableStateOf("Fetching...") }
    var localIp by remember { mutableStateOf("Fetching...") }
    var isLoading by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    suspend fun fetchIps() {
        isLoading = true
        withContext(Dispatchers.IO) {
            try {
                publicIp = URL("https://api.ipify.org").readText()
            } catch (e: Exception) {
                publicIp = "Error: ${e.message}"
            }

            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                var foundLocalIp = "Not found"
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (isIPv4) {
                                foundLocalIp = sAddr
                                break
                            }
                        }
                    }
                    if (foundLocalIp != "Not found") break
                }
                localIp = foundLocalIp
            } catch (e: Exception) {
                localIp = "Error"
            }
        }
        isLoading = false
    }

    LaunchedEffect(Unit) {
        fetchIps()
    }

    ToolScreen(
        title = "My IP",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { scope.launch { fetchIps() } }, enabled = !isLoading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IPCard("Public IP Address", publicIp, clipboardManager)
            IPCard("Local IP Address", localIp, clipboardManager)

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { scope.launch { fetchIps() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh")
            }
        }
    }
}

@Composable
private fun IPCard(label: String, ip: String, clipboard: androidx.compose.ui.platform.ClipboardManager) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(ip, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { clipboard.setText(AnnotatedString(ip)) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
            }
        }
    }
}
