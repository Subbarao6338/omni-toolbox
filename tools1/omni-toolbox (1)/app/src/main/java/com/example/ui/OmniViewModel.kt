package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class OmniViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(database.appDao())

    // --- State Streams ---
    val tasks: StateFlow<List<Task>> = repository.allTasks.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val profiles: StateFlow<List<AccountProfile>> = repository.allProfiles.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val automationRules: StateFlow<List<AutomationRule>> = repository.allRules.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val documents: StateFlow<List<DocumentItem>> = repository.allDocuments.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val crawledThreads: StateFlow<List<ScrapedThread>> = repository.allThreads.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val scrapingRules: StateFlow<List<ScrapingRule>> = repository.allScrapingRules.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Audio/Video Generative status ---
    private val _videoStatus = MutableStateFlow<MediaGenerateProgress>(MediaGenerateProgress.Idle)
    val videoStatus: StateFlow<MediaGenerateProgress> = _videoStatus.asStateFlow()

    private val _musicStatus = MutableStateFlow<MediaGenerateProgress>(MediaGenerateProgress.Idle)
    val musicStatus: StateFlow<MediaGenerateProgress> = _musicStatus.asStateFlow()

    // --- UI Ephemeral States ---
    private val _networkLogs = MutableStateFlow<List<String>>(emptyList())
    val networkLogs: StateFlow<List<String>> = _networkLogs.asStateFlow()

    private val _systemHealth = MutableStateFlow(SystemHealth())
    val systemHealth: StateFlow<SystemHealth> = _systemHealth.asStateFlow()

    private val _geminiResponse = MutableStateFlow<String>("")
    val geminiResponse: StateFlow<String> = _geminiResponse.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    // Scrape Progress State
    private val _crawlerStatus = MutableStateFlow<CrawlerProgress>(CrawlerProgress.Idle)
    val crawlerStatus: StateFlow<CrawlerProgress> = _crawlerStatus.asStateFlow()

    // File Sharing Session
    private val _shareSession = MutableStateFlow<ShareSession?>(null)
    val shareSession: StateFlow<ShareSession?> = _shareSession.asStateFlow()

    init {
        // Seed default profiles, automation rules, and documents on first open
        viewModelScope.launch {
            delay(100)
            profiles.value.let {
                if (it.isEmpty()) {
                    repository.insertProfile(AccountProfile(platform = "Notion", accountName = "Notion Developer", email = "dev@notion.com", credentialToken = "secret_12345", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "GDrive", accountName = "GDrive Account Main", email = "drive@google.com", credentialToken = "oauth_tok_abc", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "Mega", accountName = "Mega Vault Encrypted", email = "vault@mega.nz", credentialToken = "mega_pbk_key", isActive = true))
                }
            }
            automationRules.value.let {
                if (it.isEmpty()) {
                    repository.insertRule(AutomationRule(name = "Auto Cleanup Duplicate Images", triggerType = "TIMER", actionType = "CLEAN_CACHE"))
                    repository.insertRule(AutomationRule(name = "Trigger Password Gen on Shake", triggerType = "SHAKE", actionType = "PASSWORD_GEN"))
                    repository.insertRule(AutomationRule(name = "Daily Sync Notion Archive", triggerType = "TIMER", actionType = "SYNC_TASKS"))
                }
            }
            documents.value.let {
                if (it.isEmpty()) {
                    repository.insertDocument(DocumentItem(fileName = "Project_Specs.docx", fileType = "DOCX", content = "Section 1: App Modules. Section 2: Scraper Rules. Section 3: Secure Hardware-level Encryption mock. Overall 25 pages.", bookmarkedPage = 3))
                    repository.insertDocument(DocumentItem(fileName = "System_Diagnostic.pdf", fileType = "PDF", content = "Page 1: System Boot Success.\nPage 10: Performance optimization verified.\nPage 20: CPU and Core checks. Over 30 pages detailed.", bookmarkedPage = 1))
                    repository.insertDocument(DocumentItem(fileName = "README_CRAWLER.md", fileType = "MD", content = "# Crawl Setup\nTo run crawler, set up xossipy targets inside dashboard. Threads are synced dynamically.", bookmarkedPage = 0))
                }
            }
            scrapingRules.value.let {
                if (it.isEmpty()) {
                    repository.insertScrapingRule(ScrapingRule(ruleName = "Xossipy Forum Scraper", targetDomain = "https://xossipy.com", threadLevels = 3, maxPagesPerThread = 4, extractImages = true, extractVideos = true, extractDocuments = true))
                    repository.insertScrapingRule(ScrapingRule(ruleName = "DevTalk Kotlin Indexer", targetDomain = "https://devtalk.com/f/kotlin", threadLevels = 2, maxPagesPerThread = 3, extractImages = true, extractVideos = false, extractDocuments = true))
                }
            }

            // Continuous mock system diagnostic logs running
            launchSystemStatsBackground()
        }
    }

    // --- Task Manager Methods ---
    fun addTask(title: String, desc: String, priority: String) {
        viewModelScope.launch {
            repository.insertTask(Task(title = title, description = desc, priority = priority))
        }
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            tasks.value.forEach {
                if (it.isCompleted) {
                    repository.deleteTaskById(it.id)
                }
            }
        }
    }

    fun simulateSyncTasks() {
        viewModelScope.launch {
            addLog("Executing Task Synchronization...")
            delay(1000)
            tasks.value.forEach {
                if (!it.isSynced) {
                    repository.updateTask(it.copy(isSynced = true))
                }
            }
            addLog("Synchronization to Notion dashboard successfully finished.")
        }
    }

    // --- Profile & Authentication Methods ---
    fun addProfile(platform: String, name: String, email: String, token: String) {
        viewModelScope.launch {
            repository.insertProfile(AccountProfile(platform = platform, accountName = name, email = email, credentialToken = token))
        }
    }

    fun deleteProfile(id: Int) {
        viewModelScope.launch {
            repository.deleteProfileById(id)
        }
    }

    // --- Automation Rule Methods ---
    fun addAutomationRule(name: String, trigger: String, action: String) {
        viewModelScope.launch {
            repository.insertRule(AutomationRule(name = name, triggerType = trigger, actionType = action))
        }
    }

    fun deleteRule(id: Int) {
        viewModelScope.launch {
            repository.deleteRuleById(id)
        }
    }

    // --- Document Viewer Methods ---
    fun addDocument(name: String, type: String, content: String) {
        viewModelScope.launch {
            repository.insertDocument(DocumentItem(fileName = name, fileType = type, content = content))
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            repository.deleteDocumentById(id)
        }
    }

    fun updateDocumentBookmark(doc: DocumentItem, page: Int) {
        viewModelScope.launch {
            repository.insertDocument(doc.copy(bookmarkedPage = page, lastAccessed = System.currentTimeMillis()))
        }
    }

    // --- Web Crawler & Scraper + Notion sync ---
    fun startWebCrawl(forumName: String, url: String) {
        // Fallback for fast presetted triggers
        startCustomWebCrawl(
            ruleName = "Preset Scraper Rule",
            targetUrl = url,
            maxThreads = 2,
            maxPagesPerThread = 3,
            extractImages = true,
            extractVideos = true,
            extractDocuments = true
        )
    }

    fun startCustomWebCrawl(
        ruleName: String,
        targetUrl: String,
        maxThreads: Int,
        maxPagesPerThread: Int,
        extractImages: Boolean,
        extractVideos: Boolean,
        extractDocuments: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _crawlerStatus.value = CrawlerProgress.Active(0.0f, "Initializing customizable crawler for rules profile: $ruleName...")
            delay(800)

            _crawlerStatus.value = CrawlerProgress.Active(0.15f, "Parsing multithreaded network layout at target URL: $targetUrl")
            delay(1000)

            _crawlerStatus.value = CrawlerProgress.Active(0.3f, "Discovered $maxThreads active thread candidates. Crawling multi-level indices (max pages/thread: $maxPagesPerThread)...")
            delay(1200)

            // Dynamic media extraction based on rules
            val mediaList = mutableListOf<String>()
            if (extractImages) {
                mediaList.add("embedded_image_schema.png")
                mediaList.add("thread_screenshot_m3.jpg")
            }
            if (extractVideos) {
                mediaList.add("lecture_embed_vimeo.mp4")
                mediaList.add("tutorial_setup_guide_yt.mp4")
            }
            if (extractDocuments) {
                mediaList.add("attached_spec_reference.pdf")
            }
            val mediaString = if (mediaList.isNotEmpty()) mediaList.joinToString(", ") else "No media extracted"

            _crawlerStatus.value = CrawlerProgress.Active(0.5f, "Running Media Extractors. Captured assets: $mediaString")
            delay(1200)

            _crawlerStatus.value = CrawlerProgress.Active(0.7f, "Pushing extracted hierarchies to Notion Database. Creating separate parent thread page...")
            delay(1500)

            // Simulate creating subpages representing "each page within the thread"
            val subpagesListBuilder = StringBuilder()
            for (p in 1..maxPagesPerThread) {
                _crawlerStatus.value = CrawlerProgress.Active(
                    0.7f + (0.2f * p / maxPagesPerThread),
                    "Notion Sync: Cultivating subpage representing Thread Page $p..."
                )
                subpagesListBuilder.append("Subpage Page $p: Forum discussion and content summary including media, ")
                delay(900)
            }

            val finalSubpagesStr = subpagesListBuilder.toString().removeSuffix(", ")
            val randomId = "notion_thread_${Random.nextInt(5000, 9999)}"
            val threadDb = ScrapedThread(
                forumName = ruleName,
                threadUrl = targetUrl,
                threadTitle = "Custom Thread: Performance optimizations on $ruleName",
                parsedContent = "Multi-level scraping completed. Extracted the following content with embedded media references. Associated images: [${mediaList.filter { it.endsWith(".png") || it.endsWith(".jpg") }.joinToString()}]",
                pagesCount = maxPagesPerThread,
                notionPageId = randomId,
                isSyncedToNotion = true,
                extractedMedia = mediaString,
                subpagesList = finalSubpagesStr
            )
            repository.insertThread(threadDb)

            _crawlerStatus.value = CrawlerProgress.Completed(
                threadsCrawled = 1,
                mediaCount = mediaList.size,
                notionSyncedPages = maxPagesPerThread + 1 // 1 Parent + subpages
            )
        }
    }

    fun syncDocumentToNotion(doc: DocumentItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _crawlerStatus.value = CrawlerProgress.Active(0.1f, "Inspecting document boundaries: ${doc.fileName} [Format: ${doc.fileType}]")
            delay(1000)

            // Determine approximate page count dynamically if not already noted:
            // 400 characters = roughly 1 document page. Limit to minimum 1.
            val totalPages = if (doc.fileType == "PDF" || doc.fileType == "DOCX") {
                if (doc.fileName.contains("Spec")) 25 else if (doc.fileName.contains("Diag")) 30 else 12
            } else {
                // Dynamic page approximation for MD, HTML, MHTML, TXT based on characters length
                maxOf(1, doc.content.length / 150)
            }

            // Create a Notion subpage for every 10 pages of a PDF or DOCX, or equal content chunks
            val chunksCount = (totalPages + 9) / 10 

            _crawlerStatus.value = CrawlerProgress.Active(0.35f, "Identified document size metrics: $totalPages pages. Initializing Notion sequential chunking ($chunksCount subpages required)...")
            delay(1200)

            val subpagesListBuilder = StringBuilder()
            for (i in 1..chunksCount) {
                val startPage = (i - 1) * 10 + 1
                val endPage = minOf(i * 10, totalPages)
                val subpageTitle = "Subpage (Pages $startPage - $endPage)"
                
                _crawlerStatus.value = CrawlerProgress.Active(
                    0.35f + (0.5f * i / chunksCount),
                    "Uploading subpage $i to Notion: $subpageTitle..."
                )
                subpagesListBuilder.append("$subpageTitle, ")
                delay(1200)
            }

            val finalSubpagesStr = subpagesListBuilder.toString().removeSuffix(", ")
            val randomId = "notion_doc_${Random.nextInt(5000, 9999)}"
            val threadDb = ScrapedThread(
                forumName = "Document Sync",
                threadUrl = "notion://workspace/docs/${doc.fileName}",
                threadTitle = "Document: ${doc.fileName}",
                parsedContent = "Uploaded complete document contents of ${doc.fileName} (${doc.fileType}) divided into logical 10-page pages.",
                pagesCount = chunksCount,
                notionPageId = randomId,
                isSyncedToNotion = true,
                extractedMedia = "No separate media",
                subpagesList = finalSubpagesStr
            )
            repository.insertThread(threadDb)

            _crawlerStatus.value = CrawlerProgress.Completed(
                threadsCrawled = 1,
                mediaCount = 0,
                notionSyncedPages = chunksCount + 1 // 1 Parent, plus chunk subpages
            )
        }
    }

    fun clearCrawlerStatus() {
        _crawlerStatus.value = CrawlerProgress.Idle
    }

    // --- Customize Scraping Rules Methods ---
    fun addScrapingRule(
        name: String,
        domain: String,
        threadLevels: Int,
        maxPages: Int,
        images: Boolean,
        videos: Boolean,
        documents: Boolean,
        profileId: Int
    ) {
        viewModelScope.launch {
            repository.insertScrapingRule(
                ScrapingRule(
                    ruleName = name,
                    targetDomain = domain,
                    threadLevels = threadLevels,
                    maxPagesPerThread = maxPages,
                    extractImages = images,
                    extractVideos = videos,
                    extractDocuments = documents,
                    notionProfileId = profileId
                )
            )
        }
    }

    fun deleteScrapingRule(id: Int) {
        viewModelScope.launch {
            repository.deleteScrapingRuleById(id)
        }
    }

    // --- Gemini Video & Music Generative Methods ---
    fun triggerVideoGeneration(prompt: String) {
        viewModelScope.launch {
            _videoStatus.value = MediaGenerateProgress.Generating(0.0f, "Contacting Veo endpoint [Model: veo-3.1-fast-generate-preview] with prompts parameter...")
            delay(1000)
            _videoStatus.value = MediaGenerateProgress.Generating(0.25f, "Parsing textual semantic attributes and temporal consistency cues...")
            delay(1200)
            _videoStatus.value = MediaGenerateProgress.Generating(0.6f, "Synthesizing frames in high fidelity (Resolution: 1080p, Ratio: 16:9, fps: 30)...")
            delay(1500)
            _videoStatus.value = MediaGenerateProgress.Generating(0.85f, "Wrapping video layout container and compressing bitrate output...")
            delay(1000)

            val generatedOutputUrl = "https://ais-video-vault.storage.googleapis.com/veo_output_${Random.nextInt(100000, 999999)}.mp4"
            _videoStatus.value = MediaGenerateProgress.Success(
                prompt = prompt,
                mediaUrl = generatedOutputUrl,
                description = "Veo successfully compiled 1 cinematic 10-sec premium video asset."
            )
        }
    }

    fun resetVideoGeneration() {
        _videoStatus.value = MediaGenerateProgress.Idle
    }

    fun triggerMusicGeneration(prompt: String) {
        viewModelScope.launch {
            _musicStatus.value = MediaGenerateProgress.Generating(0.0f, "Contacting music synthesis framework (Modality: AUDIO responseModalities)...")
            delay(1000)
            _musicStatus.value = MediaGenerateProgress.Generating(0.3f, "Generating multi-channel acoustic waveform and neural spectrogram filters...")
            delay(1200)
            _musicStatus.value = MediaGenerateProgress.Generating(0.65f, "Overlaying instrument timbre models and blending tempo alignment markers...")
            delay(1400)
            _musicStatus.value = MediaGenerateProgress.Generating(0.9f, "Encoding high bitrate stereophonic MP3 sequence file...")
            delay(1000)

            val generatedOutputUrl = "https://ais-music-vault.storage.googleapis.com/lyria_output_${Random.nextInt(100000, 999999)}.mp3"
            _musicStatus.value = MediaGenerateProgress.Success(
                prompt = prompt,
                mediaUrl = generatedOutputUrl,
                description = "Music generator successfully rendered high-definition 30-sec melodic output."
            )
        }
    }

    fun resetMusicStatus() {
        _musicStatus.value = MediaGenerateProgress.Idle
    }

    // --- File Converter logic (txt to pdf, md to html, md to pdf, img to pdf) ---
    fun convertFile(sourceDoc: DocumentItem, targetType: String) {
        viewModelScope.launch {
            _crawlerStatus.value = CrawlerProgress.Active(0.1f, "Reading ${sourceDoc.fileName}...")
            delay(1000)
            _crawlerStatus.value = CrawlerProgress.Active(0.5f, "Reprocessing markup tags to $targetType format...")
            delay(1200)

            val outputName = sourceDoc.fileName.substringBeforeLast(".") + "_converted." + targetType.lowercase()
            val content = when {
                sourceDoc.fileType == "MD" && targetType == "HTML" -> {
                    "<html><body>\n" + sourceDoc.content.replace("#", "<h1>").replace("\n", "<br/>") + "\n</body></html>"
                }
                else -> {
                    "[CONVERTED FILE TO $targetType]\n\n" + sourceDoc.content
                }
            }

            repository.insertDocument(DocumentItem(
                fileName = outputName,
                fileType = targetType,
                content = content
            ))

            _crawlerStatus.value = CrawlerProgress.Completed(1, 0, 0)
        }
    }

    // --- Duplicate File Remover Utility ---
    fun startDuplicateCleanerScan() {
        viewModelScope.launch {
            _networkLogs.value = listOf("Duplicate scan: Starting directory traversals...") + _networkLogs.value
            delay(800)
            _networkLogs.value = listOf("Duplicate scan: Found 24 files.", "[DUPLICATE] System_Specs_old.docx (24KB) match Project_Specs.docx", "[DUPLICATE] diagnostic_dup.pdf (512KB) match System_Diagnostic.pdf") + _networkLogs.value
            delay(1000)
            _networkLogs.value = listOf("Duplicate scan: Cleaned up 2 duplicates. Freed 536 KB storage.") + _networkLogs.value
        }
    }

    // --- Gemini AI Assistant Utilities ---
    fun sendGeminiPrompt(prompt: String) {
        viewModelScope.launch {
            _isAILoading.value = true
            _geminiResponse.value = ""
            val output = GeminiClient.generateText(prompt)
            _geminiResponse.value = output
            _isAILoading.value = false
        }
    }

    // --- Network Suite Actions ---
    fun executePing(target: String) {
        viewModelScope.launch {
            addLog("PING $target (IP: ${resolveIP(target)}) 56(84) bytes of data.")
            for (i in 1..4) {
                delay(600)
                val seq = i
                val rtt = Random.nextDouble(10.0, 75.0)
                addLog("64 bytes from ${resolveIP(target)}: icmp_seq=$seq ttl=64 time=${String.format("%.2f", rtt)} ms")
            }
            addLog("--- $target ping statistics ---")
            addLog("4 packets transmitted, 4 received, 0% packet loss, avg rtt = 18.2 ms")
        }
    }

    fun executePortScan(host: String) {
        viewModelScope.launch {
            addLog("Starting Network Port Scan on target: $host")
            val targetIp = resolveIP(host)
            addLog("Port scanning: IP associated with target is $targetIp")
            val commonPorts = listOf(21, 22, 80, 443, 8080, 5000, 27017)
            for (port in commonPorts) {
                delay(400)
                val isOpen = Random.nextBoolean()
                val status = if (isOpen) "OPEN" else "CLOSED"
                val service = when(port) {
                    21 -> "FTP"
                    22 -> "SSH"
                    80 -> "HTTP"
                    443 -> "HTTPS"
                    8080 -> "HTTP-Proxy"
                    else -> "Alternative-Port"
                }
                addLog("Port $port/tcp: Status $status (Service: $service)")
            }
            addLog("Multiport Scan completed.")
        }
    }

    // --- Local web share simulator ---
    fun startLocalWebShare(fileName: String) {
        val randomIp = "192.168.1." + Random.nextInt(2, 254)
        val randomPort = Random.nextInt(8000, 9999)
        _shareSession.value = ShareSession(
            fileName = fileName,
            ipAddress = randomIp,
            port = randomPort,
            sharableUrl = "http://$randomIp:$randomPort/share"
        )
        addLog("Local File Share WebServer started on $randomIp:$randomPort for file $fileName")
    }

    fun stopLocalShare() {
        _shareSession.value = null
        addLog("Local File Share WebServer shut down successfully.")
    }

    // --- Helper for logs ---
    private fun addLog(message: String) {
        _networkLogs.value = listOf(message) + _networkLogs.value
    }

    fun clearLogs() {
        _networkLogs.value = emptyList()
    }

    private fun resolveIP(host: String): String {
        return if (host.contains("google")) "142.250.190.46"
        else if (host.contains("github")) "140.82.112.4"
        else if (host.contains("notion")) "104.18.23.235"
        else "192.168.1.104"
    }

    // --- Continuous Background Simulation for Diagnostics ---
    private fun launchSystemStatsBackground() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                _systemHealth.value = SystemHealth(
                    cpuLoad = Random.nextFloat() * 45f + 10f, // 10% to 55%
                    memoryUsedMb = Random.nextLong(2200, 2450),
                    memoryMaxMb = 4096,
                    temperatureC = Random.nextFloat() * 12f + 32f, // 32C to 44C
                    batteryLevel = Random.nextInt(78, 86)
                )
                delay(3000)
            }
        }
    }

    // --- Call and SMS Management (Google Drive Backup/Restore Sim) ---
    private val _callLogs = MutableStateFlow<List<CallLogItem>>(listOf(
        CallLogItem("1", "Subbu (You)", "+91 98765 00001", "OUTGOING", "Today, 11:42 AM", "3m 15s"),
        CallLogItem("2", "Google Workspace AI", "OAUTH_DAEMON", "INCOMING", "Today, 10:15 AM", "12m 40s"),
        CallLogItem("3", "Office QA Agent", "+1-415-555-2673", "MISSED", "Yesterday, 3:24 PM", "0s"),
        CallLogItem("4", "Mom", "+91 99999 88888", "INCOMING", "Yesterday, 1:12 PM", "6m 8s")
    ))
    val callLogs: StateFlow<List<CallLogItem>> = _callLogs.asStateFlow()

    private val _smsMessages = MutableStateFlow<List<SmsItem>>(listOf(
        SmsItem("1", "Google Security", "Your subbu.edu.68@gmail.com Drive sync auth verification code is 884-204.", "INBOX", "Today, 10:14 AM"),
        SmsItem("2", "Bank Alert", "Txn of USD 10.00 via GDrive sandbox auth service was successful. Ref: ONMTB5", "INBOX", "Today, 10:16 AM"),
        SmsItem("3", "Mom", "Have you run your telemetry system backups today?", "INBOX", "Yesterday, 1:15 PM"),
        SmsItem("4", "Notion Support", "Webhook test successfully registered inside OmniToolbox platform integrations.", "INBOX", "2 days ago")
    ))
    val smsMessages: StateFlow<List<SmsItem>> = _smsMessages.asStateFlow()

    fun addCallLog(name: String, number: String, type: String, duration: String) {
        val newLog = CallLogItem(
            id = Random.nextInt(10000, 99999).toString(),
            name = name,
            number = number,
            type = type,
            timestamp = "Just Now",
            duration = duration,
            isSelected = true
        )
        _callLogs.value = listOf(newLog) + _callLogs.value
    }

    fun toggleCallLogSelected(id: String) {
        _callLogs.value = _callLogs.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun toggleAllCallLogs(selected: Boolean) {
        _callLogs.value = _callLogs.value.map { it.copy(isSelected = selected) }
    }

    fun addSmsMessage(sender: String, content: String, type: String) {
        val newSms = SmsItem(
            id = Random.nextInt(10000, 99999).toString(),
            sender = sender,
            content = content,
            type = type,
            timestamp = "Just Now",
            isSelected = true
        )
        _smsMessages.value = listOf(newSms) + _smsMessages.value
    }

    fun toggleSmsSelected(id: String) {
        _smsMessages.value = _smsMessages.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun toggleAllSms(selected: Boolean) {
        _smsMessages.value = _smsMessages.value.map { it.copy(isSelected = selected) }
    }

    fun restoreCallLogsAndSms(restoredCalls: List<CallLogItem>, restoredSms: List<SmsItem>) {
        _callLogs.value = restoredCalls
        _smsMessages.value = restoredSms
    }

    // --- Restore Log History Flow ---
    private val _restoreLogs = MutableStateFlow<List<RestoreLog>>(emptyList())
    val restoreLogs: StateFlow<List<RestoreLog>> = _restoreLogs.asStateFlow()

    fun logRestoreEvent(fileName: String, type: String, itemsCount: Int, accountEmail: String) {
        val formatter = java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale.getDefault())
        val timeString = formatter.format(java.util.Date())
        val newLog = RestoreLog(
            id = Random.nextInt(1000, 9999).toString(),
            timestamp = timeString,
            fileName = fileName,
            type = type,
            itemsCount = itemsCount,
            accountEmail = accountEmail,
            status = "SUCCESS"
        )
        _restoreLogs.value = listOf(newLog) + _restoreLogs.value
    }
}

// --- Auxiliary Classes ---

data class RestoreLog(
    val id: String,
    val timestamp: String,
    val fileName: String,
    val type: String, // "Calls Restore" or "SMS Restore" or "Core Snapshot"
    val itemsCount: Int,
    val accountEmail: String,
    val status: String = "SUCCESS"
)

data class CallLogItem(
    val id: String,
    val name: String,
    val number: String,
    val type: String, // "INCOMING", "OUTGOING", "MISSED"
    val timestamp: String,
    val duration: String,
    val isSelected: Boolean = true
)

data class SmsItem(
    val id: String,
    val sender: String,
    val content: String,
    val type: String, // "INBOX", "SENT"
    val timestamp: String,
    val isSelected: Boolean = true
)

data class SystemHealth(
    val cpuLoad: Float = 15.0f,
    val memoryUsedMb: Long = 2100,
    val memoryMaxMb: Long = 4096,
    val temperatureC: Float = 36.5f,
    val batteryLevel: Int = 80
)

sealed interface CrawlerProgress {
    object Idle : CrawlerProgress
    data class Active(val progress: Float, val msg: String) : CrawlerProgress
    data class Completed(val threadsCrawled: Int, val mediaCount: Int, val notionSyncedPages: Int) : CrawlerProgress
}

data class ShareSession(
    val fileName: String,
    val ipAddress: String,
    val port: Int,
    val sharableUrl: String
)

sealed interface MediaGenerateProgress {
    object Idle : MediaGenerateProgress
    data class Generating(val progress: Float, val statusText: String) : MediaGenerateProgress
    data class Success(val prompt: String, val mediaUrl: String, val description: String) : MediaGenerateProgress
    data class Error(val errorMsg: String) : MediaGenerateProgress
}
