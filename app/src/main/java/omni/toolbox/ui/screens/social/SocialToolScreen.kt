package omni.toolbox.ui.screens.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SocialToolScreen(navController: NavHostController, title: String) {
    var url by remember { mutableStateOf("") }
    var isExtracting by remember { mutableStateOf(false) }
    var resultMedia = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Media Extractor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Enter profile or post URL from Instagram, Facebook, Twitter, etc.", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Social URL") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://...") },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isExtracting = true
                        resultMedia.clear()
                        delay(2000)
                        // Mock extraction results
                        repeat(5) { i ->
                            resultMedia.add("https://social-cdn.com/media/file_${i + 1}.jpg")
                        }
                        isExtracting = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExtracting && url.isNotBlank()
            ) {
                if (isExtracting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Extract Media")
                }
            }

            if (resultMedia.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Extracted Files (${resultMedia.size})", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                resultMedia.forEach { link ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(link.split("/").last(), modifier = Modifier.weight(1f), maxLines = 1)
                            IconButton(onClick = { /* Download logic */ }) {
                                Icon(Icons.Default.Download, contentDescription = "Download")
                            }
                        }
                    }
                }

                Button(
                    onClick = { /* Batch download logic */ },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Download All")
                }
            }
        }
    }
}
