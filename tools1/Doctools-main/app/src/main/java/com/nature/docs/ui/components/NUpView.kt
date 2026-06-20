package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.util.Matrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

@Composable
fun NUpView(
    initialUri: Uri? = null,
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    val accentColor = Color(0xFFF43F5E)

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUri by remember { mutableStateOf(initialUri) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var unlockPassword by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var pageCount by remember { mutableIntStateOf(0) }
    var isFileLoading by remember { mutableStateOf(false) }
    var processingTime by remember { mutableStateOf("") }

    var nUpCount by remember { mutableIntStateOf(2) } // 2 or 4

    fun handleFile(uri: Uri) {
        selectedUri = uri
        val details = getUriDetails(context, uri)
        fileName = details.name
        isFileLoading = true
        scope.launch(Dispatchers.IO) {
            val isEnc = checkIsEncryptedLocal(context, uri)
            withContext(Dispatchers.Main) {
                if (isEnc) {
                    isFileLoading = false
                    // Trigger unlock prompt logic
                } else {
                    val count = getPageCount(context, uri, null)
                    pageCount = count
                    currentState = ToolState.CONFIGURING
                    isFileLoading = false
                }
            }
        }
    }

    LaunchedEffect(initialUri) { initialUri?.let { handleFile(it) } }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleFile(it) }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { saveUri ->
            currentState = ToolState.PROCESSING
            val startTime = System.currentTimeMillis()
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(selectedUri!!)?.use { inputStream ->
                        val sourceDoc = if (unlockPassword.isNotEmpty()) PDDocument.load(inputStream, unlockPassword) else PDDocument.load(inputStream)
                        if (sourceDoc.isEncrypted) sourceDoc.isAllSecurityToBeRemoved = true

                        val targetDoc = PDDocument()
                        val totalPages = sourceDoc.numberOfPages

                        val pageSize = PDRectangle.A4
                        val rows = if (nUpCount == 2) 2 else 2
                        val cols = if (nUpCount == 2) 1 else 2

                        val cellWidth = pageSize.width / cols
                        val cellHeight = pageSize.height / rows

                        var currentPage: PDPage? = null
                        var cs: PDPageContentStream? = null

                        for (i in 0 until totalPages) {
                            val cellIndex = i % nUpCount
                            if (cellIndex == 0) {
                                cs?.close()
                                currentPage = PDPage(pageSize)
                                targetDoc.addPage(currentPage)
                                cs = PDPageContentStream(targetDoc, currentPage)
                            }

                            val sourcePage = sourceDoc.getPage(i)
                            // DO NOT use importPage as it adds the page to the doc automatically

                            val col = cellIndex % cols
                            val row = if (nUpCount == 2) (1 - cellIndex) else (1 - (cellIndex / cols))

                            val tx = col * cellWidth
                            val ty = row * cellHeight

                            val scaleX = cellWidth / sourcePage.mediaBox.width
                            val scaleY = cellHeight / sourcePage.mediaBox.height
                            val scale = minOf(scaleX, scaleY)

                            val offsetX = (cellWidth - sourcePage.mediaBox.width * scale) / 2
                            val offsetY = (cellHeight - sourcePage.mediaBox.height * scale) / 2

                            cs?.saveGraphicsState()
                            val matrix = Matrix()
                            matrix.translate(tx + offsetX, ty + offsetY)
                            matrix.scale(scale, scale)
                            cs?.transform(matrix)

                            val form = PDFormXObject(targetDoc.document.createCOSStream())
                            form.resources = sourcePage.resources
                            form.setBBox(sourcePage.mediaBox)
                            form.setMatrix(sourcePage.matrix.createAffineTransform())
                            form.cosObject.setItem(com.tom_roush.pdfbox.cos.COSName.TYPE, com.tom_roush.pdfbox.cos.COSName.XOBJECT)
                            form.cosObject.setItem(com.tom_roush.pdfbox.cos.COSName.SUBTYPE, com.tom_roush.pdfbox.cos.COSName.FORM)

                            // Copy content stream
                            sourcePage.contents?.let { contents ->
                                val output = form.cosObject.createOutputStream()
                                contents.use { input -> input.copyTo(output) }
                                output.close()
                            }

                            cs?.drawForm(form)
                            cs?.restoreGraphicsState()
                        }
                        cs?.close()

                        saveAndFlush(context, targetDoc, saveUri)
                        sourceDoc.close()
                    }
                    val endTime = System.currentTimeMillis()
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (endTime - startTime) / 1000.0)
                        outputUri = saveUri
                        SessionManager.addEntry(fileName, "N-Up", "$nUpCount per sheet", Icons.Filled.Layers, saveUri, (pageCount + nUpCount - 1) / nUpCount)
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
                            Text("N-Up PDF", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("MULTIPLE PAGES PER SHEET", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            if (isFileLoading) {
                LoadingStateView(accentColor, false, "Reading document...")
            } else {
                when (currentState) {
                    ToolState.SELECTING -> {
                        SelectionGrid(
                            onSelect = { pickLauncher.launch("application/pdf") },
                            isDark = isDark,
                            icon = Icons.Filled.GridView,
                            title = "Select PDF for N-Up",
                            subtitle = "COMBINE PAGES",
                            accentColor = accentColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    ToolState.CONFIGURING -> {
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(16.dp))
                            Text("Layout Options", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Spacer(Modifier.height(24.dp))

                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                LayoutOptionCard("2-UP", "Two pages per sheet", nUpCount == 2, Modifier.weight(1f)) { nUpCount = 2 }
                                LayoutOptionCard("4-UP", "Four pages per sheet", nUpCount == 4, Modifier.weight(1f)) { nUpCount = 4 }
                            }

                            Spacer(Modifier.height(40.dp))
                            Button(
                                onClick = { saveLauncher.launch(fileName.replace(".pdf", "-nup.pdf")) },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(20.dp)
                            ) { Text("GENERATE N-UP PDF", fontWeight = FontWeight.Black) }
                        }
                    }
                    ToolState.PROCESSING -> {
                        LoadingStateView(accentColor, false, "Generating layout...")
                    }
                    ToolState.SUCCESS -> {
                        SuccessView(
                            message = "N-Up Complete",
                            subMessage = "Pages combined successfully",
                            processingTime = processingTime,
                            onDone = onBack,
                            onProcessMore = { currentState = ToolState.SELECTING; selectedUri = null },
                            onPreview = { outputUri?.let { onOpenPreview(it, fileName, (pageCount + nUpCount - 1) / nUpCount) } },
                            accentColor = accentColor
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun LayoutOptionCard(title: String, subtitle: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) NatureGreen.copy(0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(0.3f),
        border = BorderStroke(2.dp, if (selected) NatureGreen else Color.Transparent)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 20.sp, color = if (selected) NatureGreen else MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 10.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
