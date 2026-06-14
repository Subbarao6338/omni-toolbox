package com.naturetools.app.ui.screens.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun WordRankScreen(navController: NavHostController) {
    var textInput by remember { mutableStateOf("The quick brown fox jumps over the lazy dog. The fox is quick.") }
    var ignoreCommonWords by remember { mutableStateOf(true) }

    val commonWords = setOf("the", "is", "at", "which", "on", "a", "an", "and", "in", "of", "to", "over")

    val wordRank = remember(textInput, ignoreCommonWords) {
        val words = textInput.lowercase()
            .split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.isNotEmpty() }
            .filter { !ignoreCommonWords || !commonWords.contains(it) }

        words.groupingBy { it }.eachCount()
            .toList()
            .sortedByDescending { it.second }
    }

    ToolScreen(
        title = "Word Frequency Ranker",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Advanced Text Lexicon Analyzer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("Input Text for Analysis") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Checkbox(checked = ignoreCommonWords, onCheckedChange = { ignoreCommonWords = it })
                    Text("Ignore common English stopwords (the, is, and...)")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Lexical Statistics", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatChip("Total Words", wordRank.sumOf { it.second }.toString(), Modifier.weight(1f))
                    StatChip("Unique Words", wordRank.size.toString(), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Occurrence Ranking", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(wordRank.take(50)) { (word, count) ->
                RankRow(word, count, wordRank.firstOrNull()?.second ?: 1)
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun RankRow(word: String, count: Int, maxCount: Int) {
    val density = count.toFloat() / maxCount
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(word, fontWeight = FontWeight.Bold)
            Text("$count times", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(density)
                    .fillMaxHeight()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                        )
                    )
            )
        }
    }
}
