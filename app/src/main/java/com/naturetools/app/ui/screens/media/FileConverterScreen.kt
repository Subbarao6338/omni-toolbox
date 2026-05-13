package com.naturetools.app.ui.screens.media

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FileConverterScreen(navController: NavHostController) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedTargetFormat by remember { mutableStateOf("") }
    var isConverting by remember { mutableStateOf(false) }
    var conversionProgress by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        selectedFileName = uri?.lastPathSegment ?: "Selected File"
        selectedTargetFormat = ""
    }

    val imageFormats = listOf("JPG", "PNG", "WEBP", "PDF")

    val currentExt = selectedFileName.substringAfterLast(".", "").uppercase()
    val availableFormats = when (currentExt) {
        "JPG", "JPEG", "PNG", "WEBP" -> imageFormats.filter { it != currentExt && it != (if(currentExt=="JPEG") "JPG" else "") }
        "PDF" -> listOf("JPG", "PNG", "TXT")
        else -> listOf("PDF", "TXT", "JPG", "PNG")
    }

    ToolScreen(
        title = "File Format Converter",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Transform,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                "Offline File Converter",
                style = MaterialTheme.typography.headlineSmall
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedFileUri == null) {
                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Select File")
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(selectedFileName, style = MaterialTheme.typography.bodyLarge)
                                Text("Format: $currentExt", style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = { selectedFileUri = null; selectedTargetFormat = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                }
            }

            if (selectedFileUri != null && !isConverting) {
                Text("Convert to:", style = MaterialTheme.typography.titleMedium)

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableFormats.forEach { format ->
                        FilterChip(
                            selected = selectedTargetFormat == format,
                            onClick = { selectedTargetFormat = format },
                            label = { Text(format) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        isConverting = true
                        scope.launch {
                            val success = performActualConversion(
                                context,
                                selectedFileUri!!,
                                selectedFileName,
                                selectedTargetFormat
                            ) { progress ->
                                conversionProgress = progress
                            }
                            isConverting = false
                            if (success) {
                                Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_LONG).show()
                                selectedFileUri = null
                            } else {
                                Toast.makeText(context, "Conversion failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedTargetFormat.isNotEmpty()
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Convert and Save")
                }
            }

            if (isConverting) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Processing... ${(conversionProgress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { conversionProgress },
                        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Local & Private", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "All conversions happen entirely on your device. No data is sent to servers.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private suspend fun performActualConversion(
    context: android.content.Context,
    uri: Uri,
    originalName: String,
    targetFormat: String,
    onProgress: (Float) -> Unit
): Boolean = withContext(Dispatchers.IO) {
    try {
        onProgress(0.1f)
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false
        val baseName = originalName.substringBeforeLast(".")
        val mimeType = when(targetFormat) {
            "JPG" -> "image/jpeg"
            "PNG" -> "image/png"
            "WEBP" -> "image/webp"
            "PDF" -> "application/pdf"
            else -> "text/plain"
        }
        val extension = when(targetFormat) {
            "JPG" -> "jpg"
            "PNG" -> "png"
            "WEBP" -> "webp"
            "PDF" -> "pdf"
            else -> "txt"
        }

        onProgress(0.3f)
        val outputStream: OutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$baseName.$extension")
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val targetUri = context.contentResolver.insert(contentUri, contentValues) ?: return@withContext false
            context.contentResolver.openOutputStream(targetUri) ?: return@withContext false
        } else {
            val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outputFile = File(outputDir, "$baseName.$extension")
            FileOutputStream(outputFile)
        }

        when (targetFormat) {
            "JPG", "PNG", "WEBP" -> {
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext false
                onProgress(0.6f)
                val format = when(targetFormat) {
                    "JPG" -> Bitmap.CompressFormat.JPEG
                    "PNG" -> Bitmap.CompressFormat.PNG
                    else -> Bitmap.CompressFormat.WEBP
                }
                outputStream.use { out ->
                    bitmap.compress(format, 90, out)
                }
                onProgress(1.0f)
                return@withContext true
            }
            "PDF" -> {
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext false
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()
                onProgress(1.0f)
                return@withContext true
            }
            "TXT" -> {
                val text = inputStream.bufferedReader().use { it.readText() }
                outputStream.use { out ->
                    out.write(text.toByteArray())
                }
                onProgress(1.0f)
                return@withContext true
            }
            else -> return@withContext false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext false
    }
}
