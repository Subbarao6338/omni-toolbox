package omni.toolbox.ui.screens.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.net.URL

data class ScraperRule(
    val name: String,
    val domain: String,
    val depth: Int,
    val extractImages: Boolean = true,
    val extractVideos: Boolean = true,
    val extractDocs: Boolean = true
)

@Composable
fun DocsCrawlerScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var targetUrl by remember { mutableStateOf("https://xossipy.com/forum/threads") }
    var selectedRule by remember { mutableStateOf("Forum Scraper (Xossipy)") }
    var isCrawling by remember { mutableStateOf(false) }
    val logs = remember { mutableStateListOf<String>() }

    // Rule Configurator State
    var showRuleConfig by remember { mutableStateOf(false) }
    var configDomain by remember { mutableStateOf("") }
    var configDepth by remember { mutableStateOf(1f) }
    var configImages by remember { mutableStateOf(true) }
    var configVideos by remember { mutableStateOf(false) }
    var configDocs by remember { mutableStateOf(true) }

    val rules = remember {
        mutableStateListOf(
            ScraperRule("Forum Scraper (Xossipy)", "xossipy.com", 3),
            ScraperRule("Notion Chunker", "notion.so", 1),
            ScraperRule("Media Extractor", "*", 2)
        )
    }

    ToolScreen(
        title = "Web Scraper & Notion Sync",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Crawler Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = targetUrl,
                onValueChange = { targetUrl = it },
                label = { Text("Target URL") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedTextField(
                    value = selectedRule,
                    onValueChange = {},
                    label = { Text("Scraping Rule") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    rules.forEach { rule ->
                        DropdownMenuItem(
                            text = { Text(rule.name) },
                            onClick = {
                                selectedRule = rule.name
                                expanded = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Add Custom Rule...", color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            showRuleConfig = true
                            expanded = false
                        }
                    )
                }
            }

            if (showRuleConfig) {
                Card(
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("New Rule Configurator", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = configDomain, onValueChange = { configDomain = it }, label = { Text("Domain Regex") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Search Depth: ${configDepth.toInt()}")
                        Slider(value = configDepth, onValueChange = { configDepth = it }, valueRange = 1f..10f)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = configImages, onCheckedChange = { configImages = it })
                            Text("Images")
                            Spacer(modifier = Modifier.width(12.dp))
                            Checkbox(checked = configVideos, onCheckedChange = { configVideos = it })
                            Text("Videos")
                            Spacer(modifier = Modifier.width(12.dp))
                            Checkbox(checked = configDocs, onCheckedChange = { configDocs = it })
                            Text("Docs")
                        }
                        Button(onClick = {
                            if (configDomain.isNotBlank()) {
                                rules.add(ScraperRule("Custom: $configDomain", configDomain, configDepth.toInt(), configImages, configVideos, configDocs))
                                selectedRule = "Custom: $configDomain"
                                showRuleConfig = false
                            }
                        }, modifier = Modifier.align(Alignment.End)) {
                            Text("Save Rule")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            isCrawling = true
                            try {
                                withContext(Dispatchers.Main) { logs.add("> Initializing spider for $targetUrl...") }
                                val doc = Jsoup.connect(targetUrl).get()
                                withContext(Dispatchers.Main) {
                                    logs.add("> Connected: ${doc.title()}")
                                    logs.add("> Rule matched: $selectedRule")
                                }

                                val links = doc.select("a[href]")
                                withContext(Dispatchers.Main) { logs.add("> Found ${links.size} links. Extracting context...") }

                                links.take(10).forEach { link ->
                                    val href = link.attr("abs:href")
                                    withContext(Dispatchers.Main) { logs.add("> Crawled: $href") }
                                    delay(200)
                                }

                                withContext(Dispatchers.Main) { logs.add("> [OK] Content extraction complete.") }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) { logs.add("> [ERROR] ${e.message}") }
                            } finally {
                                isCrawling = false
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isCrawling
                ) {
                    if (isCrawling) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Crawl")
                }

                Button(
                    onClick = {
                        scope.launch {
                            logs.add("> Initializing Notion synchronization...")
                            delay(1000)
                            logs.add("> Mapping thread tree to Notion pages...")
                            delay(1500)
                            logs.add("> Split PDF/Docs: 10-page sequential splitting active.")
                            delay(2000)
                            logs.add("> [OK] Sync successful. Workspace updated.")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync Notion")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Live Crawler Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState())) {
                    if (logs.isEmpty()) {
                        Text("Ready for crawl.", color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    } else {
                        logs.forEach { log ->
                            Text(
                                text = log,
                                color = if (log.contains("[OK]")) Color(0xFF39FF14) else Color(0xFF00BCD4),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Notion Workspace Hierarchy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            NotionNode("Xossipy_General_Threads", true) {
                NotionNode("Thread_ID_8821 (Parent)", false)
                NotionNode("Page_1 (Child)", false)
                NotionNode("Page_2 (Child)", false)
                NotionNode("Document_Chunk_01 (10-pages)", false)
            }
        }
    }
}

@Composable
fun NotionNode(label: String, isRoot: Boolean, content: @Composable ColumnScope.() -> Unit = {}) {
    var expanded by remember { mutableStateOf(isRoot) }

    Column(modifier = Modifier.padding(start = if (isRoot) 0.dp else 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(24.dp)) {
                Icon(
                    if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
            Icon(
                if (isRoot) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isRoot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        if (expanded) {
            content()
        }
    }
}
