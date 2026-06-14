package omni.toolbox.ui.screens.productivity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

@Composable
fun SecurityScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Encryption", "Password Gen", "QR Studio")

    ToolScreen(
        title = "Cryptographic Security Suite",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> EncryptionTab()
                    1 -> PasswordTab()
                    2 -> QrStudioTab()
                }
            }
        }
    }
}

@Composable
fun EncryptionTab() {
    var textToEncrypt by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("AES-256 Symmetric Block Cipher", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = textToEncrypt,
            onValueChange = { textToEncrypt = it },
            label = { Text("Plaintext / Ciphertext") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = secretKey,
            onValueChange = { secretKey = it },
            label = { Text("Secret Key (Salt Phrase)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { resultText = encrypt(textToEncrypt, secretKey) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Encrypt")
            }
            Button(
                onClick = { resultText = decrypt(textToEncrypt, secretKey) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Decrypt")
            }
        }

        if (resultText.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Result Output", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(resultText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun PasswordTab() {
    var length by remember { mutableFloatStateOf(16f) }
    var includeSymbols by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("High-Entropy Password Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(
                    generatedPassword.ifEmpty { "P@ssword123!" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Column {
            Text("Password Length: ${length.toInt()}")
            Slider(value = length, onValueChange = { length = it }, valueRange = 8f..64f)
        }

        ListItem(
            headlineContent = { Text("Include Symbols") },
            trailingContent = { Switch(checked = includeSymbols, onCheckedChange = { includeSymbols = it }) }
        )
        ListItem(
            headlineContent = { Text("Include Numbers") },
            trailingContent = { Switch(checked = includeNumbers, onCheckedChange = { includeNumbers = it }) }
        )

        Button(
            onClick = { generatedPassword = generatePassword(length.toInt(), includeSymbols, includeNumbers) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate New Password")
        }
    }
}

@Composable
fun QrStudioTab() {
    var qrText by remember { mutableStateOf("Omni Toolbox Secure QR") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Vector QR Utilities Sandbox", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = qrText,
            onValueChange = { qrText = it },
            label = { Text("Data for QR Matrix") },
            modifier = Modifier.fillMaxWidth()
        )

        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Canvas(modifier = Modifier.size(200.dp)) {
                // Simplified mock QR matrix drawing
                val size = 15
                val cellSize = size.toFloat() / size
                for (i in 0 until size) {
                    for (j in 0 until size) {
                        if (Random(qrText.hashCode().toLong() + i * j).nextBoolean()) {
                            drawRect(
                                color = Color.Black,
                                topLeft = androidx.compose.ui.geometry.Offset(i * (200f/size), j * (200f/size)),
                                size = androidx.compose.ui.geometry.Size(200f/size, 200f/size),
                                style = Fill
                            )
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan")
            }
            Button(onClick = {}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save QR")
            }
        }
    }
}

// Logic Utils
fun encrypt(strToEncrypt: String, key: String): String {
    return try {
        val secretKey = SecretKeySpec(key.padEnd(16).take(16).toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray()), Base64.DEFAULT)
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}

fun decrypt(strToDecrypt: String, key: String): String {
    return try {
        val secretKey = SecretKeySpec(key.padEnd(16).take(16).toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
    } catch (e: Exception) {
        "Error: Invalid Key or Payload"
    }
}

fun generatePassword(length: Int, symbols: Boolean, numbers: Boolean): String {
    val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                   (if (numbers) "0123456789" else "") +
                   (if (symbols) "!@#$%^&*()_+-=[]{}|;:,.<>?" else "")
    return (1..length)
        .map { Random().nextInt(charPool.length) }
        .map(charPool::get)
        .joinToString("")
}
