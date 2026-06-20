package com.nature.files.ui.preview

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nature.files.data.FileItem
import com.nature.files.data.StorageProvider
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.Spectral
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewer(
    fileItem: FileItem,
    storageProvider: StorageProvider,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var document by remember { mutableStateOf<PDDocument?>(null) }
    var renderer by remember { mutableStateOf<PDFRenderer?>(null) }
    var pageCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fileItem) {
        withContext(Dispatchers.IO) {
            try {
                PDFBoxResourceLoader.init(context)
                val inputStream = storageProvider.getInputStream(fileItem.path)
                val loadedDocument = PDDocument.load(inputStream)
                document = loadedDocument
                renderer = PDFRenderer(loadedDocument)
                pageCount = loadedDocument.numberOfPages
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                document?.close()
            } catch (e: Exception) {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileItem.name, style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestFloorBackground,
                    titleContentColor = BarkBrown
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = CanopyGreen)
            } else if (error != null) {
                Text(
                    text = "Failed to load PDF: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(pageCount) { pageIndex ->
                        PdfPage(renderer!!, pageIndex)
                    }
                }
            }
        }
    }
}

@Composable
fun PdfPage(renderer: PDFRenderer, pageIndex: Int) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            try {
                // Render with 1.5x scale as a balance between quality and memory
                bitmap = renderer.renderImage(pageIndex, 1.5f)
            } catch (e: Exception) {}
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Page ${pageIndex + 1}",
                modifier = Modifier.fillMaxWidth()
            )
        } ?: Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = CanopyGreen)
        }
    }

    DisposableEffect(pageIndex) {
        onDispose {
            bitmap?.recycle()
            bitmap = null
        }
    }
}
