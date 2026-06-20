package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import com.example.ui.OmniViewModel
import com.example.ui.MediaGenerateProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICompanionScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Chatbot, 1: Code Analyzer, 2: Summarizer, 3: Video Gen, 4: Music Gen
    val isAILoading by viewModel.isAILoading.collectAsState()
    val geminiResponse by viewModel.geminiResponse.collectAsState()

    val videoStatus by viewModel.videoStatus.collectAsState()
    val musicStatus by viewModel.musicStatus.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini AI Companion") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("AI Chatbot", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Code Review", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Summarizer", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("Text-to-Video", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
                Tab(selected = activeTab == 4, onClick = { activeTab = 4 }) {
                    Text("Text-to-Music", modifier = Modifier.padding(12.dp), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> ChatBotView(viewModel, isAILoading, geminiResponse)
                    1 -> CodeAnalyzerView(viewModel, isAILoading, geminiResponse)
                    2 -> TextSummarizerView(viewModel, isAILoading, geminiResponse)
                    3 -> TextToVideoView(viewModel, videoStatus)
                    4 -> TextToMusicView(viewModel, musicStatus)
                }
            }
        }
    }
}

@Composable
fun ChatBotView(viewModel: OmniViewModel, isLoading: Boolean, response: String) {
    var chatMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("FREE GEMINI CHATBOT ASSISTANT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Ask technical queries, general logic, equations, or compile plans root-free here.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(12.dp))

        // Query input box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = chatMessage,
                onValueChange = { chatMessage = it },
                placeholder = { Text("Ask Gemini anything...") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (chatMessage.isNotBlank()) {
                        viewModel.sendGeminiPrompt(chatMessage)
                        chatMessage = ""
                    }
                },
                enabled = !isLoading && chatMessage.isNotBlank()
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Output Result card
        AIResponseCard(isLoading = isLoading, response = response)
    }
}

@Composable
fun CodeAnalyzerView(viewModel: OmniViewModel, isLoading: Boolean, response: String) {
    var codeBlock by remember { mutableStateOf("fun process(input: String) {\n  val x = input + \"-verified\"\n  println(x)\n}") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("AI DEVELOPER CODE ANALYZER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Paste a Kotlin or Java code block. Gemini will review architectures, find memory faults, and check thread locks.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = codeBlock,
            onValueChange = { codeBlock = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val fullSnippet = "Analyze this Android code snippet for performance bottlenecks, compiler optimization potential, and Jetpack structural recommendations. Paste: \n\n $codeBlock"
                viewModel.sendGeminiPrompt(fullSnippet)
            },
            enabled = !isLoading && codeBlock.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Terminal, contentDescription = "Run compiler review")
            Spacer(modifier = Modifier.width(8.dp))
            Text("COMPILE INTEL SUMMARY")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AIResponseCard(isLoading = isLoading, response = response)
    }
}

@Composable
fun TextSummarizerView(viewModel: OmniViewModel, isLoading: Boolean, response: String) {
    var rawText by remember { mutableStateOf("OmniTool Workspace is a rootless multi-utility box designed for developers. It features local database repositories, real-time diagnostic caches, cryptographic encodations, automated macros responding to timers or shakes, and a complete forum crawler module exporting pages directly to a synchronized Notion profile. This prevents context switching and maintains complete sandboxing.") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("AI COGNITIVE SUMMARIZER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Input complex texts, logs, transcripts, or specifications. Gemini will convert it into a neat, high-density summary.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = rawText,
            onValueChange = { rawText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val command = "Summarize the following document content concisely using bullet points. Focus on key functional points: \n\n $rawText"
                viewModel.sendGeminiPrompt(command)
            },
            enabled = !isLoading && rawText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.Compress, contentDescription = "Summarize")
            Spacer(modifier = Modifier.width(8.dp))
            Text("GENERATE CONDENSED INSIGHTS")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AIResponseCard(isLoading = isLoading, response = response)
    }
}

@Composable
fun TextToVideoView(viewModel: OmniViewModel, status: MediaGenerateProgress) {
    var prompt by remember { mutableStateOf("Cinematic flyover of futurism developer datacenter with holographic servers, volumetric light beams, highly detailed 8k") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("GEMINI VEO VIDEO GENERATION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Access the veo-3.1-fast-generate-preview endpoint to transform prompt indices into visual temporal outputs.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Describe the movie scene prompt...") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.triggerVideoGeneration(prompt) },
            enabled = status !is MediaGenerateProgress.Generating && prompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.VideoCall, contentDescription = "Generate Video")
            Spacer(modifier = Modifier.width(8.dp))
            Text("GENERATE VEO PREVIEW VIDEO")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (status) {
            is MediaGenerateProgress.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Awaiting prompt inputs to synthesize Veo MP4 tracks...",
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            is MediaGenerateProgress.Generating -> {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = status.statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            modifier = Modifier.width(180.dp).height(4.dp).clip(RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
            is MediaGenerateProgress.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13151A))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("VEO COMPILED VIDEO PLAYBACK", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF00E676))
                            IconButton(onClick = { viewModel.resetVideoGeneration() }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset video", tint = Color.White.copy(alpha = 0.5f))
                            }
                        }

                        // Simulated visual representation of video player
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .background(Color.DarkGray.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Play video preview",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(46.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Preview size: 1080p | Duration: 10s", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("PROMPT REFERENCE ID:", fontSize = 9.sp, color = Color.Gray)
                            Text(status.prompt, fontSize = 10.sp, maxLines = 1, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("REPRESENTATION URL:", fontSize = 9.sp, color = Color.Gray)
                            Text(status.mediaUrl, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF29B6F6))
                        }
                    }
                }
            }
            is MediaGenerateProgress.Error -> {
                Text(status.errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TextToMusicView(viewModel: OmniViewModel, status: MediaGenerateProgress) {
    var prompt by remember { mutableStateOf("Lo-Fi study beats with embedded acoustic keyboard ripples, nostalgic feel, smooth tempo") }
    var isPlaying by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("GEMINI NEURAL MUSIC COMPOSER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Compose 30-sec custom harmonics utilizing direct multi-modal audio spectrogram vectors.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Describe instrument blend, tempo pattern...") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.triggerMusicGeneration(prompt) },
            enabled = status !is MediaGenerateProgress.Generating && prompt.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Default.MusicVideo, contentDescription = "Generate Music")
            Spacer(modifier = Modifier.width(8.dp))
            Text("SYNTHESIZE NEURAL WAVEFORM")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (status) {
            is MediaGenerateProgress.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Awaiting design cues to align melody streams...",
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            is MediaGenerateProgress.Generating -> {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = status.statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            is MediaGenerateProgress.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13151A))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("LYRIA COG MELODY WAVEFORM", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFFAB47BC))
                            IconButton(onClick = { 
                                isPlaying = false
                                viewModel.resetMusicStatus() 
                            }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset audio", tint = Color.White.copy(alpha = 0.5f))
                            }
                        }

                        // Simulated audio player controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { isPlaying = !isPlaying }) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                    contentDescription = "Control audio status",
                                    tint = Color(0xFFAB47BC),
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Generated Track_${Random.nextInt(100, 999)}.mp3",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Audio Synthesizer verified | Sample Rate: 48kHz",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Visual spectrum indicator mock
                        Row(
                            modifier = Modifier.fillMaxWidth().height(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val barsCount = 20
                            for (b in 1..barsCount) {
                                val barHeight = if (isPlaying) Random.nextInt(2, 16) else 2
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(barHeight.dp)
                                        .background(Color(0xFFAB47BC).copy(alpha = 0.8f), RoundedCornerShape(1.dp))
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("TRACK ACCESS URL:", fontSize = 9.sp, color = Color.Gray)
                            Text(status.mediaUrl, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF29B6F6))
                        }
                    }
                }
            }
            is MediaGenerateProgress.Error -> {
                Text(status.errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AIResponseCard(isLoading: Boolean, response: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("AI RESPONSE CONSOLE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Gemini is compiling response parameters...", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = if (response.isNotBlank()) response else "Awaiting input parameters...",
                            fontSize = 13.sp,
                            color = if (response.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
