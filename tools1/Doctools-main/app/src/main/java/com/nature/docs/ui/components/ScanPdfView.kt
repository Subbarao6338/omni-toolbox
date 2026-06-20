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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import com.tom_roush.pdfbox.util.Matrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun ScanPdfView(
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accentColor = Color(0xFF3B82F6)

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var processingTime by remember { mutableStateOf("") }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            selectedUris = uris
            currentState = ToolState.CONFIGURING
        }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { saveUri ->
            currentState = ToolState.PROCESSING
            val startTime = System.currentTimeMillis()
            scope.launch(Dispatchers.IO) {
                try {
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val pdfDoc = PDDocument()

                    selectedUris.forEach { imageUri ->
                        val image = InputImage.fromFilePath(context, imageUri)
                        val result = recognizer.process(image).await()

                        val pageWidth = image.width.toFloat()
                        val pageHeight = image.height.toFloat()
                        val page = PDPage(com.tom_roush.pdfbox.pdmodel.common.PDRectangle(pageWidth, pageHeight))
                        pdfDoc.addPage(page)

                        PDPageContentStream(pdfDoc, page).use { contentStream ->
                            result.textBlocks.forEach { block ->
                                block.lines.forEach { line ->
                                    val box = line.boundingBox
                                    if (box != null) {
                                        contentStream.beginText()
                                        // Adjust font size to fit bounding box height roughly
                                        val fontSize = (box.height().toFloat() * 0.7f).coerceAtLeast(1f)
                                        contentStream.setFont(PDType1Font.HELVETICA, fontSize)

                                        // PDF coordinates are bottom-up
                                        val x = box.left.toFloat()
                                        val y = pageHeight - box.bottom.toFloat()

                                        // Apply rotation if needed
                                        val angle = line.angle
                                        if (angle != 0f) {
                                            contentStream.saveGraphicsState()
                                            contentStream.transform(Matrix.getRotateInstance(Math.toRadians(-angle.toDouble()), x, y))
                                        }

                                        contentStream.newLineAtOffset(x, y)
                                        val safeText = line.text.filter { it.code in 32..126 }
                                        if (safeText.isNotEmpty()) {
                                            try {
                                                contentStream.showText(safeText)
                                            } catch (e: Exception) {
                                                // Fallback for individual char issues
                                            }
                                        }
                                        contentStream.endText()
                                        if (angle != 0f) {
                                            contentStream.restoreGraphicsState()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    saveAndFlush(context, pdfDoc, saveUri)
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (System.currentTimeMillis() - startTime) / 1000.0)
                        outputUri = saveUri
                        SessionManager.addEntry("Scanned PDF", "Scan PDF", "${selectedUris.size} pages", Icons.Filled.DocumentScanner, saveUri, selectedUris.size)
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
                            Text("Scan PDF", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("OCR ENGINE", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
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
                        onSelect = { pickLauncher.launch("image/*") },
                        isDark = MaterialTheme.colorScheme.background == Color.Black,
                        icon = Icons.Filled.DocumentScanner,
                        title = "Select Photos to Scan",
                        subtitle = "OCR WILL EXTRACT TEXT",
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ToolState.CONFIGURING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                        Icon(Icons.Filled.DocumentScanner, null, modifier = Modifier.size(80.dp), tint = accentColor)
                        Spacer(Modifier.height(16.dp))
                        Text("${selectedUris.size} Images selected", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { saveLauncher.launch("scanned_doc.pdf") },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("RECOGNIZE & SAVE PDF", fontWeight = FontWeight.Black) }
                    }
                }
                ToolState.PROCESSING -> {
                    LoadingStateView(accentColor, false, "Performing OCR on images...")
                }
                ToolState.SUCCESS -> {
                    SuccessView(
                        message = "Scan Complete",
                        subMessage = "OCR finished and PDF created",
                        processingTime = processingTime,
                        onDone = onBack,
                        onProcessMore = { currentState = ToolState.SELECTING; selectedUris = emptyList() },
                        onPreview = { outputUri?.let { uri -> onOpenPreview(uri, "Scanned PDF", selectedUris.size) } },
                        accentColor = accentColor
                    )
                }
                else -> {}
            }
        }
    }
}
