package com.naturetools.app.ui.screens.ai

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.random.Random

@Composable
fun ImageGeneratorScreen(navController: NavHostController) {
    var prompt by remember { mutableStateOf("") }
    var seed by remember { mutableStateOf(0L) }

    ToolScreen(
        title = "AI Image Generator",
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
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Enter prompt for generation") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { seed = prompt.hashCode().toLong() + Random.nextLong() }) {
                Text("Generate Offline")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                val random = remember(seed) { Random(seed) }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (prompt.isNotEmpty()) {
                        // Procedural "AI Art" generation
                        repeat(50) {
                            drawCircle(
                                color = Color(
                                    random.nextInt(256),
                                    random.nextInt(256),
                                    random.nextInt(256),
                                    random.nextInt(100, 200)
                                ),
                                radius = random.nextFloat() * 200f,
                                center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height)
                            )
                        }
                    } else {
                        // Empty state background
                    }
                }
            }

            if (prompt.isEmpty()) {
                Text("Enter a prompt to generate abstract art offline", modifier = Modifier.padding(top = 16.dp))
            } else {
                Text("Offline implementation using procedural generation based on: '$prompt'", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
