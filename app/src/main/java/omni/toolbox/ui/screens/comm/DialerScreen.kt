package omni.toolbox.ui.screens.comm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun DialerScreen(navController: NavHostController) {
    var phoneNumber by remember { mutableStateOf("") }
    // Ported T9 logic from nature-dailer-main
    val contacts = remember { listOf("Alice Johnson", "Bob Builder", "Charlie Chaplin", "David Bowie", "Eve Online", "Frank Sinatra", "Grace Hopper") }
    val filteredContacts = remember(phoneNumber) {
        if (phoneNumber.isEmpty()) emptyList()
        else {
            val t9Map = mapOf(
                '2' to "abc", '3' to "def", '4' to "ghi",
                '5' to "jkl", '6' to "mno", '7' to "pqrs",
                '8' to "tuv", '9' to "wxyz"
            )
            contacts.filter { contact ->
                var digitIndex = 0
                val contactLower = contact.lowercase()
                for (char in contactLower) {
                    if (digitIndex < phoneNumber.length) {
                        val possibleChars = t9Map[phoneNumber[digitIndex]] ?: ""
                        if (char in possibleChars) {
                            digitIndex++
                        }
                    }
                }
                digitIndex == phoneNumber.length || contactLower.contains(phoneNumber)
            }
        }
    }
    val keys = listOf(
        "1" to "", "2" to "ABC", "3" to "DEF",
        "4" to "GHI", "5" to "JKL", "6" to "MNO",
        "7" to "PQRS", "8" to "TUV", "9" to "WXYZ",
        "*" to "", "0" to "+", "#" to ""
    )

    ToolScreen(title = "Dialer", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // T9 Search Results
                if (filteredContacts.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            filteredContacts.take(3).forEach { contact ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(contact, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
                Text(
                    text = phoneNumber,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
                if (phoneNumber.isNotEmpty()) {
                    IconButton(onClick = { phoneNumber = phoneNumber.dropLast(1) }) {
                        Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Keypad
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().height(400.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(keys) { (digit, letters) ->
                    DialerButton(
                        text = digit,
                        subText = letters,
                        onClick = { phoneNumber += digit }
                    )
                }
            }

            // Call Button
            Button(
                onClick = { /* Implement call logic */ },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DialerButton(
    text: String,
    subText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = CircleShape
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            if (subText.isNotEmpty()) {
                Text(
                    text = subText,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
