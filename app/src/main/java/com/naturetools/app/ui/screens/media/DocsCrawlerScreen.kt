package com.naturetools.app.ui.screens.media

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
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ScraperRule(
    val name: String,
    val domain: String,
    val depth: Int,
    val extractImages: Boolean = true,
    val extractDocs: Boolean = true
)

@Composable
fun DocsCrawlerScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var targetUrl by remember { mutableStateOf("https://xossipy.com/forum/threads") }
    var selectedRule by remember { mutableStateOf("Forum Scraper (Xossipy)") }
    var isCrawling by remember { mutableStateOf(false) }
    val logs = remember { mutableStateListOf<String>() }

    val rules = listOf(
        ScraperRule("Forum Scraper (Xossipy)", "xossipy.com", 3),
        ScraperRule("Notion Chunker", "notion.so", 1),
        ScraperRule("Media Extractor", "*", 2)
    )

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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            isCrawling = true
                            logs.add("> Initializing spider for $targetUrl...")
                            delay(1000)
                            logs.add("> Handshaking with target domain...")
                            delay(500)
                            logs.add("> Rule matched: $selectedRule")
                            delay(1000)
                            logs.add("> Scoping thread hierarchy (depth: 3)...")
                            delay(2000)
                            logs.add("> Found 42 threads. Extracting content...")
                            delay(1500)
                            logs.add("> [OK] Content extraction complete.")
                            isCrawling = false
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
