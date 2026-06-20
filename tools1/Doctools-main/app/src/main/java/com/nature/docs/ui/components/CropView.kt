package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.ui.theme.NatureGreen
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CropView(
    initialUri: Uri? = null,
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    val accentColor = Color(0xFF14B8A6)

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUri by remember { mutableStateOf(initialUri) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var pageCount by remember { mutableIntStateOf(0) }
    var isFileLoading by remember { mutableStateOf(false) }
    var processingTime by remember { mutableStateOf("") }
    var unlockPassword by remember { mutableStateOf("") }
    var fileToUnlock by remember { mutableStateOf<String?>(null) }

    // Crop settings (0.0 to 1.0)
    var cropTop by remember { mutableFloatStateOf(0.1f) }
    var cropBottom by remember { mutableFloatStateOf(0.1f) }
    var cropLeft by remember { mutableFloatStateOf(0.1f) }
    var cropRight by remember { mutableFloatStateOf(0.1f) }

    fun handleFile(uri: Uri) {
        selectedUri = uri
        val details = getUriDetails(context, uri)
        fileName = details.name
        isFileLoading = true
        scope.launch(Dispatchers.IO) {
            val isEnc = checkIsEncryptedLocal(context, uri)
            withContext(Dispatchers.Main) {
                if (isEnc) {
                    fileToUnlock = fileName
                    isFileLoading = false
                } else {
                    pageCount = getPageCount(context, uri, null)
                    currentState = ToolState.CONFIGURING
                    isFileLoading = false
                }
            }
        }
    }

    LaunchedEffect(initialUri) { initialUri?.let { handleFile(it) } }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { handleFile(it) } }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { saveUri ->
            currentState = ToolState.PROCESSING
            val startTime = System.currentTimeMillis()
            scope.launch(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(selectedUri!!)?.use { inputStream ->
                        val document = if (unlockPassword.isNotEmpty()) PDDocument.load(inputStream, unlockPassword) else PDDocument.load(inputStream)
                        document.pages.forEach { page ->
                            val box = page.mediaBox
                            val newX = box.lowerLeftX + (box.width * cropLeft)
                            val newY = box.lowerLeftY + (box.height * cropBottom)
                            val newW = box.width * (1f - cropLeft - cropRight)
                            val newH = box.height * (1f - cropTop - cropBottom)

                            val newRect = PDRectangle(newX, newY, newW, newH)
                            page.mediaBox = newRect
                            page.cropBox = newRect
                        }
                        saveAndFlush(context, document, saveUri)
                    }
                    val endTime = System.currentTimeMillis()
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (endTime - startTime) / 1000.0)
                        outputUri = saveUri
                        SessionManager.addEntry(fileName, "Crop", "Pages cropped", Icons.Filled.Crop, saveUri, pageCount)
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
                            Text("Crop PDF", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("ADJUST MEDIA BOX", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
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
                            icon = Icons.Filled.Crop,
                            title = "Select PDF to Crop",
                            subtitle = "ADJUST PAGE MARGINS",
                            accentColor = accentColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    ToolState.CONFIGURING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                            Spacer(Modifier.height(16.dp))
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                UnifiedPdfPreview(
                                    uri = selectedUri!!,
                                    pageCount = pageCount,
                                    mode = PreviewMode.COVER,
                                    accentColor = accentColor,
                                    itemOverlay = {
                                        Box(Modifier.fillMaxSize()) {
                                            // Semi-transparent overlay for cropped area
                                            Box(Modifier.fillMaxWidth().fillMaxHeight(cropTop).align(Alignment.TopCenter).background(Color.Black.copy(0.4f)))
                                            Box(Modifier.fillMaxWidth().fillMaxHeight(cropBottom).align(Alignment.BottomCenter).background(Color.Black.copy(0.4f)))
                                            Box(Modifier.fillMaxWidth(cropLeft).fillMaxHeight().align(Alignment.CenterStart).background(Color.Black.copy(0.4f)))
                                            Box(Modifier.fillMaxWidth(cropRight).fillMaxHeight().align(Alignment.CenterEnd).background(Color.Black.copy(0.4f)))

                                            // Crop Border
                                            Box(Modifier.fillMaxSize().padding(start = (cropLeft * 100).dp, top = (cropTop * 100).dp, end = (cropRight * 100).dp, bottom = (cropBottom * 100).dp).border(2.dp, accentColor))
                                        }
                                    }
                                )
                            }

                            Spacer(Modifier.height(24.dp))
                            Text("ADJUST MARGINS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = accentColor)

                            Column(Modifier.padding(vertical = 16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Top: ${(cropTop * 100).toInt()}%", modifier = Modifier.width(80.dp), fontSize = 12.sp)
                                    Slider(value = cropTop, onValueChange = { cropTop = it }, valueRange = 0f..0.4f, modifier = Modifier.weight(1f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Bottom: ${(cropBottom * 100).toInt()}%", modifier = Modifier.width(80.dp), fontSize = 12.sp)
                                    Slider(value = cropBottom, onValueChange = { cropBottom = it }, valueRange = 0f..0.4f, modifier = Modifier.weight(1f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Left: ${(cropLeft * 100).toInt()}%", modifier = Modifier.width(80.dp), fontSize = 12.sp)
                                    Slider(value = cropLeft, onValueChange = { cropLeft = it }, valueRange = 0f..0.4f, modifier = Modifier.weight(1f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Right: ${(cropRight * 100).toInt()}%", modifier = Modifier.width(80.dp), fontSize = 12.sp)
                                    Slider(value = cropRight, onValueChange = { cropRight = it }, valueRange = 0f..0.4f, modifier = Modifier.weight(1f))
                                }
                            }

                            Button(
                                onClick = { saveLauncher.launch(fileName.replace(".pdf", "-cropped.pdf")) },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(20.dp)
                            ) { Text("CROP & SAVE PDF", fontWeight = FontWeight.Black) }
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                    ToolState.PROCESSING -> {
                        ProcessingStateView(accentColor, uri = selectedUri, text = "Cropping document pages...", current = 0, total = pageCount, showWarning = false)
                    }
                    ToolState.SUCCESS -> {
                        SuccessView(
                            message = "Crop Complete",
                            subMessage = "Margins adjusted on all pages",
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
