package com.nature.files.ui.preview

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.nature.files.data.FileItem
import com.nature.files.data.StorageProvider
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.Spectral
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlViewer(
    fileItem: FileItem,
    storageProvider: StorageProvider,
    onBack: () -> Unit
) {
    var htmlContent by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(fileItem) {
        withContext(Dispatchers.IO) {
            try {
                storageProvider.getInputStream(fileItem.path).use { inputStream ->
                    htmlContent = inputStream.bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
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
                    text = "Failed to load HTML: $error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.allowFileAccess = true
                        }
                    },
                    update = { webView ->
                        htmlContent?.let {
                            webView.loadDataWithBaseURL(null, it, "text/html", "UTF-8", null)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
