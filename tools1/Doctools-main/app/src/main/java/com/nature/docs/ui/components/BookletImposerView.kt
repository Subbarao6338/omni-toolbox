package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.data.pdf.BookletImposer
import com.nature.docs.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun BookletImposerView(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingTime by remember { mutableStateOf("") }
    var resultUri by remember { mutableStateOf<Uri?>(null) }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { selectedUri = it }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { outputUri ->
            isProcessing = true
            val startTime = System.currentTimeMillis()
            scope.launch {
                try {
                    BookletImposer.impose(context, selectedUri!!, outputUri)
                    processingTime = String.format("%.1fs", (System.currentTimeMillis() - startTime) / 1000.0)
                    resultUri = outputUri
                    isProcessing = false
                } catch (e: Exception) {
                    isProcessing = false
                    Toast.makeText(context, "Imposition failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LinenCanvas(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = InkBrown)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Booklet Imposer", style = MaterialTheme.typography.titleLarge, color = InkBrown)
                        Text("PREPARE FOR PRINTING", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen, letterSpacing = 1.sp)
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                if (resultUri != null) {
                    SuccessView(
                        message = "Booklet Ready",
                        subMessage = "Specimen rearranged for printing",
                        processingTime = processingTime,
                        onDone = onBack,
                        onProcessMore = { resultUri = null; selectedUri = null },
                        onPreview = { /* Open preview */ },
                        accentColor = BotanicalGreen
                    )
                } else if (selectedUri == null) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AgedPaperCard(modifier = Modifier.size(280.dp).clickable { pickLauncher.launch("application/pdf") }) {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Book, null, tint = BotanicalGreen, modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("SELECT PDF", style = MaterialTheme.typography.headlineMedium, color = InkBrown)
                                Text("Pick a document to impose", style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.6f))
                            }
                        }
                    }
                } else {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AgedPaperCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Layers, null, tint = BotanicalGreen, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(16.dp))
                                Text(getUriDetails(context, selectedUri!!).name, style = MaterialTheme.typography.bodyLarge, color = InkBrown)
                                Text(getUriDetails(context, selectedUri!!).size, style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.4f))
                            }
                        }
                    }

                    Button(
                        onClick = { saveLauncher.launch("booklet_${System.currentTimeMillis() / 1000}.pdf") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen)
                    ) {
                        Text("IMPOSE BOOKLET", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            if (isProcessing) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.2f)), contentAlignment = Alignment.Center) {
                    VineProgressBar(0.5f, Modifier.padding(32.dp))
                }
            }
        }
    }
}
