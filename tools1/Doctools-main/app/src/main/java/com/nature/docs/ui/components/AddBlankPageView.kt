package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.activity.result.contract.ActivityResultContracts
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
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AddBlankPageView(
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

    var insertAfterPage by remember { mutableIntStateOf(0) }
    var pageSizeOption by remember { mutableStateOf("Match Source") }

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
                } else {
                    val count = getPageCount(context, uri, null)
                    pageCount = count
                    insertAfterPage = count
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
                        val document = if (unlockPassword.isNotEmpty()) PDDocument.load(inputStream, unlockPassword) else PDDocument.load(inputStream)
                        if (document.isEncrypted) document.isAllSecurityToBeRemoved = true

                        val sourcePage = if (pageCount > 0) document.getPage(0) else null
                        val rect = when(pageSizeOption) {
                            "A4" -> PDRectangle.A4
                            "Letter" -> PDRectangle.LETTER
                            else -> sourcePage?.mediaBox ?: PDRectangle.A4
                        }

                        val targetIndex = (insertAfterPage).coerceIn(0, document.numberOfPages)
                        val newDoc = PDDocument()
                        val newPage = PDPage(rect)

                        for (i in 0 until document.numberOfPages) {
                            if (i == targetIndex) newDoc.addPage(newPage)
                            // Using manual Form XObject copy to avoid importPage side effects
                            val sourcePage = document.getPage(i)
                            val targetPage = newDoc.importPage(sourcePage)
                            // Wait, importPage is okay if we are building a NEW document from scratch
                            // The issue with N-Up was drawing multiple pages on ONE page.
                            // But let's be safe and ensure no link issues.
                        }
                        if (targetIndex == document.numberOfPages) newDoc.addPage(newPage)

                        saveAndFlush(context, newDoc, saveUri)
                        document.close()
                        return@use
                    }
                    val endTime = System.currentTimeMillis()
                    withContext(Dispatchers.Main) {
                        processingTime = String.format("%.1fs", (endTime - startTime) / 1000.0)
                        outputUri = saveUri
                        SessionManager.addEntry(fileName, "Add Page", "Blank page inserted", Icons.Filled.AddCircleOutline, saveUri, pageCount + 1)
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
                            Text("Add Blank Page", fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("DOCUMENT EXPANSION", fontSize = 8.sp, fontWeight = FontWeight.Black, color = accentColor)
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
                            icon = Icons.Filled.AddCircleOutline,
                            title = "Select PDF",
                            subtitle = "INSERT BLANK PAGES",
                            accentColor = accentColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    ToolState.CONFIGURING -> {
                        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                            Spacer(Modifier.height(24.dp))
                            Text("INSERTION POINT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 1.sp)
                            Spacer(Modifier.height(12.dp))

                            Slider(
                                value = insertAfterPage.toFloat(),
                                onValueChange = { insertAfterPage = it.toInt() },
                                valueRange = 0f..pageCount.toFloat(),
                                steps = pageCount - 1,
                                colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                            )
                            Text(
                                text = if (insertAfterPage == 0) "At the beginning"
                                       else if (insertAfterPage == pageCount) "At the end"
                                       else "After page $insertAfterPage",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(Modifier.height(32.dp))
                            Text("PAGE SIZE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 1.sp)
                            Spacer(Modifier.height(12.dp))

                            val sizes = listOf("Match Source", "A4", "Letter")
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                sizes.forEach { size ->
                                    val selected = pageSizeOption == size
                                    Surface(
                                        onClick = { pageSizeOption = size },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (selected) accentColor.copy(0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(0.3f),
                                        border = BorderStroke(1.dp, if (selected) accentColor else Color.Transparent)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(size, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selected) accentColor else MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(48.dp))
                            Button(
                                onClick = { saveLauncher.launch(fileName.replace(".pdf", "-expanded.pdf")) },
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                shape = RoundedCornerShape(20.dp)
                            ) { Text("INSERT & SAVE PDF", fontWeight = FontWeight.Black) }
                        }
                    }
                    ToolState.PROCESSING -> {
                        LoadingStateView(accentColor, false, "Inserting blank page...")
                    }
                    ToolState.SUCCESS -> {
                        SuccessView(
                            message = "Insertion Complete",
                            subMessage = "Blank page added successfully",
                            processingTime = processingTime,
                            onDone = onBack,
                            onProcessMore = { currentState = ToolState.SELECTING; selectedUri = null },
                            onPreview = { outputUri?.let { onOpenPreview(it, fileName, pageCount + 1) } },
                            accentColor = accentColor
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
