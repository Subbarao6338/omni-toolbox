package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.ui.theme.NatureGreen
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.text.TextPosition
import org.apache.poi.xwpf.usermodel.XWPFDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PdfToWordView(
    initialUri: Uri? = null,
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accentColor = Color(0xFF3B82F6)

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUri by remember { mutableStateOf(initialUri) }
    var fileName by remember { mutableStateOf("") }
    var processingTime by remember { mutableStateOf("") }

    fun handleFile(uri: Uri) {
        selectedUri = uri
        fileName = getUriDetails(context, uri).name
        currentState = ToolState.CONFIGURING
    }

    LaunchedEffect(initialUri) { initialUri?.let { handleFile(it) } }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleFile(it) }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) { uri ->
        uri?.let { saveUri ->
            currentState = ToolState.PROCESSING
            val startTime = System.currentTimeMillis()
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(selectedUri!!)?.use { inputStream ->
                        val pdf = PDDocument.load(inputStream)
                        val docx = XWPFDocument()

                        val stripper = object : PDFTextStripper() {
                            override fun writeString(text: String?, textPositions: MutableList<TextPosition>?) {
                                if (text == null) return
                                val para = docx.createParagraph()
                                val run = para.createRun()
                                run.setText(text.filter { it.code in 32..126 })
                            }
                        }
                        stripper.sortByPosition = true
                        stripper.getText(pdf)

                        context.contentResolver.openOutputStream(saveUri)?.use { outputStream ->
                            docx.write(outputStream)
                        }
                        docx.close()
                        pdf.close()
                    }
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (System.currentTimeMillis() - startTime) / 1000.0)
                        SessionManager.addEntry(fileName, "PDF to Word", "Exported", Icons.Filled.HistoryEdu, null, 0)
                        currentState = ToolState.SUCCESS
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        currentState = ToolState.CONFIGURING
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentState != ToolState.SUCCESS && currentState != ToolState.PROCESSING) {
                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                    Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("PDF to Word", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("OFFICE EXPORT", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            when (currentState) {
                ToolState.SELECTING -> {
                    SelectionGrid(
                        onSelect = { pickLauncher.launch("application/pdf") },
                        isDark = MaterialTheme.colorScheme.background == Color.Black,
                        icon = Icons.Filled.HistoryEdu,
                        title = "Select PDF Document",
                        subtitle = "TEXT-BASED PDFS WORK BEST",
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ToolState.CONFIGURING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                        Icon(Icons.Filled.HistoryEdu, null, modifier = Modifier.size(80.dp), tint = accentColor)
                        Spacer(Modifier.height(16.dp))
                        Text(fileName, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { saveLauncher.launch(fileName.replace(".pdf", ".docx")) },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("EXPORT TO WORD", fontWeight = FontWeight.Black) }
                    }
                }
                ToolState.PROCESSING -> {
                    LoadingStateView(accentColor, false, "Extracting text and building DOCX...")
                }
                ToolState.SUCCESS -> {
                    SuccessView(
                        message = "Export Complete",
                        subMessage = "PDF exported to Word document",
                        processingTime = processingTime,
                        onDone = onBack,
                        onProcessMore = { currentState = ToolState.SELECTING; selectedUri = null },
                        accentColor = accentColor
                    )
                }
                else -> {}
            }
        }
    }
}
