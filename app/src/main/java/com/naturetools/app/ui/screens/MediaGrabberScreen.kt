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
import androidx.compose.material.icons.filled.Delete
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
                    mediaLinks = emptySet()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Results")
                }
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

                                        function addUrl(url) {
                                            if (!url) return;
                                            try {
                                                var absolute = new URL(url, document.baseURI).href;
                                                // Filter out small icons or common tracking pixels
                                                if (absolute.startsWith('http') && !absolute.includes('pixel') && !absolute.includes('analytics')) {
                                                    links.add(absolute);
                                                }
                                            } catch(e) {}
                                        }

                                        // Robust extraction logic
                                        function scan(root) {
                                            var imgs = root.getElementsByTagName('img');
                                            for (var i = 0; i < imgs.length; i++) {
                                                addUrl(imgs[i].src);
                                                addUrl(imgs[i].dataset.src);
                                                addUrl(imgs[i].dataset.lazySrc);
                                                addUrl(imgs[i].dataset.original);
                                                addUrl(imgs[i].getAttribute('data-src'));
                                                addUrl(imgs[i].getAttribute('data-lazy-src'));
                                                if (imgs[i].srcset) {
                                                    imgs[i].srcset.split(',').forEach(s => addUrl(s.trim().split(' ')[0]));
                                                }
                                            }

                                            var pictures = root.getElementsByTagName('picture');
                                            for (var i = 0; i < pictures.length; i++) {
                                                var sources = pictures[i].getElementsByTagName('source');
                                                for (var j = 0; j < sources.length; j++) {
                                                    if (sources[j].srcset) {
                                                        sources[j].srcset.split(',').forEach(s => addUrl(s.trim().split(' ')[0]));
                                                    }
                                                }
                                            }

                                            var videos = root.getElementsByTagName('video');
                                            for (var i = 0; i < videos.length; i++) {
                                                addUrl(videos[i].src);
                                                addUrl(videos[i].poster);
                                                var sources = videos[i].getElementsByTagName('source');
                                                for (var j = 0; j < sources.length; j++) addUrl(sources[j].src);
                                            }

                                            var audios = root.getElementsByTagName('audio');
                                            for (var i = 0; i < audios.length; i++) {
                                                addUrl(audios[i].src);
                                                var sources = audios[i].getElementsByTagName('source');
                                                for (var j = 0; j < sources.length; j++) addUrl(sources[j].src);
                                            }

                                            var anchors = root.getElementsByTagName('a');
                                            for (var i = 0; i < anchors.length; i++) {
                                                var href = anchors[i].href;
                                                if (href && (href.match(/\.(jpg|jpeg|png|gif|webp|mp4|webm|ogg|mp3|wav|zip|pdf|apk)$/i) || href.includes('drive.google.com/file'))) {
                                                    addUrl(href);
                                                }
                                            }

                                            // Iframes (YouTube, Vimeo, etc)
                                            var iframes = root.getElementsByTagName('iframe');
                                            for (var i = 0; i < iframes.length; i++) {
                                                addUrl(iframes[i].src);
                                            }
                                        }

                                        scan(document);

                                        // Background images
                                        var allElements = document.getElementsByTagName('*');
                                        for (var i = 0; i < allElements.length; i++) {
                                            var bg = window.getComputedStyle(allElements[i]).backgroundImage;
                                            if (bg && bg !== 'none' && bg.includes('url')) {
                                                var urlMatch = bg.match(/url\(["']?([^"']+)["']?\)/);
                                                if (urlMatch && urlMatch[1]) addUrl(urlMatch[1]);
                                            }
                                        }

                                        // Metadata
                                        var metas = document.getElementsByTagName('meta');
                                        for (var i = 0; i < metas.length; i++) {
                                            var prop = metas[i].getAttribute('property') || metas[i].getAttribute('name');
                                            var content = metas[i].getAttribute('content');
                                            if (prop && (prop.includes('image') || prop.includes('video') || prop.includes('og:')) && content) {
                                                addUrl(content);
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
