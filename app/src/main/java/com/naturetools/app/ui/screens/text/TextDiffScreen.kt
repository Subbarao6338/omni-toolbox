package com.naturetools.app.ui.screens.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun TextDiffScreen(navController: NavHostController) {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }

    ToolScreen(
        title = "Text Diff",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = text1,
                onValueChange = { text1 = it },
                label = { Text("Original Text") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = text2,
                onValueChange = { text2 = it },
                label = { Text("Modified Text") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showResult = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Compare")
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Comparison Result", style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (text1 == text2) {
                            Text("Texts are identical.", color = Color.Green)
                        } else {
                            Text("Original: $text1", style = MaterialTheme.typography.bodySmall)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Modified: $text2", style = MaterialTheme.typography.bodySmall)
                            Text("\nDifferences detected.", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}
