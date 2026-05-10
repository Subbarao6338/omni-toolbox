package com.naturetools.app.ui.screens.text

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.util.Locale
import kotlin.random.Random

@Composable
fun TextToolScreen(navController: NavHostController, title: String) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(scrollState)) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Input Text") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    result = when (title) {
                        "Anagram Finder" -> findAnagrams(input)
                        "Lorem Ipsum" -> generateLorem(input)
                        "Case Converter" -> convertCase(input)
                        else -> input.reversed()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Process")
            }

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Result", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(result)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                        Text(result)
                    }
                }
            }
        }
    }
}

fun findAnagrams(input: String): String {
    val text = input.lowercase().filter { it.isLetter() }
    if (text.isEmpty()) return "Enter some text to analyze."

    val charCount = text.groupingBy { it }.eachCount().toSortedMap()
    val sortedText = text.toCharArray().sortedArray().joinToString("")

    return "Sorted Letters: $sortedText\n\nCharacter Breakdown:\n" +
           charCount.map { "${it.key}: ${it.value}" }.joinToString("\n") +
           "\n\nAnagrams search: Try 'listen' (silent), 'debit card' (bad credit), 'dormitory' (dirty room)."
}

fun generateLorem(input: String): String {
    val count = input.toIntOrNull() ?: 50
    val words = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
        "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
        "magna", "aliqua", "ut", "enim", "ad", "minim", "veniam", "quis", "nostrud",
        "exercitation", "ullamco", "laboris", "nisi", "ut", "aliquip", "ex", "ea",
        "commodo", "consequat", "duis", "aute", "irure", "dolor", "in", "reprehenderit",
        "in", "voluptate", "velit", "esse", "cillum", "dolore", "eu", "fugiat", "nulla", "pariatur"
    )
    val sb = StringBuilder()
    for (i in 0 until count) {
        val word = words[Random.nextInt(words.size)]
        if (i == 0) sb.append(word.replaceFirstChar { it.uppercase() })
        else sb.append(word)

        if (i < count - 1) {
            if (Random.nextInt(10) > 8) sb.append(". ")
            else sb.append(" ")
        }
    }
    if (!sb.endsWith(".")) sb.append(".")
    return sb.toString()
}

fun convertCase(input: String): String {
    if (input.isEmpty()) return "Enter text to convert."
    return "UPPERCASE: ${input.uppercase()}\n\n" +
           "lowercase: ${input.lowercase()}\n\n" +
           "Title Case: ${input.split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.getDefault()) else c.toString() } }}\n\n" +
           "Sentence case: ${input.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
}
