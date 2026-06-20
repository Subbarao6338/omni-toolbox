package com.nature.files.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nature.files.utils.ChecksumUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ChecksumVerifierDialog(
    file1: File,
    file2: File?,
    onDismiss: () -> Unit
) {
    var checksum1 by remember { mutableStateOf("Calculating...") }
    var checksum2 by remember { mutableStateOf(file2?.let { "Calculating..." } ?: "N/A") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(file1) {
        scope.launch {
            checksum1 = withContext(Dispatchers.IO) { ChecksumUtils.calculateSHA256(file1) }
        }
    }

    LaunchedEffect(file2) {
        file2?.let {
            scope.launch {
                checksum2 = withContext(Dispatchers.IO) { ChecksumUtils.calculateSHA256(it) }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Checksum Verifier (SHA-256)") },
        text = {
            Column {
                Text("File 1: ${file1.name}", style = MaterialTheme.typography.labelMedium)
                SelectionContainer {
                    Text(checksum1, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
                if (file2 != null) {
                    Text("File 2: ${file2.name}", style = MaterialTheme.typography.labelMedium)
                    SelectionContainer {
                        Text(checksum2, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(16.dp))
                    if (checksum1 != "Calculating..." && checksum2 != "Calculating...") {
                        val match = checksum1 == checksum2
                        Text(
                            text = if (match) "MATCH ✓" else "MISMATCH ✗",
                            color = if (match) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    Text("Select another file to compare.")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun SelectionContainer(content: @Composable () -> Unit) {
    androidx.compose.foundation.text.selection.SelectionContainer(content = content)
}
