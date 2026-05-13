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
import java.math.BigInteger
import java.util.Locale
import kotlin.random.Random

@Composable
fun TextToolScreen(navController: NavHostController, title: String) {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
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

            if (title == "Find & Replace") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = findText, onValueChange = { findText = it }, label = { Text("Find") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = replaceText, onValueChange = { replaceText = it }, label = { Text("Replace") }, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    result = when (title) {
                        "Anagram Finder" -> findAnagrams(input)
                        "Lorem Ipsum" -> generateLorem(input)
                        "Case Converter" -> convertCase(input)
                        "Word Rank" -> calculateWordRank(input)
                        "Sort Lines" -> input.lines().sorted().joinToString("\n")
                        "Dedupe Lines" -> input.lines().distinct().joinToString("\n")
                        "Clean Whitespace" -> input.replace(Regex("\\s+"), " ").trim()
                        "Find & Replace" -> input.replace(findText, replaceText)
                        "HTML Entities" -> encodeHtmlEntities(input)
                        "PII Extractor" -> extractPii(input)
                        "PII Anonymizer" -> anonymizePii(input)
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

fun extractPii(input: String): String {
    val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    val urlRegex = Regex("https?://[\\w\\d.-]+(?:\\.[\\w\\d.-]+)+[\\w\\d._~:/?#\\[\\]@!$&'()*+,;=%-]+")

    val emails = emailRegex.findAll(input).map { it.value }.toList()
    val urls = urlRegex.findAll(input).map { it.value }.toList()

    return "Emails Found (${emails.size}):\n${emails.joinToString("\n")}\n\nURLs Found (${urls.size}):\n${urls.joinToString("\n")}"
}

fun anonymizePii(input: String): String {
    val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    val phoneRegex = Regex("\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}")

    return input.replace(emailRegex, "[EMAIL]")
        .replace(phoneRegex, "[PHONE]")
}

fun encodeHtmlEntities(input: String): String {
    return input.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")
}

fun calculateWordRank(input: String): String {
    val word = input.uppercase().filter { it.isLetter() }
    if (word.isEmpty()) return "Enter a valid word."

    var rank = BigInteger.ONE
    val n = word.length
    val freqMap = word.groupingBy { it }.eachCount().toMutableMap()

    fun factorial(num: Int): BigInteger {
        var res = BigInteger.ONE
        for (i in 2..num) res = res.multiply(BigInteger.valueOf(i.toLong()))
        return res
    }

    fun getPermutations(totalLen: Int, counts: Map<Char, Int>): BigInteger {
        var den = BigInteger.ONE
        for (count in counts.values) den = den.multiply(factorial(count))
        return factorial(totalLen).divide(den)
    }

    for (i in word.indices) {
        val smallerChars = freqMap.keys.filter { it < word[i] && freqMap[it]!! > 0 }
        for (char in smallerChars) {
            val tempMap = freqMap.toMutableMap()
            tempMap[char] = tempMap[char]!! - 1
            rank = rank.add(getPermutations(n - 1 - i, tempMap))
        }
        freqMap[word[i]] = freqMap[word[i]]!! - 1
    }

    return "The lexicographical rank of '$word' is: $rank"
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
