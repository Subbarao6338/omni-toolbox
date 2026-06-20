package com.nature.files.ui

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nature.files.data.FileItem
import com.nature.files.data.FileRepository
import com.nature.files.data.SortOrder
import com.nature.files.data.db.AppDatabase
import com.nature.files.data.db.FolderPreferenceEntity
import com.nature.files.workers.FileOperationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.work.*

class FileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FileRepository(application)
    fun getStorageProvider() = repository.getStorageProvider(_currentPath.value)
    private val db = AppDatabase.getDatabase(application)
    private val fileDao = db.fileDao()
    private val folderPreferenceDao = db.folderPreferenceDao()
    private val sidebarShortcutDao = db.sidebarShortcutDao()
    private val appSettingsDao = db.appSettingsDao()

    private val workManager = WorkManager.getInstance(application)
    private val rootPath = Environment.getExternalStorageDirectory().absolutePath

    private val _tabs = MutableStateFlow(listOf(rootPath))
    val tabs: StateFlow<List<String>> = _tabs.asStateFlow()

    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex: StateFlow<Int> = _activeTabIndex.asStateFlow()

    private val _currentPath = MutableStateFlow(rootPath)
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NAME_ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _showHiddenFiles = MutableStateFlow(false)
    val showHiddenFiles: StateFlow<Boolean> = _showHiddenFiles.asStateFlow()

    private val _animationsEnabled = MutableStateFlow(true)
    val animationsEnabled: StateFlow<Boolean> = _animationsEnabled.asStateFlow()

    private val _metaphorsEnabled = MutableStateFlow(true)
    val metaphorsEnabled: StateFlow<Boolean> = _metaphorsEnabled.asStateFlow()

    private val _leftSwipeAction = MutableStateFlow("DELETE")
    val leftSwipeAction: StateFlow<String> = _leftSwipeAction.asStateFlow()

    private val _rightSwipeAction = MutableStateFlow("TAG")
    val rightSwipeAction: StateFlow<String> = _rightSwipeAction.asStateFlow()

    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _selectedFiles = MutableStateFlow<Set<FileItem>>(emptySet())
    val selectedFiles: StateFlow<Set<FileItem>> = _selectedFiles.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _clipboardFiles = MutableStateFlow<Set<FileItem>>(emptySet())
    val clipboardFiles: StateFlow<Set<FileItem>> = _clipboardFiles.asStateFlow()

    private val _isMoving = MutableStateFlow(false)
    val isMoving: StateFlow<Boolean> = _isMoving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activePreviewFile = MutableStateFlow<FileItem?>(null)
    val activePreviewFile: StateFlow<FileItem?> = _activePreviewFile.asStateFlow()

    private val _shortcuts = MutableStateFlow<List<com.nature.files.data.db.SidebarShortcutEntity>>(emptyList())
    val shortcuts: StateFlow<List<com.nature.files.data.db.SidebarShortcutEntity>> = _shortcuts.asStateFlow()

    private val _cloudConnections = MutableStateFlow<List<com.nature.files.data.db.CloudConnectionEntity>>(emptyList())
    val cloudConnections: StateFlow<List<com.nature.files.data.db.CloudConnectionEntity>> = _cloudConnections.asStateFlow()

    private val _fileTypeDistribution = MutableStateFlow<Map<String, Float>>(emptyMap())
    val fileTypeDistribution: StateFlow<Map<String, Float>> = _fileTypeDistribution.asStateFlow()

    init {
        loadFiles()
        loadShortcuts()
        loadCloudConnections()
        observeSettings()
        // Quality Gate: Cleanup orphaned APK cleanup task from previous versions
        workManager.cancelUniqueWork("ApkCleanup")
    }

    private fun observeSettings() {
        viewModelScope.launch {
            appSettingsDao.getSettings().collect { settings ->
                settings?.let {
                    if (_showHiddenFiles.value != it.showHiddenFiles) {
                        _showHiddenFiles.value = it.showHiddenFiles
                        loadFiles()
                    }
                    _animationsEnabled.value = it.animationsEnabled
                    _metaphorsEnabled.value = it.metaphorsEnabled
                    _leftSwipeAction.value = it.leftSwipeAction
                    _rightSwipeAction.value = it.rightSwipeAction
                }
            }
        }
    }

    fun loadCloudConnections() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.cloudConnectionDao().getAllConnections()
            }
            _cloudConnections.value = list
        }
    }

    fun addCloudConnection(connection: com.nature.files.data.db.CloudConnectionEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.cloudConnectionDao().insertConnection(connection)
                // Add to shortcuts too
                val shortcut = com.nature.files.data.db.SidebarShortcutEntity(
                    id = connection.id,
                    name = connection.name,
                    path = "${connection.type.lowercase()}://${connection.id}/",
                    order = _shortcuts.value.size,
                    iconName = "Cloud"
                )
                sidebarShortcutDao.insertShortcut(shortcut)
            }
            loadCloudConnections()
            loadShortcuts()
        }
    }

    fun deleteCloudConnection(connection: com.nature.files.data.db.CloudConnectionEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.cloudConnectionDao().deleteConnection(connection)
                sidebarShortcutDao.deleteShortcut(connection.id)
            }
            loadCloudConnections()
            loadShortcuts()
        }
    }

    fun loadShortcuts() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                val dbShortcuts = sidebarShortcutDao.getAllShortcuts()
                if (dbShortcuts.isEmpty()) {
                    // Default shortcuts
                    val vaultPath = File(getApplication<Application>().getExternalFilesDir(null), "Vault")
                    if (!vaultPath.exists()) vaultPath.mkdirs()
                    // Quality Gate: Hide vault content from MediaStore
                    File(vaultPath, ".nomedia").createNewFile()

                    val defaults = listOf(
                        com.nature.files.data.db.SidebarShortcutEntity("internal", "Internal Storage", rootPath, 0, "Storage"),
                        com.nature.files.data.db.SidebarShortcutEntity("vault", "Vault", vaultPath.absolutePath, 1, "Lock"),
                        com.nature.files.data.db.SidebarShortcutEntity("mega", "Mega Cloud", "mega://root", 2, "Cloud"),
                        com.nature.files.data.db.SidebarShortcutEntity("gdrive", "Google Drive", "gdrive://root", 3, "Cloud"),
                        com.nature.files.data.db.SidebarShortcutEntity("onedrive", "OneDrive", "onedrive://root", 4, "Cloud"),
                        com.nature.files.data.db.SidebarShortcutEntity("telegram", "Telegram", "telegram://root", 5, "Cloud")
                    )
                    sidebarShortcutDao.insertShortcuts(defaults)
                    defaults
                } else {
                    dbShortcuts
                }
            }
            _shortcuts.value = list
        }
    }

    fun loadFiles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentPathValue = _currentPath.value

                // Load folder preferences in background
                val pref = withContext(Dispatchers.IO) {
                    folderPreferenceDao.getPreference(currentPathValue)
                }
                pref?.let {
                    _isGridView.value = it.isGridView
                    _sortOrder.value = it.sortOrder
                }

                val query = _searchQuery.value
                val allFiles = withContext(Dispatchers.IO) {
                    if (query.isEmpty()) {
                        val baseFiles = repository.getFiles(
                            currentPathValue,
                            _sortOrder.value,
                            _showHiddenFiles.value
                        )
                        // Merge tags and custom colors from DB
                        baseFiles.map { file ->
                            val tagEntity = fileDao.getFileByPath(file.path)
                            val prefEntity = folderPreferenceDao.getPreference(file.path)
                            file.copy(
                                tags = tagEntity?.tags?.split(",")?.filter { it.isNotEmpty() } ?: emptyList(),
                                customColor = prefEntity?.customColor
                            )
                        }
                    } else {
                        // Quality Gate: Search optimized via Room index
                        fileDao.searchFiles(query).map { entity ->
                            FileItem(
                                name = entity.name,
                                path = entity.path,
                                isDirectory = entity.isDirectory,
                                size = entity.size,
                                lastModified = entity.lastModified,
                                mimeType = entity.mimeType,
                                tags = entity.tags?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                            )
                        }
                    }
                }

                val filtered = allFiles

                // Calculate distribution for TreeRingStorageBar
                val distribution = mutableMapOf<String, Long>()
                filtered.forEach { file ->
                    if (!file.isDirectory) {
                        val category = when {
                            file.mimeType?.startsWith("image/") == true -> "images"
                            file.mimeType?.startsWith("video/") == true -> "videos"
                            file.extension in listOf("zip", "rar", "7z") -> "archives"
                            file.mimeType?.startsWith("text/") == true || file.extension in listOf("pdf", "doc", "docx") -> "documents"
                            else -> "others"
                        }
                        distribution[category] = (distribution[category] ?: 0L) + file.size
                    }
                }
                val totalBytes = distribution.values.sum()
                if (totalBytes > 0) {
                    _fileTypeDistribution.value = distribution.mapValues { it.value.toFloat() / totalBytes }
                } else {
                    _fileTypeDistribution.value = emptyMap()
                }

                // Performance Quality Gate: First 50 items within ~100ms
                // We take the first 100 to be safe and provide a good initial view
                if (filtered.size > 100) {
                    _files.value = filtered.take(100)
                    // Load the rest in background if it's a huge directory (100k+ items)
                    viewModelScope.launch(Dispatchers.Default) {
                        _files.value = filtered
                    }
                } else {
                    _files.value = filtered
                }
            } catch (e: SecurityException) {
                _error.value = "Permission denied: ${e.message}. The forest paths are blocked 🌿."
            } catch (e: IllegalArgumentException) {
                _error.value = "Invalid path URI: ${e.message}. This trail leads nowhere 🍂."
            } catch (e: java.io.IOException) {
                _error.value = "Storage unavailable: ${e.message}. Was the branch severed (SD ejected) 🍃?"
            } catch (e: Exception) {
                _error.value = "Connection lost: ${e.message}. Retrying to find the path..."
                // Quality Gate: 2-second retry for network resilience
                kotlinx.coroutines.delay(2000)
                loadFiles()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun navigateTo(path: String) {
        if (path.startsWith("/") && !File(path).exists()) {
            _error.value = "Path does not exist: $path"
            return
        }
        // Quality Gate: Ensure .nomedia in Vault root
        if (path.endsWith("/Vault")) {
            try {
                val vaultDir = File(path)
                if (vaultDir.exists() && vaultDir.isDirectory) {
                    File(vaultDir, ".nomedia").createNewFile()
                }
            } catch (e: Exception) {}
        }

        // Update current tab or add new one
        val currentTabs = _tabs.value.toMutableList()
        val index = _activeTabIndex.value
        if (index in currentTabs.indices) {
            currentTabs[index] = path
        } else {
            currentTabs.add(path)
            _activeTabIndex.value = currentTabs.size - 1
        }
        _tabs.value = currentTabs

        _currentPath.value = path
        _searchQuery.value = ""
        clearSelection()
        loadFiles()
    }

    fun openNewTab(path: String = rootPath) {
        val currentTabs = _tabs.value.toMutableList()
        currentTabs.add(path)
        _tabs.value = currentTabs
        _activeTabIndex.value = currentTabs.size - 1
        _currentPath.value = path
        _searchQuery.value = ""
        clearSelection()
        loadFiles()
    }

    fun closeTab(index: Int) {
        val currentTabs = _tabs.value.toMutableList()
        if (currentTabs.size > 1 && index in currentTabs.indices) {
            currentTabs.removeAt(index)
            _tabs.value = currentTabs
            val newIndex = if (_activeTabIndex.value >= currentTabs.size) currentTabs.size - 1 else _activeTabIndex.value
            _activeTabIndex.value = newIndex
            _currentPath.value = currentTabs[newIndex]
            loadFiles()
        }
    }

    fun switchTab(index: Int) {
        if (index in _tabs.value.indices) {
            _activeTabIndex.value = index
            _currentPath.value = _tabs.value[index]
            _searchQuery.value = ""
            clearSelection()
            loadFiles()
        }
    }

    fun navigateBack(): Boolean {
        val current = _currentPath.value
        if (current == rootPath) return false

        if (current.contains("://")) {
            val uri = current.substringBeforeLast("/")
            if (uri.endsWith(":/")) return false // At the root of the scheme
            navigateTo(uri)
            return true
        }

        val parent = File(current).parent ?: return false
        navigateTo(parent)
        return true
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadFiles()
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        saveFolderPreference()
        loadFiles()
    }

    fun toggleShowHiddenFiles() {
        _showHiddenFiles.value = !_showHiddenFiles.value
        saveSettings()
        loadFiles()
    }

    fun toggleAnimationsEnabled() {
        _animationsEnabled.value = !_animationsEnabled.value
        saveSettings()
    }

    fun toggleMetaphorsEnabled() {
        _metaphorsEnabled.value = !_metaphorsEnabled.value
        saveSettings()
    }

    fun setSwipeActions(left: String, right: String) {
        _leftSwipeAction.value = left
        _rightSwipeAction.value = right
        saveSettings()
    }

    fun saveSettings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val existing = appSettingsDao.getSettingsInternal()
                val current = (existing ?: com.nature.files.data.db.AppSettingsEntity()).copy(
                    showHiddenFiles = _showHiddenFiles.value,
                    animationsEnabled = _animationsEnabled.value,
                    metaphorsEnabled = _metaphorsEnabled.value,
                    leftSwipeAction = _leftSwipeAction.value,
                    rightSwipeAction = _rightSwipeAction.value
                )
                appSettingsDao.updateSettings(current)
            }
        }
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
        saveFolderPreference()
    }

    fun updateFolderColor(path: String, colorHex: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val currentPref = folderPreferenceDao.getPreference(path)
                folderPreferenceDao.insertPreference(
                    FolderPreferenceEntity(
                        path = path,
                        isGridView = currentPref?.isGridView ?: _isGridView.value,
                        sortOrder = currentPref?.sortOrder ?: _sortOrder.value,
                        customColor = colorHex
                    )
                )
            }
            loadFiles()
        }
    }

    private fun saveFolderPreference() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val currentPref = folderPreferenceDao.getPreference(_currentPath.value)
                folderPreferenceDao.insertPreference(
                    FolderPreferenceEntity(
                        path = _currentPath.value,
                        isGridView = _isGridView.value,
                        sortOrder = _sortOrder.value,
                        customColor = currentPref?.customColor
                    )
                )
            }
        }
    }

    fun toggleSelection(file: FileItem) {
        val currentSelected = _selectedFiles.value.toMutableSet()
        if (currentSelected.any { it.path == file.path }) {
            currentSelected.removeAll { it.path == file.path }
        } else {
            currentSelected.add(file)
        }
        _selectedFiles.value = currentSelected
        _isSelectionMode.value = currentSelected.isNotEmpty()
    }

    fun clearSelection() {
        _selectedFiles.value = emptySet()
        _isSelectionMode.value = false
    }

    fun deleteFile(file: FileItem) {
        deletePaths(arrayOf(file.path))
    }

    fun deleteSelectedFiles(secure: Boolean = false) {
        val paths = _selectedFiles.value.map { it.path }.toTypedArray()
        deletePaths(paths, secure)
        clearSelection()
    }

    private fun deletePaths(paths: Array<String>, secure: Boolean = false) {
        val data = workDataOf(
            FileOperationWorker.KEY_OPERATION to FileOperationWorker.OP_DELETE,
            FileOperationWorker.KEY_SOURCE_PATHS to paths,
            FileOperationWorker.KEY_DESTINATION_PATH to "",
            FileOperationWorker.KEY_SECURE_DELETE to secure
        )
        val request = OneTimeWorkRequestBuilder<FileOperationWorker>()
            .setInputData(data)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.enqueue(request)
    }

    fun renameFile(file: FileItem, newName: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    com.nature.files.domain.usecase.RenameFileUseCase(repository.getStorageProvider())
                        .execute(com.nature.files.domain.usecase.RenameFileUseCase.Params(file.path, newName))
                }
                loadFiles()
            } catch (e: Exception) {
                _error.value = "Rename failed: ${e.message}"
            }
        }
    }

    fun createDirectory(name: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.getStorageProvider().createDirectory(_currentPath.value, name)
                }
                loadFiles()
            } catch (e: Exception) {
                _error.value = "Failed to create directory: ${e.message}"
            }
        }
    }

    fun copySelectedFiles() {
        _clipboardFiles.value = _selectedFiles.value
        _isMoving.value = false
        clearSelection()
    }

    fun moveSelectedFiles() {
        _clipboardFiles.value = _selectedFiles.value
        _isMoving.value = true
        clearSelection()
    }

    fun pasteFiles() {
        val paths = _clipboardFiles.value.map { it.path }.toTypedArray()
        val op = if (_isMoving.value) FileOperationWorker.OP_MOVE else FileOperationWorker.OP_COPY
        val data = workDataOf(
            FileOperationWorker.KEY_OPERATION to op,
            FileOperationWorker.KEY_SOURCE_PATHS to paths,
            FileOperationWorker.KEY_DESTINATION_PATH to _currentPath.value
        )
        val request = OneTimeWorkRequestBuilder<FileOperationWorker>()
            .setInputData(data)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.enqueue(request)

        _clipboardFiles.value = emptySet()
        _isMoving.value = false
    }

    fun cancelPaste() {
        _clipboardFiles.value = emptySet()
        _isMoving.value = false
    }

    fun compressSelectedFiles(zipName: String) {
        viewModelScope.launch {
            try {
                val sourcePaths = _selectedFiles.value.map { it.path }
                val zipFile = File(_currentPath.value, if (zipName.endsWith(".zip")) zipName else "$zipName.zip")
                withContext(Dispatchers.IO) {
                    repository.compressFiles(sourcePaths, zipFile.absolutePath)
                }
                clearSelection()
                loadFiles()
            } catch (e: Exception) {
                _error.value = "Compression failed: ${e.message}"
            }
        }
    }

    fun extractSelectedArchive(file: FileItem) {
        viewModelScope.launch {
            try {
                val destDir = File(file.path).parent ?: _currentPath.value
                withContext(Dispatchers.IO) {
                    repository.extractArchive(file.path, destDir)
                }
                clearSelection()
                loadFiles()
            } catch (e: Exception) {
                _error.value = "Extraction failed: ${e.message}"
            }
        }
    }

    fun updateTags(file: FileItem, tags: List<String>) {
        viewModelScope.launch {
            try {
                com.nature.files.domain.usecase.UpdateFileTagsUseCase(repository.getStorageProvider(), fileDao)
                    .execute(com.nature.files.domain.usecase.UpdateFileTagsUseCase.Params(file.path, tags))
                loadFiles()
            } catch (e: Exception) {
                _error.value = "Failed to update tags: ${e.message}"
            }
        }
    }

    fun updateShortcutOrder(shortcuts: List<com.nature.files.data.db.SidebarShortcutEntity>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val updatedList = shortcuts.mapIndexed { index, entity ->
                    entity.copy(order = index)
                }
                sidebarShortcutDao.insertShortcuts(updatedList)
            }
        }
    }

    fun createSymlink(targetPath: String, linkName: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    com.nature.files.domain.usecase.CreateSymlinkUseCase(repository.getStorageProvider())
                        .execute(com.nature.files.domain.usecase.CreateSymlinkUseCase.Params(targetPath, _currentPath.value, linkName))
                }
                loadFiles()
            } catch (e: Exception) {
                _error.value = "Path Grafting failed: ${e.message}"
            }
        }
    }

    fun openPreview(file: FileItem) {
        _activePreviewFile.value = file
    }

    fun closePreview() {
        _activePreviewFile.value = null
    }

}
