package omni.toolbox.ui.screens.system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import android.os.Build
import omni.toolbox.BuildConfig

@Composable
fun AppInfoScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            if (title == "App Info" || title == "Update Check") {
                InfoRow("App Version", BuildConfig.VERSION_NAME)
            InfoRow("Build Number", BuildConfig.VERSION_CODE.toString())
            InfoRow("OS Version", Build.VERSION.RELEASE)
            InfoRow("SDK Int", Build.VERSION.SDK_INT.toString())
            InfoRow("Manufacturer", Build.MANUFACTURER)
            InfoRow("Model", Build.MODEL)
            InfoRow("Board", Build.BOARD)
            InfoRow("Hardware", Build.HARDWARE)
                InfoRow("Supported ABIs", Build.SUPPORTED_ABIS.joinToString())
                if (title == "Update Check") {
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {}, Modifier.fillMaxWidth()) { Text("Check for Updates") }
                }
            } else if (title == "Process Manager") {
                InfoRow("Active Processes", "Simulated data - system restriction")
                InfoRow("User", System.getProperty("user.name") ?: "N/A")
            } else if (title == "Hardware ID" || title == "Device ID") {
                InfoRow("Android ID", "Unavailable (API Level Restriction)")
                InfoRow("Serial", Build.SERIAL)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
