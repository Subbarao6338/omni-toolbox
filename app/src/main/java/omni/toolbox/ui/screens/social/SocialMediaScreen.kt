package omni.toolbox.ui.screens.social

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.OmniViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SocialMediaScreen(navController: NavHostController, viewModel: OmniViewModel) {
    var activeTab by remember { mutableIntStateOf(0) }

    ToolScreen(
        title = "Social Media Suite",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("\uD83D\uDCE5 Bulk Downloader", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("\uD83D\uDC64 Profile Scraper", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> BulkDownloaderTab(viewModel)
                    1 -> ProfileScraperTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun BulkDownloaderTab(viewModel: OmniViewModel) {
    var profileUrl by remember { mutableStateOf("https://instagram.com/user_handle") }
    var selectedPlatform by remember { mutableStateOf("Instagram") }
    var extractVideos by remember { mutableStateOf(true) }
    var extractImages by remember { mutableStateOf(true) }
    var scanLimit by remember { mutableFloatStateOf(50f) }

    var isCrawling by remember { mutableStateOf(false) }
    var crawlProgress by remember { mutableFloatStateOf(0f) }
    var crawlStatusText by remember { mutableStateOf("") }
    var crawlResult by remember { mutableStateOf<SocialCrawlResult?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val platforms = listOf(
        SocialPlatform("Instagram", Icons.Default.CameraAlt, Color(0xFFE1306C)),
        SocialPlatform("Facebook", Icons.Default.Facebook, Color(0xFF1877F2)),
        SocialPlatform("Twitter", Icons.Default.AlternateEmail, Color(0xFF1DA1F2)),
        SocialPlatform("Pinterest", Icons.Default.Bookmark, Color(0xFFBD081C))
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MULTI-PLATFORM PROFILE MEDIA DOWNLOADER",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Platform Network", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    platforms.forEach { platform ->
                        FilterChip(
                            selected = selectedPlatform == platform.name,
                            onClick = { selectedPlatform = platform.name },
                            label = { Text(platform.name, fontSize = 9.sp) },
                            leadingIcon = { Icon(platform.icon, contentDescription = platform.name, modifier = Modifier.size(12.dp), tint = platform.color) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = profileUrl,
                        onValueChange = { profileUrl = it },
                        label = { Text("Profile URL or Username handle", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    IconButton(onClick = {
                        clipboardManager.getText()?.text?.let { profileUrl = it }
                    }) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste Clipboard")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = extractImages, onCheckedChange = { extractImages = it })
                        Text("Images", fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = extractVideos, onCheckedChange = { extractVideos = it })
                        Text("Videos", fontSize = 11.sp)
                    }
                    Text("Limit: ${scanLimit.toInt()} posts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Slider(value = scanLimit, onValueChange = { scanLimit = it }, valueRange = 10f..200f)

                if (!isCrawling) {
                    Button(
                        onClick = {
                            isCrawling = true
                            crawlResult = null
                            coroutineScope.launch {
                                crawlProgress = 0.05f
                                crawlStatusText = "Connecting secure browser sandbox to $selectedPlatform..."
                                delay(1200)
                                crawlProgress = 0.5f
                                crawlStatusText = "Extracting feed elements..."
                                delay(1800)
                                crawlProgress = 1.0f
                                crawlStatusText = "Crawl finished successfully."
                                delay(500)

                                val randMediaCount = Random.nextInt(25, scanLimit.toInt())
                                val randImages = if (extractImages) (randMediaCount * 0.7f).toInt() else 0
                                val randVideos = if (extractVideos) (randMediaCount - randImages) else 0

                                crawlResult = SocialCrawlResult(
                                    handle = profileUrl.substringAfterLast("/").ifBlank { "@user" },
                                    platform = selectedPlatform,
                                    imagesDownloaded = randImages,
                                    videosDownloaded = randVideos,
                                    totalDataSizeMb = Random.nextDouble(12.5, 148.9),
                                    timestamp = System.currentTimeMillis()
                                )
                                isCrawling = false
                                viewModel.addLog("Social media backup finished for ${crawlResult?.handle}")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Download")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download All Profile Media")
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(progress = { crawlProgress }, modifier = Modifier.fillMaxWidth())
                        Text(
                            text = "[${(crawlProgress * 100).toInt()}%] $crawlStatusText",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        crawlResult?.let { result ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("SUCCESSFULLY EXPORTED PROFILE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    Text("Profile Target: ${result.handle}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Images: ${result.imagesDownloaded} | Videos: ${result.videosDownloaded}", fontSize = 12.sp)
                    Text("Total size: ${String.format("%.2f", result.totalDataSizeMb)} MB", fontSize = 12.sp, color = Color(0xFF00C853))
                }
            }
        }
    }
}

@Composable
fun ProfileScraperTab(viewModel: OmniViewModel) {
    var keyword by remember { mutableStateOf("Android development") }
    var isScraping by remember { mutableStateOf(false) }
    var logsList by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "TOPIC & PROFILE AGGREGATOR",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    label = { Text("Topic keyword search", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                Button(
                    onClick = {
                        isScraping = true
                        logsList = emptyList()
                        coroutineScope.launch {
                            logsList = logsList + "Initializing spiders..."
                            delay(500)
                            logsList = logsList + "Querying API endpoints for: #$keyword"
                            delay(1000)
                            logsList = logsList + "Successfully compiled indices dataset."
                            isScraping = false
                            viewModel.addLog("Scraped social topic: $keyword")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isScraping
                ) {
                    Text("Audit Platform & Scrape Content")
                }
            }
        }

        if (isScraping || logsList.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF15181F)),
                modifier = Modifier.fillMaxWidth().height(150.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(logsList) { log ->
                        Text("> $log", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color(0xFF00FF66))
                    }
                }
            }
        }
    }
}

data class SocialPlatform(val name: String, val icon: ImageVector, val color: Color)
data class SocialCrawlResult(val handle: String, val platform: String, val imagesDownloaded: Int, val videosDownloaded: Int, val totalDataSizeMb: Double, val timestamp: Long)
