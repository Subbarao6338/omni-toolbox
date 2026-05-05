package com.naturetools.app.ui.screens.audio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun AudioBaseScreen(
    navController: NavHostController,
    title: String,
    mimeType: String = "audio/*",
    content: @Composable (ColumnScope, Uri?) -> Unit
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
    }

    val typeLabel = when {
        mimeType.startsWith("video") -> "video"
        mimeType.startsWith("image") -> "image"
        else -> "audio"
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedFileUri == null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            when (typeLabel) {
                                "video" -> Icons.Default.VideoLibrary
                                "image" -> Icons.Default.Image
                                else -> Icons.Default.AudioFile
                            },
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No $typeLabel file selected", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { launcher.launch(mimeType) }) {
                            Text("Select ${typeLabel.replaceFirstChar { it.uppercase() }} File")
                        }
                    }
                }
            } else {
                Text(
                    text = "Selected: ${selectedFileUri?.path?.split("/")?.lastOrNull() ?: "Unknown"}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(16.dp))

                content(this, selectedFileUri)

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { launcher.launch(mimeType) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change File")
                }
            }
        }
    }
}
