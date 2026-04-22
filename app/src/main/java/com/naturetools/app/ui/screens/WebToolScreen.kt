package com.naturetools.app.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
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
fun WebToolScreen(navController: NavHostController) {
    val context = LocalContext.current
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var urlToLoad by remember { mutableStateOf("https://www.google.com") }
    var isOffline by remember { mutableStateOf(false) }

    fun checkConnectivity(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    LaunchedEffect(urlToLoad) {
        isOffline = !checkConnectivity()
    }

    ToolScreen(title = "Web Search", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("URL") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    urlToLoad = if (urlInput.startsWith("http")) urlInput else "https://$urlInput"
                }) {
                    Text("Go")
                }
            }

            if (isOffline) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Text("No Internet Connection", style = MaterialTheme.typography.titleLarge)
                        Button(onClick = { isOffline = !checkConnectivity() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                AndroidView(factory = {
                    WebView(it).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(urlToLoad)
                    }
                }, update = {
                    it.loadUrl(urlToLoad)
                }, modifier = Modifier.fillMaxSize().weight(1f))
            }
        }
    }
}
