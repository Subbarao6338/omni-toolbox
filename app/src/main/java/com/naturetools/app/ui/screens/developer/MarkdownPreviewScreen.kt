package com.naturetools.app.ui.screens.developer

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun MarkdownPreviewScreen(navController: NavHostController) {
    var markdownText by remember { mutableStateOf("# Markdown Preview\n\nType some **Markdown** here to see it rendered!\n\n- List item 1\n- List item 2\n\n```kotlin\nval hello = \"world\"\n```") }
    var selectedTab by remember { mutableIntStateOf(0) }

    ToolScreen(title = "Markdown Preview", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Edit") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Preview") },
                    icon = { Icon(Icons.Default.Visibility, contentDescription = null) }
                )
            }

            if (selectedTab == 0) {
                OutlinedTextField(
                    value = markdownText,
                    onValueChange = { markdownText = it },
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    placeholder = { Text("Enter markdown...") }
                )
            } else {
                MarkdownRenderer(markdownText)
            }
        }
    }
}

@Composable
fun MarkdownRenderer(markdown: String) {
    // We'll use a simple WebView with marked.js from a CDN for rendering
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; padding: 16px; line-height: 1.6; }
                pre { background: #f4f4f4; padding: 10px; border-radius: 5px; overflow-x: auto; }
                code { font-family: monospace; }
                img { max-width: 100%; }
                blockquote { border-left: 4px solid #ccc; padding-left: 16px; margin-left: 0; color: #666; }
            </style>
        </head>
        <body>
            <div id="content"></div>
            <script>
                document.getElementById('content').innerHTML = marked.parse(`${markdown.replace("`", "\\`").replace("$", "\\$")}`);
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        },
        modifier = Modifier.fillMaxSize()
    )
}
