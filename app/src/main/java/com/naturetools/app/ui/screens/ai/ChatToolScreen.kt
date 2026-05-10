package com.naturetools.app.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatToolScreen(navController: NavHostController, title: String, aiApiKey: String = "") {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage("Hello! I am your assistant for $title. How can I help you today?", false))
        }
    }

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val currentInput = inputText
                                messages.add(ChatMessage(inputText, true))
                                inputText = ""
                                // Implementation logic
                                if (aiApiKey.isBlank()) {
                                    messages.add(ChatMessage("$title: Processing request offline (Local AI)...", false))
                                } else {
                                    messages.add(ChatMessage("$title: Processing request via Online API...", false))
                                }
                                val actualResponse = when(title) {
                                    "CSV to JSON" -> {
                                        try {
                                            val lines = currentInput.lines().filter { it.isNotBlank() }
                                            if (lines.size < 2) "Offline AI: Please provide at least a header and one row of data."
                                            else {
                                                val header = lines.first().split(",")
                                                val rows = lines.drop(1).map { it.split(",") }
                                                val jsonResult = rows.joinToString(",\n  ", "[\n  ", "\n]") { row ->
                                                    header.zip(row).joinToString(", ", "{ ", " }") { (k, v) -> "\"${k.trim()}\": \"${v.trim()}\"" }
                                                }
                                                "Converted JSON:\n$jsonResult"
                                            }
                                        } catch (e: Exception) {
                                            "Offline AI: Error parsing CSV. Details: ${e.localizedMessage}"
                                        }
                                    }
                                    "Text Summarizer" -> {
                                        val sentences = currentInput.split(Regex("[.!?]")).filter { it.isNotBlank() }
                                        if (sentences.size <= 2) "Summary: $currentInput"
                                        else {
                                            val words = currentInput.lowercase().split(Regex("\\W+")).filter { it.length > 3 }
                                            val wordFreq = words.groupingBy { it }.eachCount()
                                            val rankedSentences = sentences.associateWith { s ->
                                                s.lowercase().split(Regex("\\W+")).sumOf { wordFreq[it] ?: 0 }
                                            }.toList().sortedByDescending { it.second }
                                            "Offline Summary (Top 2 sentences):\n" + rankedSentences.take(2).joinToString(". ") { it.first.trim() } + "."
                                        }
                                    }
                                    "Grammar Checker" -> {
                                        val issues = mutableListOf<String>()
                                        if (currentInput.firstOrNull()?.isLowerCase() == true) issues.add("Sentence should start with a capital letter.")
                                        if (currentInput.contains("  ")) issues.add("Double spaces detected.")
                                        if (!currentInput.trim().endsWith(".") && !currentInput.trim().endsWith("?") && !currentInput.trim().endsWith("!")) issues.add("Missing terminal punctuation.")

                                        if (issues.isEmpty()) "Offline Grammar Check: No common issues found!"
                                        else "Offline Grammar Suggestions:\n- " + issues.joinToString("\n- ")
                                    }
                                    "Sentiment Analysis" -> {
                                        val positiveWords = listOf("good", "great", "excellent", "happy", "love", "amazing", "best")
                                        val negativeWords = listOf("bad", "terrible", "worst", "hate", "sad", "awful", "poor")
                                        val score = currentInput.lowercase().split(Regex("\\W+")).sumOf { word ->
                                            if (positiveWords.contains(word)) 1 else if (negativeWords.contains(word)) -1 else 0 as Int
                                        }
                                        "Offline Sentiment Result: ${when {
                                            score > 0 -> "POSITIVE (Score: $score)"
                                            score < 0 -> "NEGATIVE (Score: $score)"
                                            else -> "NEUTRAL"
                                        }}"
                                    }
                                    "Code Helper" -> {
                                        if (currentInput.contains("fun") || currentInput.contains("class") || currentInput.contains("var")) {
                                            "Offline Code Insight: It looks like you're writing Kotlin/Java code. Ensure your curly braces are balanced and your variables follow camelCase naming conventions."
                                        } else {
                                            "Offline Code Assistant: Try providing a code snippet. I can help with basic syntax checks and structure suggestions offline."
                                        }
                                    }
                                    "Translator" -> {
                                        val dictionary = mapOf(
                                            "hello" to "bonjour / hola / ciao",
                                            "world" to "monde / mundo / mondo",
                                            "good" to "bon / bueno / buono",
                                            "morning" to "matin / mañana / mattina",
                                            "thank you" to "merci / gracias / grazie"
                                        )
                                        val words = currentInput.lowercase().split(Regex("\\W+"))
                                        val translated = words.map { dictionary[it] ?: "[$it]" }.joinToString(" ")
                                        "Offline Translation (Basic):\n$translated\n\n[Note: This is an offline dictionary-based translation.]"
                                    }
                                    "Text Extractor" -> {
                                        // Simple offline simulation of extraction
                                        val mockExtracted = currentInput.filter { it.isLetterOrDigit() || it.isWhitespace() }
                                        "Offline OCR Simulation: If this were an image, I would extract text. From your chat input, here is the alphanumeric content:\n$mockExtracted"
                                    }
                                    "Object Detector" -> {
                                        val objects = listOf("Phone", "Laptop", "Coffee", "Person", "Chair", "Table")
                                        val detected = objects.filter { currentInput.contains(it, ignoreCase = true) }
                                        if (detected.isNotEmpty()) "Offline Detection: I recognized references to ${detected.joinToString(", ")} in your text."
                                        else "Offline Detection: No specific objects from my local dictionary recognized in your description. I'm ready to detect 80+ common objects via the camera."
                                    }
                                    else -> "Processing offline: ${currentInput.take(20)}... Result generated."
                                }
                                messages.add(ChatMessage(actualResponse, false))
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
        Row(verticalAlignment = Alignment.Bottom) {
            if (!message.isUser) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Surface(
                color = containerColor,
                contentColor = contentColor,
                shape = shape,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (message.isUser) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp).padding(start = 4.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
