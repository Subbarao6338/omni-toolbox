package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nature.docs.data.FileVault
import com.nature.docs.data.pdf.PdfEngine
import com.nature.docs.ui.theme.BotanicalGreen
import com.nature.docs.ui.theme.ParchmentBg
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfEditorView(uri: Uri, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nature Editor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                val doc = PdfEngine.loadDocument(context, uri)
                                if (doc != null) {
                                    val output = FileVault.getTempFile(context, ".pdf")
                                    PdfEngine.saveDocument(doc, output)
                                    Toast.makeText(context, "Saved to ${output.name}", Toast.LENGTH_SHORT).show()
                                }
                                isSaving = false
                            }
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ParchmentBg)
            )
        }
    ) { padding ->
        LinenCanvas(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("PDF Editor Canvas", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
                AgedPaperCard(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    Text("Document Preview Area")
                }

                Spacer(Modifier.weight(1f))

                BotanicalBorder(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen)) {
                            Text("Sign")
                        }
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen)) {
                            Text("Annotate")
                        }
                    }
                }
            }
        }
    }
}
