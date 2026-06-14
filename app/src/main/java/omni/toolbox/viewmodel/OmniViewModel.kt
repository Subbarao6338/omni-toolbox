package omni.toolbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

class OmniViewModel : ViewModel() {
    private val _accounts = mutableStateListOf<CloudAccount>()
    val accounts: List<CloudAccount> = _accounts

    private val _syncLogs = mutableStateListOf<SyncLog>()
    val syncLogs: List<SyncLog> = _syncLogs

    private val _isSyncing = mutableStateOf(false)
    val isSyncing: State<Boolean> = _isSyncing

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
        _syncLogs.add(SyncLog(java.util.UUID.randomUUID().toString(), message))
    }

    suspend fun startSyncAll() {
        _isSyncing.value = true
        addLog("> Starting global cross-cloud synchronization...")

        accounts.forEach { account ->
            addLog("> Connecting to ${account.type} [${account.email}]...")
            kotlinx.coroutines.delay(1000)
            addLog("> Handshake successful. Syncing logical file trees...")
            kotlinx.coroutines.delay(1500)
            addLog("> [OK] ${account.type} sync complete.")
        }

        addLog("> Global synchronization finished successfully.")
        _isSyncing.value = false
    }

    fun removeAccount(id: String) {
        _accounts.removeIf { it.id == id }
    }
}
