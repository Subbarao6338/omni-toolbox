package com.nature.docs.ui.components

import coil.imageLoader

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.imageLoader
import coil.compose.rememberAsyncImagePainter
import com.nature.docs.ui.theme.*
import com.nature.docs.data.SettingsManager
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CompressView(
    initialUri: Uri? = null,
    initialPassword: String? = null,
    onBack: () -> Unit,
    onOpenPreview: (Uri, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = MaterialTheme.colorScheme.background == Color.Black
    val accentColor = BotanicalGreen

    var currentState by remember { mutableStateOf<ToolState>(ToolState.SELECTING) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var outputUri by remember { mutableStateOf<Uri?>(null) }
    var unlockPassword by remember { mutableStateOf(initialPassword ?: "") }
    var fileName by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("") }
    var fileSizeOld by remember { mutableLongStateOf(0L) }
    var isFileLoading by remember { mutableStateOf(false) }
    var processingTime by remember { mutableStateOf("") }
    
    var compressionLevel by remember { mutableStateOf("Recommended") }
    var pageCount by remember { mutableIntStateOf(0) }
    var progressPage by remember { mutableIntStateOf(0) }
    var showLoadingWarning by remember { mutableStateOf(false) }
    var fileToUnlock by remember { mutableStateOf<String?>(null) }

    // Load last-used settings
    LaunchedEffect(Unit) {
        SettingsManager.getCompressLevel(context).collect { compressionLevel = it }
    }

    fun handleFileSelection(uri: Uri, password: String? = null) {
        selectedUri = uri
        val details = getUriDetails(context, uri)
        fileName = details.name
        fileSize = details.size
        fileSizeOld = details.sizeBytes
        
        isFileLoading = true
        scope.launch(Dispatchers.IO) {
            val isEncrypted = checkIsEncryptedLocal(context, uri)
            if (isEncrypted && password == null) {
                withContext(Dispatchers.Main) {
                    fileToUnlock = fileName
                    isFileLoading = false
                }
            } else {
                val count = getPageCount(context, uri, password)
                withContext(Dispatchers.Main) {
                    pageCount = count
                    currentState = ToolState.CONFIGURING
                    isFileLoading = false
                }
            }
        }
    }

    LaunchedEffect(initialUri) {
        initialUri?.let { handleFileSelection(it, initialPassword) }
    }

    LaunchedEffect(isFileLoading, currentState) {
        if (isFileLoading || currentState == ToolState.PROCESSING) {
            delay(5000)
            showLoadingWarning = true
        } else {
            showLoadingWarning = false
        }
    }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleFileSelection(it) }
    }

    val saveLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let { saveUri ->
            currentState = ToolState.PROCESSING
            val startTime = System.currentTimeMillis()
            scope.launch(Dispatchers.IO) {
                try {
                    // Save last-used setting
                    SettingsManager.saveCompressLevel(context, compressionLevel)

                    compressPdf(context, selectedUri!!, saveUri, if (unlockPassword.isEmpty()) null else unlockPassword, compressionLevel) { current, total ->
                        progressPage = current
                    }
                    val endTime = System.currentTimeMillis()
                    val timeStr = String.format("%.1fs", (endTime - startTime) / 1000.0)
                    
                    val finalCount = getPageCount(context, saveUri, null)
                    
                    withContext(Dispatchers.Main) {
                        processingTime = timeStr
                        outputUri = saveUri
                        SessionManager.addEntry(fileName, "Compress", "Optimized", Icons.Filled.FlashOn, saveUri, finalCount)
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

    LaunchedEffect(Unit) { PDFBoxResourceLoader.init(context) }

    LinenCanvas(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (currentState != ToolState.SUCCESS && currentState != ToolState.PROCESSING) {
                    Row(
                        modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = InkBrown)
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Compress", style = MaterialTheme.typography.titleLarge, color = InkBrown)
                            Text("OPTIMIZE SPECIMEN SIZE", style = MaterialTheme.typography.labelSmall, color = accentColor, letterSpacing = 1.sp)
                        }
                        if (selectedUri != null && currentState == ToolState.CONFIGURING) {
                            TextButton(onClick = { selectedUri = null; currentState = ToolState.SELECTING }) {
                                Text("CHANGE", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.4f))
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                if (isFileLoading) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        VineProgressBar(0.5f, Modifier.padding(horizontal = 32.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Analyzing specimen structure...", style = MaterialTheme.typography.bodyMedium, color = InkBrown)
                    }
                } else {
                    when (currentState) {
                        ToolState.SELECTING -> {
                            SelectionGrid(
                                onSelect = { pickLauncher.launch("application/pdf") },
                                isDark = isDark,
                                icon = Icons.Outlined.Bolt,
                                title = "Select Specimen",
                                subtitle = "OPTIMIZE ANY PDF DOCUMENT",
                                accentColor = accentColor,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        ToolState.CONFIGURING -> {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(16.dp))

                                AgedPaperCard(modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(0.707f)) {
                                    UnifiedPdfPreview(
                                        uri = selectedUri!!,
                                        pageCount = pageCount,
                                        mode = PreviewMode.COVER,
                                        password = null,
                                        accentColor = accentColor,
                                        disableLightbox = true
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                                Text(fileName, style = MaterialTheme.typography.bodyLarge, color = InkBrown, maxLines = 1)
                                Text("$fileSize • $pageCount PAGES", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.4f))

                                Spacer(Modifier.height(24.dp))
                                Text("OPTIMIZATION LEVEL", style = MaterialTheme.typography.labelSmall, color = accentColor, letterSpacing = 1.5.sp)
                                Spacer(Modifier.height(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Basic", "Recommended", "Extreme").forEach { level ->
                                        val isSelected = compressionLevel == level
                                        AgedPaperCard(
                                            modifier = Modifier.fillMaxWidth().clickable { compressionLevel = level }
                                        ) {
                                            Row(Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                RadioButton(selected = isSelected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = accentColor))
                                                Spacer(Modifier.width(12.dp))
                                                Column {
                                                    Text(level, style = MaterialTheme.typography.bodyLarge, color = InkBrown)
                                                    Text(
                                                        text = when(level) {
                                                            "Extreme" -> "Maximum pruning of unnecessary data"
                                                            "Recommended" -> "Perfect balance for field work"
                                                            else -> "Retain all delicate details"
                                                        },
                                                        style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.5f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(32.dp))
                                Button(
                                    onClick = {
                                        val defaultName = fileName.replace(".pdf", "", true) + "-optimized.pdf"
                                        saveLauncher.launch(defaultName)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(60.dp),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = Color.White)
                                ) {
                                    Text("OPTIMIZE & SAVE", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                                Spacer(Modifier.height(100.dp))
                            }
                        }
                        ToolState.PROCESSING -> {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                VineProgressBar(progressPage.toFloat() / pageCount.coerceAtLeast(1), Modifier.padding(horizontal = 32.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("Pruning specimen: $progressPage of $pageCount", style = MaterialTheme.typography.bodyMedium, color = InkBrown)
                            }
                        }
                        ToolState.SUCCESS -> {
                            SuccessView(
                                message = "Optimization Complete",
                                subMessage = "Specimen has been refined",
                                processingTime = processingTime,
                                onDone = onBack,
                                onProcessMore = {
                                    selectedUri = null
                                    outputUri = null
                                    unlockPassword = ""
                                    currentState = ToolState.SELECTING
                                },
                                onPreview = {
                                    outputUri?.let { uri -> onOpenPreview(uri, fileName, pageCount) }
                                },
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
                            isFileLoading = true
                            scope.launch(Dispatchers.IO) {
                                val decryptedUri = decryptToCache(context, selectedUri!!, pass)
                                if (decryptedUri != null) {
                                    val count = getPageCount(context, decryptedUri, null)
                                    withContext(Dispatchers.Main) {
                                        if (count > 0) {
                                            unlockPassword = pass
                                            selectedUri = decryptedUri
                                            pageCount = count
                                            currentState = ToolState.CONFIGURING
                                            fileToUnlock = null
                                        } else {
                                            Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show()
                                        }
                                        isFileLoading = false
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show()
                                        isFileLoading = false
                                    }
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
}
