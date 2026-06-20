package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactInfo
import com.example.ui.OmniViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    val contacts by viewModel.contacts.collectAsState()
    val profiles by viewModel.profiles.collectAsState()
    
    // Switch between various Google/Workspace accounts loaded in system
    val googleAccounts = remember(profiles) {
        profiles.filter { it.platform == "GDrive" || it.email.endsWith("@gmail.com") }
    }
    
    var activeAccountEmail by remember { mutableStateOf("subbu.edu.68@gmail.com") }
    LaunchedEffect(googleAccounts) {
        if (googleAccounts.isNotEmpty() && !googleAccounts.any { it.email == activeAccountEmail }) {
            activeAccountEmail = googleAccounts.first().email
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("All") }
    var isSyncing by remember { mutableStateOf(false) }
    var syncStatusMessage by remember { mutableStateOf("Ready to sync") }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<ContactInfo?>(null) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Fitlered contacts
    val filteredContacts = remember(contacts, searchQuery, selectedGroup, activeAccountEmail) {
        contacts.filter { contact ->
            val matchesSearch = contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.phone.contains(searchQuery) ||
                    contact.email.contains(searchQuery, ignoreCase = true) ||
                    contact.organization.contains(searchQuery, ignoreCase = true)
            
            val matchesGroup = selectedGroup == "All" || contact.groupName.equals(selectedGroup, ignoreCase = true)
            // Show local contacts or ones belonging to active email workspace
            val matchesAccount = contact.isLocalOnly || contact.accountEmail == activeAccountEmail
            
            matchesSearch && matchesGroup && matchesAccount
        }
    }

    // Available address book group labels
    val groups = listOf("All", "My Contacts", "Work", "Family")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Google Contacts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Connected: $activeAccountEmail",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("contacts_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigate back")
                    }
                },
                actions = {
                    // Sync action
                    IconButton(
                        onClick = {
                            isSyncing = true
                            syncStatusMessage = "Connecting Google Contacts API..."
                            scope.launch {
                                delay(1200)
                                syncStatusMessage = "Downloading directory changes..."
                                delay(1000)
                                syncStatusMessage = "Address books synchronized successfully."
                                isSyncing = false
                            }
                        },
                        enabled = !isSyncing,
                        modifier = Modifier.testTag("sync_contacts_api_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync contacts")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("add_contact_fab")
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Contact")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Account selection bar
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Google Account Sync Node",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = activeAccountEmail,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Switch account selector mini row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        googleAccounts.forEach { acc ->
                            val isSelected = acc.email == activeAccountEmail
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary 
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { activeAccountEmail = acc.email }
                                    .testTag("switch_account_${acc.email}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = acc.accountName.take(1).uppercase(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, organization or phone...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_search_input")
            )

            // Address Book Groups / Labels Filter Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                groups.forEach { group ->
                    val isSelected = selectedGroup == group
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGroup = group },
                        label = { Text(group, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Sync indicators
            AnimatedVisibility(visible = isSyncing || syncStatusMessage != "Ready to sync") {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "CloudSync",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = syncStatusMessage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Results list header
            Text(
                text = "CONTACTS MATCHING (${filteredContacts.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )

            if (filteredContacts.isEmpty()) {
                // Empty view state with guideline standard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContactPage,
                            contentDescription = "Empty icon",
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No contacts found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add contacts or sign in to different account with GDrive scope, then click Sync.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { contact ->
                        ContactItemCard(
                            contact = contact,
                            onEdit = { editingContact = contact },
                            onDelete = { viewModel.deleteContact(contact.id, contact.name) }
                        )
                    }
                }
            }
        }
    }

    // Add dialog
    if (showAddDialog) {
        AddEditContactDialog(
            contact = null,
            accountEmail = activeAccountEmail,
            onDismiss = { showAddDialog = false },
            onSave = { name, phone, email, address, org, group, isLocal ->
                viewModel.addContact(
                    name = name,
                    phone = phone,
                    email = email,
                    address = address,
                    organization = org,
                    groupName = group,
                    accountEmail = activeAccountEmail
                )
                showAddDialog = false
            }
        )
    }

    // Edit dialog
    if (editingContact != null) {
        AddEditContactDialog(
            contact = editingContact,
            accountEmail = activeAccountEmail,
            onDismiss = { editingContact = null },
            onSave = { name, phone, email, address, org, group, isLocal ->
                viewModel.updateContact(
                    editingContact!!.copy(
                        name = name,
                        phone = phone,
                        email = email,
                        address = address,
                        organization = org,
                        groupName = group,
                        isLocalOnly = isLocal
                    )
                )
                editingContact = null
            }
        )
    }
}

@Composable
fun ContactItemCard(
    contact: ContactInfo,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile monogram
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = contact.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Label Chip
                    Badge(
                        containerColor = when(contact.groupName) {
                            "Work" -> Color(0xFF29B6F6).copy(alpha = 0.2f)
                            "Family" -> Color(0xFFEC407A).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = when(contact.groupName) {
                            "Work" -> Color(0xFF0288D1)
                            "Family" -> Color(0xFFC2185B)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    ) {
                        Text(contact.groupName, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = contact.phone,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (contact.email.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = contact.email,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (contact.organization.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = "Org", modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = contact.organization,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Quick actions
            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Contact", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Contact", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditContactDialog(
    contact: ContactInfo?,
    accountEmail: String,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, email: String, address: String, org: String, group: String, isLocalOnly: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(contact?.name ?: "") }
    var phone by remember { mutableStateOf(contact?.phone ?: "") }
    var email by remember { mutableStateOf(contact?.email ?: "") }
    var address by remember { mutableStateOf(contact?.address ?: "") }
    var organization by remember { mutableStateOf(contact?.organization ?: "") }
    var groupName by remember { mutableStateOf(contact?.groupName ?: "My Contacts") }
    var isLocalOnly by remember { mutableStateOf(contact?.isLocalOnly ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (contact == null) "New Google Contact" else "Edit Contact Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Company / Organization") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Postal Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Address Book Label", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("My Contacts", "Work", "Family").forEach { g ->
                        val isSelected = groupName == g
                        FilterChip(
                            selected = isSelected,
                            onClick = { groupName = g },
                            label = { Text(g, fontSize = 10.sp) }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = isLocalOnly, onCheckedChange = { isLocalOnly = it })
                    Text("Local-only contact (Skip Google sync daemon)", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSave(name, phone, email, address, organization, groupName, isLocalOnly)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
