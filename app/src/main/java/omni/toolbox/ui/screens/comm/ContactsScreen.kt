package omni.toolbox.ui.screens.comm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class Contact(val name: String, val phone: String)

@Composable
fun ContactsScreen(navController: NavHostController) {
    val contacts = listOf(
        Contact("Alice Johnson", "+1 111 222 333"),
        Contact("Bob Wilson", "+1 444 555 666"),
        Contact("Charlie Brown", "+1 777 888 999"),
        Contact("David Miller", "+1 000 111 222")
    ).sortedBy { it.name }

    ToolScreen(
        title = "Contacts",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { /* Add contact */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            items(contacts) { contact ->
                ListItem(
                    headlineContent = { Text(contact.name) },
                    supportingContent = { Text(contact.phone) },
                    leadingContent = {
                        Surface(
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(contact.name.take(1), color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}
