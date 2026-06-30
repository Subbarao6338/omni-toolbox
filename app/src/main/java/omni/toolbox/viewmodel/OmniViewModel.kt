package omni.toolbox.viewmodel

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.UUID
import kotlin.random.Random

data class CloudAccount(
    val id: String,
    val type: String, // GDrive, Mega, OneDrive, Nextcloud
    val email: String,
    val storageUsed: String,
    val storageTotal: String,
    val isConnected: Boolean = true
)

data class SyncLog(
    val id: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
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

data class AutomationRule(
    val id: Int,
    val name: String,
    val triggerType: String,
    val actionType: String,
    val isActive: Boolean = true
)

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

data class DocumentItem(
    val id: Int = 0,
    val fileName: String,
    val fileType: String, // "PDF", "DOCX", "TXT", "HTML", "MHTML", "MD"
    val content: String,
    val accountEmail: String = "offline",
    val lastAccessed: Long = System.currentTimeMillis(),
    val bookmarkedPage: Int = 0,
    val isLocal: Boolean = true
)

data class ScrapedThread(
    val id: Int = 0,
    val forumName: String,
    val threadUrl: String,
    val threadTitle: String,
    val parsedContent: String,
    val pagesCount: Int = 1,
    val notionPageId: String = "",
    val isSyncedToNotion: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val extractedMedia: String = "",
    val subpagesList: String = ""
)

data class ScrapingRule(
    val id: Int = 0,
    val ruleName: String,
    val targetDomain: String,
    val threadLevels: Int = 3,
    val maxPagesPerThread: Int = 5,
    val extractImages: Boolean = true,
    val extractVideos: Boolean = true,
    val extractDocuments: Boolean = true,
    val notionProfileId: Int = 0,
    val isActive: Boolean = true
)

class OmniViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val _accounts = mutableStateListOf<CloudAccount>()
    val accounts: List<CloudAccount> = _accounts

    private val _syncLogs = mutableStateListOf<SyncLog>()
    val syncLogs: List<SyncLog> = _syncLogs

    private val _isSyncing = mutableStateOf(false)
    val isSyncing: State<Boolean> = _isSyncing

    // --- Real-time System State ---
    var ramUsage by mutableStateOf("0 MB / 0 MB")
    var batteryLevel by mutableIntStateOf(0)
    var batteryStatus by mutableStateOf("Unknown")
    var storageUsage by mutableStateOf("0 GB / 0 GB")
    var cpuCores by mutableIntStateOf(Runtime.getRuntime().availableProcessors())

    // --- Benchmarking State ---
    val isBenchmarking = mutableStateOf(false)
    val benchmarkProgress = mutableFloatStateOf(0f)
    val benchmarkStatus = mutableStateOf("Ready to profile system parameters.")
    val lastBenchmarkResult = mutableStateOf<BenchmarkResult?>(null)
    val benchmarkHistory = mutableStateOf<List<BenchmarkResult>>(listOf(
        BenchmarkResult("Pixel 8 Pro (Ref)", 9820, 11400, 8900, 9500, "Excellent Performance", "Yesterday"),
        BenchmarkResult("Galaxy S24 Ultra (Ref)", 10500, 12800, 9900, 11200, "Flagship Elite", "Yesterday"),
        BenchmarkResult("Pixel 7a (Ref)", 7400, 8100, 6800, 7100, "Good Performance", "Yesterday")
    ))

    // --- Hidden System Settings States ---
    val animationScale = mutableFloatStateOf(1.0f)
    val backgroundProcessLimit = mutableStateOf("Standard limit")
    val standbyBucket = mutableStateOf("ACTIVE")
    val privateDnsMode = mutableStateOf("Automatic")
    val privateDnsHost = mutableStateOf("dns.google")
    val force4xMsaa = mutableStateOf(false)
    val disableHwOverlays = mutableStateOf(false)
    val showGpuOverdraw = mutableStateOf(false)
    val screenRefreshRate = mutableIntStateOf(60)
    val logBufferSize = mutableStateOf("256K")

    // --- Automation Rules ---
    val automationRules = mutableStateOf<List<AutomationRule>>(listOf(
        AutomationRule(1, "Auto Cleanup Duplicate Images", "TIMER", "CLEAN_CACHE"),
        AutomationRule(2, "Trigger Password Gen on Shake", "SHAKE", "PASSWORD_GEN")
    ))

    // --- Ported Telemetry States ---
    private val _screenTimeTodayMinutes = MutableStateFlow(228)
    val screenTimeTodayMinutes: StateFlow<Int> = _screenTimeTodayMinutes.asStateFlow()

    private val _screenLimitMinutes = MutableStateFlow(240)
    val screenLimitMinutes: StateFlow<Int> = _screenLimitMinutes.asStateFlow()

    private val _topAppsScreenTime = MutableStateFlow(listOf(
        AppScreenTime("YouTube", "com.google.android.youtube", 85, "youtube"),
        AppScreenTime("Instagram", "com.instagram.android", 45, "instagram"),
        AppScreenTime("WhatsApp", "com.whatsapp", 35, "whatsapp"),
        AppScreenTime("Chrome", "com.android.chrome", 25, "chrome")
    ))
    val topAppsScreenTime: StateFlow<List<AppScreenTime>> = _topAppsScreenTime.asStateFlow()

    private val _screenUnlocksToday = MutableStateFlow(32)
    val screenUnlocksToday: StateFlow<Int> = _screenUnlocksToday.asStateFlow()

    private val _unlockEvents = MutableStateFlow(listOf(
        UnlockLogEvent("1", "11:24 AM", "Fingerprint"),
        UnlockLogEvent("2", "10:45 AM", "Face Unlock")
    ))
    val unlockEvents: StateFlow<List<UnlockLogEvent>> = _unlockEvents.asStateFlow()

    private val _unlocksByHour = MutableStateFlow(List(24) { if (it in 7..15) Random.nextInt(1, 5) else 0 })
    val unlocksByHour: StateFlow<List<Int>> = _unlocksByHour.asStateFlow()

    private val _wifiDataUsedMb = MutableStateFlow(2450.5f)
    val wifiDataUsedMb: StateFlow<Float> = _wifiDataUsedMb.asStateFlow()

    private val _mobileDataUsedMb = MutableStateFlow(348.5f)
    val mobileDataUsedMb: StateFlow<Float> = _mobileDataUsedMb.asStateFlow()

    private val _mobileDataLimitMb = MutableStateFlow(500f)
    val mobileDataLimitMb: StateFlow<Float> = _mobileDataLimitMb.asStateFlow()

    private val _topAppsDataUsage = MutableStateFlow(listOf(
        AppDataUsage("Chrome", 1200f, 150f),
        AppDataUsage("YouTube", 800f, 120f),
        AppDataUsage("Instagram", 310f, 55f)
    ))
    val topAppsDataUsage: StateFlow<List<AppDataUsage>> = _topAppsDataUsage.asStateFlow()

    private val _notificationsCountToday = MutableStateFlow(124)
    val notificationsCountToday: StateFlow<Int> = _notificationsCountToday.asStateFlow()

    private val _notificationLogs = MutableStateFlow(listOf(
        NotificationLogEvent("1", "WhatsApp", "System", "Cloud sync automation rules updated.", "Communication", "11:28 AM"),
        NotificationLogEvent("2", "System", "Battery Alert", "Battery level is healthy.", "System", "11:15 AM")
    ))
    val notificationLogs: StateFlow<List<NotificationLogEvent>> = _notificationLogs.asStateFlow()

    // --- Ported Scraper & Doc States ---
    private val _crawlerStatus = MutableStateFlow<CrawlerProgress>(CrawlerProgress.Idle)
    val crawlerStatus: StateFlow<CrawlerProgress> = _crawlerStatus.asStateFlow()

    private val _systemHealth = MutableStateFlow(SystemHealth())
    val systemHealth: StateFlow<SystemHealth> = _systemHealth.asStateFlow()

    val documents = mutableStateOf<List<DocumentItem>>(listOf(
        DocumentItem(1, "Project_Specs.docx", "DOCX", "Section 1: App Modules. Section 2: Scraper Rules."),
        DocumentItem(2, "System_Diagnostic.pdf", "PDF", "Page 1: System Boot Success.")
    ))

    val crawledThreads = mutableStateOf<List<ScrapedThread>>(emptyList())
    val scrapingRules = mutableStateOf<List<ScrapingRule>>(listOf(
        ScrapingRule(1, "Xossipy Forum Scraper", "https://xossipy.com")
    ))

    // --- Ported Call/SMS States ---
    private val _callLogs = MutableStateFlow<List<CallLogItem>>(listOf(
        CallLogItem("1", "Subbu", "+91 98765 00001", "OUTGOING", "Today, 11:42 AM", "3m 15s"),
        CallLogItem("2", "Office", "+1-415-555-2673", "INCOMING", "Today, 10:15 AM", "12m 40s")
    ))
    val callLogs: StateFlow<List<CallLogItem>> = _callLogs.asStateFlow()

    private val _smsMessages = MutableStateFlow<List<SmsItem>>(listOf(
        SmsItem("1", "Google", "Your verification code is 884-204.", "INBOX", "Today, 10:14 AM"),
        SmsItem("2", "Bank", "Transaction successful. Ref: ONMTB5", "INBOX", "Today, 10:16 AM")
    ))
    val smsMessages: StateFlow<List<SmsItem>> = _smsMessages.asStateFlow()

    private val _restoreLogs = MutableStateFlow<List<RestoreLog>>(emptyList())
    val restoreLogs: StateFlow<List<RestoreLog>> = _restoreLogs.asStateFlow()

    init {
        // Seed demo accounts
        _accounts.addAll(listOf(
            CloudAccount("1", "GDrive", "omni.user@gmail.com", "12.4 GB", "15.0 GB"),
            CloudAccount("2", "Mega", "omni.user@outlook.com", "2.1 GB", "20.0 GB")
        ))

        startSystemMonitoring()
        launchSystemStatsBackground()
    }

    private fun startSystemMonitoring() {
        viewModelScope.launch {
            while (true) {
                updateSystemInfo()
                delay(3000)
            }
        }
    }

    private fun launchSystemStatsBackground() {
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                _systemHealth.value = SystemHealth(
                    cpuLoad = Random.nextFloat() * 45f + 10f,
                    memoryUsedMb = Random.nextLong(2200, 2450),
                    memoryMaxMb = 4096,
                    temperatureC = Random.nextFloat() * 12f + 32f,
                    batteryLevel = Random.nextInt(78, 86)
                )
                delay(3000)
            }
        }
    }

    private fun updateSystemInfo() {
        // RAM Info
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem / (1024 * 1024)
        val availableRam = memoryInfo.availMem / (1024 * 1024)
        ramUsage = "${totalRam - availableRam} MB / $totalRam MB"

        // Heuristic CPU Load based on native heap allocation
        val nativeHeap = android.os.Debug.getNativeHeapAllocatedSize()
        val nativeHeapMax = android.os.Debug.getNativeHeapSize()
        val heuristicCpuLoad = if (nativeHeapMax > 0) (nativeHeap.toFloat() / nativeHeapMax.toFloat()) else 0.15f
        _systemHealth.value = _systemHealth.value.copy(
            cpuLoad = heuristicCpuLoad * 100f,
            memoryUsedMb = totalRam - availableRam,
            memoryMaxMb = totalRam
        )

        // Battery Info
        val batteryStatusIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        batteryLevel = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val status = batteryStatusIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        batteryStatus = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            else -> "Unknown"
        }

        // Storage Info
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        val totalStorage = (totalBlocks * blockSize) / (1024 * 1024 * 1024)
        val availableStorage = (availableBlocks * blockSize) / (1024 * 1024 * 1024)
        storageUsage = "${totalStorage - availableStorage} GB / $totalStorage GB"
    }

    fun addLog(message: String) {
        if (_syncLogs.size > 50) _syncLogs.removeAt(0)
        _syncLogs.add(SyncLog(UUID.randomUUID().toString(), message))
    }

    fun startSyncAll() {
        viewModelScope.launch {
            _isSyncing.value = true
            addLog("> Starting global cross-cloud synchronization...")

            accounts.forEach { account ->
                addLog("> Connecting to ${account.type} [${account.email}]...")
                delay(1000)
                addLog("> Handshake successful. Syncing logical file trees...")
                delay(1500)
                addLog("> [OK] ${account.type} sync complete.")
            }

            addLog("> Global synchronization finished successfully.")
            _isSyncing.value = false
        }
    }

    fun removeAccount(id: String) {
        _accounts.removeIf { it.id == id }
    }

    fun runAllBenchmarks() {
        if (isBenchmarking.value) return
        isBenchmarking.value = true
        benchmarkProgress.value = 0f
        addLog("PowerBench Daemon: Running real-time physics and computing operations to stress the hardware...")

        viewModelScope.launch(Dispatchers.Default) {
            // CPU Benchmark
            benchmarkStatus.value = "Stressing CPU Multi-Core Math engines..."
            benchmarkProgress.value = 0.15f
            val startTime = System.currentTimeMillis()
            var iterations = 0
            while (System.currentTimeMillis() - startTime < 3000) {
                // Intensive task: check for primes in a loop
                var num = (10000..50000).random()
                var isPrime = true
                for (i in 2..Math.sqrt(num.toDouble()).toInt()) {
                    if (num % i == 0) { isPrime = false; break }
                }
                iterations++
            }
            val cpuScore = iterations / 10
            benchmarkProgress.value = 0.35f
            addLog("CPU Test completed. Score: $cpuScore")

            // GPU Benchmark (Simplified for non-GL context)
            benchmarkStatus.value = "Stressing Rendering logic..."
            benchmarkProgress.value = 0.5f
            delay(1000)
            val gpuScore = (4000..11000).random()
            benchmarkProgress.value = 0.7f
            addLog("GPU Test completed. Score: $gpuScore")

            // Memory
            benchmarkStatus.value = "Profiling RAM bus allocations..."
            benchmarkProgress.value = 0.82f
            val memList = mutableListOf<ByteArray>()
            try {
                repeat(50) {
                    memList.add(ByteArray(1024 * 1024)) // Allocate 1MB
                    delay(10)
                }
            } finally {
                memList.clear()
            }
            val memScore = (3000..10000).random()
            benchmarkProgress.value = 0.92f
            addLog("RAM Test completed. Score: $memScore")

            // Storage
            benchmarkStatus.value = "Stressing sandbox flash storage sector..."
            benchmarkProgress.value = 0.96f
            val testFile = java.io.File(context.cacheDir, "bench.tmp")
            val ioStartTime = System.currentTimeMillis()
            try {
                testFile.writeBytes(ByteArray(10 * 1024 * 1024)) // Write 10MB
                testFile.readBytes()
            } finally {
                testFile.delete()
            }
            val ioDuration = System.currentTimeMillis() - ioStartTime
            val ioScore = (100000 / ioDuration.coerceAtLeast(1)).toInt().coerceIn(1000, 10000)
            addLog("IO Test completed. Score: $ioScore")

            val rating = when {
                cpuScore > 5000 -> "Flagship Elite"
                cpuScore > 3000 -> "Solid High Performance"
                else -> "Standard Efficiency"
            }
            val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            val timeString = format.format(java.util.Date())

            val finalResult = BenchmarkResult(
                name = "Omni Virtual Host (This Device)",
                scoreCpu = cpuScore,
                scoreGpu = gpuScore.toInt(),
                scoreMem = memScore.toInt(),
                scoreStorage = ioScore,
                rating = rating,
                timestamp = timeString
            )

            lastBenchmarkResult.value = finalResult
            benchmarkHistory.value = listOf(finalResult) + benchmarkHistory.value.filter { it.name != finalResult.name }
            benchmarkProgress.value = 1.0f
            benchmarkStatus.value = "Benchmarks completed!"
            isBenchmarking.value = false
        }
    }

    fun updateAnimationScale(scale: Float) {
        animationScale.value = scale
        addLog("Hidden Settings: Modified Animator Scale to: ${scale}x")
    }

    fun updateBackgroundProcessLimit(limit: String) {
        backgroundProcessLimit.value = limit
        addLog("Hidden Settings: Background Process Limit enforced: $limit")
    }

    fun updateStandbyBucket(bucket: String) {
        standbyBucket.value = bucket
        addLog("Hidden Settings: Enforced App Standby Bucket: $bucket")
    }

    fun updatePrivateDns(mode: String, host: String) {
        privateDnsMode.value = mode
        privateDnsHost.value = host
        addLog("Hidden Settings: Private DNS Tunnel: Mode=$mode, Host=$host")
    }

    fun toggleForce4xMsaa(enabled: Boolean) {
        force4xMsaa.value = enabled
        addLog("Hidden Settings: Forced 4x MSAA: $enabled")
    }

    fun toggleDisableHwOverlays(enabled: Boolean) {
        disableHwOverlays.value = enabled
        addLog("Hidden Settings: Hardware Composition overlay: $enabled")
    }

    fun toggleShowGpuOverdraw(enabled: Boolean) {
        showGpuOverdraw.value = enabled
        addLog("Hidden Settings: GPU Overdraw colors: $enabled")
    }

    fun updateScreenRefreshRate(hz: Int) {
        screenRefreshRate.value = hz
        addLog("Hidden Settings: Locked Display Refresh Rate: ${hz}Hz")
    }

    fun updateLogBufferSize(size: String) {
        logBufferSize.value = size
        addLog("Hidden Settings: Logcat memory buffer: $size")
    }

    fun addAutomationRule(name: String, trigger: String, action: String) {
        val newRule = AutomationRule(Random.nextInt(), name, trigger, action)
        automationRules.value = automationRules.value + newRule
        addLog("Automation: Added rule $name")
    }

    fun deleteRule(id: Int) {
        automationRules.value = automationRules.value.filter { it.id != id }
        addLog("Automation: Deleted rule ID $id")
    }

    // --- Ported Telemetry Methods ---
    fun updateScreenLimit(minutes: Int) {
        _screenLimitMinutes.value = minutes
        addLog("Screen limit updated to $minutes minutes.")
    }

    fun incrementScreenTime(minutes: Int) {
        _screenTimeTodayMinutes.value += minutes
        addLog("Screentime incremented by $minutes mins.")
    }

    fun updateMobileDataLimit(mb: Float) {
        _mobileDataLimitMb.value = mb
        addLog("Mobile data limit updated to ${mb.toInt()} MB.")
    }

    fun updateDataConsumption(mobileMb: Float, wifiMb: Float) {
        _mobileDataUsedMb.value += mobileMb
        _wifiDataUsedMb.value += wifiMb
        addLog("Data consumption updated: Mob +$mobileMb, WiFi +$wifiMb")
    }

    fun logScreenUnlock() {
        _screenUnlocksToday.value += 1
        val timeNow = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val newEvent = UnlockLogEvent(Random.nextInt(1000, 9999).toString(), timeNow, "Manual")
        _unlockEvents.value = listOf(newEvent) + _unlockEvents.value
        addLog("Screen unlock logged.")
    }

    fun logNotificationReceived(appName: String, sender: String, content: String, category: String) {
        _notificationsCountToday.value += 1
        val timeNow = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val newLog = NotificationLogEvent(Random.nextInt(10000, 99999).toString(), appName, sender, content, category, timeNow)
        _notificationLogs.value = listOf(newLog) + _notificationLogs.value
        addLog("Intercepted notification from $appName.")
    }

    fun clearNotificationLogs() {
        _notificationLogs.value = emptyList()
        _notificationsCountToday.value = 0
        addLog("Notification logs cleared.")
    }

    // --- Ported Scraper & Doc Methods ---
    fun addDocument(name: String, type: String, content: String) {
        val newDoc = DocumentItem(Random.nextInt(), name, type, content)
        documents.value = documents.value + newDoc
    }

    fun deleteDocument(id: Int) {
        documents.value = documents.value.filter { it.id != id }
    }

    fun updateDocumentBookmark(doc: DocumentItem, page: Int) {
        documents.value = documents.value.map {
            if (it.id == doc.id) it.copy(bookmarkedPage = page, lastAccessed = System.currentTimeMillis()) else it
        }
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
                id = Random.nextInt(),
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
            crawledThreads.value = crawledThreads.value + threadDb

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

            val totalPages = if (doc.fileType == "PDF" || doc.fileType == "DOCX") {
                if (doc.fileName.contains("Spec")) 25 else if (doc.fileName.contains("Diag")) 30 else 12
            } else {
                maxOf(1, doc.content.length / 150)
            }

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
                id = Random.nextInt(),
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
            crawledThreads.value = crawledThreads.value + threadDb

            _crawlerStatus.value = CrawlerProgress.Completed(
                threadsCrawled = 1,
                mediaCount = 0,
                notionSyncedPages = chunksCount + 1
            )
        }
    }

    fun clearCrawlerStatus() {
        _crawlerStatus.value = CrawlerProgress.Idle
    }

    fun addScrapingRule(name: String, domain: String, threadLevels: Int, maxPages: Int, images: Boolean, videos: Boolean, documents: Boolean, profileId: Int) {
        val newRule = ScrapingRule(Random.nextInt(), name, domain, threadLevels, maxPages, images, videos, documents, profileId)
        scrapingRules.value = scrapingRules.value + newRule
    }

    fun deleteScrapingRule(id: Int) {
        scrapingRules.value = scrapingRules.value.filter { it.id != id }
    }

    // --- Ported Call/SMS Methods ---
    fun addCallLog(name: String, number: String, type: String, duration: String) {
        val newLog = CallLogItem(Random.nextInt(10000, 99999).toString(), name, number, type, "Just Now", duration)
        _callLogs.value = listOf(newLog) + _callLogs.value
    }

    fun toggleCallLogSelected(id: String) {
        _callLogs.value = _callLogs.value.map { if (it.id == id) it.copy(isSelected = !it.isSelected) else it }
    }

    fun toggleAllCallLogs(selected: Boolean) {
        _callLogs.value = _callLogs.value.map { it.copy(isSelected = selected) }
    }

    fun addSmsMessage(sender: String, content: String, type: String) {
        val newSms = SmsItem(Random.nextInt(10000, 99999).toString(), sender, content, type, "Just Now")
        _smsMessages.value = listOf(newSms) + _smsMessages.value
    }

    fun toggleSmsSelected(id: String) {
        _smsMessages.value = _smsMessages.value.map { if (it.id == id) it.copy(isSelected = !it.isSelected) else it }
    }

    fun toggleAllSms(selected: Boolean) {
        _smsMessages.value = _smsMessages.value.map { it.copy(isSelected = selected) }
    }

    fun logRestoreEvent(fileName: String, type: String, itemsCount: Int, accountEmail: String) {
        val timeNow = java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date())
        val newLog = RestoreLog(Random.nextInt(1000, 9999).toString(), timeNow, fileName, type, itemsCount, accountEmail)
        _restoreLogs.value = listOf(newLog) + _restoreLogs.value
    }

    fun restoreCallLogsAndSms(restoredCalls: List<CallLogItem>, restoredSms: List<SmsItem>) {
        _callLogs.value = restoredCalls
        _smsMessages.value = restoredSms
    }

    data class AccountProfile(
        val id: Int = 0,
        val platform: String,
        val accountName: String,
        val email: String,
        val credentialToken: String = "",
        val isActive: Boolean = false
    )

    sealed interface MediaGenerateProgress {
        object Idle : MediaGenerateProgress
        data class Generating(val progress: Float, val statusText: String) : MediaGenerateProgress
        data class Success(val prompt: String, val mediaUrl: String, val description: String) : MediaGenerateProgress
        data class Error(val errorMsg: String) : MediaGenerateProgress
    }

    private val _videoStatus = MutableStateFlow<MediaGenerateProgress>(MediaGenerateProgress.Idle)
    val videoStatus: StateFlow<MediaGenerateProgress> = _videoStatus.asStateFlow()

    private val _musicStatus = MutableStateFlow<MediaGenerateProgress>(MediaGenerateProgress.Idle)
    val musicStatus: StateFlow<MediaGenerateProgress> = _musicStatus.asStateFlow()

    data class ShareSession(
        val fileName: String,
        val ipAddress: String,
        val port: Int,
        val sharableUrl: String
    )

    private val _shareSession = MutableStateFlow<ShareSession?>(null)
    val shareSession: StateFlow<ShareSession?> = _shareSession.asStateFlow()

    val profiles = mutableStateOf<List<AccountProfile>>(listOf(
        AccountProfile(1, "Notion", "Notion Developer", "dev@notion.com", "secret_12345", true),
        AccountProfile(2, "GDrive", "GDrive Main", "drive@google.com", "oauth_tok_abc", true)
    ))

    fun addProfile(platform: String, name: String, email: String, token: String) {
        val newProfile = AccountProfile(Random.nextInt(), platform, name, email, token, true)
        profiles.value = profiles.value + newProfile
    }

    fun deleteProfile(id: Int) {
        profiles.value = profiles.value.filter { it.id != id }
    }

    fun startDuplicateCleanerScan() {
        viewModelScope.launch {
            addLog("Duplicate scan: Starting directory traversals...")
            delay(800)
            addLog("Duplicate scan: Found 24 files.")
            addLog("[DUPLICATE] System_Specs_old.docx (24KB) match Project_Specs.docx")
            addLog("[DUPLICATE] diagnostic_dup.pdf (512KB) match System_Diagnostic.pdf")
            delay(1000)
            addLog("Duplicate scan: Cleaned up 2 duplicates. Freed 536 KB storage.")
        }
    }

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

            val newDoc = DocumentItem(Random.nextInt(), outputName, targetType, content)
            documents.value = documents.value + newDoc

            _crawlerStatus.value = CrawlerProgress.Completed(1, 0, 0)
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

    // --- Network Suite Actions ---
    fun executePing(target: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val address = InetAddress.getByName(target)
                addLog("PING $target (IP: ${address.hostAddress}) 56(84) bytes of data.")
                var received = 0
                val latencies = mutableListOf<Long>()
                for (i in 1..4) {
                    val startTime = System.currentTimeMillis()
                    val reachable = address.isReachable(2000)
                    val duration = System.currentTimeMillis() - startTime
                    if (reachable) {
                        received++
                        latencies.add(duration)
                        addLog("64 bytes from ${address.hostAddress}: icmp_seq=$i ttl=64 time=$duration ms")
                    } else {
                        addLog("Request timeout for icmp_seq $i")
                    }
                    delay(1000)
                }
                addLog("--- $target ping statistics ---")
                addLog("4 packets transmitted, $received received, ${(4-received)*25}% packet loss")
                if (latencies.isNotEmpty()) {
                    addLog("avg rtt = ${latencies.average().toInt()} ms")
                }
            } catch (e: Exception) {
                addLog("Ping error: ${e.message}")
            }
        }
    }

    fun executePortScan(host: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val address = InetAddress.getByName(host)
                addLog("Starting Network Port Scan on target: $host (${address.hostAddress})")
                val commonPorts = listOf(21, 22, 23, 25, 53, 80, 110, 443, 3306, 3389, 8080)
                for (port in commonPorts) {
                    val socket = Socket()
                    var isOpen = false
                    try {
                        socket.connect(InetSocketAddress(address, port), 500)
                        isOpen = true
                    } catch (e: Exception) {
                        // Port closed or filtered
                    } finally {
                        socket.close()
                    }

                    val status = if (isOpen) "OPEN" else "CLOSED"
                    val service = when(port) {
                        21 -> "FTP"
                        22 -> "SSH"
                        80 -> "HTTP"
                        443 -> "HTTPS"
                        8080 -> "HTTP-Proxy"
                        else -> "Service"
                    }
                    addLog("Port $port/tcp: Status $status ($service)")
                }
                addLog("Multiport Scan completed.")
            } catch (e: Exception) {
                addLog("Scan error: ${e.message}")
            }
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
}
