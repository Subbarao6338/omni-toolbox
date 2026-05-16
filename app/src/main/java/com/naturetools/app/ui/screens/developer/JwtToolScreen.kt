package com.naturetools.app.ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.Base64

@Composable
fun JwtToolScreen(navController: NavHostController) {
    var jwtInput by remember { mutableStateOf("") }

    val parts = jwtInput.split(".")
    val header = remember(jwtInput) {
        try {
            if (parts.size >= 1) String(Base64.getUrlDecoder().decode(parts[0])) else ""
        } catch (e: Exception) { "Invalid Header" }
    }
    val payload = remember(jwtInput) {
        try {
            if (parts.size >= 2) String(Base64.getUrlDecoder().decode(parts[1])) else ""
        } catch (e: Exception) { "Invalid Payload" }
    }

    ToolScreen(
        title = "JWT Tool",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = jwtInput,
                onValueChange = { jwtInput = it },
                label = { Text("Encoded JWT") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5
            )

            Text("Header", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(header, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
            }

            Text("Payload", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(payload, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
