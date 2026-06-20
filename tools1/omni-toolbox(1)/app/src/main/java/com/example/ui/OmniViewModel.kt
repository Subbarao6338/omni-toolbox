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
    val contacts: StateFlow<List<ContactInfo>> = repository.allContacts.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    // --- Screen Usage, Data Usage, Screen Unlocks, and Notifications States ---
    private val _screenTimeTodayMinutes = MutableStateFlow(228) // 3h 48m default
    val screenTimeTodayMinutes: StateFlow<Int> = _screenTimeTodayMinutes.asStateFlow()

    private val _screenLimitMinutes = MutableStateFlow(240) // 4 hours default
    val screenLimitMinutes: StateFlow<Int> = _screenLimitMinutes.asStateFlow()

    private val _topAppsScreenTime = MutableStateFlow(listOf(
        AppScreenTime("YouTube", "com.google.android.youtube", 85, "youtube"),
        AppScreenTime("Instagram", "com.instagram.android", 45, "instagram"),
        AppScreenTime("WhatsApp", "com.whatsapp", 35, "whatsapp"),
        AppScreenTime("Chrome", "com.android.chrome", 25, "chrome"),
        AppScreenTime("OmniToolbox", "com.example.omnitoolbox", 38, "omnitoolbox")
    ))
    val topAppsScreenTime: StateFlow<List<AppScreenTime>> = _topAppsScreenTime.asStateFlow()

    private val _screenUnlocksToday = MutableStateFlow(32)
    val screenUnlocksToday: StateFlow<Int> = _screenUnlocksToday.asStateFlow()

    private val _unlockEvents = MutableStateFlow(listOf(
        UnlockLogEvent("1", "11:24 AM", "Fingerprint"),
        UnlockLogEvent("2", "10:45 AM", "Face Unlock"),
        UnlockLogEvent("3", "09:12 AM", "Pin Pattern"),
        UnlockLogEvent("4", "08:31 AM", "Fingerprint"),
        UnlockLogEvent("5", "07:15 AM", "Smart Lock")
    ))
    val unlockEvents: StateFlow<List<UnlockLogEvent>> = _unlockEvents.asStateFlow()

    private val _unlocksByHour = MutableStateFlow(listOf(
        0, 0, 0, 0, 0, 0, 1, 2, 4, 3, 5, 2, 6, 2, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0
    ))
    val unlocksByHour: StateFlow<List<Int>> = _unlocksByHour.asStateFlow()

    private val _wifiDataUsedMb = MutableStateFlow(2450.5f) // 2.45 GB
    val wifiDataUsedMb: StateFlow<Float> = _wifiDataUsedMb.asStateFlow()

    private val _mobileDataUsedMb = MutableStateFlow(348.5f) // 348.5 MB
    val mobileDataUsedMb: StateFlow<Float> = _mobileDataUsedMb.asStateFlow()

    private val _mobileDataLimitMb = MutableStateFlow(500f) // 500 MB limit
    val mobileDataLimitMb: StateFlow<Float> = _mobileDataLimitMb.asStateFlow()

    private val _topAppsDataUsage = MutableStateFlow(listOf(
        AppDataUsage("Chrome", 1200f, 150f),
        AppDataUsage("YouTube", 800f, 120f),
        AppDataUsage("Instagram", 310f, 55f),
        AppDataUsage("WhatsApp", 100.5f, 20f),
        AppDataUsage("System Services", 40f, 3.5f)
    ))
    val topAppsDataUsage: StateFlow<List<AppDataUsage>> = _topAppsDataUsage.asStateFlow()

    private val _notificationsCountToday = MutableStateFlow(124)
    val notificationsCountToday: StateFlow<Int> = _notificationsCountToday.asStateFlow()

    private val _notificationLogs = MutableStateFlow(listOf(
        NotificationLogEvent("1", "WhatsApp", "Arun Kumar", "Hey, are we still debugging the crawler sync automation rules?", "Communication", "11:28 AM"),
        NotificationLogEvent("2", "System", "Battery Alert", "Battery level is healthy at 80% with performance boost enabled.", "System", "11:15 AM"),
        NotificationLogEvent("3", "Instagram", "Sarah Connor", "Liked your post about Notion workspace mapping backups.", "Social", "10:42 AM"),
        NotificationLogEvent("4", "Slack", "Alexander Graham", "Task synchronization completed with zero conflicts.", "Productivity", "09:35 AM"),
        NotificationLogEvent("5", "WhatsApp", "Mom", "Don't forget to take a break and stretch!", "Communication", "08:15 AM")
    ))
    val notificationLogs: StateFlow<List<NotificationLogEvent>> = _notificationLogs.asStateFlow()

    init {
        // Seed default profiles, automation rules, and documents on first open
        viewModelScope.launch {
            delay(100)
            profiles.value.let {
                if (it.isEmpty()) {
                    repository.insertProfile(AccountProfile(platform = "Notion", accountName = "Notion Developer", email = "dev@notion.com", credentialToken = "secret_12345", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "GDrive", accountName = "GDrive Account Main", email = "drive@google.com", credentialToken = "oauth_tok_abc", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "GDrive", accountName = "GDrive Share Folder", email = "subbu.edu.68@gmail.com", credentialToken = "oauth_tok_xyz", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "Mega", accountName = "Mega Primary Cloud", email = "vault@mega.nz", credentialToken = "mega_pbk_key_1", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "Mega", accountName = "Mega Backup Node", email = "subbu.backup@mega.co", credentialToken = "mega_pbk_key_2", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "OneDrive", accountName = "OneDrive Personal", email = "personal@outlook.com", credentialToken = "onedrive_ms_tok", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "OneDrive", accountName = "OneDrive Enterprise", email = "enterprise@cloud.com", credentialToken = "onedrive_corporate_tok", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "Nextcloud", accountName = "Nextcloud Lab Server", email = "subbu@local-nextcloud.internal", credentialToken = "nc_app_key_alpha", isActive = true))
                    repository.insertProfile(AccountProfile(platform = "Nextcloud", accountName = "Nextcloud Secure Storage", email = "admin@nextcloud.org", credentialToken = "nc_app_key_omega", isActive = true))
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
            contacts.value.let {
                if (it.isEmpty()) {
                    repository.insertContact(ContactInfo(name = "Subha Prasad", phone = "+91 98765 43210", email = "subbu.edu.68@gmail.com", address = "Chennai, Tamil Nadu, India", organization = "AI Studio Corp", groupName = "My Contacts", accountEmail = "subbu.edu.68@gmail.com"))
                    repository.insertContact(ContactInfo(name = "Alexander Graham", phone = "+1-555-0199", email = "alex@bell-labs.org", address = "Boston, MA", organization = "Bell Labs", groupName = "Work", accountEmail = "subbu.edu.68@gmail.com"))
                    repository.insertContact(ContactInfo(name = "Arun Kumar", phone = "+91 99988 77766", email = "arun@syncmail.com", address = "Bangalore, India", organization = "OmniTech", groupName = "My Contacts", accountEmail = "subbu.edu.68@gmail.com"))
                    repository.insertContact(ContactInfo(name = "Sarah Connor", phone = "+1-555-8000", email = "sarah@resistance.net", address = "Los Angeles, CA", organization = "Resistance", groupName = "Family", accountEmail = "subbu.edu.68@gmail.com"))
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

    // --- Contacts Methods ---
    fun addContact(name: String, phone: String, email: String, address: String = "", organization: String = "", groupName: String = "My Contacts", accountEmail: String = "subbu.edu.68@gmail.com") {
        viewModelScope.launch {
            repository.insertContact(ContactInfo(
                name = name, phone = phone, email = email, address = address,
                organization = organization, groupName = groupName, accountEmail = accountEmail
            ))
            addLog("Google Contacts API: Contact '$name' successfully created.")
        }
    }

    fun updateContact(contact: ContactInfo) {
        viewModelScope.launch {
            repository.updateContact(contact)
            addLog("Google Contacts API: Contact '${contact.name}' updated in directory.")
        }
    }

    fun deleteContact(id: Int, name: String) {
        viewModelScope.launch {
            repository.deleteContactById(id)
            addLog("Google Contacts API: Contact '$name' deleted from address book.")
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

    // --- Core Settings Dashboard States ---
    private val prefs = application.getSharedPreferences("omni_app_settings", android.content.Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "System") ?: "System")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _syncFrequency = MutableStateFlow(prefs.getString("sync_frequency", "1 Hour") ?: "1 Hour")
    val syncFrequency: StateFlow<String> = _syncFrequency.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _megaCryptKey = MutableStateFlow(prefs.getString("mega_crypt_key", "") ?: "")
    val megaCryptKey: StateFlow<String> = _megaCryptKey.asStateFlow()

    private val _onedriveToken = MutableStateFlow(prefs.getString("onedrive_token", "") ?: "")
    val onedriveToken: StateFlow<String> = _onedriveToken.asStateFlow()

    private val _nextcloudToken = MutableStateFlow(prefs.getString("nextcloud_token", "") ?: "")
    val nextcloudToken: StateFlow<String> = _nextcloudToken.asStateFlow()

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _themeMode.value = mode
        addLog("Theme preference updated: $mode")
    }

    fun setSyncFrequency(freq: String) {
        prefs.edit().putString("sync_frequency", freq).apply()
        _syncFrequency.value = freq
        addLog("Sync frequency updated to: $freq")
    }

    fun setGeminiApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key).apply()
        _geminiApiKey.value = key
        addLog("Gemini API key configuration synchronized offline.")
    }

    fun setMegaCryptKey(key: String) {
        prefs.edit().putString("mega_crypt_key", key).apply()
        _megaCryptKey.value = key
        addLog("MEGA cryptographic token database updated.")
    }

    fun setOnedriveToken(token: String) {
        prefs.edit().putString("onedrive_token", token).apply()
        _onedriveToken.value = token
        addLog("OneDrive workspace bearer token cached locally.")
    }

    fun setNextcloudToken(token: String) {
        prefs.edit().putString("nextcloud_token", token).apply()
        _nextcloudToken.value = token
        addLog("Nextcloud client app key successfully committed.")
    }

    // --- Custom simulation setters ---
    fun updateScreenLimit(minutes: Int) {
        _screenLimitMinutes.value = minutes
        addLog("Screen usage limit safety checkpoint updated to $minutes minutes.")
    }

    fun incrementScreenTime(minutes: Int) {
        val current = _screenTimeTodayMinutes.value
        val next = current + minutes
        _screenTimeTodayMinutes.value = next

        // Randomly assign minutes to a top app to simulate app screentime distribution
        val currentAppsList = _topAppsScreenTime.value
        val randomIndex = Random.nextInt(currentAppsList.size)
        val updatedAppsList = currentAppsList.mapIndexed { index, app ->
            if (index == randomIndex) {
                app.copy(usageMinutes = app.usageMinutes + minutes)
            } else app
        }
        _topAppsScreenTime.value = updatedAppsList

        addLog("Screen Time Simulation: Today's screentime cumulative metrics updated to $next mins.")
    }

    fun updateMobileDataLimit(mb: Float) {
        _mobileDataLimitMb.value = mb
        addLog("Mobile data daily budget policy optimized to ${mb.toInt()} MB.")
    }

    fun simulatedDataConsumption(mobileMb: Float, wifiMb: Float) {
        _mobileDataUsedMb.value = _mobileDataUsedMb.value + mobileMb
        _wifiDataUsedMb.value = _wifiDataUsedMb.value + wifiMb

        // Randomly distribute data consumption to apps
        val apps = _topAppsDataUsage.value
        val indexMobile = Random.nextInt(apps.size)
        val indexWifi = Random.nextInt(apps.size)
        val updatedApps = apps.mapIndexed { index, app ->
            val mUsed = if (index == indexMobile) mobileMb else 0f
            val wUsed = if (index == indexWifi) wifiMb else 0f
            app.copy(mobileUsedMb = app.mobileUsedMb + mUsed, wifiUsedMb = app.wifiUsedMb + wUsed)
        }
        _topAppsDataUsage.value = updatedApps

        addLog("Data Usage Simulation: Consumed +${mobileMb.toInt()}MB mobile, +${wifiMb.toInt()}MB Wi-Fi.")
    }

    fun simulateScreenUnlock() {
        val currentUnlocks = _screenUnlocksToday.value + 1
        _screenUnlocksToday.value = currentUnlocks

        val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        val timeNow = format.format(java.util.Date())
        val methods = listOf("Fingerprint", "Face Unlock", "Pin Pattern", "Smart Lock")
        val randomMethod = methods[Random.nextInt(methods.size)]

        val newEvent = UnlockLogEvent(
            id = Random.nextInt(1000, 9999).toString(),
            timestamp = timeNow,
            unlockMethod = randomMethod
        )
        _unlockEvents.value = listOf(newEvent) + _unlockEvents.value

        // Increment unlocks for the current hour
        val hourNow = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val currentDistribution = _unlocksByHour.value.toMutableList()
        currentDistribution[hourNow % 24] = currentDistribution[hourNow % 24] + 1
        _unlocksByHour.value = currentDistribution

        addLog("Terminal Unlock Daemon: Authenticated via Security Level: $randomMethod.")
    }

    fun simulateNotificationReceived(appName: String, sender: String, content: String, category: String) {
        _notificationsCountToday.value = _notificationsCountToday.value + 1

        val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        val timeNow = format.format(java.util.Date())

        val newLog = NotificationLogEvent(
            id = Random.nextInt(10000, 99999).toString(),
            appName = appName,
            sender = sender,
            content = content,
            category = category,
            timestamp = timeNow
        )
        _notificationLogs.value = listOf(newLog) + _notificationLogs.value

        addLog("Notification Interceptor Service: Captured dispatch from $appName ($category).")
    }

    fun clearNotificationLogs() {
        _notificationLogs.value = emptyList()
        _notificationsCountToday.value = 0
        addLog("Cleared all intercepted real-time notification buffer nodes.")
    }

    // --- Benchmarking State ---
    private val _isBenchmarking = MutableStateFlow(false)
    val isBenchmarking: StateFlow<Boolean> = _isBenchmarking.asStateFlow()

    private val _benchmarkProgress = MutableStateFlow(0f)
    val benchmarkProgress: StateFlow<Float> = _benchmarkProgress.asStateFlow()

    private val _benchmarkStatus = MutableStateFlow("Ready to profile system parameters.")
    val benchmarkStatus: StateFlow<String> = _benchmarkStatus.asStateFlow()

    private val _lastBenchmarkResult = MutableStateFlow<BenchmarkResult?>(null)
    val lastBenchmarkResult: StateFlow<BenchmarkResult?> = _lastBenchmarkResult.asStateFlow()

    private val _benchmarkHistory = MutableStateFlow<List<BenchmarkResult>>(listOf(
        BenchmarkResult("Pixel 8 Pro (Ref)", 9820, 11400, 8900, 9500, "Excellent Performance", "Yesterday"),
        BenchmarkResult("Galaxy S24 Ultra (Ref)", 10500, 12800, 9900, 11200, "Flagship Elite", "Yesterday"),
        BenchmarkResult("Pixel 7a (Ref)", 7400, 8100, 6800, 7100, "Good Performance", "Yesterday")
    ))
    val benchmarkHistory: StateFlow<List<BenchmarkResult>> = _benchmarkHistory.asStateFlow()

    fun runAllBenchmarks() {
        if (_isBenchmarking.value) return
        _isBenchmarking.value = true
        _benchmarkProgress.value = 0f
        addLog("PowerBench Daemon: Running real-time physics and computing operations to stress the hardware...")

        viewModelScope.launch(Dispatchers.Default) {
            // STEP 1: CPU Benchmark (Prime Search)
            _benchmarkStatus.value = "Stressing CPU Multi-Core Math engines (Finding prime distributions 0-35k)"
            _benchmarkProgress.value = 0.15f
            val cpuStartTime = System.currentTimeMillis()
            var mathCount = 0
            for (num in 2..35000) {
                var isPrime = true
                for (i in 2..kotlin.math.sqrt(num.toDouble()).toInt()) {
                    if (num % i == 0) { isPrime = false; break }
                }
                if (isPrime) mathCount++
            }
            val cpuElapsedTime = System.currentTimeMillis() - cpuStartTime
            val cpuScore = ((35000 / cpuElapsedTime.coerceAtLeast(1)) * 32).coerceIn(1500, 11500).toInt()
            _benchmarkProgress.value = 0.35f
            addLog("CPU Prime Test completed in ${cpuElapsedTime}ms. Score: $cpuScore")
            delay(500)

            // STEP 2: GPU Shader math logic (Fractal Calculations)
            _benchmarkStatus.value = "Simulating GPU Blitter Rendering (Calculating Mandelbrot grid 90x90 coords)"
            _benchmarkProgress.value = 0.5f
            val gpuStartTime = System.currentTimeMillis()
            var activePoints = 0
            for (x in 0..90) {
                for (y in 0..90) {
                    val cr = x / 45.0 - 1.5
                    val ci = y / 45.0 - 1.0
                    var zr = 0.0
                    var zi = 0.0
                    var i = 0
                    while (zr * zr + zi * zi < 4.0 && i < 100) {
                        val temp = zr * zr - zi * zi + cr
                        zi = 2.0 * zr * zi + ci
                        zr = temp
                        i++
                    }
                    if (i == 100) activePoints++
                }
            }
            val gpuElapsedTime = System.currentTimeMillis() - gpuStartTime
            val gpuScore = ((91 * 91 * 100 / gpuElapsedTime.coerceAtLeast(1)) * 12).coerceIn(1500, 15000).toInt()
            _benchmarkProgress.value = 0.7f
            addLog("GPU Mandelbrot grid calculated in ${gpuElapsedTime}ms. Score: $gpuScore")
            delay(500)

            // STEP 3: Memory Bandwidth Write Read Speed
            _benchmarkStatus.value = "Profiling RAM bus allocations with float sequences write & read loops"
            _benchmarkProgress.value = 0.82f
            val memStartTime = System.currentTimeMillis()
            val memSize = 100000
            val floatArr = FloatArray(memSize)
            for (i in 0 until memSize) {
                floatArr[i] = i * 3.14159f
            }
            var sum = 0f
            for (i in 0 until memSize) {
                sum += floatArr[i]
            }
            val memElapsedTime = System.currentTimeMillis() - memStartTime
            val memScore = ((memSize / memElapsedTime.coerceAtLeast(1)) * 9).coerceIn(2000, 12000).toInt()
            _benchmarkProgress.value = 0.92f
            addLog("RAM float matrix tests done in ${memElapsedTime}ms. Sum check=${sum.toInt()}. Score: $memScore")
            delay(500)

            // STEP 4: IO Storage performance (Write, Read, Purge)
            _benchmarkStatus.value = "Stressing sandbox flash storage sector (sequential file write 2MB buffer)"
            _benchmarkProgress.value = 0.96f
            val ioStartTime = System.currentTimeMillis()
            val testString = "StorageCheckBandwidthSeedText".repeat(1000)
            var ioScore = 6500
            try {
                val context = getApplication<Application>().applicationContext
                val file = java.io.File(context.cacheDir, "temp_bench.txt")
                file.writeText(testString)
                val readBack = file.readText()
                if (readBack.length == testString.length) {
                    val ioElapsedTime = System.currentTimeMillis() - ioStartTime
                    ioScore = ((testString.length / ioElapsedTime.coerceAtLeast(1)) * 14).coerceIn(1000, 10000).toInt()
                    file.delete()
                    addLog("IO Sequential disk check done in ${ioElapsedTime}ms. Score: $ioScore")
                }
            } catch (e: Exception) {
                addLog("Storage disk check fell back onto virtual simulator stream. Score: $ioScore")
            }
            delay(400)

            // Compile overall performance indicators
            val finalScore = (cpuScore + gpuScore + memScore + ioScore) / 4
            val rating = when {
                finalScore >= 8500 -> "Flagship Premium Elite"
                finalScore >= 7000 -> "Sizzling Powerhouse"
                finalScore >= 5200 -> "Solid High Performance"
                else -> "Standard Dev Sandbox Host"
            }

            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            val timeString = format.format(java.util.Date())

            val finalResult = BenchmarkResult(
                name = "Omni Virtual Host (This Device)",
                scoreCpu = cpuScore,
                scoreGpu = gpuScore,
                scoreMem = memScore,
                scoreStorage = ioScore,
                rating = rating,
                timestamp = timeString
            )

            _lastBenchmarkResult.value = finalResult
            _benchmarkHistory.value = listOf(finalResult) + _benchmarkHistory.value.filter { it.name != finalResult.name }
            _benchmarkProgress.value = 1.0f
            _benchmarkStatus.value = "Benchmarks completed! Overall Score Index: $finalScore ($rating)"
            _isBenchmarking.value = false
            addLog("System Telemetry: Live Hardware Profiling done. Score composite: $finalScore ($rating).")
        }
    }

    // --- Hidden System Settings States ---
    private val _animationScale = MutableStateFlow(1.0f)
    val animationScale: StateFlow<Float> = _animationScale.asStateFlow()

    private val _backgroundProcessLimit = MutableStateFlow("Standard limit")
    val backgroundProcessLimit: StateFlow<String> = _backgroundProcessLimit.asStateFlow()

    private val _standbyBucket = MutableStateFlow("ACTIVE")
    val standbyBucket: StateFlow<String> = _standbyBucket.asStateFlow()

    private val _privateDnsMode = MutableStateFlow("Automatic")
    val privateDnsMode: StateFlow<String> = _privateDnsMode.asStateFlow()

    private val _privateDnsHost = MutableStateFlow("dns.google")
    val privateDnsHost: StateFlow<String> = _privateDnsHost.asStateFlow()

    private val _force4xMsaa = MutableStateFlow(false)
    val force4xMsaa: StateFlow<Boolean> = _force4xMsaa.asStateFlow()

    private val _disableHwOverlays = MutableStateFlow(false)
    val disableHwOverlays: StateFlow<Boolean> = _disableHwOverlays.asStateFlow()

    private val _showGpuOverdraw = MutableStateFlow(false)
    val showGpuOverdraw: StateFlow<Boolean> = _showGpuOverdraw.asStateFlow()

    private val _screenRefreshRate = MutableStateFlow(60)
    val screenRefreshRate: StateFlow<Int> = _screenRefreshRate.asStateFlow()

    private val _logBufferSize = MutableStateFlow("256K")
    val logBufferSize: StateFlow<String> = _logBufferSize.asStateFlow()

    fun updateAnimationScale(scale: Float) {
        _animationScale.value = scale
        addLog("Hidden Settings: Modified Animator Scale coefficient to: ${scale}x")
    }

    fun updateBackgroundProcessLimit(limit: String) {
        _backgroundProcessLimit.value = limit
        addLog("Hidden Settings: Background Process Limit policy enforced: $limit")
    }

    fun updateStandbyBucket(bucket: String) {
        _standbyBucket.value = bucket
        addLog("Hidden Settings: Enforced Power App Standby Bucket state: $bucket")
    }

    fun updatePrivateDns(mode: String, host: String) {
        _privateDnsMode.value = mode
        _privateDnsHost.value = host
        addLog("Hidden Settings: System Network config - Private DNS Tunnel: Mode=$mode, Host=$host")
    }

    fun toggleForce4xMsaa(enabled: Boolean) {
        _force4xMsaa.value = enabled
        addLog("Hidden Settings: GPU Renderer override forced 4x MSAA: $enabled")
    }

    fun toggleDisableHwOverlays(enabled: Boolean) {
        _disableHwOverlays.value = enabled
        addLog("Hidden Settings: Hardware Composition overlay disable flag: $enabled")
    }

    fun toggleShowGpuOverdraw(enabled: Boolean) {
        _showGpuOverdraw.value = enabled
        addLog("Hidden Settings: Debugging Visualizations - GPU Overdraw colors: $enabled")
    }

    fun updateScreenRefreshRate(hz: Int) {
        _screenRefreshRate.value = hz
        addLog("Hidden Settings: Locked Display Peak Refresh Rate: ${hz}Hz")
    }

    fun updateLogBufferSize(size: String) {
        _logBufferSize.value = size
        addLog("Hidden Settings: Logcat active circular memory buffer allocated: $size")
    }

    // --- Quick Tiles Creator States ---
    private val _customQuickTiles = MutableStateFlow<List<CustomQuickTile>>(listOf(
        CustomQuickTile("t1", "Speed Boost", "Speed", "Inactive", "Toggle Developer Setting", "Set Animation Scale to 0.5x"),
        CustomQuickTile("t2", "Launch Specs", "Terminal", "Active", "Open App", "Developer Specs Screen"),
        CustomQuickTile("t3", "DND Overdrive", "Vibration", "Active", "Run Automation", "Auto Cleanup Duplicate Images")
    ))
    val customQuickTiles: StateFlow<List<CustomQuickTile>> = _customQuickTiles.asStateFlow()

    fun createQuickTile(name: String, iconName: String, state: String, actionType: String, actionDetail: String) {
        val newTile = CustomQuickTile(
            id = "t_" + Random.nextInt(1000, 9999).toString(),
            name = name,
            iconName = iconName,
            state = state,
            actionType = actionType,
            actionDetail = actionDetail
        )
        _customQuickTiles.value = _customQuickTiles.value + newTile
        addLog("QuickTiles Daemon: Custom quick settings system tile registered: [$name] (ID: ${newTile.id})")
    }

    fun deleteQuickTile(id: String) {
        val tile = _customQuickTiles.value.find { it.id == id }
        _customQuickTiles.value = _customQuickTiles.value.filter { it.id != id }
        tile?.let {
            addLog("QuickTiles Daemon: Unregistered custom quick settings tile: [${it.name}]")
        }
    }

    fun toggleQuickTileStateInShade(id: String) {
        val updatedList = _customQuickTiles.value.map { tile ->
            if (tile.id == id) {
                val nextState = when (tile.state) {
                    "Active" -> "Inactive"
                    "Inactive" -> "Active"
                    else -> "Active"
                }
                addLog("System Shade: Clicked QuickTile [${tile.name}] -> Toggled active state to: $nextState")
                
                // Execute actions!
                executeQuickTileAction(tile, nextState)
                
                tile.copy(state = nextState)
            } else tile
        }
        _customQuickTiles.value = updatedList
    }

    private fun executeQuickTileAction(tile: CustomQuickTile, nextState: String) {
        viewModelScope.launch {
            when (tile.actionType) {
                "Open App" -> {
                    addLog("QuickTile Activated [${tile.name}]: Simulating navigation launch intent target: ${tile.actionDetail}")
                }
                "Toggle Developer Setting" -> {
                    if (tile.actionDetail.contains("Animation")) {
                        val currentScale = _animationScale.value
                        val newScale = if (currentScale == 1.0f) 0.5f else 1.0f
                        updateAnimationScale(newScale)
                    } else if (tile.actionDetail.contains("MSAA")) {
                        toggleForce4xMsaa(!_force4xMsaa.value)
                    } else if (tile.actionDetail.contains("Overlays")) {
                        toggleDisableHwOverlays(!_disableHwOverlays.value)
                    } else if (tile.actionDetail.contains("Refresh")) {
                        val currentHz = _screenRefreshRate.value
                        val newHz = if (currentHz == 60) 120 else 60
                        updateScreenRefreshRate(newHz)
                    } else {
                        addLog("QuickTile Activated [${tile.name}]: Applied setting toggle: ${tile.actionDetail}")
                    }
                }
                "Run Automation" -> {
                    addLog("QuickTile Activated [${tile.name}]: Triggering rule logic for background macro: ${tile.actionDetail}")
                    val rule = automationRules.value.find { it.name.contains(tile.actionDetail) || tile.actionDetail.contains(it.name) }
                    if (rule != null) {
                        addLog("Registered System Automation Rule Fired: Running action ${rule.actionType}")
                    }
                }
                "Show Dialog" -> {
                    addLog("QuickTile Activated [${tile.name}]: Broadcasted alert notification HUD pop: '${tile.actionDetail}'")
                }
                "Webhook" -> {
                    addLog("QuickTile Activated [${tile.name}]: Contacting remote webhook server endpoint: ${tile.actionDetail}")
                }
            }
        }
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

data class AppScreenTime(
    val appName: String,
    val packageName: String,
    val usageMinutes: Int,
    val iconName: String
)

data class AppDataUsage(
    val appName: String,
    val wifiUsedMb: Float,
    val mobileUsedMb: Float
)

data class UnlockLogEvent(
    val id: String,
    val timestamp: String,
    val unlockMethod: String
)

data class NotificationLogEvent(
    val id: String,
    val appName: String,
    val sender: String,
    val content: String,
    val category: String, // "Communication", "Social", "System", "Productivity"
    val timestamp: String,
    val channel: String = "Normal"
)

data class BenchmarkResult(
    val name: String,
    val scoreCpu: Int,
    val scoreGpu: Int,
    val scoreMem: Int,
    val scoreStorage: Int,
    val rating: String,
    val timestamp: String
)

data class CustomQuickTile(
    val id: String,
    val name: String,
    val iconName: String,
    val state: String, // "Active", "Inactive", "Unavailable"
    val actionType: String, // "Open App", "Toggle Developer Setting", "Run Automation", "Show Dialog", "Webhook"
    val actionDetail: String
)


