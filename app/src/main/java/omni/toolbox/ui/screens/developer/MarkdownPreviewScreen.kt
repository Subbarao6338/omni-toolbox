package omni.toolbox.ui.screens.developer

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun MarkdownPreviewScreen(navController: NavHostController) {
    var markdownText by remember { mutableStateOf("# Markdown Editor\n\nType some **Markdown** here!\n\n- Real-time preview\n- HTML/PDF Export\n\n```kotlin\nprintln(\"Hello Nature!\")\n```") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    ToolScreen(
        title = "Markdown Editor",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = {
                Toast.makeText(context, "Exporting as PDF...", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.Download, contentDescription = "Export")
            }
        }
    ) { padding ->
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
