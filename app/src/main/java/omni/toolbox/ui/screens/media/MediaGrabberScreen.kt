package omni.toolbox.ui.screens.media

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
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.PlayArrow
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
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONArray

@Composable
fun MediaGrabberScreen(navController: NavHostController, initialUrl: String? = null) {
    var urlInput by remember { mutableStateOf(initialUrl ?: "") }
    var urlToLoad by remember { mutableStateOf(initialUrl ?: "") }
    var mediaLinks by remember { mutableStateOf(setOf<String>()) }
    var selectedLinks by remember { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var useExternalDownloader by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(urlToLoad, webView) {
        if (urlToLoad.isNotEmpty() && webView != null) {
            mediaLinks = emptySet()
            selectedLinks = emptySet()
            isLoading = true
            webView?.loadUrl(urlToLoad)
        }
    }

    fun downloadLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        if (useExternalDownloader) {
            // Try to force external downloader if possible, or just use general VIEW
            // Some managers catch specific intent extras
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        navController.context.startActivity(intent)
    }

    ToolScreen(
        title = "Media Grabber",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { useExternalDownloader = !useExternalDownloader }) {
                Icon(
                    if (useExternalDownloader) Icons.Default.DownloadDone else Icons.Default.SystemUpdateAlt,
                    contentDescription = "Toggle External Downloader",
                    tint = if (useExternalDownloader) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
            if (mediaLinks.isNotEmpty()) {
                IconButton(onClick = {
                    val linksToDownload = if (selectedLinks.isNotEmpty()) selectedLinks else mediaLinks
                    linksToDownload.forEach { downloadLink(it) }
                }) {
                    Icon(Icons.Default.DownloadForOffline, contentDescription = "Download All")
                }
                IconButton(onClick = {
                    selectedLinks = if (selectedLinks.size == mediaLinks.size) emptySet() else mediaLinks
                }) {
                    Icon(
                        if (selectedLinks.size == mediaLinks.size) Icons.Default.Deselect else Icons.Default.SelectAll,
                        contentDescription = "Select All"
                    )
                }
                IconButton(onClick = {
                    val linksToCopy = if (selectedLinks.isNotEmpty()) selectedLinks else mediaLinks
                    val allLinks = linksToCopy.joinToString("\n")
                    clipboardManager.setText(AnnotatedString(allLinks))
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Links")
                }
                IconButton(onClick = {
                    mediaLinks = emptySet()
                    selectedLinks = emptySet()
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Results")
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

            if (urlToLoad.contains("youtube.com") || urlToLoad.contains("youtu.be")) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val intent = Intent(context, omni.toolbox.service.YoutubeForegroundService::class.java).apply {
                            putExtra("videoUrl", urlToLoad)
                        }
                        context.startService(intent)
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Play in Background")
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
                    items(mediaLinks.toList(), key = { it }) { link ->
                        val isSelected = selectedLinks.contains(link)
                        ElevatedCard(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedLinks = if (isSelected) selectedLinks - link else selectedLinks + link
                                },
                            colors = if (isSelected) CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.elevatedCardColors(),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = link,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
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
                                        Icons.Default.Download,
                                        contentDescription = "Download",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(4.dp)
                                            .clickable { downloadLink(link) },
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

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
                                                if (absolute.startsWith('http')) links.add(absolute);
                                            } catch(e) {}
                                        }

                                        function scan(root) {
                                            var imgs = root.getElementsByTagName('img');
                                            for (var i = 0; i < imgs.length; i++) {
                                                addUrl(imgs[i].src);
                                                addUrl(imgs[i].dataset.src);
                                                addUrl(imgs[i].getAttribute('srcset')?.split(' ')[0]);
                                            }
                                            var videos = root.getElementsByTagName('video');
                                            for (var i = 0; i < videos.length; i++) {
                                                addUrl(videos[i].src);
                                                addUrl(videos[i].poster);
                                                var sources = videos[i].getElementsByTagName('source');
                                                for(var j=0; j<sources.length; j++) addUrl(sources[j].src);
                                            }
                                            // Instagram / TikTok / Pinterest / Twitter / Reddit specific
                                            if (location.href.includes('instagram.com') || location.href.includes('tiktok.com') ||
                                                location.href.includes('pinterest.com') || location.href.includes('reddit.com') ||
                                                location.href.includes('twitter.com') || location.href.includes('x.com')) {

                                                var all = root.querySelectorAll('img, video, img.H_j, source, a[href$=".jpg"], a[href$=".png"], a[href$=".mp4"], div[style*="background-image"]');
                                                all.forEach(el => {
                                                    addUrl(el.src || el.poster || el.currentSrc || el.href);
                                                    if(el.style.backgroundImage) {
                                                        var url = el.style.backgroundImage.slice(4, -1).replace(/"/g, "");
                                                        addUrl(url);
                                                    }
                                                });

                                                // Regex based extraction for embedded media
                                                var html = root.innerHTML;
                                                var imgRegex = /https?:\/\/[^"'\s]+\.(?:jpg|jpeg|png|webp|gif)/g;
                                                var vidRegex = /https?:\/\/[^"'\s]+\.(?:mp4|webm|m3u8)/g;
                                                var match;
                                                while ((match = imgRegex.exec(html)) !== null) addUrl(match[0]);
                                                while ((match = vidRegex.exec(html)) !== null) addUrl(match[0]);
                                            }

                                            // Additional social media detection (TikTok/Instagram specialized)
                                            var scripts = root.getElementsByTagName('script');
                                            for(var i=0; i<scripts.length; i++) {
                                                var content = scripts[i].innerHTML;
                                                if(content.includes('video_url')) {
                                                    var m = content.match(/"video_url":"([^"]+)"/);
                                                    if(m) addUrl(m[1].replace(/\\u0026/g, '&'));
                                                }
                                                if(content.includes('display_url')) {
                                                    var m = content.match(/"display_url":"([^"]+)"/);
                                                    if(m) addUrl(m[1].replace(/\\u0026/g, '&'));
                                                }
                                            }
                                        }
                                        scan(document);
                                        return JSON.stringify(Array.from(links));
                                    })();
                                """.trimIndent()

                                view?.evaluateJavascript(script) { result ->
                                    if (result != null && result != "null") {
                                        try {
                                            val jsonString = if (result.startsWith("\"") && result.endsWith("\"")) {
                                                result.substring(1, result.length - 1).replace("\\\"", "\"").replace("\\\\", "\\")
                                            } else result
                                            val jsonArray = JSONArray(jsonString)
                                            val links = mutableSetOf<String>()
                                            for (i in 0 until jsonArray.length()) {
                                                val link = jsonArray.getString(i)
                                                if (link.startsWith("http")) links.add(link)
                                            }
                                            mediaLinks = links
                                        } catch (e: Exception) {}
                                    }
                                    isLoading = false
                                }
                            }
                        }
                        webView = this
                    }
                },
                update = { webView = it },
                modifier = Modifier.size(1.dp).alpha(0f)
            )
        }
    }
}
