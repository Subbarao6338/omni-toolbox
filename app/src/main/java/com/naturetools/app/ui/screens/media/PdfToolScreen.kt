package com.naturetools.app.ui.screens.media

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun PdfToolScreen(navController: NavHostController, title: String) {
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        selectedFiles = it
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(getPdfToolDescription(title), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedFiles.isEmpty()) {
                Button(onClick = { launcher.launch("application/pdf") }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select PDF Files")
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${selectedFiles.size} file(s) selected", fontWeight = FontWeight.Bold)
                        selectedFiles.forEach { uri ->
                            Text(uri.lastPathSegment ?: "Unknown file", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PdfToolOptions(title)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Simulated PDF Action */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val actionLabel = when(title) {
                        "Flatten PDF" -> "Flatten & Secure"
                        "Grayscale PDF" -> "Convert to Grayscale"
                        "PDF Metadata" -> "Update Metadata"
                        else -> "Process PDF"
                    }
                    Text(actionLabel)
                }

                OutlinedButton(
                    onClick = { selectedFiles = emptyList() },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Clear Selection")
                }
            }
        }
    }
}

@Composable
fun PdfToolOptions(title: String) {
    when (title) {
        "Split PDF" -> {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Page Ranges (e.g., 1-5, 8, 11-13)") }, modifier = Modifier.fillMaxWidth())
        }
        "Merge PDF" -> {
            Text("Files will be merged in the order they were selected.")
        }
        "Protect PDF" -> {
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Set Password") }, modifier = Modifier.fillMaxWidth())
        }
        "Rotate PDF" -> {
            var rotation by remember { mutableIntStateOf(90) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Rotation Angle:")
                Spacer(modifier = Modifier.width(16.dp))
                listOf(90, 180, 270).forEach { angle ->
                    FilterChip(
                        selected = rotation == angle,
                        onClick = { rotation = angle },
                        label = { Text("$angle°") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
        "PDF Metadata" -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Author") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Subject") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("Keywords") }, modifier = Modifier.fillMaxWidth())
            }
        }
        "Flatten PDF" -> {
            Text("This will make all form fields and annotations permanent and non-editable.")
        }
        "Grayscale PDF" -> {
            Text("This will convert all colored elements in the PDF to shades of gray to save ink or reduce complexity.")
        }
    }
}

fun getPdfToolDescription(title: String): String {
    return when (title) {
        "Merge PDF" -> "Combine multiple PDF documents into one."
        "Split PDF" -> "Divide a PDF into separate files by page ranges."
        "Rotate PDF" -> "Rotate PDF pages in 90-degree increments."
        "Protect PDF" -> "Add password protection to your PDF."
        "Unlock PDF" -> "Remove password protection from a PDF."
        "PDF Metadata" -> "Edit PDF Title, Author, Subject, and Keywords."
        "Compress PDF" -> "Reduce file size by optimizing PDF streams."
        "Grayscale PDF" -> "Convert all PDF pages to black and white."
        "Flatten PDF" -> "Make forms and annotations permanent."
        else -> "Professional PDF manipulation tool."
    }
}
