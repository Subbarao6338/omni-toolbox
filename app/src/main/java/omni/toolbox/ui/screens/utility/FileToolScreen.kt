package omni.toolbox.ui.screens.utility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.launch
import java.io.File

data class FileItem(val name: String, val isDirectory: Boolean, val size: String = "", val file: File? = null)

@Composable
fun FileToolScreen(navController: NavHostController, title: String) {
    val context = LocalContext.current
    val rootDir = context.filesDir
    var currentDir by remember { mutableStateOf(rootDir) }
    var fileItems by remember { mutableStateOf(listOf<FileItem>()) }
    var showRenameDialog by remember { mutableStateOf<File?>(null) }
    var newFileName by remember { mutableStateOf("") }
    var storageMode by remember { mutableStateOf("Local") }
    var isRootEnabled by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun refreshFiles() {
        val files = currentDir.listFiles()?.toList() ?: emptyList()
        fileItems = files.map { file ->
            FileItem(
                name = file.name,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) "" else "${file.length() / 1024} KB",
                file = file
            )
        }.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
    }

    LaunchedEffect(currentDir) {
        if (currentDir == rootDir && (rootDir.listFiles()?.isEmpty() == true)) {
            File(rootDir, "Welcome_Note.txt").writeText("Welcome to Omni Toolbox File Manager!")
            File(rootDir, "System_Logs").mkdir()
            File(File(rootDir, "System_Logs"), "boot.log").writeText("System initialized.")
        }
        refreshFiles()
    }

    ToolScreen(
        title = title,
        onBack = {
            if (currentDir != rootDir && storageMode == "Local") {
                currentDir = currentDir.parentFile ?: rootDir
            } else {
                navController.popBackStack()
            }
        },
        actions = {
            IconButton(onClick = { /* Search */ }) { Icon(Icons.Default.Search, null) }
            IconButton(onClick = { /* Filter */ }) { Icon(Icons.Default.FilterList, null) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = when(storageMode) {
                    "Local" -> 0
                    "Cloud" -> 1
                    "NAS" -> 2
                    "Root" -> 3
                    else -> 0
                },
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                Tab(selected = storageMode == "Local", onClick = { storageMode = "Local" }) {
                    Text("Local", Modifier.padding(12.dp))
                }
                Tab(selected = storageMode == "Cloud", onClick = { storageMode = "Cloud" }) {
                    Text("Cloud", Modifier.padding(12.dp))
                }
                Tab(selected = storageMode == "NAS", onClick = { storageMode = "NAS" }) {
                    Text("NAS", Modifier.padding(12.dp))
                }
                Tab(selected = storageMode == "Root", onClick = { storageMode = "Root" }) {
                    Text("Root", Modifier.padding(12.dp))
                }
            }

            if (title == "Storage Cleaner") {
                StorageCleanerHeader()
            }

            Text(
                text = if (storageMode == "Local") "Path: ${currentDir.absolutePath.replace(context.applicationInfo.dataDir, "")}" else "$storageMode Storage",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Box(modifier = Modifier.weight(1f)) {
                when (storageMode) {
                    "Cloud" -> CloudStorageView()
                    "NAS" -> NASStorageView { server, share, user, pass, path ->
                        scope.launch {
                            val dest = File(context.cacheDir, "smb_download_${System.currentTimeMillis()}.tmp")
                            val success = NASManager.connectSMB(context, server, share, user, pass, path, dest)
                            if (success) {
                                android.widget.Toast.makeText(context, "Downloaded to ${dest.name}", android.widget.Toast.LENGTH_LONG).show()
                            } else {
                                android.widget.Toast.makeText(context, "SMB Connection Failed", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    "Root" -> {
                        if (!isRootEnabled) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Security, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.height(16.dp))
                                    Text("Root Access Required", style = MaterialTheme.typography.titleMedium)
                                    Button(onClick = { isRootEnabled = true }, Modifier.padding(16.dp)) {
                                        Text("Grant Permission")
                                    }
                                }
                            }
                        } else {
                            Text("Root filesystem mounted.", modifier = Modifier.padding(16.dp))
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(fileItems) { item ->
                                FileItemRow(item, onRename = {
                                    showRenameDialog = item.file
                                    newFileName = item.name
                                }, onDelete = {
                                    item.file?.delete()
                                    refreshFiles()
                                }, onClick = {
                                    if (item.isDirectory && item.file != null) {
                                        currentDir = item.file
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }

        if (showRenameDialog != null) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = null },
                title = { Text("Rename File") },
                text = {
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("New Name") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val file = showRenameDialog!!
                        val newFile = File(file.parentFile, newFileName)
                        if (file.renameTo(newFile)) {
                            refreshFiles()
                        }
                        showRenameDialog = null
                    }) {
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
    }
}

@Composable
fun FileItemRow(item: FileItem, onRename: () -> Unit, onDelete: () -> Unit, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(item.name) },
        supportingContent = { if (!item.isDirectory) Text(item.size) },
        leadingContent = {
            Icon(
                if (item.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = if (item.isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        },
        trailingContent = {
            Row {
                IconButton(onClick = onRename) {
                    Icon(Icons.Default.Edit, contentDescription = "Rename", modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(20.dp))
                }
            }
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun CloudStorageView() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Connected Accounts", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        CloudAccountItem("Google Drive", "active", Icons.Default.CloudQueue)
        CloudAccountItem("OneDrive", "not connected", Icons.Default.CloudOff)
        CloudAccountItem("Mega.nz", "not connected", Icons.Default.Lock)

        Spacer(Modifier.height(24.dp))
        Button(onClick = {}, Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text("Add Cloud Provider")
        }
    }
}

@Composable
fun CloudAccountItem(name: String, status: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        ListItem(
            headlineContent = { Text(name) },
            supportingContent = { Text(status) },
            leadingContent = { Icon(icon, null) },
            trailingContent = { Icon(Icons.Default.ChevronRight, null) }
        )
    }
}

@Composable
fun NASStorageView(onConnect: (String, String, String, String, String) -> Unit) {
    var server by remember { mutableStateOf("192.168.1.100") }
    var share by remember { mutableStateOf("Public") }
    var user by remember { mutableStateOf("guest") }
    var pass by remember { mutableStateOf("") }
    var path by remember { mutableStateOf("test.txt") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Manual SMB Connection", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = server, onValueChange = { server = it }, label = { Text("Server IP") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = share, onValueChange = { share = it }, label = { Text("Share Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        OutlinedTextField(value = path, onValueChange = { path = it }, label = { Text("Remote Path") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = { onConnect(server, share, user, pass, path) },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text("Connect & Download")
        }
    }
}

@Composable
fun StorageCleanerHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Storage Usage", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Used: 48 GB", style = MaterialTheme.typography.bodySmall)
                Text("Free: 16 GB", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* Clean */ }, modifier = Modifier.align(Alignment.End)) {
                Text("Clean Now")
            }
        }
    }
}
