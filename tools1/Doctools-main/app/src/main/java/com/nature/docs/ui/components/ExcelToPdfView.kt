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
import org.apache.poi.ss.usermodel.WorkbookFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ExcelToPdfView(
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
                        val workbook = WorkbookFactory.create(inputStream)
                        val pdfDoc = PDDocument()

                        for (i in 0 until workbook.numberOfSheets) {
                            val sheet = workbook.getSheetAt(i)
                            val page = PDPage()
                            pdfDoc.addPage(page)
                            PDPageContentStream(pdfDoc, page).use { contentStream ->
                                contentStream.beginText()
                                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14f)
                                contentStream.newLineAtOffset(50f, 750f)
                                contentStream.showText("Sheet: ${sheet.sheetName}")
                                contentStream.newLineAtOffset(0f, -20f)
                                contentStream.setFont(PDType1Font.HELVETICA, 10f)

                                val rowHeight = 15f
                                val colWidth = 60f
                                var y = 730f

                                sheet.forEach { row ->
                                    var x = 50f
                                    row.forEach { cell ->
                                        val cellText = cell.toString().filter { it.code in 32..126 }.take(15)
                                        contentStream.beginText()
                                        contentStream.setFont(PDType1Font.HELVETICA, 8f)
                                        contentStream.newLineAtOffset(x + 2f, y + 4f)
                                        contentStream.showText(cellText)
                                        contentStream.endText()

                                        // Draw cell border
                                        contentStream.setLineWidth(0.5f)
                                        contentStream.addRect(x, y, colWidth, rowHeight)
                                        contentStream.stroke()

                                        x += colWidth
                                        if (x > 540f) return@forEach
                                    }
                                    y -= rowHeight
                                    if (y < 50f) return@forEach
                                }
                                contentStream.endText()
                            }
                        }
                        saveAndFlush(context, pdfDoc, saveUri)

                        withContext(Dispatchers.Main) {
                            processingTime = String.format("%.1fs", (System.currentTimeMillis() - startTime) / 1000.0)
                            outputUri = saveUri
                            SessionManager.addEntry(fileName, "Excel to PDF", "Converted", Icons.Filled.TableChart, saveUri, workbook.numberOfSheets)
                            currentState = ToolState.SUCCESS
                        }
                        workbook.close()
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
                            Text("Excel to PDF", fontSize = 16.sp, fontWeight = FontWeight.Black)
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
                        onSelect = { pickLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                        isDark = MaterialTheme.colorScheme.background == Color.Black,
                        icon = Icons.Filled.TableChart,
                        title = "Select Excel Spreadsheet",
                        subtitle = "XLSX FILES ONLY",
                        accentColor = accentColor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                ToolState.CONFIGURING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                        Icon(Icons.Filled.TableChart, null, modifier = Modifier.size(80.dp), tint = accentColor)
                        Spacer(Modifier.height(16.dp))
                        Text(fileName, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { saveLauncher.launch(fileName.replace(".xlsx", ".pdf")) },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(20.dp)
                        ) { Text("CONVERT TO PDF", fontWeight = FontWeight.Black) }
                    }
                }
                ToolState.PROCESSING -> {
                    LoadingStateView(accentColor, false, "Converting spreadsheet...")
                }
                ToolState.SUCCESS -> {
                    SuccessView(
                        message = "Conversion Complete",
                        subMessage = "Excel sheet converted to PDF",
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
