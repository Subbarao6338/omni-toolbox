package omni.toolbox.ui.screens.utility

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONArray
import org.json.JSONObject

data class MetaTag(val name: String, val content: String)

@Composable
fun MetaTagScreen(navController: NavHostController) {
    var urlInput by remember { mutableStateOf("") }
    var urlToLoad by remember { mutableStateOf("") }
    var metaTags by remember { mutableStateOf(listOf<MetaTag>()) }
    var isLoading by remember { mutableStateOf(false) }
    var webView: WebView? by remember { mutableStateOf(null) }

    ToolScreen(
        title = "Metatag Analyzer",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Website URL") },
                    placeholder = { Text("example.com") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val url = if (urlInput.startsWith("http")) urlInput else "https://$urlInput"
                    urlToLoad = url
                    isLoading = true
                    webView?.loadUrl(url)
                }) {
                    Text("Analyze")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (metaTags.isNotEmpty()) {
                Text("Detected Metatags", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(metaTags) { tag ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(tag.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Text(tag.content, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else if (!isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Enter a URL to see its SEO metatags")
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
                                        var results = [];
                                        var metas = document.getElementsByTagName('meta');
                                        for (var i = 0; i < metas.length; i++) {
                                            var name = metas[i].getAttribute('name') || metas[i].getAttribute('property');
                                            var content = metas[i].getAttribute('content');
                                            if (name && content) {
                                                results.push({name: name, content: content});
                                            }
                                        }
                                        var title = document.title;
                                        if (title) results.unshift({name: 'title', content: title});
                                        return JSON.stringify(results);
                                    })();
                                """.trimIndent()

                                view?.evaluateJavascript(script) { result ->
                                    if (result != null && result != "null") {
                                        try {
                                            val jsonString = if (result.startsWith("\"") && result.endsWith("\"")) {
                                                result.substring(1, result.length - 1).replace("\\\"", "\"").replace("\\\\", "\\")
                                            } else result
                                            val jsonArray = JSONArray(jsonString)
                                            val tags = mutableListOf<MetaTag>()
                                            for (i in 0 until jsonArray.length()) {
                                                val obj = jsonArray.getJSONObject(i)
                                                tags.add(MetaTag(obj.getString("name"), obj.getString("content")))
                                            }
                                            metaTags = tags
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
