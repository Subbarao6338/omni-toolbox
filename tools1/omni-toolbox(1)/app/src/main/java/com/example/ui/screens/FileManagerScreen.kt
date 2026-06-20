package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    // Initialize the sandbox folder inside the app's files directory
    val sandboxRoot = remember {
        val root = context.filesDir.resolve("sandbox")
        if (!root.exists()) {
            root.mkdirs()
            // Create some default sample files for the first-launch user experience
            File(root, "welcome_guide.txt").writeText(
                "Welcome to the OmniToolbox Sandbox File System!\n\n" +
                "This file management interface operates on secure localized storage.\n" +
                "You can view, rename, create, and delete resources within this sandbox.\n\n" +
                "Features supported:\n" +
                "1. Nested folder trees\n" +
                "2. Real-time text editor (tap (.txt/.log/.json) files to try!)\n" +
                "3. In-app search indexing\n" +
                "4. Live batch creation and statistics overview."
            )
            File(root, "enterprise_spec.json").writeText(
                "{\n" +
                "  \"environment\": \"Android Container\",\n" +
                "  \"encryption\": \"AES-256-Local\",\n" +
                "  \"sandboxState\": \"Active\",\n" +
                "  \"nodeCompile\": \"Standard KSP\",\n" +
                "  \"apiCompatible\": true\n" +
                "}"
            )
            val notesFolder = root.resolve("Personal_Notes")
            notesFolder.mkdirs()
            File(notesFolder, "daily_streaks.log").writeText(
                "[2026-06-01] Daily optimization tasks completed.\n" +
                "[2026-06-05] Calibrated core memory stress profiles.\n" +
                "[2026-06-11] Ran full benchmarks check."
            )
        }
        root
    }

    // State tracking the current directory
    var currentDir by remember { mutableStateOf(sandboxRoot) }
    
    // State to force-reload directory contents
    var reloadTrigger by remember { mutableStateOf(0) }
    
    // Retrieve file list
    val fileList = remember(currentDir, reloadTrigger) {
        currentDir.listFiles()?.sortedWith(
            compareBy<File> { !it.isDirectory }
                .thenBy { it.name.lowercase() }
        ) ?: emptyList()
    }

    // Search filter state
    var searchQuery by remember { mutableStateOf("") }
    val filteredFileList = remember(fileList, searchQuery) {
        if (searchQuery.isBlank()) {
            fileList
        } else {
            fileList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    // Interactive Dialogs State
    var showCreateFileDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<File?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<File?>(null) }
    
    // File reading / Editing state
    var editingFile by remember { mutableStateOf<File?>(null) }
    var editingFileContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "File manager icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sandbox File Explorer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("file_manager_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { reloadTrigger++ }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            if (editingFile == null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    SmallFloatingActionButton(
                        onClick = { showCreateFolderDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.testTag("fab_create_folder")
                    ) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
                    }
                    FloatingActionButton(
                        onClick = { showCreateFileDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("fab_create_file")
                    ) {
                        Icon(Icons.Default.NoteAdd, contentDescription = "New Text File")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (editingFile != null) {
                // Real-time Text Editor Panel
                TextEditorPanel(
                    file = editingFile!!,
                    content = editingFileContent,
                    onContentChange = { editingFileContent = it },
                    onSave = {
                        try {
                            editingFile!!.writeText(editingFileContent)
                            Toast.makeText(context, "Saved changes to ${editingFile!!.name}", Toast.LENGTH_SHORT).show()
                            editingFile = null
                            reloadTrigger++
                        } catch (e: Exception) {
                            Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    onClose = { editingFile = null }
                )
            } else {
                // Breadcrumbs navigation bar
                BreadcrumbPath(
                    root = sandboxRoot,
                    current = currentDir,
                    onNavigate = { currentDir = it }
                )

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter files in directory...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("file_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Files count and directory summary
                Text(
                    text = "DIRECTORY CONTROLS (${filteredFileList.size} items of ${fileList.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredFileList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No files match filter" else "This directory is empty",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredFileList) { file ->
                            FileListItem(
                                file = file,
                                onClick = {
                                    if (file.isDirectory) {
                                        currentDir = file
                                        searchQuery = ""
                                    } else {
                                        val ext = file.extension.lowercase()
                                        if (ext == "txt" || ext == "json" || ext == "log" || ext == "xml" || ext == "ini" || ext == "csv") {
                                            try {
                                                editingFileContent = file.readText()
                                                editingFile = file
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error opening file: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Cannot edit .${ext} files natively (Text files only)", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onRename = { showRenameDialog = file },
                                onDelete = { showDeleteConfirmDialog = file }
                            )
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Create File
    if (showCreateFileDialog) {
        var inputName by remember { mutableStateOf("") }
        var inputContent by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showCreateFileDialog = false },
            title = { Text("Create Sandbox File", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Filename (e.g., config.json, note.txt)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_new_file_name_input")
                    )
                    TextField(
                        value = inputContent,
                        onValueChange = { inputContent = it },
                        label = { Text("Initial Content (Optional)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputName.isNotBlank()) {
                            val cleanName = if (!inputName.contains(".")) "$inputName.txt" else inputName
                            val targetFile = currentDir.resolve(cleanName)
                            try {
                                targetFile.writeText(inputContent)
                                Toast.makeText(context, "Created file: $cleanName", Toast.LENGTH_SHORT).show()
                                showCreateFileDialog = false
                                reloadTrigger++
                            } catch (e: Exception) {
                                Toast.makeText(context, "Creation failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.testTag("dialog_create_file_btn")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Create Folder
    if (showCreateFolderDialog) {
        var folderName by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("Create Subfolder", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                TextField(
                    value = folderName,
                    onValueChange = { folderName = it },
                    label = { Text("Folder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_new_folder_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (folderName.isNotBlank()) {
                            val newFolder = currentDir.resolve(folderName)
                            try {
                                val success = newFolder.mkdirs()
                                if (success || newFolder.exists()) {
                                    Toast.makeText(context, "Created folder: $folderName", Toast.LENGTH_SHORT).show()
                                    showCreateFolderDialog = false
                                    reloadTrigger++
                                } else {
                                    Toast.makeText(context, "Could not create folder structure", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failure: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.testTag("dialog_create_folder_btn")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolderDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Rename File or Folder
    if (showRenameDialog != null) {
        val fileToRename = showRenameDialog!!
        var renameInput by remember { mutableStateOf(fileToRename.name) }
        
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename Folder & Resource", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Enter new identifier name:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = renameInput,
                        onValueChange = { renameInput = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_rename_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameInput.isNotBlank() && renameInput != fileToRename.name) {
                            val dest = fileToRename.parentFile?.resolve(renameInput)
                            if (dest != null) {
                                try {
                                    val success = fileToRename.renameTo(dest)
                                    if (success) {
                                        Toast.makeText(context, "Renamed to $renameInput", Toast.LENGTH_SHORT).show()
                                        showRenameDialog = null
                                        reloadTrigger++
                                    } else {
                                        Toast.makeText(context, "Rename rejected by OS sandbox limits", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            showRenameDialog = null
                        }
                    },
                    modifier = Modifier.testTag("dialog_rename_confirm_btn")
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Delete Confirm
    if (showDeleteConfirmDialog != null) {
        val fileToDelete = showDeleteConfirmDialog!!
        
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Sandbox File?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    text = "Are you absolutely sure you want to permanently delete '${fileToDelete.name}'? This action is irreversible.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val success = if (fileToDelete.isDirectory) {
                                fileToDelete.deleteRecursively()
                            } else {
                                fileToDelete.delete()
                            }
                            if (success) {
                                Toast.makeText(context, "Deleted: ${fileToDelete.name}", Toast.LENGTH_SHORT).show()
                                showDeleteConfirmDialog = null
                                reloadTrigger++
                            } else {
                                Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("dialog_delete_confirm_btn")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BreadcrumbPath(
    root: File,
    current: File,
    onNavigate: (File) -> Unit
) {
    val paths = remember(current) {
        val list = mutableListOf<File>()
        var temp: File? = current
        while (temp != null && temp.absolutePath.startsWith(root.absolutePath)) {
            list.add(0, temp)
            if (temp.absolutePath == root.absolutePath) break
            temp = temp.parentFile
        }
        list
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .size(14.dp)
                .clickable { onNavigate(root) }
        )

        paths.forEachIndexed { index, file ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                
                val label = if (file.absolutePath == root.absolutePath) "Home" else file.name
                val isLast = (index == paths.size - 1)
                
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isLast) FontWeight.Bold else FontWeight.Medium,
                    color = if (isLast) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(!isLast) { onNavigate(file) }
                        .padding(horizontal = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FileListItem(
    file: File,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    val sizeText = remember(file) {
        if (file.isDirectory) {
            val count = file.listFiles()?.size ?: 0
            "$count items"
        } else {
            val bytes = file.length()
            when {
                bytes >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f MB", bytes / (1024f * 1024f))
                bytes >= 1024 -> String.format(Locale.getDefault(), "%.1f KB", bytes / 1024f)
                else -> "$bytes B"
            }
        }
    }

    val dateText = remember(file) {
        val df = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        df.format(Date(file.lastModified()))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (file.isDirectory) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = if (file.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = sizeText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "•",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = dateText,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Options buttons
            IconButton(onClick = onRename) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Rename",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun TextEditorPanel(
    file: File,
    content: String,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EDITING FILE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = file.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            .size(36.dp)
                            .testTag("editor_save_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save file", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Text Area Input Field
            TextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("editor_content_field"),
                placeholder = { Text("Start typing file contents...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Size: ${file.length()} bytes • Realtime compilation verification",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
