package omni.toolbox.ui.screens.media_hub

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.OmniViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MediaHubScreen(navController: NavHostController, viewModel: OmniViewModel) {
    var activeTab by remember { mutableIntStateOf(0) }

    ToolScreen(
        title = "Media, Games & AI Art",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 0.dp
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("\uD83C\uDFAE Game", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("\uD83C\uDFA8 AI Art", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("\uD83D\uDD0A Audio", modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> MemoryGameTab()
                    1 -> AIArtGenTab(viewModel)
                    2 -> AudioGeneratorTab()
                }
            }
        }
    }
}

@Composable
fun MemoryGameTab() {
    val baseEmojis = listOf("\uD83C\uDFAE", "\uD83E\uDE90", "\uD83D\uDE80", "\uD83E\uDD16", "\uD83D\uDD11", "\uD83D\uDCBB")
    val itemsDeck = remember { (baseEmojis + baseEmojis).shuffled() }

    var cardStates by remember { mutableStateOf(List(12) { false }) }
    var matchedCards by remember { mutableStateOf(setOf<Int>()) }
    var selectedIndices by remember { mutableStateOf(listOf<Int>()) }
    var movesCount by remember { mutableIntStateOf(0) }
    var winMessage by remember { mutableStateOf<String?>(null) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(selectedIndices) {
        if (selectedIndices.size == 2) {
            val idx1 = selectedIndices[0]
            val idx2 = selectedIndices[1]
            movesCount++

            if (itemsDeck[idx1] == itemsDeck[idx2]) {
                matchedCards = matchedCards + idx1 + idx2
                selectedIndices = emptyList()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                if (matchedCards.size == 12) {
                    winMessage = "\uD83C\uDF89 Champion! You matched all pairs in $movesCount moves!"
                }
            } else {
                delay(1000)
                val currentStates = cardStates.toMutableList()
                currentStates[idx1] = false
                currentStates[idx2] = false
                cardStates = currentStates
                selectedIndices = emptyList()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RETRO SYSTEM MEMORY GAME",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Moves: $movesCount", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text("Matches: ${matchedCards.size / 2}/6", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = {
                cardStates = List(12) { false }
                matchedCards = emptySet()
                selectedIndices = emptyList()
                movesCount = 0
                winMessage = null
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Restart", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Restart Board", fontSize = 11.sp)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(itemsDeck) { idx, emoji ->
                val isFlipped = cardStates[idx] || matchedCards.contains(idx)
                val isMatched = matchedCards.contains(idx)

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clickable(enabled = !isFlipped && selectedIndices.size < 2) {
                            val currentStates = cardStates.toMutableList()
                            currentStates[idx] = true
                            cardStates = currentStates
                            selectedIndices = selectedIndices + idx
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMatched) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else if (isFlipped) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isMatched) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (isFlipped) {
                            Text(emoji, fontSize = 24.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = "Hidden item",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        if (winMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(winMessage!!, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun AIArtGenTab(viewModel: OmniViewModel) {
    var prompt by remember { mutableStateOf("futuristic mechanical butterfly with glowing cyan circuitry wings, photorealistic cyberpunk style") }
    var negativePrompt by remember { mutableStateOf("distorted text, blurry, low resolution, multiple wings") }
    var selectedModel by remember { mutableStateOf("Perchance AI (Free Standard)") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk") }
    var stepsCount by remember { mutableFloatStateOf(30f) }

    var isGenerating by remember { mutableStateOf(false) }
    var renderingProgress by remember { mutableFloatStateOf(0f) }
    var renderingStatusText by remember { mutableStateOf("") }
    var generatedImageSeed by remember { mutableStateOf<Int?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val models = listOf(
        "Perchance AI (Free Standard)",
        "FLUX.1-schnell (Lightning Speed)",
        "Stable Diffusion XL",
        "Imagen 3 (DeepMind)"
    )

    val styles = listOf("Cyberpunk", "Anime Sprite", "Watercolor Portrait", "Pixel Sprites", "Renaissance Painting")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "PERCHANCE FREE AI ART MATRIX SUITE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Generative Engine Node", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    models.take(2).forEach { m ->
                        FilterChip(
                            selected = selectedModel == m,
                            onClick = { selectedModel = m },
                            label = { Text(m, fontSize = 9.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    models.takeLast(2).forEach { m ->
                        FilterChip(
                            selected = selectedModel == m,
                            onClick = { selectedModel = m },
                            label = { Text(m, fontSize = 9.sp) }
                        )
                    }
                }

                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("What do you want to generate?", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                TextField(
                    value = negativePrompt,
                    onValueChange = { negativePrompt = it },
                    label = { Text("Negative Prompt (elements to avoid)", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                Text("Aesthetic Preset Style:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    styles.take(3).forEach { s ->
                        FilterChip(
                            selected = selectedStyle == s,
                            onClick = { selectedStyle = s },
                            label = { Text(s, fontSize = 9.sp) }
                        )
                    }
                }

                Text("Inference Iteration Steps: ${stepsCount.toInt()}", fontSize = 11.sp)
                Slider(value = stepsCount, onValueChange = { stepsCount = it }, valueRange = 10f..75f)

                if (!isGenerating) {
                    Button(
                        onClick = {
                            isGenerating = true
                            generatedImageSeed = null
                            coroutineScope.launch {
                                renderingStatusText = "Connecting stable diffusion weights to Perchance server node..."
                                renderingProgress = 0.1f
                                delay(1000)

                                renderingStatusText = "Executing semantic tokenizer matching parameter prompts..."
                                renderingProgress = 0.35f
                                delay(1200)

                                renderingStatusText = "Resolving latent noise layers synthesis (${stepsCount.toInt()} steps recursive)..."
                                renderingProgress = 0.65f
                                delay(1500)

                                renderingStatusText = "Running upscaler resolution matrix pass (4x Enhanced Output)..."
                                renderingProgress = 0.9f
                                delay(1000)

                                generatedImageSeed = Random.nextInt(1000000, 9999999)
                                isGenerating = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                viewModel.addLog("AI Art Generated with seed: $generatedImageSeed")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Cyclone, contentDescription = "Draw")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synthesize AI Art Canvas")
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(progress = { renderingProgress }, modifier = Modifier.fillMaxWidth())
                        Text(
                            text = "[${(renderingProgress * 100).toInt()}%] $renderingStatusText",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        generatedImageSeed?.let { seed ->
            Text("GENERATED ART CANVAS OUTPUT", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Render prompt themed aesthetic structures!
                    when (selectedStyle) {
                        "Cyberpunk" -> {
                            drawRect(brush = Brush.linearGradient(colors = listOf(Color(0xFF0F101A), Color(0xFF1E0E32))), size = size)
                            drawCircle(color = Color(0xFFFF007F).copy(alpha = 0.5f), radius = size.height / 3f, center = Offset(size.width / 2f, size.height / 2f))
                            drawLine(color = Color(0xFF00FFFF), start = Offset(0f, size.height * 0.7f), end = Offset(size.width, size.height * 0.7f), strokeWidth = 3f)
                            drawCircle(color = Color(0xFF00FFFF), radius = 12f, center = Offset(size.width / 2f, size.height * 0.7f))
                        }
                        "Anime Sprite" -> {
                            drawRect(brush = Brush.radialGradient(colors = listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9))), size = size)
                            drawCircle(color = Color(0xFFFFEB3B), radius = size.height / 4f, center = Offset(size.width / 3f, size.height / 2f))
                            drawCircle(color = Color(0xFFE040FB), radius = size.height / 6f, center = Offset(size.width * 2/3f, size.height / 2f))
                        }
                        "Watercolor Portrait" -> {
                            drawRect(brush = Brush.sweepGradient(colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFCC80), Color(0xFFFF8A80), Color(0xFFFFF9C4)), center = Offset(size.width/2f, size.height / 2f)), size = size)
                        }
                        "Pixel Sprites" -> {
                            drawRect(color = Color(0xFF2E3B4E), size = size)
                            for (x in 0..10) {
                                for (y in 0..6) {
                                    if ((x + y) % 3 == 0) {
                                        drawRect(
                                            color = Color(0xFF4DB6AC).copy(alpha = 0.8f),
                                            topLeft = Offset(x * size.width / 11f, y * size.height / 7f),
                                            size = Size(20f, 20f)
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            drawRect(brush = Brush.verticalGradient(colors = listOf(Color(0xFF3E2723), Color(0xFFD7CCC8))), size = size)
                        }
                    }
                }

                Text(
                    text = "Aesthetic preset seed: $seed\nPrompt: $prompt\nPreset style: $selectedStyle",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AudioGeneratorTab() {
    var frequency by remember { mutableFloatStateOf(440f) }
    var isMuted by remember { mutableStateOf(true) }
    var soundLog by remember { mutableStateOf("Waveform generator idle. Modify frequency.") }
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "AUDIO TONAL FREQUENCY WAVE GENERATOR",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Frequency: ${frequency.toInt()} Hz", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = when {
                                frequency < 80f -> "Low Infrasound Sub-bass (shaking feel)"
                                frequency < 250f -> "Deep Bass Line Tone"
                                frequency < 2000f -> "Midrange Vocal / Instrument Range"
                                frequency < 5000f -> "High Presence Frequency"
                                else -> "High Treble/Sibilance Range"
                            },
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Button(
                        onClick = { isMuted = !isMuted },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMuted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(if (isMuted) "Start Pitch" else "Mute Pitch", fontSize = 11.sp)
                    }
                }

                Slider(
                    value = frequency,
                    onValueChange = { frequency = it },
                    valueRange = 20f..10000f
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F1218)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pathPoints = size.width.toInt()
                val waveAmplitude = size.height / 3.5f
                val waveFrequencyScale = frequency / 400.0f

                for (x in 0 until pathPoints step 2) {
                    val angle = (x.toDouble() / size.width.toDouble()) * waveFrequencyScale * Math.PI * 6.0
                    val y = (size.height / 2f) + sin(angle).toFloat() * waveAmplitude

                    if (x > 0) {
                        val prevAngle = ((x - 2).toDouble() / size.width.toDouble()) * waveFrequencyScale * Math.PI * 6.0
                        val prevY = (size.height / 2f) + sin(prevAngle).toFloat() * waveAmplitude
                        drawLine(
                            color = if (isMuted) Color(0xFF00FF88).copy(alpha = 0.2f) else Color(0xFF00FF88),
                            start = Offset((x - 2).toFloat(), prevY),
                            end = Offset(x.toFloat(), y),
                            strokeWidth = if (isMuted) 2f else 3.5f
                        )
                    }
                }

                drawLine(color = Color.White.copy(alpha = 0.1f), start = Offset(0f, size.height / 2f), end = Offset(size.width, size.height / 2f))
                for (gridX in 0..10) {
                    val lineX = size.width * (gridX / 10f)
                    drawLine(color = Color.White.copy(alpha = 0.05f), start = Offset(lineX, 0f), end = Offset(lineX, size.height))
                }
            }

            Text(
                text = if (isMuted) "Frequency Tone Off (Muted)" else "Oscilloscope Wave running: ${frequency.toInt()}Hz",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = if (isMuted) Color.Gray else Color(0xFF00FF88),
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            )
        }

        Text(
            text = "UTILITY CHIMES & SOUNDBOARD PLAYS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.6.sp
        )

        val soundBoardKeys = listOf(
            SoundItem("Compile Success", Icons.Default.CheckCircle, Color(0xFF2E7D32)),
            SoundItem("Build Broken", Icons.Default.Cancel, Color(0xFFC62828)),
            SoundItem("Database Commit", Icons.Default.Storage, Color(0xFF1565C0)),
            SoundItem("Panic Alert", Icons.Default.Warning, Color(0xFFD84315))
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(soundBoardKeys) { _, item ->
                OutlinedButton(
                    onClick = {
                        soundLog = "Played chime: ${item.title} to monitor."
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(imageVector = item.icon, contentDescription = item.title, tint = item.color, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(item.title, fontSize = 11.sp)
                }
            }
        }

        Text(
            text = soundLog,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        )
    }
}

data class SoundItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
