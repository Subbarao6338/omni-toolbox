package com.naturetools.app.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URLEncoder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun WebToolScreen(
    navController: NavHostController,
    initialUrl: String? = null,
    showUrlBar: Boolean = true,
    title: String = "Web Search"
) {
    val context = LocalContext.current
    val defaultUrl = initialUrl ?: "https://www.google.com"
    var urlInput by remember { mutableStateOf(defaultUrl) }
    var urlToLoad by remember { mutableStateOf(defaultUrl) }
    var isOffline by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }

    fun checkConnectivity(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    LaunchedEffect(urlToLoad, webView) {
        isOffline = !checkConnectivity()
        if (!isOffline && webView != null) {
            val currentUrl = webView?.url?.removeSuffix("/")
            val targetUrl = urlToLoad.removeSuffix("/")
            if (currentUrl != targetUrl) {
                webView?.loadUrl(urlToLoad)
            }
        }
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { webView?.reload() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
            IconButton(onClick = {
                val currentUrl = webView?.url ?: urlToLoad
                navController.navigate("media_grabber?url=$currentUrl")
            }) {
                Icon(Icons.Default.Download, contentDescription = "Grab Media")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (showUrlBar) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("URL") },
                        leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val trimmedInput = urlInput.trim()
                        urlToLoad = when {
                            trimmedInput.startsWith("http://") || trimmedInput.startsWith("https://") -> trimmedInput
                            trimmedInput.contains(".") && !trimmedInput.contains(" ") -> "https://$trimmedInput"
                            else -> "https://www.google.com/search?q=${URLEncoder.encode(trimmedInput, "UTF-8")}"
                        }
                    }) {
                        Text("Go")
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (isOffline) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.WifiOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text("No Internet Connection", style = MaterialTheme.typography.titleLarge)
                        Button(onClick = {
                            isOffline = !checkConnectivity()
                            if (!isOffline) {
                                webView?.loadUrl(urlToLoad)
                            }
                        }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                AndroidView(
                    factory = {
                        WebView(it).apply {
                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    isLoading = true
                                    super.onPageStarted(view, url, favicon)
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                    super.onPageFinished(view, url)
                                }

                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    return false
                                }
                            }
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                userAgentString = WebSettings.getDefaultUserAgent(context)
                            }
                            webView = this
                        }
                    },
                    update = {
                        // Avoid reload on recomposition if possible, but keep it filling space
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
        }
    }
}
