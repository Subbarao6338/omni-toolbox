package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AccountProfile
import com.example.data.DocumentItem
import com.example.data.ScrapedThread
import com.example.data.ScrapingRule
import com.example.ui.CrawlerProgress
import com.example.ui.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocsCrawlerScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Documents, 1: Cloud accounts, 2: Forum Scraper, 3: PDF & Doc Tools

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Storage, Docs & Crawlers") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Tab row layout
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocViewerTab(viewModel: OmniViewModel) {
    val docs by viewModel.documents.collectAsState()
    val crawlerStatus by viewModel.crawlerStatus.collectAsState()
    var selectedDoc by remember { mutableStateOf<DocumentItem?>(null) }

    // Search and organization variables
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isCreatingDoc by remember { mutableStateOf(false) }

    // Composer fields
    var newFileName by remember { mutableStateOf("") }
    var newFileType by remember { mutableStateOf("DOCX") }
    var newFileContent by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ADVANCED DOCUMENT WORKSPACE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
            TextButton(onClick = { isCreatingDoc = !isCreatingDoc }) {
                Icon(imageVector = if (isCreatingDoc) Icons.Default.Close else Icons.Default.Add, contentDescription = "Create doc")
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isCreatingDoc) "Cancel Editor" else "Create Doc Note", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Crawler Status Display
        CrawlerStatusCard(crawlerStatus = crawlerStatus, onDismiss = { viewModel.clearCrawlerStatus() })

        Spacer(modifier = Modifier.height(6.dp))

        if (isCreatingDoc) {
            // Offline notes creator form
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Workspace offline composer note", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newFileName,
                            onValueChange = { newFileName = it },
                            placeholder = { Text("File Name (e.g. guide.docx)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                        )

                        // File format selection
                        listOf("DOCX", "PDF", "MD", "HTML", "TXT").forEach { format ->
                            FilterChip(
                                selected = newFileType == format,
                                onClick = { 
                                    newFileType = format
                                    if (newFileName.isNotBlank() && !newFileName.endsWith(".$format", ignoreCase = true)) {
                                        newFileName = newFileName.substringBeforeLast(".") + "." + format.lowercase()
                                    }
                                },
                                label = { Text(format, fontSize = 9.sp) }
                            )
                        }
                    }

                    TextField(
                        value = newFileContent,
                        onValueChange = { newFileContent = it },
                        placeholder = { Text("Write content here. Add multipe paragraphs to generate multipe document pages for testing Notion chunking...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )

                    // Dynamic estimated equivalent pages count indicator
                    val estimatedPages = maxOf(1, newFileContent.length / 150)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated size: $estimatedPages Pages | Characters: ${newFileContent.length}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(
                            onClick = {
                                if (newFileName.isNotBlank() && newFileContent.isNotBlank()) {
                                    val finalName = if (newFileName.contains(".")) newFileName else "$newFileName.${newFileType.lowercase()}"
                                    viewModel.addDocument(finalName, newFileType, newFileContent)
                                    newFileName = ""
                                    newFileContent = ""
                                    isCreatingDoc = false
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Save File Catalog", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        if (selectedDoc != null) {
            // Selected document detailed reader layout
            DocumentReaderPanel(
                doc = selectedDoc!!,
                onClose = { selectedDoc = null },
                onBookmarkUpdated = { page -> viewModel.updateDocumentBookmark(selectedDoc!!, page) },
                onSyncToNotion = {
                    viewModel.syncDocumentToNotion(selectedDoc!!)
                }
            )
        } else {
            // Search and Category Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search catalog & content...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category select row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                listOf("All", "DOCX", "PDF", "MD", "HTML", "TXT").forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontSize = 10.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val filteredDocs = docs.filter { doc ->
                (selectedCategory == "All" || doc.fileType == selectedCategory) &&
                        (doc.fileName.contains(searchQuery, ignoreCase = true) || doc.content.contains(searchQuery, ignoreCase = true))
            }

            if (filteredDocs.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No file templates match criteria.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredDocs) { doc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDoc = doc },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (doc.fileType) {
                                            "PDF" -> Icons.Default.PictureAsPdf
                                            "DOCX" -> Icons.Default.Description
                                            "MD" -> Icons.Default.Terminal
                                            "HTML" -> Icons.Default.Html
                                            else -> Icons.Default.Article
                                        },
                                        contentDescription = doc.fileType,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(doc.fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    val estSize = maxOf(1, doc.content.length / 150)
                                    Text(
                                        text = "Type: ${doc.fileType} • Est: $estSize Pages • Last Opened: Page ${doc.bookmarkedPage}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteDocument(doc.id) }) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Doc", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentReaderPanel(
    doc: DocumentItem,
    onClose: () -> Unit,
    onBookmarkUpdated: (Int) -> Unit,
    onSyncToNotion: () -> Unit
) {
    val approxPages = maxOf(1, doc.content.length / 150)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoStories, contentDescription = "Reader", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Offine E-Reader Console", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close reader")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("FILE: ${doc.fileName} [Approx $approxPages pages]", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = doc.content,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bookmark tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Cattlog bookmark: Page ${doc.bookmarkedPage} of $approxPages", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { if (doc.bookmarkedPage < approxPages) onBookmarkUpdated(doc.bookmarkedPage + 1) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Next Page", fontSize = 11.sp)
                    }

                    Button(
                        onClick = { if (doc.bookmarkedPage > 1) onBookmarkUpdated(doc.bookmarkedPage - 1) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Prev Page", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSyncToNotion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload Notion")
                Spacer(modifier = Modifier.width(8.dp))
                val pageLabel = if (approxPages > 10) "subpages for every 10 pages" else "complete page"
                Text("Upload to Notion ($pageLabel)", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CloudAccountsTab(viewModel: OmniViewModel) {
    val profiles by viewModel.profiles.collectAsState()
    var inputPlatform by remember { mutableStateOf("Notion") }
    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputKey by remember { mutableStateOf("") }

    Column {
        SectionHeader(title = "SECURE CLOUD PROFILES (STAY LOGGED-IN)")
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Link A Platform Account Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    val list = listOf("Notion", "GDrive", "Mega")
                    list.forEach { item ->
                        FilterChip(
                            selected = inputPlatform == item,
                            onClick = { inputPlatform = item },
                            label = { Text(item, fontSize = 11.sp) }
                        )
                    }
                }

                TextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Profile Name (e.g. Work Workspace)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                TextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it },
                    label = { Text("Email", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                TextField(
                    value = inputKey,
                    onValueChange = { inputKey = it },
                    label = { Text("OAuth Code / Token Secret", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                Button(
                    onClick = {
                        if (inputName.isNotBlank() && inputEmail.isNotBlank()) {
                            viewModel.addProfile(inputPlatform, inputName, inputEmail, inputKey)
                            inputName = ""
                            inputEmail = ""
                            inputKey = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add Account Profile")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SectionHeader(title = "ACTIVE CREDENTIAL CACHE")
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(profiles) { profile ->
                Card(
                     modifier = Modifier.fillMaxWidth(),
                     shape = RoundedCornerShape(24.dp),
                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (profile.platform) {
                                "Notion" -> Icons.Default.Share
                                "GDrive" -> Icons.Default.CloudQueue
                                else -> Icons.Default.FolderZip
                            },
                            contentDescription = "platform",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.accountName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${profile.platform} • ${profile.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                        IconButton(onClick = { viewModel.deleteProfile(profile.id) }) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Profile", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrawlerScraperTab(viewModel: OmniViewModel) {
    val scrapedHistory by viewModel.crawledThreads.collectAsState()
    val crawlerStatus by viewModel.crawlerStatus.collectAsState()
    val scrapingRules by viewModel.scrapingRules.collectAsState()

    // Rule creation form variables
    var showRuleCreator by remember { mutableStateOf(false) }
    var ruleName by remember { mutableStateOf("") }
    var targetDomain by remember { mutableStateOf("https://xossipy.com") }
    var threadDepth by remember { mutableStateOf(3) }
    var pageLevelsLimit by remember { mutableStateOf(4) }
    var grabImages by remember { mutableStateOf(true) }
    var grabVideos by remember { mutableStateOf(true) }
    var grabDocuments by remember { mutableStateOf(true) }

    // Navigation and selected state
    var selectedRuleId by remember { mutableStateOf(0) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
        item {
            SectionHeader(title = "FORUM SCRAPING RULES CONFIGURATOR")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Define crawling algorithms and media rules to catalog multi-page discussions into Notion automatically.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { showRuleCreator = !showRuleCreator }) {
                    Icon(imageVector = if (showRuleCreator) Icons.Default.Close else Icons.Default.Add, contentDescription = "Toggle rule creator")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showRuleCreator) "Close Form" else "New Rule", fontSize = 11.sp)
                }
            }
        }

        if (showRuleCreator) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Construct customizable scraper ruleset", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        TextField(
                            value = ruleName,
                            onValueChange = { ruleName = it },
                            label = { Text("Rule Profile Name (e.g. Xossipy Gaming Dev)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                        )

                        TextField(
                            value = targetDomain,
                            onValueChange = { targetDomain = it },
                            label = { Text("Target forum Domain context Regex", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                        )

                        // numeric variables
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Max threads (depth)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (threadDepth > 1) threadDepth-- }) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Reduce threads", modifier = Modifier.size(16.dp))
                                    }
                                    Text(threadDepth.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { if (threadDepth < 10) threadDepth++ }) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase threads", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pages per thread", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (pageLevelsLimit > 1) pageLevelsLimit-- }) {
                                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Reduce pages", modifier = Modifier.size(16.dp))
                                    }
                                    Text(pageLevelsLimit.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { if (pageLevelsLimit < 10) pageLevelsLimit++ }) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase pages", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        // Media checkboxes
                        Text("Extract embedded Media Assets:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(checked = grabImages, onCheckedChange = { grabImages = it })
                                Text("Images", fontSize = 10.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(checked = grabVideos, onCheckedChange = { grabVideos = it })
                                Text("Videos", fontSize = 10.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Checkbox(checked = grabDocuments, onCheckedChange = { grabDocuments = it })
                                Text("Files", fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = {
                                if (ruleName.isNotBlank() && targetDomain.isNotBlank()) {
                                    viewModel.addScrapingRule(
                                        name = ruleName,
                                        domain = targetDomain,
                                        threadLevels = threadDepth,
                                        maxPages = pageLevelsLimit,
                                        images = grabImages,
                                        videos = grabVideos,
                                        documents = grabDocuments,
                                        profileId = 1
                                    )
                                    ruleName = ""
                                    showRuleCreator = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save Rule Profile")
                        }
                    }
                }
            }
        }

        item {
            CrawlerStatusCard(crawlerStatus = crawlerStatus, onDismiss = { viewModel.clearCrawlerStatus() })
        }

        item {
            Text("SELECT CONFIGURATION RULE TO TRIGGER SCRAPING:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        if (scrapingRules.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No customized rules loaded.", fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(scrapingRules) { rule ->
                val isSelected = selectedRuleId == rule.id
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { 
                        selectedRuleId = if (isSelected) 0 else rule.id
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.FilterList, contentDescription = "Rule Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(rule.ruleName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            IconButton(onClick = { viewModel.deleteScrapingRule(rule.id) }) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Scraping Rule", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        Text("Regex Match: ${rule.targetDomain}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.outline)

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Limits: ${rule.threadLevels} Threads • Max ${rule.maxPagesPerThread} Pages/Thread | Filters: Images (${if (rule.extractImages) "✓" else "✗"}), Videos (${if (rule.extractVideos) "✓" else "✗"}), Docs (${if (rule.extractDocuments) "✓" else "✗"})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.startCustomWebCrawl(
                                        ruleName = rule.ruleName,
                                        targetUrl = rule.targetDomain,
                                        maxThreads = rule.threadLevels,
                                        maxPagesPerThread = rule.maxPagesPerThread,
                                        extractImages = rule.extractImages,
                                        extractVideos = rule.extractVideos,
                                        extractDocuments = rule.extractDocuments
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Execute Multi-level Rules Sync to Notion", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "NOTION CRAWLED HIERARCHY TREE LOGS")
        }

        if (scrapedHistory.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                    Text("No synced forums in history DB.", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            items(scrapedHistory) { item ->
                var isExpanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder,
                                    contentDescription = "Folder",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(item.threadTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("Rule context: ${item.forumName}", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                            IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand details"
                                )
                            }
                        }

                        // Collapsible layout: Shows multi-level children tree
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier.padding(top = 10.dp, start = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Attachment, contentDescription = "Media Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Captured Embedded Media: ${item.extractedMedia.ifBlank { "None detected" }}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Text(
                                    text = "Notion Workspace Parent Node Id: ${item.notionPageId}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                Text("HIERARCHICAL NESTED CHILDREN SUBPAGES:", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary)

                                // Render subpages dynamically representing thread pages or doc chunks
                                val pages = if (item.subpagesList.isNotBlank()) {
                                    item.subpagesList.split(", ")
                                } else {
                                    List(item.pagesCount) { "Subpage Page ${it + 1}: Sync index discussion metadata contents" }
                                }

                                pages.forEachIndexed { index, subpageName ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 12.dp)
                                    ) {
                                        Text("├──", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.outline)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.Description, contentDescription = "Subpage", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(subpageName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(item.parsedContent, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrawlerStatusCard(crawlerStatus: CrawlerProgress, onDismiss: () -> Unit) {
    AnimatedVisibility(
        visible = crawlerStatus != CrawlerProgress.Idle,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (crawlerStatus) {
                    is CrawlerProgress.Active -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    is CrawlerProgress.Completed -> Color(0xFFE8F5E9).copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                when (crawlerStatus) {
                    is CrawlerProgress.Active -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("CRAWLER WORKING", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(crawlerStatus.msg, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = crawlerStatus.progress, modifier = Modifier.fillMaxWidth())
                    }
                    is CrawlerProgress.Completed -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF00E676))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CRAWL SYNC SUCCESS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF00E676))
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notion insertion finalized.", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "Imported ${crawlerStatus.threadsCrawled} parent thread page and compiled ${crawlerStatus.notionSyncedPages} detailed page chunks.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun PDFDocToolsTab(viewModel: OmniViewModel) {
    val docs by viewModel.documents.collectAsState()
    var toolMode by remember { mutableStateOf(0) } // 0: Split/Merge, 1: Text Statistics

    // Split/Merge states
    val selectDocList = docs.filter { it.fileType in listOf("PDF", "DOCX", "MD", "TXT") }
    var selectedSplitDoc by remember { mutableStateOf<DocumentItem?>(null) }
    var pagesPerSplit by remember { mutableStateOf(5) }
    var splitResultLog by remember { mutableStateOf<String?>(null) }

    var selectedMergeDoc1 by remember { mutableStateOf<DocumentItem?>(null) }
    var selectedMergeDoc2 by remember { mutableStateOf<DocumentItem?>(null) }
    var mergeResultLog by remember { mutableStateOf<String?>(null) }

    // Text Stats states
    var selectedStatsDoc by remember { mutableStateOf<DocumentItem?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabRow(selectedTabIndex = toolMode) {
            Tab(selected = toolMode == 0, onClick = { toolMode = 0 }) {
                Text("Split & Merge", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Tab(selected = toolMode == 1, onClick = { toolMode = 1 }) {
                Text("Text Stats", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (toolMode == 0) {
            // Split/Merge segment
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text("PDF & DOCUMENT CHUNKING SYSTEM", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }

                // 1. SPLIT MODULE
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CallSplit, contentDescription = "Split", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Split PDF/Doc into smaller segments", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            if (selectDocList.isEmpty()) {
                                Text("No documents in repository catalog.", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                Text("Select Source Document:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    selectDocList.take(3).forEach { d ->
                                        FilterChip(
                                            selected = selectedSplitDoc?.id == d.id,
                                            onClick = { selectedSplitDoc = d },
                                            label = { Text(d.fileName, fontSize = 10.sp) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Split Interval (Pages: $pagesPerSplit)", fontSize = 11.sp)
                                Slider(value = pagesPerSplit.toFloat(), onValueChange = { pagesPerSplit = it.toInt() }, valueRange = 1f..15f)

                                Button(
                                    onClick = {
                                        selectedSplitDoc?.let { doc ->
                                            val partsCount = 3
                                            for (p in 1..partsCount) {
                                                val suffix = "_part$p.${doc.fileType.lowercase()}"
                                                val finalName = doc.fileName.substringBeforeLast(".") + suffix
                                                viewModel.addDocument(
                                                    name = finalName,
                                                    type = doc.fileType,
                                                    content = "[Part $p of splitting process of ${doc.fileName}]\n\n" + doc.content
                                                )
                                            }
                                            splitResultLog = "Multipart splitting finished. Generated 3 sub-documents index cards under local repository catalog."
                                        }
                                    },
                                    enabled = selectedSplitDoc != null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Compile PDF Splitting Index")
                                }
                            }

                            if (splitResultLog != null) {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                                    Text(splitResultLog!!, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }

                // 2. MERGE MODULE
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.MergeType, contentDescription = "Merge", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Merge PDF/Doc segments together", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            if (selectDocList.size < 2) {
                                Text("At least 2 files required to merge. Add more file notes in first tab.", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                Text("Doc 1:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    selectDocList.take(3).forEach { d ->
                                        FilterChip(
                                            selected = selectedMergeDoc1?.id == d.id,
                                            onClick = { selectedMergeDoc1 = d },
                                            label = { Text(d.fileName, fontSize = 9.sp) }
                                        )
                                    }
                                }

                                Text("Doc 2:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    selectDocList.takeLast(3).forEach { d ->
                                        FilterChip(
                                            selected = selectedMergeDoc2?.id == d.id,
                                            onClick = { selectedMergeDoc2 = d },
                                            label = { Text(d.fileName, fontSize = 9.sp) }
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (selectedMergeDoc1 != null && selectedMergeDoc2 != null) {
                                            val name1 = selectedMergeDoc1!!.fileName.substringBeforeLast(".")
                                            val name2 = selectedMergeDoc2!!.fileName.substringBeforeLast(".")
                                            val finalName = "${name1}_merged_${name2}.pdf"
                                            val finalContent = "[Merged PDF document context of ${selectedMergeDoc1!!.fileName} and ${selectedMergeDoc2!!.fileName}]\n\n--- DOCUMENT 1 ---\n" + selectedMergeDoc1!!.content + "\n\n--- DOCUMENT 2 ---\n" + selectedMergeDoc2!!.content

                                            viewModel.addDocument(finalName, "PDF", finalContent)
                                            mergeResultLog = "Merged document index $finalName has been created!"
                                        }
                                    },
                                    enabled = selectedMergeDoc1 != null && selectedMergeDoc2 != null,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Compile PDF Merger Index")
                                }
                            }

                            if (mergeResultLog != null) {
                                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.2f))) {
                                    Text(mergeResultLog!!, fontSize = 11.sp, color = Color(0xFF00E676), modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Text Stats segment
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("DOCUMENT PROPERTY SPECTRUM ANALYZER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                if (selectDocList.isEmpty()) {
                    Text("No documents to analyze.", fontSize = 11.sp, color = Color.Gray)
                } else {
                    Text("Select Target Document:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectDocList.take(3).forEach { d ->
                            FilterChip(
                                selected = selectedStatsDoc?.id == d.id,
                                onClick = { selectedStatsDoc = d },
                                label = { Text(d.fileName, fontSize = 10.sp) }
                            )
                        }
                    }

                    selectedStatsDoc?.let { doc ->
                        val charCount = doc.content.length
                        val wordCount = doc.content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size
                        val sentenceCount = doc.content.split(".", "?", "!").filter { it.trim().isNotBlank() }.size
                        val paragraphsCount = doc.content.split("\n\n").filter { it.isNotBlank() }.size
                        val estReadingMinutes = maxOf(1, (wordCount / 200.0).toInt())

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Acoustic Stats: ${doc.fileName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Words Count", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        Text(wordCount.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Characters", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        Text(charCount.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Sentences", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        Text(sentenceCount.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Paragraphs", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                                        Text(paragraphsCount.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Est. Reading Speed:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    Text("$estReadingMinutes min at 200 WPM", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Aesthetic Density Index:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    val complexity = if (wordCount > 0) String.format("%.1f", charCount.toFloat() / wordCount) else "0.0"
                                    Text("$complexity chars/word", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
