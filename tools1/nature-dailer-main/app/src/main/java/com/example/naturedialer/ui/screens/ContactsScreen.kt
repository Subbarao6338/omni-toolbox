package com.example.naturedialer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturedialer.ui.theme.DialerTheme
import com.example.naturedialer.ui.theme.getNatureColors

data class Contact(val name: String, val number: String, val category: String)

val MOCK_CONTACTS = listOf(
    Contact("Arjuna Smith", "+1 (555) 123-4567", "Family"),
    Contact("Leila Moss", "+1 (555) 987-6543", "Work"),
    Contact("Cedar Greene", "+1 (555) 456-7890", "Friends"),
    Contact("River Stone", "+1 (555) 111-2222", "Work"),
    Contact("Sky Blue", "+1 (555) 333-4444", "Family")
)

@Composable
fun ContactsScreen(
    theme: DialerTheme,
    onCall: (String) -> Unit
) {
    val natureColors = getNatureColors(theme)

    Column(modifier = Modifier.fillMaxSize().background(natureColors.bg).padding(16.dp)) {
        Text(
            text = "Contacts",
            color = natureColors.text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(MOCK_CONTACTS) { contact ->
                ContactItem(contact = contact, theme = theme, onCall = onCall)
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact, theme: DialerTheme, onCall: (String) -> Unit) {
    val natureColors = getNatureColors(theme)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(natureColors.surface)
            .clickable { onCall(contact.number) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(natureColors.dial),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = natureColors.accent)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = contact.name, color = natureColors.text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(text = contact.category, color = natureColors.text.copy(alpha = 0.6f), fontSize = 12.sp)
        }
        IconButton(onClick = { onCall(contact.number) }) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = natureColors.accent)
        }
    }
}
