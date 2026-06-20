package com.nature.files

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nature.files.data.SortOrder
import com.nature.files.data.db.SidebarShortcutEntity
import com.nature.files.utils.FileUtils.formatSize
import com.nature.files.ui.FileViewModel
import com.nature.files.ui.components.*
import com.nature.files.ui.preview.*
import com.nature.files.ui.theme.*
import com.nature.files.ui.theme.NatureFilesTheme
import com.nature.files.ui.theme.NatureTheme
import kotlinx.coroutines.launch
import java.io.File
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Quality Gate: Handle widget navigation
        val intentPath = intent.getStringExtra("path")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                PermissionWrapper {
                    FileExplorerApp(initialPath = intentPath)
                }
            }
        }
    }

    fun showBiometricPrompt(onSuccess: () -> Unit) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Vault Access")
            .setSubtitle("Enter biometric to unlock the clearing")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@Composable
fun PermissionWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(checkStoragePermission(context))
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted || checkStoragePermission(context)
    }

    val manageStorageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        hasPermission = checkStoragePermission(context)
    }

    if (hasPermission) {
        content()
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(id = R.string.storage_permission_required))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:${context.packageName}")
                        manageStorageLauncher.launch(intent)
                    } else {
                        hasPermission = true
                    }
                } else {
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }) {
                Text(text = stringResource(id = R.string.grant_permission))
            }
        }
    }
}

private fun checkStoragePermission(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerApp(
    viewModel: FileViewModel = viewModel(),
    initialPath: String? = null
) {
    val context = LocalContext.current
    val db = remember { com.nature.files.data.db.AppDatabase.getDatabase(context) }
    val settings by db.appSettingsDao().getSettings().collectAsState(initial = null)
    var natureTheme by remember { mutableStateOf(NatureTheme.FOREST_FLOOR) }

    LaunchedEffect(settings) {
        settings?.let {
            natureTheme = NatureTheme.valueOf(it.natureTheme)
        }
    }

    LaunchedEffect(initialPath) {
        initialPath?.let { viewModel.navigateTo(it) }
    }

    val scope = rememberCoroutineScope()
    NatureFilesTheme(natureTheme = natureTheme) {
        Box(modifier = Modifier.leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))) {
            FileExplorerAppContent(
                viewModel = viewModel,
                currentTheme = natureTheme,
                onThemeChange = { newTheme ->
                    natureTheme = newTheme
                    scope.launch {
                        val current = settings ?: com.nature.files.data.db.AppSettingsEntity()
                        db.appSettingsDao().updateSettings(current.copy(natureTheme = newTheme.name))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerAppContent(
    viewModel: FileViewModel,
    currentTheme: NatureTheme,
    onThemeChange: (NatureTheme) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentPath by viewModel.currentPath.collectAsState()
    val files by viewModel.files.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedFiles by viewModel.selectedFiles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    val clipboardFiles by viewModel.clipboardFiles.collectAsState()
    val showHiddenFiles by viewModel.showHiddenFiles.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val animationsEnabled by viewModel.animationsEnabled.collectAsState()
    val metaphorsEnabled by viewModel.metaphorsEnabled.collectAsState()

    var isFtpRunning by remember { mutableStateOf(false) }
    var isWatcherRunning by remember { mutableStateOf(false) }

    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showCompressDialog by remember { mutableStateOf(false) }
    var showPropertiesDialog by remember { mutableStateOf(false) }
    var showChecksumDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showStorageAnalyzer by remember { mutableStateOf(false) }
    var showDuplicateFinder by remember { mutableStateOf(false) }
    var showTimelineView by remember { mutableStateOf(false) }
    var showPermissionAudit by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showSymlinkDialog by remember { mutableStateOf(false) }
    var isHomeMode by remember { mutableStateOf(true) }
    var duplicateGroups by remember { mutableStateOf<List<List<com.nature.files.data.FileItem>>>(emptyList()) }
    var fileToTag by remember { mutableStateOf<com.nature.files.data.FileItem?>(null) }
    var isSearchMode by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    val activePreviewFile by viewModel.activePreviewFile.collectAsState()

    val context = LocalContext.current as MainActivity
    val snackbarHostState = remember { SnackbarHostState() }
    val error by viewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val rootPath = Environment.getExternalStorageDirectory().absolutePath
    val isRoot = currentPath == rootPath

    BackHandler(enabled = activePreviewFile != null || drawerState.isOpen || isSelectionMode || isSearchMode || !isHomeMode || showPermissionAudit || showSettings || showTimelineView || showDuplicateFinder || showStorageAnalyzer) {
        if (activePreviewFile != null) {
            viewModel.closePreview()
        } else if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (isSelectionMode) {
            viewModel.clearSelection()
        } else if (isSearchMode) {
            isSearchMode = false
            viewModel.setSearchQuery("")
        } else if (showPermissionAudit) {
            showPermissionAudit = false
        } else if (showSettings) {
            showSettings = false
        } else if (showTimelineView) {
            showTimelineView = false
        } else if (showDuplicateFinder) {
            showDuplicateFinder = false
        } else if (showStorageAnalyzer) {
            showStorageAnalyzer = false
        } else if (!isRoot) {
            viewModel.navigateBack()
        } else {
            isHomeMode = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                // Integration: TreeRingStorageBar in Navigation Drawer
                val totalSpace = remember { formatSize(Environment.getExternalStorageDirectory().totalSpace) }
                val usedSpace = remember { formatSize(Environment.getExternalStorageDirectory().totalSpace - Environment.getExternalStorageDirectory().freeSpace) }
                val usedPercentage = remember {
                    val total = Environment.getExternalStorageDirectory().totalSpace
                    val free = Environment.getExternalStorageDirectory().freeSpace
                    if (total > 0) (total - free).toFloat() / total else 0f
                }
                val distribution by viewModel.fileTypeDistribution.collectAsState()
                TreeRingStorageBar(
                    usedPercentage = usedPercentage,
                    totalSpace = totalSpace,
                    usedSpace = usedSpace,
                    fileTypeDistribution = distribution,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                Text("Themes", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                NatureTheme.values().forEach { theme ->
                    NavigationDrawerItem(
                        label = { Text(theme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                        selected = false,
                        onClick = { onThemeChange(theme); scope.launch { drawerState.close() } },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Divider()
                Text("Shortcuts", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                val haptic = LocalHapticFeedback.current
                var shortcutsState by remember { mutableStateOf(shortcuts) }
                LaunchedEffect(shortcuts) { shortcutsState = shortcuts }

                shortcutsState.forEachIndexed { index, shortcut ->
                    NavigationDrawerItem(
                        icon = {
                            val icon = when (shortcut.iconName) {
                                "Storage" -> Icons.Default.Storage
                                "Lock" -> Icons.Default.Lock
                                else -> Icons.Default.Folder
                            }
                            Icon(icon, contentDescription = null)
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(shortcut.name, modifier = Modifier.weight(1f))
                                // Quality Gate: Haptic-enabled drag reorder metaphor
                                IconButton(onClick = {
                                    if (index > 0) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val newList = shortcutsState.toMutableList()
                                        val item = newList.removeAt(index)
                                        newList.add(index - 1, item)
                                        shortcutsState = newList
                                        viewModel.updateShortcutOrder(newList)
                                    }
                                }) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                                }
                                IconButton(onClick = {
                                    if (index < shortcutsState.size - 1) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val newList = shortcutsState.toMutableList()
                                        val item = newList.removeAt(index)
                                        newList.add(index + 1, item)
                                        shortcutsState = newList
                                        viewModel.updateShortcutOrder(newList)
                                    }
                                }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                                }
                            }
                        },
                        selected = currentPath == shortcut.path,
                        onClick = {
                            if (shortcut.id == "vault") {
                                context.showBiometricPrompt {
                                    viewModel.navigateTo(shortcut.path)
                                }
                            } else {
                                viewModel.navigateTo(shortcut.path)
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Divider()
                Text("Forest Tools", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Timeline, contentDescription = null) },
                    label = { Text("File Timeline") },
                    selected = false,
                    onClick = {
                        showTimelineView = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.CopyAll, contentDescription = null) },
                    label = { Text("Duplicate Finder") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            val useCase = com.nature.files.domain.usecase.FindDuplicatesUseCase(viewModel.getStorageProvider())
                            duplicateGroups = useCase.execute(com.nature.files.domain.usecase.FindDuplicatesUseCase.Params(currentPath))
                            showDuplicateFinder = true
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Divider()
                Text("LAN & Monitoring", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Router, contentDescription = null) },
                    label = { Text(if (isFtpRunning) "Stop FTP Server" else "Start FTP Server") },
                    selected = isFtpRunning,
                    onClick = {
                        val intent = Intent(context, com.nature.files.network.FtpServerService::class.java).apply {
                            action = if (isFtpRunning) com.nature.files.network.FtpServerService.ACTION_STOP else com.nature.files.network.FtpServerService.ACTION_START
                        }
                        context.startService(intent)
                        isFtpRunning = !isFtpRunning
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Shield, contentDescription = null) },
                    label = { Text("Permission Audit") },
                    selected = false,
                    onClick = {
                        showPermissionAudit = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        showSettings = true
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                    label = { Text(if (isWatcherRunning) "Stop File Watcher" else "Start File Watcher") },
                    selected = isWatcherRunning,
                    onClick = {
                        val intent = Intent(context, com.nature.files.workers.FileWatcherService::class.java).apply {
                            putExtra("path", currentPath)
                        }
                        if (isWatcherRunning) {
                            context.stopService(intent)
                        } else {
                            context.startService(intent)
                        }
                        isWatcherRunning = !isWatcherRunning
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    if (isSearchMode && !isSelectionMode) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            onBack = {
                                isSearchMode = false
                                viewModel.setSearchQuery("")
                            }
                        )
                    } else {
                        TopAppBar(
                            title = {
                                Text(
                                    text = if (isSelectionMode) "${selectedFiles.size} selected"
                                    else currentPath.split("/").last().ifEmpty { stringResource(R.string.internal_storage) }
                                )
                            },
                            navigationIcon = {
                                if (isSelectionMode) {
                                    IconButton(onClick = { viewModel.clearSelection() }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close Selection")
                                    }
                                } else if (!isHomeMode) {
                                    IconButton(onClick = { isHomeMode = true }) {
                                        Icon(Icons.Default.Home, contentDescription = "Home")
                                    }
                                } else {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            },
                            actions = {
                                if (isSelectionMode) {
                                    IconButton(onClick = { viewModel.copySelectedFiles() }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }
                                    IconButton(onClick = { viewModel.moveSelectedFiles() }) {
                                        Icon(Icons.Default.ContentCut, contentDescription = "Move")
                                    }
                                    IconButton(onClick = { viewModel.deleteSelectedFiles() }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                    IconButton(onClick = { viewModel.deleteSelectedFiles(secure = true) }) {
                                        Icon(Icons.Default.Eco, contentDescription = "Secure Delete")
                                    }
                                    IconButton(onClick = { showSortMenu = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                                    }
                                    DropdownMenu(
                                        expanded = showSortMenu,
                                        onDismissRequest = { showSortMenu = false }
                                    ) {
                                        if (selectedFiles.size == 1) {
                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.rename)) },
                                                onClick = { showRenameDialog = true; showSortMenu = false }
                                            )
                                        }
                                        DropdownMenuItem(
                                            text = { Text(stringResource(R.string.compress)) },
                                            onClick = { showCompressDialog = true; showSortMenu = false }
                                        )
                                        if (selectedFiles.size == 1 && selectedFiles.first().isDirectory) {
                                            DropdownMenuItem(
                                                text = { Text("Custom Color") },
                                                onClick = { showColorPicker = true; showSortMenu = false }
                                            )
                                        }
                                        if (selectedFiles.size == 1) {
                                            DropdownMenuItem(
                                                text = { Text("Properties") },
                                                onClick = { showPropertiesDialog = true; showSortMenu = false }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Checksum") },
                                                onClick = { showChecksumDialog = true; showSortMenu = false }
                                            )
                                        }
                                        DropdownMenuItem(
                                            text = { Text("Storage Analyzer") },
                                            onClick = { showStorageAnalyzer = true; showSortMenu = false }
                                        )
                                        if (selectedFiles.size == 1) {
                                            DropdownMenuItem(
                                                text = { Text("Path Graft (Symlink)") },
                                                onClick = { showSymlinkDialog = true; showSortMenu = false }
                                            )
                                        }
                                    }
                                } else {
                                    IconButton(onClick = { isSearchMode = true }) {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                    }
                                    IconButton(onClick = { viewModel.toggleGridView() }) {
                                        Icon(
                                            if (isGridView) Icons.Default.ViewList else Icons.Default.ViewModule,
                                            contentDescription = "Toggle View"
                                        )
                                    }
                                    Box {
                                        IconButton(onClick = { showSortMenu = true }) {
                                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                                        }
                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false }
                                        ) {
                                            SortOrder.values().forEach { order ->
                                                DropdownMenuItem(
                                                    text = { Text(order.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                                    onClick = { viewModel.setSortOrder(order); showSortMenu = false }
                                                )
                                            }
                                            Divider()
                                            DropdownMenuItem(
                                                text = { Text(if (showHiddenFiles) "Hide Hidden" else "Show Hidden") },
                                                onClick = { viewModel.toggleShowHiddenFiles(); showSortMenu = false }
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        if (!isSearchMode && !isSelectionMode && !isHomeMode) {
                            val tabs by viewModel.tabs.collectAsState()
                            val activeTabIndex by viewModel.activeTabIndex.collectAsState()

                            ScrollableTabRow(
                                selectedTabIndex = activeTabIndex,
                                containerColor = Color.Transparent,
                                edgePadding = 16.dp,
                                divider = {}
                            ) {
                                tabs.forEachIndexed { index, path ->
                                    Tab(
                                        selected = activeTabIndex == index,
                                        onClick = { viewModel.switchTab(index) },
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(path.split("/").last().ifEmpty { "Root" })
                                                if (tabs.size > 1) {
                                                    IconButton(onClick = { viewModel.closeTab(index) }, modifier = Modifier.size(16.dp)) {
                                                        Icon(Icons.Default.Close, contentDescription = null)
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                                IconButton(onClick = { viewModel.openNewTab() }) {
                                    Icon(Icons.Default.Add, contentDescription = "New Tab")
                                }
                            }

                            ForestPathBreadcrumb(
                                path = currentPath,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                Column(horizontalAlignment = Alignment.End) {
                    if (clipboardFiles.isNotEmpty()) {
                        ExtendedFloatingActionButton(
                            onClick = { viewModel.pasteFiles() },
                            icon = { Icon(Icons.Default.ContentPaste, contentDescription = null) },
                            text = { Text("Paste (${clipboardFiles.size})") },
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    if (!isSelectionMode) {
                        FloatingActionButton(onClick = { showCreateFolderDialog = true }) {
                            Icon(Icons.Default.CreateNewFolder, contentDescription = "Create Folder")
                        }
                    }
                }
            }
        ) { paddingValues ->
            if (isHomeMode) {
                HomeScreen(
                    onNavigateToPath = { path ->
                        viewModel.navigateTo(path)
                        isHomeMode = false
                    },
                    onToolClick = { tool ->
                        when(tool) {
                            "Timeline" -> showTimelineView = true
                            "Duplicates" -> {
                                scope.launch {
                                    val useCase = com.nature.files.domain.usecase.FindDuplicatesUseCase(viewModel.getStorageProvider())
                                    duplicateGroups = useCase.execute(com.nature.files.domain.usecase.FindDuplicatesUseCase.Params(currentPath))
                                    showDuplicateFinder = true
                                }
                            }
                            "Analyzer" -> showStorageAnalyzer = true
                            "Audit" -> showPermissionAudit = true
                            "FTP" -> {
                                val intent = Intent(context, com.nature.files.network.FtpServerService::class.java).apply {
                                    action = if (isFtpRunning) com.nature.files.network.FtpServerService.ACTION_STOP else com.nature.files.network.FtpServerService.ACTION_START
                                }
                                context.startService(intent)
                                isFtpRunning = !isFtpRunning
                            }
                            "Watcher" -> {
                                val intent = Intent(context, com.nature.files.workers.FileWatcherService::class.java).apply {
                                    putExtra("path", currentPath)
                                }
                                if (isWatcherRunning) context.stopService(intent) else context.startService(intent)
                                isWatcherRunning = !isWatcherRunning
                            }
                        }
                    },
                    shortcuts = shortcuts,
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                androidx.compose.animation.AnimatedContent(
                    targetState = currentPath,
                    transitionSpec = {
                        if (animationsEnabled) {
                            val easing = androidx.compose.animation.core.FastOutSlowInEasing
                            // Nature Design Mandate: Petal-bloom shared element transition metaphor
                            (scaleIn(initialScale = 0.8f, animationSpec = tween(700, easing = easing)) +
                                    fadeIn(animationSpec = tween(700, easing = easing)))
                                .togetherWith(scaleOut(targetScale = 1.2f, animationSpec = tween(700, easing = easing)) +
                                        fadeOut(animationSpec = tween(700, easing = easing)))
                        } else {
                            EnterTransition.None togetherWith ExitTransition.None
                        }
                    },
                    label = "PetalBloomTransition"
                ) { targetPath ->
                    FileExplorerScreen(
                        files = files,
                        paddingValues = paddingValues,
                        isLoading = isLoading,
                        onFileClick = { fileItem ->
                            if (isSelectionMode) {
                                viewModel.toggleSelection(fileItem)
                            } else if (fileItem.isDirectory) {
                                if (fileItem.name == "Vault") {
                                    context.showBiometricPrompt {
                                        viewModel.navigateTo(fileItem.path)
                                    }
                                } else {
                                    viewModel.navigateTo(fileItem.path)
                                }
                            } else {
                                viewModel.openPreview(fileItem)
                            }
                        },
                        onFileLongClick = { fileItem ->
                            viewModel.toggleSelection(fileItem)
                        },
                        onDelete = { fileItem ->
                            viewModel.deleteFile(fileItem)
                        },
                        onTagClick = { fileItem ->
                            fileToTag = fileItem
                            showPropertiesDialog = true
                        },
                        selectedFiles = selectedFiles,
                        isGridView = isGridView,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onConfirm = { name ->
                viewModel.createDirectory(name)
                showCreateFolderDialog = false
            }
        )
    }

    if (showRenameDialog && selectedFiles.size == 1) {
        val fileToRename = selectedFiles.first()
        RenameDialog(
            initialName = fileToRename.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                viewModel.renameFile(fileToRename, newName)
                showRenameDialog = false
                viewModel.clearSelection()
            }
        )
    }

    if (showCompressDialog) {
        CompressDialog(
            onDismiss = { showCompressDialog = false },
            onConfirm = { zipName ->
                viewModel.compressSelectedFiles(zipName)
                showCompressDialog = false
            }
        )
    }

    if (showPropertiesDialog && (selectedFiles.size == 1 || fileToTag != null)) {
        val file = fileToTag ?: selectedFiles.first()
        RichPreviewSheet(
            fileItem = file,
            onDismiss = { showPropertiesDialog = false; fileToTag = null },
            onUpdateTags = { tags ->
                viewModel.updateTags(file, tags)
            },
            currentTags = file.tags
        )
    }

    if (showChecksumDialog && selectedFiles.isNotEmpty()) {
        val file1 = selectedFiles.first()
        val file2 = if (selectedFiles.size > 1) selectedFiles.toList()[1] else null
        ChecksumVerifierDialog(
            file1 = File(file1.path),
            file2 = file2?.let { File(it.path) },
            onDismiss = { showChecksumDialog = false }
        )
    }

    if (showColorPicker && selectedFiles.size == 1) {
        val file = selectedFiles.first()
        ColorPickerDialog(
            onDismiss = { showColorPicker = false },
            onColorSelected = { colorHex ->
                viewModel.updateFolderColor(file.path, colorHex)
                showColorPicker = false
                viewModel.clearSelection()
            }
        )
    }


    if (showSymlinkDialog && selectedFiles.size == 1) {
        val target = selectedFiles.first()
        var linkName by remember { mutableStateOf("${target.name}_graft") }
        NatureDialog(
            onDismissRequest = { showSymlinkDialog = false },
            title = "Path Grafting",
            confirmButton = {
                Button(onClick = {
                    viewModel.createSymlink(target.path, linkName)
                    showSymlinkDialog = false
                    viewModel.clearSelection()
                }) {
                    Text("Graft")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSymlinkDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            Text("Graft a branch (symlink) of '${target.name}' here.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            TextField(
                value = linkName,
                onValueChange = { linkName = it },
                label = { Text("Graft Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDuplicateFinder) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Duplicate Finder", style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                    navigationIcon = {
                        IconButton(onClick = { showDuplicateFinder = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ForestFloorBackground,
                        titleContentColor = BarkBrown
                    )
                )
            }
        ) { padding ->
            DuplicateFinderScreen(
                duplicateGroups = duplicateGroups,
                onDeleteFile = { file ->
                    viewModel.deleteFile(file)
                    // Update local state after deletion
                    duplicateGroups = duplicateGroups.map { group ->
                        group.filter { it.path != file.path }
                    }.filter { it.size > 1 }
                },
                modifier = Modifier.padding(padding).fillMaxSize()
            )
        }
    }

    if (showTimelineView) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("File Timeline", style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                    navigationIcon = {
                        IconButton(onClick = { showTimelineView = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ForestFloorBackground,
                        titleContentColor = BarkBrown
                    )
                )
            }
        ) { padding ->
            TimelineViewScreen(
                files = files,
                onFileClick = { file -> },
                modifier = Modifier.padding(padding).fillMaxSize()
            )
        }
    }

    if (showStorageAnalyzer) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Storage Analyzer", style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                    navigationIcon = {
                        IconButton(onClick = { showStorageAnalyzer = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ForestFloorBackground,
                        titleContentColor = BarkBrown
                    )
                )
            }
        ) { padding ->
            StorageAnalyzerTreemap(
                files = files,
                modifier = Modifier.padding(padding).fillMaxSize()
            )
        }
    }

    if (showPermissionAudit) {
        PermissionAuditScreen(
            onBack = { showPermissionAudit = false },
            modifier = Modifier.fillMaxSize()
        )
    }

    if (showSettings) {
        val cloudConnections by viewModel.cloudConnections.collectAsState()
        val leftSwipeAction by viewModel.leftSwipeAction.collectAsState()
        val rightSwipeAction by viewModel.rightSwipeAction.collectAsState()
        SettingsScreen(
            onBack = { showSettings = false },
            showHidden = showHiddenFiles,
            onShowHiddenChange = { viewModel.toggleShowHiddenFiles() },
            animationsEnabled = animationsEnabled,
            onAnimationsEnabledChange = { viewModel.toggleAnimationsEnabled() },
            metaphorsEnabled = metaphorsEnabled,
            onMetaphorsEnabledChange = { viewModel.toggleMetaphorsEnabled() },
            leftSwipeAction = leftSwipeAction,
            rightSwipeAction = rightSwipeAction,
            onSwipeActionsChange = { left, right -> viewModel.setSwipeActions(left, right) },
            currentTheme = currentTheme,
            onThemeChange = { onThemeChange(it) },
            cloudConnections = cloudConnections,
            onAddConnection = { viewModel.addCloudConnection(it) },
            onDeleteConnection = { viewModel.deleteCloudConnection(it) },
            modifier = Modifier.fillMaxSize()
        )
    }

    activePreviewFile?.let { file ->
        Box(modifier = Modifier.fillMaxSize()) {
            val mimeType = file.mimeType ?: com.nature.files.utils.FileUtils.getMimeTypeFromName(file.name)
            val storageProvider = viewModel.getStorageProvider()

            when {
                mimeType?.startsWith("image/") == true -> {
                    ImagePreview(file, storageProvider, onBack = { viewModel.closePreview() })
                }
                mimeType?.startsWith("video/") == true -> {
                    VideoPlayer(file, storageProvider, onBack = { viewModel.closePreview() })
                }
                mimeType?.startsWith("audio/") == true -> {
                    AudioPlayer(file, storageProvider, onBack = { viewModel.closePreview() })
                }
                mimeType == "application/pdf" -> {
                    PdfViewer(file, storageProvider, onBack = { viewModel.closePreview() })
                }
                mimeType == "text/html" -> {
                    HtmlViewer(file, storageProvider, onBack = { viewModel.closePreview() })
                }
                mimeType == "application/vnd.android.package-archive" -> {
                    ApkBatchInstaller(listOf(file), onDismiss = { viewModel.closePreview() })
                }
                mimeType?.startsWith("text/") == true || mimeType == "application/json" || mimeType == "application/xml" -> {
                    TextEditor(file, storageProvider, onBack = { viewModel.closePreview() })
                }
                else -> {
                    // Fallback to text editor for unknown types or show error
                    TextEditor(file, storageProvider, onBack = { viewModel.closePreview() })
                }
            }
        }
    }
}

@Composable
fun FileExplorerScreen(
    files: List<com.nature.files.data.FileItem>,
    paddingValues: PaddingValues,
    isLoading: Boolean,
    onFileClick: (com.nature.files.data.FileItem) -> Unit,
    onFileLongClick: (com.nature.files.data.FileItem) -> Unit,
    onDelete: (com.nature.files.data.FileItem) -> Unit,
    onTagClick: (com.nature.files.data.FileItem) -> Unit,
    selectedFiles: Set<com.nature.files.data.FileItem>,
    isGridView: Boolean = false,
    viewModel: FileViewModel
) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        if (files.isEmpty() && !isLoading) {
            EmptyForestClearing()
        } else {
            if (isGridView) {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 100.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        count = files.size,
                        key = { index -> files[index].path }
                    ) { index ->
                        val fileItem = files[index]
                        val isSelected = selectedFiles.any { it.path == fileItem.path }
                        FileItemGrid(
                            fileItem = fileItem,
                            isSelected = isSelected,
                            onClick = { onFileClick(fileItem) },
                            onLongClick = { onFileLongClick(fileItem) }
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(
                        items = files,
                        key = { it.path }
                    ) { fileItem ->
                        val isSelected = selectedFiles.any { it.path == fileItem.path }
                        val leftAction by viewModel.leftSwipeAction.collectAsState()
                        val rightAction by viewModel.rightSwipeAction.collectAsState()
                        FileItemRow(
                            fileItem = fileItem,
                            isSelected = isSelected,
                            onClick = { onFileClick(fileItem) },
                            onLongClick = { onFileLongClick(fileItem) },
                            onDelete = { onDelete(fileItem) },
                            onTag = { onTagClick(fileItem) },
                            leftSwipeAction = leftAction,
                            rightSwipeAction = rightAction
                        )
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun EmptyForestClearing() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(120.dp)) {
            // Draw a forest clearing illustration
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            // Ground
            drawCircle(
                color = com.nature.files.ui.theme.LichenGrey.copy(alpha = 0.3f),
                radius = size.width / 2f,
                center = androidx.compose.ui.geometry.Offset(centerX, centerY + 20.dp.toPx())
            )

            // A tiny sapling
            val saplingPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(centerX, centerY + 30.dp.toPx())
                lineTo(centerX, centerY - 10.dp.toPx())
            }
            drawPath(
                path = saplingPath,
                color = com.nature.files.ui.theme.BarkBrown,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )

            // Leaves on sapling
            drawCircle(
                color = com.nature.files.ui.theme.CanopyGreen,
                radius = 8.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(centerX, centerY - 10.dp.toPx())
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "This folder is a clearing — nothing here yet 🌿",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = com.nature.files.ui.theme.Spectral
            ),
            color = com.nature.files.ui.theme.BarkBrown,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Step lightly, for nature is at rest.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = com.nature.files.ui.theme.Spectral,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            ),
            color = com.nature.files.ui.theme.LichenGrey
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxSize(),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun RenameDialog(initialName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newName by remember { mutableStateOf(initialName) }
    NatureDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.rename),
        confirmButton = {
            Button(onClick = { onConfirm(newName) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        TextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text(stringResource(R.string.rename)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = CanopyGreen,
                unfocusedIndicatorColor = BarkBrown
            )
        )
    }
}

@Composable
fun CompressDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var zipName by remember { mutableStateOf("archive") }
    NatureDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.compress),
        confirmButton = {
            Button(onClick = { onConfirm(zipName) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        TextField(
            value = zipName,
            onValueChange = { zipName = it },
            label = { Text(stringResource(R.string.zip_name)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = CanopyGreen,
                unfocusedIndicatorColor = BarkBrown
            )
        )
    }
}

@Composable
fun CreateFolderDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var folderName by remember { mutableStateOf("") }
    NatureDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.new_folder),
        confirmButton = {
            Button(onClick = { onConfirm(folderName) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        TextField(
            value = folderName,
            onValueChange = { folderName = it },
            label = { Text(stringResource(R.string.folder_name)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = CanopyGreen,
                unfocusedIndicatorColor = BarkBrown
            )
        )
    }
}
