package omni.toolbox.ui.screens.productivity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import android.util.Base64
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.security.KeyPairGenerator
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

@Composable
fun PasswordGenScreen(navController: NavHostController) {
    var length by remember { mutableFloatStateOf(16f) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    fun generatePassword() {
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val symbols = "!@#$%^&*()-_=+[]{}|;:,.<>?"

        var charPool = lowercase
        if (includeUppercase) charPool += uppercase
        if (includeNumbers) charPool += numbers
        if (includeSymbols) charPool += symbols

        generatedPassword = (1..length.toInt())
            .map { charPool[Random.nextInt(charPool.length)] }
            .joinToString("")
    }

    LaunchedEffect(Unit) {
        generatePassword()
    }

    ToolScreen(
        title = "Password Generator",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = generatedPassword,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        IconButton(onClick = { generatePassword() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
                        }
                        IconButton(onClick = { clipboardManager.setText(AnnotatedString(generatedPassword)) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Length: ${length.toInt()}", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = length,
                onValueChange = { length = it },
                valueRange = 4f..64f
            )

            Spacer(modifier = Modifier.height(16.dp))

            SwitchRow("Include Uppercase", includeUppercase) { includeUppercase = it }
            SwitchRow("Include Numbers", includeNumbers) { includeNumbers = it }
            SwitchRow("Include Symbols", includeSymbols) { includeSymbols = it }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { generatePassword() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate New Password")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            SecurityUtilities()
        }
    }
}

@Composable
fun SecurityUtilities() {
    var input by remember { mutableStateOf("Omni Toolbox") }
    var shaHash by remember(input) {
        mutableStateOf(
            MessageDigest.getInstance("SHA-256")
                .digest(input.toByteArray())
                .joinToString("") { "%02x".format(it) }
        )
    }

    var rsaPublicKey by remember { mutableStateOf("") }
    var rsaPrivateKey by remember { mutableStateOf("") }

    var aesKey by remember { mutableStateOf("SecretKey1234567") } // Needs 16 bytes for AES-128
    var encryptedText by remember { mutableStateOf("") }

    var hmacResult by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Security Utilities", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Input Text") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Text("SHA-256 Hash", style = MaterialTheme.typography.titleSmall)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(shaHash, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("RSA Key Pair", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val kpg = KeyPairGenerator.getInstance("RSA")
                kpg.initialize(2048)
                val kp = kpg.generateKeyPair()
                rsaPublicKey = Base64.encodeToString(kp.public.encoded, Base64.DEFAULT)
                rsaPrivateKey = Base64.encodeToString(kp.private.encoded, Base64.DEFAULT)
            }, modifier = Modifier.weight(1f)) {
                Text(if (rsaPublicKey.isEmpty()) "Generate Pair" else "Regenerate")
            }
        }
        if (rsaPublicKey.isNotEmpty()) {
            Text("Public Key (Base64):", style = MaterialTheme.typography.labelSmall)
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(rsaPublicKey.take(50) + "...", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("AES-GCM Encryption", style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
            value = aesKey,
            onValueChange = { aesKey = it },
            label = { Text("Secret Key (16 chars)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                try {
                    val digest = MessageDigest.getInstance("SHA-256")
                    val keyBytes = digest.digest(aesKey.toByteArray())
                    val key = SecretKeySpec(keyBytes, "AES")
                    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                    cipher.init(Cipher.ENCRYPT_MODE, key)
                    val iv = cipher.iv
                    val encryption = cipher.doFinal(input.toByteArray())
                    encryptedText = Base64.encodeToString(iv + encryption, Base64.DEFAULT)
                } catch (e: Exception) {
                    encryptedText = "Error: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Encrypt with AES-GCM") }

        if (encryptedText.isNotEmpty()) {
            Text("Encrypted Result (Base64):", style = MaterialTheme.typography.labelSmall)
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(encryptedText, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("HMAC Calculator", style = MaterialTheme.typography.titleSmall)
        Button(onClick = {
            val keySpec = SecretKeySpec(aesKey.toByteArray(), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(keySpec)
            val result = mac.doFinal(input.toByteArray())
            hmacResult = result.joinToString("") { "%02x".format(it) }
        }, modifier = Modifier.fillMaxWidth()) { Text("Calculate HMAC-SHA256") }

        if (hmacResult.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(hmacResult, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
