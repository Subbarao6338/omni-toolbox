package omni.toolbox.ui.screens.text

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
import omni.toolbox.ui.components.ToolScreen
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
    val word = input.trim().uppercase().filter { it.isLetter() }
    if (word.isEmpty()) return "Enter a valid word."

    val n = word.length
    if (n > 25) return "Word too long for rank calculation."

    val fact = Array(n + 1) { BigInteger.ONE }
    for (i in 2..n) fact[i] = fact[i - 1].multiply(BigInteger.valueOf(i.toLong()))

    var rank = BigInteger.ONE
    val freqMap = mutableMapOf<Char, Int>()
    for (c in word) freqMap[c] = freqMap.getOrDefault(c, 0) + 1

    for (i in 0 until n) {
        val sortedKeys = freqMap.keys.filter { it < word[i] && freqMap[it]!! > 0 }.sorted()
        for (char in sortedKeys) {
            freqMap[char] = freqMap[char]!! - 1

            var den = BigInteger.ONE
            for (count in freqMap.values) {
                if (count > 1) den = den.multiply(fact[count])
            }
            val permutations = fact[n - 1 - i].divide(den)
            rank = rank.add(permutations)

            freqMap[char] = freqMap[char]!! + 1
        }
        freqMap[word[i]] = freqMap[word[i]]!! - 1
    }

    return "The lexicographical rank of '$word' is: $rank"
}

fun findAnagrams(input: String): String {
    val cleaned = input.trim().lowercase()
    val lettersOnly = cleaned.filter { it.isLetter() }
    if (lettersOnly.isEmpty()) return "Enter some text to analyze."

    val charCount = lettersOnly.groupingBy { it }.eachCount().toSortedMap()
    val sortedLetters = lettersOnly.toCharArray().sortedArray().joinToString("")

    // Simple built-in dictionary for demo purposes
    val demoDict = listOf("listen", "silent", "enlist", "tinsel", "inlets", "rail safety", "fairy tales", "dormitory", "dirty room", "astronomer", "moon starer", "the eyes", "they see", "school master", "the classroom")

    val matches = demoDict.filter { dictWord ->
        val dictLetters = dictWord.lowercase().filter { it.isLetter() }
        dictLetters.length == lettersOnly.length &&
        dictLetters.toCharArray().sortedArray().joinToString("") == sortedLetters &&
        dictWord.lowercase() != cleaned
    }

    return buildString {
        append("Sorted Letters: $sortedLetters\n")
        append("Character Breakdown: ${charCount}\n\n")
        if (matches.isNotEmpty()) {
            append("Known Anagrams:\n")
            matches.forEach { append("- $it\n") }
        } else {
            append("No anagrams found in local demo dictionary.")
        }
    }
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
