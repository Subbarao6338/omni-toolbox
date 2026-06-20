package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Layers
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
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.rendering.ImageType
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FlattenView(
    initialUri: Uri? = null,
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    val accentColor = Color(0xFFF59E0B)

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUri by remember { mutableStateOf(initialUri) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var unlockPassword by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("") }
    var pageCount by remember { mutableIntStateOf(0) }
    var isFileLoading by remember { mutableStateOf(false) }
    var processingTime by remember { mutableStateOf("") }
    var progressCount by remember { mutableIntStateOf(0) }
    var fileToUnlock by remember { mutableStateOf<String?>(null) }
    var showLoadingWarning by remember { mutableStateOf(false) }

    fun handleFile(uri: Uri) {
        selectedUri = uri
        val details = getUriDetails(context, uri)
        fileName = details.name
        fileSize = details.size
        isFileLoading = true
        scope.launch(Dispatchers.IO) {
            val isEnc = checkIsEncryptedLocal(context, uri)
            withContext(Dispatchers.Main) {
                if (isEnc) {
                    fileToUnlock = fileName
                    isFileLoading = false
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
                        val targetDoc = PDDocument()
                        val renderer = PDFRenderer(sourceDoc)
                        val total = sourceDoc.numberOfPages

                        for (i in 0 until total) {
                            withContext(Dispatchers.Main) { progressCount = i + 1 }
                            val scale = 2.0f // High quality flattening
                            val bitmap = renderer.renderImage(i, scale, ImageType.RGB)
                            val pdImage = JPEGFactory.createFromImage(targetDoc, bitmap, 0.8f)
                            val page = PDPage(PDRectangle(pdImage.width.toFloat() / scale, pdImage.height.toFloat() / scale))
                            targetDoc.addPage(page)
                            PDPageContentStream(targetDoc, page).use { cs ->
                                cs.drawImage(pdImage, 0f, 0f, page.mediaBox.width, page.mediaBox.height)
                            }
                            bitmap.recycle()
                        }
                        saveAndFlush(context, targetDoc, saveUri)
                        sourceDoc.close()
                    }
                    val endTime = System.currentTimeMillis()
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (endTime - startTime) / 1000.0)
                        outputUri = saveUri
                        SessionManager.addEntry(fileName, "Flatten", "Annotations merged", Icons.Filled.Layers, saveUri, pageCount)
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
                            Text("Flatten PDF", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("MERGE ANNOTATIONS & FORMS", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
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
                            icon = Icons.Outlined.Layers,
                            title = "Select PDF to Flatten",
                            subtitle = "ANNOTATIONS WILL BE MERGED",
                            accentColor = accentColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    ToolState.CONFIGURING -> {
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(Modifier.height(16.dp))
                            Box(modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(0.707f)) {
                                UnifiedPdfPreview(uri = selectedUri!!, pageCount = pageCount, mode = PreviewMode.COVER, accentColor = accentColor)
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(fileName, fontWeight = FontWeight.Bold)
                            Text("$fileSize • $pageCount PAGES", fontSize = 10.sp, color = Color.Gray)
                            Spacer(Modifier.height(32.dp))
                            Text("Flattening will convert all pages into images, permanently merging all signatures, forms, and annotations into the background. This makes the document non-editable.", fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(Modifier.height(32.dp))
                            Button(
                                onClick = { saveLauncher.launch(fileName.replace(".pdf", "-flattened.pdf")) },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(20.dp)
                            ) { Text("FLATTEN & SAVE PDF", fontWeight = FontWeight.Black) }
                        }
                    }
                    ToolState.PROCESSING -> {
                        ProcessingStateView(accentColor = accentColor, uri = selectedUri, text = "Flattening page $progressCount of $pageCount...", current = progressCount, total = pageCount, showWarning = false)
                    }
                    ToolState.SUCCESS -> {
                        SuccessView(
                            message = "Flatten Complete",
                            subMessage = "All layers merged into background",
                            processingTime = processingTime,
                            onDone = onBack,
                            onProcessMore = { currentState = ToolState.SELECTING; selectedUri = null },
                            onPreview = { outputUri?.let { onOpenPreview(it, fileName, pageCount) } },
                            accentColor = accentColor
                        )
                    }
                    else -> {}
                }
            }

            if (fileToUnlock != null) {
                LockedFilePrompt(
                    fileName = fileToUnlock!!,
                    onDismiss = { fileToUnlock = null; selectedUri = null; currentState = ToolState.SELECTING },
                    onUnlocked = { pass ->
                        unlockPassword = pass
                        isFileLoading = true
                        scope.launch(Dispatchers.IO) {
                            val count = getPageCount(context, selectedUri!!, pass)
                            if (count > 0) {
                                withContext(Dispatchers.Main) {
                                    pageCount = count; currentState = ToolState.CONFIGURING; isFileLoading = false; fileToUnlock = null
                                }
                            } else {
                                withContext(Dispatchers.Main) { Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show(); isFileLoading = false }
                            }
                        }
                    },
                    accentColor = accentColor,
                    isLoading = isFileLoading
                )
            }
        }
    }
}
