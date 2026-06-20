package com.nature.files.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.NatureTheme
import com.nature.files.ui.theme.Spectral

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    showHidden: Boolean,
    onShowHiddenChange: (Boolean) -> Unit,
    animationsEnabled: Boolean,
    onAnimationsEnabledChange: (Boolean) -> Unit,
    metaphorsEnabled: Boolean,
    onMetaphorsEnabledChange: (Boolean) -> Unit,
    leftSwipeAction: String = "DELETE",
    rightSwipeAction: String = "TAG",
    onSwipeActionsChange: (String, String) -> Unit = { _, _ -> },
    currentTheme: NatureTheme,
    onThemeChange: (NatureTheme) -> Unit,
    cloudConnections: List<com.nature.files.data.db.CloudConnectionEntity> = emptyList(),
    onAddConnection: (com.nature.files.data.db.CloudConnectionEntity) -> Unit = {},
    onDeleteConnection: (com.nature.files.data.db.CloudConnectionEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showThemeMenu by remember { mutableStateOf(false) }
    var showAddConnectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forest Settings", style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestFloorBackground,
                    titleContentColor = BarkBrown
                )
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("General Trail Preferences", style = MaterialTheme.typography.titleMedium, color = BarkBrown)
            Spacer(Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Show Hidden Overgrowth") },
                supportingContent = { Text("Reveal files starting with a dot") },
                trailingContent = {
                    Switch(checked = showHidden, onCheckedChange = onShowHiddenChange)
                }
            )

            Box {
                ListItem(
                    headlineContent = { Text("Current Environment (Theme)") },
                    supportingContent = { Text(currentTheme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                    modifier = Modifier.clickable { showThemeMenu = true }
                )
                DropdownMenu(expanded = showThemeMenu, onDismissRequest = { showThemeMenu = false }) {
                    NatureTheme.values().forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(theme.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                onThemeChange(theme)
                                showThemeMenu = false
                            }
                        )
                    }
                }
            }

            Text("Living Forest Experience", style = MaterialTheme.typography.titleMedium, color = BarkBrown)
            Spacer(Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Enable Leaf Animations") },
                supportingContent = { Text("Petal-bloom transitions and leaf-particle trails") },
                trailingContent = {
                    Switch(checked = animationsEnabled, onCheckedChange = onAnimationsEnabledChange)
                }
            )

            ListItem(
                headlineContent = { Text("Use Nature Metaphors") },
                supportingContent = { Text("Tree species icons and grove-themed terminology") },
                trailingContent = {
                    Switch(checked = metaphorsEnabled, onCheckedChange = onMetaphorsEnabledChange)
                }
            )

            var showLeftSwipeMenu by remember { mutableStateOf(false) }
            Box {
                ListItem(
                    headlineContent = { Text("Left Swipe Action") },
                    supportingContent = { Text(leftSwipeAction) },
                    modifier = Modifier.clickable { showLeftSwipeMenu = true }
                )
                DropdownMenu(expanded = showLeftSwipeMenu, onDismissRequest = { showLeftSwipeMenu = false }) {
                    DropdownMenuItem(text = { Text("DELETE") }, onClick = { onSwipeActionsChange("DELETE", rightSwipeAction); showLeftSwipeMenu = false })
                    DropdownMenuItem(text = { Text("TAG") }, onClick = { onSwipeActionsChange("TAG", rightSwipeAction); showLeftSwipeMenu = false })
                }
            }

            var showRightSwipeMenu by remember { mutableStateOf(false) }
            Box {
                ListItem(
                    headlineContent = { Text("Right Swipe Action") },
                    supportingContent = { Text(rightSwipeAction) },
                    modifier = Modifier.clickable { showRightSwipeMenu = true }
                )
                DropdownMenu(expanded = showRightSwipeMenu, onDismissRequest = { showRightSwipeMenu = false }) {
                    DropdownMenuItem(text = { Text("DELETE") }, onClick = { onSwipeActionsChange(leftSwipeAction, "DELETE"); showRightSwipeMenu = false })
                    DropdownMenuItem(text = { Text("TAG") }, onClick = { onSwipeActionsChange(leftSwipeAction, "TAG"); showRightSwipeMenu = false })
                }
            }


            Spacer(Modifier.height(24.dp))
            Text("Grove Connections (Cloud)", style = MaterialTheme.typography.titleMedium, color = BarkBrown)
            Spacer(Modifier.height(8.dp))

            cloudConnections.forEach { connection ->
                ListItem(
                    headlineContent = { Text(connection.name) },
                    supportingContent = { Text("${connection.type} - ${connection.host}") },
                    trailingContent = {
                        IconButton(onClick = { onDeleteConnection(connection) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }

            Button(
                onClick = { showAddConnectionDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CanopyGreen)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Graft New Connection (SMB/SFTP)")
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { /* Clear History */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BarkBrown)
            ) {
                Text("Clear Path History (Cache)")
            }
        }
    }

    if (showAddConnectionDialog) {
        AddConnectionDialog(
            onDismiss = { showAddConnectionDialog = false },
            onConfirm = { connection ->
                onAddConnection(connection)
                showAddConnectionDialog = false
            }
        )
    }
}

@Composable
fun AddConnectionDialog(
    onDismiss: () -> Unit,
    onConfirm: (com.nature.files.data.db.CloudConnectionEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("SMB") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("445") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showTypeMenu by remember { mutableStateOf(false) }

    NatureDialog(
        onDismissRequest = onDismiss,
        title = "Graft New Connection",
        confirmButton = {
            Button(onClick = {
                val connection = com.nature.files.data.db.CloudConnectionEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    type = type,
                    host = host,
                    port = port.toIntOrNull() ?: if (type == "SMB") 445 else 22,
                    username = username,
                    password = password
                )
                onConfirm(connection)
            }) {
                Text("Graft")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TextField(value = name, onValueChange = { name = it }, label = { Text("Connection Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Box {
                OutlinedButton(onClick = { showTypeMenu = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Type: $type")
                }
                DropdownMenu(expanded = showTypeMenu, onDismissRequest = { showTypeMenu = false }) {
                    DropdownMenuItem(text = { Text("SMB") }, onClick = { type = "SMB"; port = "445"; showTypeMenu = false })
                    DropdownMenuItem(text = { Text("SFTP") }, onClick = { type = "SFTP"; port = "22"; showTypeMenu = false })
                }
            }
            Spacer(Modifier.height(8.dp))
            TextField(value = host, onValueChange = { host = it }, label = { Text("Host/IP") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            TextField(value = port, onValueChange = { port = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            TextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
        }
    }
}
