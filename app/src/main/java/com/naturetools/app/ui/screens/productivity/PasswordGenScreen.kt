package com.naturetools.app.ui.screens.productivity

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
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
