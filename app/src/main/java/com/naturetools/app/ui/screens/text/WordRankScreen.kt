package com.naturetools.app.ui.screens.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun WordRankScreen(navController: NavHostController) {
    var textInput by remember { mutableStateOf("") }
    var filterStopwords by remember { mutableStateOf(true) }

    val stopwords = setOf(
        "the", "and", "a", "an", "in", "on", "at", "to", "for", "of", "with", "is", "are", "was", "were",
        "it", "this", "that", "those", "these", "i", "you", "he", "she", "it", "we", "they", "my", "your",
        "his", "her", "its", "our", "their", "be", "been", "being", "have", "has", "had", "do", "does", "did",
        "but", "or", "if", "then", "else", "when", "where", "why", "how", "all", "any", "both", "each", "few",
        "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very"
    )

    val wordCounts = remember(textInput, filterStopwords) {
        val words = textInput.lowercase()
            .split(Regex("[^a-zA-Z]+"))
            .filter { it.length > 1 }
            .filter { !filterStopwords || !stopwords.contains(it) }

        words.groupingBy { it }.eachCount().toList().sortedByDescending { it.second }
    }

    val totalWords = remember(wordCounts) { wordCounts.sumOf { it.second } }

    ToolScreen(
        title = "Word Rank Analyzer",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                label = { Text("Paste text here to analyze") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Example: The quick brown fox jumps over the lazy dog...") }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = filterStopwords,
                    onCheckedChange = { filterStopwords = it }
                )
                Text("Filter common stopwords", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            if (wordCounts.isNotEmpty()) {
                Text(
                    "Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox("Unique Words", wordCounts.size.toString(), Modifier.weight(1f))
                    StatBox("Total Count", totalWords.toString(), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Word Rankings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(wordCounts) { (word, count) ->
                        WordRankItem(word, count, totalWords)
                    }
                }
            } else if (textInput.isNotBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No significant words found.")
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Enter text to see ranking analysis.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WordRankItem(word: String, count: Int, total: Int) {
    val density = count.toFloat() / total

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(word, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Text("$count (${String.format("%.1f", density * 100)}%)", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { density * (total.toFloat() / (total / 2 + count)) .coerceAtMost(1f) }, // Scale for visibility
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
