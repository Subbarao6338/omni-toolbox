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
                    2 -> MediaGenTab(aiApiKey)
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
                            val prompt = "Provide a detailed answer to the following query. If it's a coding question, provide optimized code examples and explain potential edge cases:\n\n$input"
                            val result = generativeModel?.generateContent(prompt)
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
fun MediaGenTab(apiKey: String) {
    var prompt by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val generativeModel = remember(apiKey) {
        if (apiKey.isNotEmpty()) {
            GenerativeModel(modelName = "gemini-1.5-pro", apiKey = apiKey)
        } else null
    }

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
                        try {
                            val fullPrompt = "Explain how you would generate a video for this prompt using Veo-3.1 and provide a detailed cinematic description: $prompt"
                            val result = generativeModel?.generateContent(fullPrompt)
                            response = result?.text ?: "No generation plan available."
                        } catch (e: Exception) {
                            response = "Error: ${e.localizedMessage}"
                        } finally {
                            isGenerating = false
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = apiKey.isNotEmpty() && !isGenerating
            ) {
                if (isGenerating) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.Movie, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Veo Video Plan")
            }

            Button(
                onClick = {
                    scope.launch {
                        isGenerating = true
                        try {
                            val fullPrompt = "Describe a musical composition for this prompt in terms of key, tempo, instruments, and mood: $prompt"
                            val result = generativeModel?.generateContent(fullPrompt)
                            response = result?.text ?: "No musical description available."
                        } catch (e: Exception) {
                            response = "Error: ${e.localizedMessage}"
                        } finally {
                            isGenerating = false
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                enabled = apiKey.isNotEmpty() && !isGenerating
            ) {
                if (isGenerating) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.MusicNote, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Music Gen Plan")
            }
        }

        if (response.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Generative Design:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(response, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
