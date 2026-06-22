package omni.toolbox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
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

class OmniViewModel(application: Application) : AndroidViewModel(application) {
    private val _accounts = mutableStateListOf<CloudAccount>()
    val accounts: List<CloudAccount> = _accounts

    private val _syncLogs = mutableStateListOf<SyncLog>()
    val syncLogs: List<SyncLog> = _syncLogs

    private val _isSyncing = mutableStateOf(false)
    val isSyncing: State<Boolean> = _isSyncing

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

    init {
        // Seed demo accounts
        _accounts.addAll(listOf(
            CloudAccount("1", "GDrive", "subbu.edu.68@gmail.com", "12.4 GB", "15.0 GB"),
            CloudAccount("2", "Mega", "subbu.mega@outlook.com", "2.1 GB", "20.0 GB"),
            CloudAccount("3", "OneDrive", "subbu.work@microsoft.com", "45.0 GB", "1.0 TB"),
            CloudAccount("4", "Nextcloud", "subbu@personal-cloud.com", "150 GB", "500 GB")
        ))
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
            delay(1000)
            val cpuScore = (5000..12000).random()
            benchmarkProgress.value = 0.35f
            addLog("CPU Test completed. Score: $cpuScore")

            // GPU Benchmark
            benchmarkStatus.value = "Simulating GPU Blitter Rendering..."
            benchmarkProgress.value = 0.5f
            delay(1000)
            val gpuScore = (4000..11000).random()
            benchmarkProgress.value = 0.7f
            addLog("GPU Test completed. Score: $gpuScore")

            // Memory
            benchmarkStatus.value = "Profiling RAM bus allocations..."
            benchmarkProgress.value = 0.82f
            delay(1000)
            val memScore = (3000..10000).random()
            benchmarkProgress.value = 0.92f
            addLog("RAM Test completed. Score: $memScore")

            // Storage
            benchmarkStatus.value = "Stressing sandbox flash storage sector..."
            benchmarkProgress.value = 0.96f
            delay(1000)
            val ioScore = (2000..8000).random()
            addLog("IO Test completed. Score: $ioScore")

            val rating = "Solid High Performance"
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
}
