package com.naturetools.app.ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun DeveloperExpertScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "Hex Viewer" -> HexViewer()
                "ASCII Table" -> AsciiTable()
                else -> Text("Developer Utility for $title")
            }
        }
    }
}

@Composable
fun HexViewer() {
    var textInput by remember { mutableStateOf("Nature Tools") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Hex Viewer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Input Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val hexString = textInput.toByteArray().joinToString(" ") {
            String.format("%02X", it)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = hexString,
                modifier = Modifier.padding(16.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AsciiTable() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("ASCII Table (Common)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Dec", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Hex", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Char", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()
                (32..126).forEach { i ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(i.toString(), modifier = Modifier.weight(1f))
                        Text(String.format("%02X", i), modifier = Modifier.weight(1f))
                        Text(i.toChar().toString(), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
