package com.naturetools.app.ui.screens

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun Base64Screen(navController: NavHostController) {
    var input by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    ToolScreen(title = "Base64 Tool", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Input Text") },
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        output = try {
                            Base64.encodeToString(input.toByteArray(), Base64.DEFAULT)
                        } catch (e: Exception) {
                            "Error encoding"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Encode")
                }
                Button(
                    onClick = {
                        output = try {
                            String(Base64.decode(input, Base64.DEFAULT))
                        } catch (e: Exception) {
                            "Error decoding (Invalid Base64)"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decode")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = output,
                onValueChange = { },
                label = { Text("Output") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    input = ""
                    output = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear All")
            }
        }
    }
}
