package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordRankScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var searchFilter by remember { mutableStateOf("") }
    var excludeStopwords by remember { mutableStateOf(true) }
    var caseSensitive by remember { mutableStateOf(false) }

    val ENGLISH_STOPWORDS = remember {
        setOf(
            "the", "a", "an", "and", "or", "but", "if", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out",
            "on", "off", "over", "under", "again", "further", "then", "once", "here", "there",
            "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most",
            "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very",
            "s", "t", "can", "will", "just", "don", "should", "now", "i", "me", "my", "myself", "we", "our",
            "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his",
            "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their",
            "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those",
            "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "would", "could", "should"
        )
    }

    // Computing Word Frequencies and Text Metrics
    val stats = remember(rawText, excludeStopwords, caseSensitive) {
        if (rawText.isBlank()) {
            TextStats(0, 0, 0, 0f, emptyList())
        } else {
            // Tokenize text finding alpha words
            val regex = Regex("\\b[a-zA-Z']+\\b")
            val rawTokens = regex.findAll(rawText).map { it.value }
            
            val tokens = rawTokens.map {
                if (caseSensitive) it else it.lowercase(Locale.getDefault())
            }.toList()

            val filteredTokens = if (excludeStopwords) {
                tokens.filter {
                    val check = if (caseSensitive) it.lowercase(Locale.getDefault()) else it
                    !ENGLISH_STOPWORDS.contains(check)
                }
            } else {
                tokens
            }

            val totalCount = filteredTokens.size
            val uniqueCount = filteredTokens.distinct().size
            val totalCharCount = rawText.length
            val avgWordLength = if (filteredTokens.isNotEmpty()) {
                filteredTokens.sumOf { it.length }.toFloat() / filteredTokens.size
            } else 0f

            val frequencies = filteredTokens.groupingBy { it }.eachCount()
                .toList()
                .sortedByDescending { it.second }

            TextStats(totalCount, uniqueCount, totalCharCount, avgWordLength, frequencies)
        }
    }

    val displayList = remember(stats.frequencies, searchFilter) {
        if (searchFilter.isBlank()) {
            stats.frequencies
        } else {
            stats.frequencies.filter { it.first.contains(searchFilter, ignoreCase = true) }
        }
    }

    val maxFrequency = remember(stats.frequencies) {
        stats.frequencies.maxOfOrNull { it.second } ?: 1
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Word rank icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Word Frequency Analyzer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("word_rank_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Section 1: Text Input Panel
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SOURCE PLAIN TEXT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )

                                // Preset selection helpers
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    PresetPill(
                                        label = "Sample Article",
                                        onClick = {
                                            rawText = "Modern cloud-native platforms are built with highly decoupled systems. " +
                                                    "This software framework utilizes decentralized messaging routers to synchronize " +
                                                    "asynchronous events successfully. High reliability, high computing performance, " +
                                                    "and fast memory structures ensure low latency logs inside sandboxed virtual machines. " +
                                                    "Performance metrics are monitored in real-time by a daemon compiler process."
                                        }
                                    )
                                    PresetPill(
                                        label = "Clear",
                                        onClick = { rawText = "" }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            TextField(
                                value = rawText,
                                onValueChange = { rawText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("word_rank_text_input"),
                                placeholder = { Text("Paste text here for indexing...", fontSize = 13.sp) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(14.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Settings Toggle Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { excludeStopwords = !excludeStopwords }
                                ) {
                                    Checkbox(
                                        checked = excludeStopwords,
                                        onCheckedChange = { excludeStopwords = it },
                                        modifier = Modifier.testTag("exclude_stopwords_checkbox")
                                    )
                                    Text("Exclude English Stopwords", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { caseSensitive = !caseSensitive }
                                ) {
                                    Checkbox(
                                        checked = caseSensitive,
                                        onCheckedChange = { caseSensitive = it }
                                    )
                                    Text("Case Sensitive Analysis", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                // Section 2: Analytics Summary Card
                item {
                    Text(
                        text = "COMPILATION ANALYTICS METRICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MetricBlock(label = "Total Words", value = stats.totalWords.toString(), modifier = Modifier.weight(1f))
                            MetricBlock(label = "Unique Words", value = stats.uniqueWords.toString(), modifier = Modifier.weight(1f))
                            MetricBlock(label = "Characters", value = stats.totalCharacters.toString(), modifier = Modifier.weight(1f))
                            MetricBlock(label = "Avg Length", value = String.format(Locale.getDefault(), "%.1f", stats.avgWordLength), modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Section 3: Rankings List Table
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORD FREQUENCY RANKINGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        if (stats.frequencies.isNotEmpty()) {
                            Text(
                                text = "Indexed ${stats.frequencies.size} keys",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                if (stats.frequencies.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Enter text above to process the indexing cache.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    item {
                        // Quick table word filter
                        TextField(
                            value = searchFilter,
                            onValueChange = { searchFilter = it },
                            placeholder = { Text("Filter words search index...", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ranking_word_filter"),
                            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    if (displayList.isEmpty()) {
                        item {
                            Text(
                                "No matches found for search key.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    } else {
                        // Items rendered with staggered layouts
                        itemsIndexed(displayList) { index, (word, freq) ->
                            WordRankWidget(
                                rank = index + 1,
                                word = word,
                                freq = freq,
                                percentage = (freq.toFloat() / stats.totalWords * 100),
                                fraction = freq.toFloat() / maxFrequency
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TextStats(
    val totalWords: Int,
    val uniqueWords: Int,
    val totalCharacters: Int,
    val avgWordLength: Float,
    val frequencies: List<Pair<String, Int>>
)

@Composable
fun PresetPill(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MetricBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WordRankWidget(
    rank: Int,
    word: String,
    freq: Int,
    percentage: Float,
    fraction: Float
) {
    val highlightColor = when (rank) {
        1 -> Color(0xFFFF9100) // Saffron Gold
        2 -> Color(0xFF00FF88) // Glowing Green
        3 -> Color(0xFF00E5FF) // Cool Cyan
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number badge
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (rank <= 3) highlightColor.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = highlightColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = word,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$freq hits",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f%%", percentage),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Beautiful Gradient percentage bar meter
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = fraction.coerceIn(0.01f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(highlightColor.copy(alpha = 0.5f), highlightColor)
                                )
                            )
                    )
                }
            }
        }
    }
}
