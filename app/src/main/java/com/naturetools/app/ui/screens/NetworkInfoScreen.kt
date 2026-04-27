package com.naturetools.app.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun NetworkInfoScreen(navController: NavHostController) {
    val context = LocalContext.current
    var networkInfo by remember { mutableStateOf(getNetworkDetails(context)) }

    ToolScreen(
        title = "Network Info",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { networkInfo = getNetworkDetails(context) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            InfoSection("Connection Status", networkInfo)
        }
    }
}

@Composable
fun InfoSection(title: String, info: Map<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            info.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(key, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(value, style = MaterialTheme.typography.bodyMedium)
                }
                Divider(modifier = Modifier.alpha(0.5f))
            }
        }
    }
}

fun getNetworkDetails(context: Context): Map<String, String> {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetwork
    val capabilities = cm.getNetworkCapabilities(activeNetwork)

    val details = mutableMapOf<String, String>()

    if (capabilities == null) {
        details["Status"] = "Disconnected"
        return details
    }

    details["Status"] = "Connected"

    when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> details["Type"] = "Wi-Fi"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> details["Type"] = "Cellular"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> details["Type"] = "Ethernet"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> details["Type"] = "Bluetooth"
        else -> details["Type"] = "Unknown"
    }

    details["Downstream Bandwidth"] = "${capabilities.linkDownstreamBandwidthKbps / 1000} Mbps"
    details["Upstream Bandwidth"] = "${capabilities.linkUpstreamBandwidthKbps / 1000} Mbps"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        details["Signal Strength"] = when (capabilities.signalStrength) {
            in -1000..-80 -> "Weak"
            in -79..-60 -> "Moderate"
            in -59..-1 -> "Strong"
            else -> "N/A"
        }
    }

    details["Metered"] = if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)) "No" else "Yes"
    details["Internet Access"] = if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) "Yes" else "No"
    details["Validated"] = if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) "Yes" else "No"

    return details
}
