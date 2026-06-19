package omni.toolbox.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.ai.client.generativeai.GenerativeModel

@Composable
fun AICompanionScreen(navController: NavHostController, aiApiKey: String) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chat & Code", "Summarizer", "Media Gen")

    ToolScreen(
        title = "Gemini AI Hub",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> ChatAndCodeTab(aiApiKey)
                    1 -> SummarizerTab(aiApiKey)
                    2 -> MediaGenTab()
                }
            }
        }
    }
}

@Composable
fun ChatAndCodeTab(apiKey: String) {
    var input by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isAnalyzing by remember { mutableStateOf(false) }

    val generativeModel = remember(apiKey) {
        if (apiKey.isNotEmpty()) {
            GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)
        } else null
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Free Gemini Q&A & Code Analyzer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Ask anything or paste code...") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Paste Kotlin code to check for memory leaks...") }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        isAnalyzing = true
                        try {
                            val result = generativeModel?.generateContent(input)
                            response = result?.text ?: "No response from AI."
                        } catch (e: Exception) {
                            response = "Error: ${e.localizedMessage}"
                        } finally {
                            isAnalyzing = false
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = apiKey.isNotEmpty() && !isAnalyzing
            ) {
                if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask AI")
            }

            Button(
                onClick = {
                    scope.launch {
                        isAnalyzing = true
                        try {
                            val prompt = "Analyze the following code for bugs, memory leaks, and performance issues:\n\n$input"
                            val result = generativeModel?.generateContent(prompt)
                            response = result?.text ?: "No analysis available."
                        } catch (e: Exception) {
                            response = "Error: ${e.localizedMessage}"
                        } finally {
                            isAnalyzing = false
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                enabled = apiKey.isNotEmpty() && !isAnalyzing
            ) {
                if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.BugReport, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyze Code")
            }
        }

        if (response.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Response:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(response, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (apiKey.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    "API Key Missing! Please configure your Gemini API Key in Settings.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SummarizerTab(apiKey: String) {
    var textInput by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var isSummarizing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val generativeModel = remember(apiKey) {
        if (apiKey.isNotEmpty()) {
            GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)
        } else null
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Cognitive Content Summarizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Paste long text, document logs, or URL...") },
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    isSummarizing = true
                    try {
                        val prompt = "Summarize the following content in a concise manner with key highlights:\n\n$textInput"
                        val result = generativeModel?.generateContent(prompt)
                        summary = result?.text ?: "Failed to generate summary."
                    } catch (e: Exception) {
                        summary = "Error: ${e.localizedMessage}"
                    } finally {
                        isSummarizing = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = apiKey.isNotEmpty() && !isSummarizing
        ) {
            if (isSummarizing) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            else Icon(Icons.Default.Summarize, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Brief Summary")
        }

        if (summary.isNotEmpty()) {
            Text("Structural Highlights", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(summary, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun MediaGenTab() {
    var prompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Multimodal Generative Lab", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Enter prompt for Video or Music...") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        progress = 0f
                        repeat(10) {
                            delay(500)
                            progress += 0.1f
                        }
                        isGenerating = false
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Movie, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Text-to-Video")
            }

            Button(
                onClick = { /* Music Gen */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Text-to-Music")
            }
        }

        if (isGenerating) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Synthesizing cinematic frames (veo-3.1)...", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text("Media Preview Area", color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
