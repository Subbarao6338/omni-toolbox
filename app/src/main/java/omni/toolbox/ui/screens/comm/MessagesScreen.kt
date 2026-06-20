package omni.toolbox.ui.screens.comm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class Message(val sender: String, val content: String, val isMe: Boolean, val time: String)

@Composable
fun MessagesScreen(navController: NavHostController) {
    val messages = remember {
        mutableStateListOf(
            Message("John Doe", "Hey, how are you?", false, "10:00 AM"),
            Message("Me", "I'm good, thanks! Working on the app.", true, "10:02 AM"),
            Message("John Doe", "That's awesome! Let me know if you need help.", false, "10:05 AM")
        )
    }
    var text by remember { mutableStateOf("") }

    ToolScreen(title = "Messages", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }

            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (text.isNotBlank()) {
                                messages.add(Message("Me", text, true, "Now"))
                                text = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: Message) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (msg.isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (msg.isMe) 16.dp else 0.dp,
                bottomEnd = if (msg.isMe) 0.dp else 16.dp
            )
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (msg.isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(msg.time, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 2.dp))
    }
}
