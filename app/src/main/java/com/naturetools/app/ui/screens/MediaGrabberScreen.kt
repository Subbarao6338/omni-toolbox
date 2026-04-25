package com.naturetools.app.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
fun MediaGrabberScreen(navController: NavHostController, initialUrl: String? = null) {
    var urlInput by remember { mutableStateOf(initialUrl ?: "") }
    var urlToLoad by remember { mutableStateOf(initialUrl ?: "") }
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

    ToolScreen(
        title = "Media Grabber",
        onBack = { navController.popBackStack() },
        actions = {
            if (mediaLinks.isNotEmpty()) {
                IconButton(onClick = {
                    val allLinks = mediaLinks.joinToString("\n")
                    clipboardManager.setText(AnnotatedString(allLinks))
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy All")
                }
            }
        }
    ) { padding ->
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
                        ElevatedCard(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(link))
                                },
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = link,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                            RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Link",
                                        modifier = Modifier.size(20.dp).padding(2.dp),
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
                                        var links = new Set();
                                        var imgs = document.getElementsByTagName('img');
                                        for (var i = 0; i < imgs.length; i++) {
                                            if (imgs[i].src) links.add(imgs[i].src);
                                            if (imgs[i].srcset) {
                                                var srcset = imgs[i].srcset.split(',');
                                                srcset.forEach(s => {
                                                    var url = s.trim().split(' ')[0];
                                                    if (url) {
                                                        try { links.add(new URL(url, document.baseURI).href); } catch(e) {}
                                                    }
                                                });
                                            }
                                        }
                                        var videos = document.getElementsByTagName('video');
                                        for (var i = 0; i < videos.length; i++) {
                                            if (videos[i].src) links.add(videos[i].src);
                                            if (videos[i].poster) links.add(videos[i].poster);
                                            var sources = videos[i].getElementsByTagName('source');
                                            for (var j = 0; j < sources.length; j++) {
                                                if (sources[j].src) links.add(sources[j].src);
                                            }
                                        }
                                        var metas = document.getElementsByTagName('meta');
                                        for (var i = 0; i < metas.length; i++) {
                                            var prop = metas[i].getAttribute('property');
                                            var content = metas[i].getAttribute('content');
                                            if (prop && (prop === 'og:image' || prop === 'og:video' || prop === 'og:image:secure_url') && content) {
                                                try { links.add(new URL(content, document.baseURI).href); } catch(e) {}
                                            }
                                        }
                                        return JSON.stringify(Array.from(links));
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
                modifier = Modifier.size(1.dp).alpha(0f) // Small but not hidden from JS engine
            )
        }
    }
}
