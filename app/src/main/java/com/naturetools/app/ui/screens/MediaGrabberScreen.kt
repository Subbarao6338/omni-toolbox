package com.naturetools.app.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.naturetools.app.ui.components.ToolScreen
import org.json.JSONArray

@Composable
fun MediaGrabberScreen(navController: NavHostController) {
    var urlInput by remember { mutableStateOf("") }
    var urlToLoad by remember { mutableStateOf("") }
    var mediaLinks by remember { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(urlToLoad, webView) {
        if (urlToLoad.isNotEmpty() && webView != null) {
            mediaLinks = emptySet()
            isLoading = true
            webView?.loadUrl(urlToLoad)
        }
    }

    ToolScreen(title = "Media Grabber", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Website URL") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    urlToLoad = if (urlInput.startsWith("http")) urlInput else "https://$urlInput"
                }) {
                    Text("Grab")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (mediaLinks.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Enter a URL to grab images and videos", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(mediaLinks.toList()) { link ->
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(link))
                                }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = link,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(link)) },
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = "Copy Link",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Hidden WebView for extraction
            AndroidView(
                factory = {
                    WebView(it).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                val script = """
                                    (function() {
                                        var links = [];
                                        var imgs = document.getElementsByTagName('img');
                                        for (var i = 0; i < imgs.length; i++) {
                                            if (imgs[i].src) links.push(imgs[i].src);
                                        }
                                        var videos = document.getElementsByTagName('video');
                                        for (var i = 0; i < videos.length; i++) {
                                            if (videos[i].src) links.push(videos[i].src);
                                            var sources = videos[i].getElementsByTagName('source');
                                            for (var j = 0; j < sources.length; j++) {
                                                if (sources[j].src) links.push(sources[j].src);
                                            }
                                        }
                                        return JSON.stringify(links);
                                    })();
                                """.trimIndent()

                                view?.evaluateJavascript(script) { result ->
                                    if (result != null && result != "null") {
                                        try {
                                            // result is a JSON string, possibly double encoded by evaluateJavascript
                                            val jsonString = if (result.startsWith("\"") && result.endsWith("\"")) {
                                                // It's a quoted string, we need to unquote it
                                                val unquoted = result.substring(1, result.length - 1)
                                                    .replace("\\\"", "\"")
                                                    .replace("\\\\", "\\")
                                                unquoted
                                            } else {
                                                result
                                            }

                                            val jsonArray = JSONArray(jsonString)
                                            val links = mutableSetOf<String>()
                                            for (i in 0 until jsonArray.length()) {
                                                val link = jsonArray.getString(i)
                                                if (link.startsWith("http")) {
                                                    links.add(link)
                                                }
                                            }
                                            mediaLinks = links
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    isLoading = false
                                }
                            }
                        }
                        webView = this
                    }
                },
                update = {
                    webView = it
                },
                modifier = Modifier.size(0.dp) // Hidden
            )
        }
    }
}
