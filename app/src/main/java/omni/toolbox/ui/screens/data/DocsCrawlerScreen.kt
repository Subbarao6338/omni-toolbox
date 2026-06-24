package omni.toolbox.ui.screens.data

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.*

@Composable
fun DocsCrawlerScreen(navController: NavHostController, viewModel: OmniViewModel) {
    var activeTab by remember { mutableIntStateOf(0) }

    ToolScreen(
        title = "Storage, Docs & Crawlers",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = activeTab) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("Documents", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Cloud Accounts", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Web Scraper", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("PDF & Doc Tools", modifier = Modifier.padding(12.dp), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> DocViewerTab(viewModel)
                    1 -> CloudAccountsTab(viewModel)
                    2 -> CrawlerScraperTab(viewModel)
                    3 -> PDFDocToolsTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun DocViewerTab(viewModel: OmniViewModel) {
    val docs = viewModel.documents.value
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search documents...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(docs.filter { it.fileName.contains(searchQuery, ignoreCase = true) }) { doc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.fileName, fontWeight = FontWeight.Bold)
                            Text("${doc.fileType} • Page ${doc.bookmarkedPage}", fontSize = 11.sp, color = Color.Gray)
                        }
                        IconButton(onClick = { viewModel.syncDocumentToNotion(doc) }) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Sync to Notion")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CloudAccountsTab(viewModel: OmniViewModel) {
    val profiles = viewModel.profiles.value

    Column {
        Text("Active Cloud Profiles", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(profiles) { profile ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (profile.platform) {
                                "Notion" -> Icons.Default.Share
                                "GDrive" -> Icons.Default.CloudQueue
                                else -> Icons.Default.FolderZip
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.accountName, fontWeight = FontWeight.Bold)
                            Text("${profile.platform} • ${profile.email}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrawlerScraperTab(viewModel: OmniViewModel) {
    val scrapedHistory = viewModel.crawledThreads.value
    val scrapingRules = viewModel.scrapingRules.value
    val crawlerStatus by viewModel.crawlerStatus.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Scraping Rules", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (crawlerStatus != CrawlerProgress.Idle) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Crawler Active", fontWeight = FontWeight.Bold)
                    if (crawlerStatus is CrawlerProgress.Active) {
                        val active = crawlerStatus as CrawlerProgress.Active
                        Text(active.msg, fontSize = 11.sp)
                        LinearProgressIndicator(progress = { active.progress }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    } else if (crawlerStatus is CrawlerProgress.Completed) {
                        Text("Crawl Completed successfully.", fontSize = 11.sp)
                        Button(onClick = { viewModel.clearCrawlerStatus() }) { Text("Dismiss") }
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(scrapingRules) { rule ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(rule.ruleName, fontWeight = FontWeight.Bold)
                        Text(rule.targetDomain, fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            viewModel.startCustomWebCrawl(rule.ruleName, rule.targetDomain, rule.threadLevels, rule.maxPagesPerThread, rule.extractImages, rule.extractVideos, rule.extractDocuments)
                        }) {
                            Text("Run Crawler")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)); Text("Scraped History", fontWeight = FontWeight.Bold) }

            items(scrapedHistory) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.threadTitle, fontWeight = FontWeight.Bold)
                        Text(item.forumName, fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun PDFDocToolsTab(viewModel: OmniViewModel) {
    Column {
        Text("PDF & Document Tools", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CallSplit, contentDescription = null)
                    Text("Split PDF", fontSize = 12.sp)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MergeType, contentDescription = null)
                    Text("Merge PDF", fontSize = 12.sp)
                }
            }
        }
    }
}
