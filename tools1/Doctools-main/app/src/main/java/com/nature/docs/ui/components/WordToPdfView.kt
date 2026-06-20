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
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import org.apache.poi.xwpf.usermodel.XWPFDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

@Composable
fun WordToPdfView(
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val accentColor = Color(0xFF3B82F6)

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var processingTime by remember { mutableStateOf("") }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedUri = it
            fileName = getUriDetails(context, it).name
            currentState = ToolState.CONFIGURING
        }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { saveUri ->
            currentState = ToolState.PROCESSING
            val startTime = System.currentTimeMillis()
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(selectedUri!!)?.use { inputStream ->
                        val docx = XWPFDocument(inputStream)
                        val pdfDoc = PDDocument()
                        val fontSize = 12f
                        val leading = 14.5f
                        val margin = 50f
                        val pageWidth = PDPage(com.tom_roush.pdfbox.pdmodel.common.PDRectangle.A4).mediaBox.width
                        val pageHeight = PDPage(com.tom_roush.pdfbox.pdmodel.common.PDRectangle.A4).mediaBox.height
                        val maxWidth = pageWidth - 2 * margin

                        var currentPage = PDPage(com.tom_roush.pdfbox.pdmodel.common.PDRectangle.A4)
                        pdfDoc.addPage(currentPage)
                        var contentStream = PDPageContentStream(pdfDoc, currentPage)
                        contentStream.beginText()
                        contentStream.setFont(PDType1Font.HELVETICA, fontSize)
                        contentStream.setLeading(leading)
                        var yPosition = pageHeight - margin
                        contentStream.newLineAtOffset(margin, yPosition)

                        docx.paragraphs.forEach { para ->
                            val text = para.text.filter { it.code in 32..126 }
                            if (text.isNotEmpty()) {
                                val words = text.split(" ")
                                var line = ""
                                for (word in words) {
                                    val testLine = if (line.isEmpty()) word else "$line $word"
                                    val lineWidth = PDType1Font.HELVETICA.getStringWidth(testLine) / 1000 * fontSize
                                    if (lineWidth > maxWidth) {
                                        contentStream.showText(line)
                                        contentStream.newLineAtOffset(0f, -leading)
                                        yPosition -= leading
                                        if (yPosition < margin) {
                                            contentStream.endText()
                                            contentStream.close()
                                            currentPage = PDPage(com.tom_roush.pdfbox.pdmodel.common.PDRectangle.A4)
                                            pdfDoc.addPage(currentPage)
                                            contentStream = PDPageContentStream(pdfDoc, currentPage)
                                            contentStream.beginText()
                                            contentStream.setFont(PDType1Font.HELVETICA, fontSize)
                                            contentStream.setLeading(leading)
                                            yPosition = pageHeight - margin
                                            contentStream.newLineAtOffset(margin, yPosition)
                                        }
                                        line = word
                                    } else {
                                        line = testLine
                                    }
                                }
                                if (line.isNotEmpty()) {
                                    contentStream.showText(line)
                                    contentStream.newLineAtOffset(0f, -leading)
                                    yPosition -= leading
                                }
                                // Extra space between paragraphs
                                contentStream.newLineAtOffset(0f, -leading / 2)
                                yPosition -= leading / 2

                                if (yPosition < margin) {
                                    contentStream.endText()
                                    contentStream.close()
                                    currentPage = PDPage(com.tom_roush.pdfbox.pdmodel.common.PDRectangle.A4)
                                    pdfDoc.addPage(currentPage)
                                    contentStream = PDPageContentStream(pdfDoc, currentPage)
                                    contentStream.beginText()
                                    contentStream.setFont(PDType1Font.HELVETICA, fontSize)
                                    contentStream.setLeading(leading)
                                    yPosition = pageHeight - margin
                                    contentStream.newLineAtOffset(margin, yPosition)
                                }
                            }
                        }
                        contentStream.endText()
                        contentStream.close()

                        saveAndFlush(context, pdfDoc, saveUri)
                        docx.close()
                    }
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (System.currentTimeMillis() - startTime) / 1000.0)
                        outputUri = saveUri
                        SessionManager.addEntry(fileName, "Word to PDF", "Converted", Icons.Filled.Description, saveUri, 1)
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
                            Text("Word to PDF", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("OFFICE CONVERTER", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
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
                        onSelect = { pickLauncher.launch("application/vnd.openxmlformats-officedocument.wordprocessingml.document") },
                        isDark = MaterialTheme.colorScheme.background == Color.Black,
                        icon = Icons.Filled.Description,
                        title = "Select Word Document",
                        subtitle = "DOCX FILES ONLY",
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ToolState.CONFIGURING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                        Icon(Icons.Filled.Description, null, modifier = Modifier.size(80.dp), tint = accentColor)
                        Spacer(Modifier.height(16.dp))
                        Text(fileName, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { saveLauncher.launch(fileName.replace(".docx", ".pdf")) },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("CONVERT TO PDF", fontWeight = FontWeight.Black) }
                    }
                }
                ToolState.PROCESSING -> {
                    LoadingStateView(accentColor, false, "Converting document...")
                }
                ToolState.SUCCESS -> {
                    SuccessView(
                        message = "Conversion Complete",
                        subMessage = "Word document converted to PDF",
                        processingTime = processingTime,
                        onDone = onBack,
                        onProcessMore = { currentState = ToolState.SELECTING; selectedUri = null },
                        onPreview = { outputUri?.let { onOpenPreview(it, fileName, 1) } },
                        accentColor = accentColor
                    )
                }
                else -> {}
            }
        }
    }
}
