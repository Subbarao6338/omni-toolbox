package omni.toolbox.ui.screens.utility

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.security.MessageDigest

@Composable
fun HashGeneratorScreen(navController: NavHostController) {
    var input by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val algorithms = listOf("MD5", "SHA-1", "SHA-256", "SHA-512")
    var base64Output by remember { mutableStateOf("") }

    ToolScreen(
        title = "Cryptography Hub",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Input Payload", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Enter string to process...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Message Digests", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            algorithms.forEach { algo ->
                val hash = try {
                    if (input.isEmpty()) ""
                    else {
                        val digest = MessageDigest.getInstance(algo)
                        val bytes = digest.digest(input.toByteArray())
                        bytes.joinToString("") { "%02x".format(it) }
                    }
                } catch (e: Exception) {
                    "Error"
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(algo, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            if (hash.isNotEmpty()) {
                                IconButton(onClick = { clipboardManager.setText(AnnotatedString(hash)) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Text(
                            text = if (hash.isEmpty()) "Waiting for input..." else hash,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Text("Base64 Tunnel", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    base64Output = try {
                        android.util.Base64.encodeToString(input.toByteArray(), android.util.Base64.NO_WRAP)
                    } catch (e: Exception) { "Error" }
                }, modifier = Modifier.weight(1f)) { Text("Encode") }
                Button(onClick = {
                    base64Output = try {
                        String(android.util.Base64.decode(input, android.util.Base64.DEFAULT))
                    } catch (e: Exception) { "Invalid Base64" }
                }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Decode") }
            }

            if (base64Output.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Output Result", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text(base64Output, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
