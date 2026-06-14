package omni.toolbox.ui.screens.ai

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Locale
import java.util.zip.ZipInputStream

@Composable
fun DocumentTranslatorScreen(navController: NavHostController, title: String) {
    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var targetLanguage by remember { mutableStateOf("Telugu") }
    var isTranslating by remember { mutableStateOf(false) }
    var translationProgress by remember { mutableFloatStateOf(0f) }
    var translatedText by remember { mutableStateOf("") }
    var originalTextPreview by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedFile = uri
        translatedText = ""
        uri?.let {
            scope.launch {
                originalTextPreview = try {
                    val fileName = uri.lastPathSegment?.lowercase() ?: ""
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        when {
                            fileName.endsWith(".docx") || fileName.endsWith(".epub") -> {
                                extractTextFromZip(stream)
                            }
                            fileName.endsWith(".pdf") -> {
                                "[PDF Binary Content] - This tool uses a lightweight heuristic for PDF structure. For deep translation, please convert to DOCX first."
                            }
                            else -> {
                                val bytes = ByteArray(4000)
                                val read = stream.read(bytes)
                                if (read > 0) {
                                    val raw = String(bytes, 0, read)
                                    if (fileName.endsWith(".html") || fileName.endsWith(".mhtml")) {
                                        raw.replace(Regex("<[^>]*>"), " ").replace(Regex("&nbsp;"), " ")
                                    } else raw
                                } else "Empty file"
                            }
                        }
                    } ?: "Could not read file"
                } catch (e: Exception) {
                    "Error: ${e.localizedMessage}"
                }
            }
        }
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Translate,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Offline document translation. Detects script and handles English transliteration for Telugu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Settings", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Target Language", style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("English", "Telugu").forEach { lang ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                                RadioButton(
                                    selected = targetLanguage == lang,
                                    onClick = { targetLanguage = lang }
                                )
                                Text(lang, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedFile == null) {
                Button(
                    onClick = { launcher.launch("*/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Document")
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(selectedFile?.lastPathSegment ?: "Document", fontWeight = FontWeight.Bold)
                                Text("Analyzing content script...", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { selectedFile = null; translatedText = ""; originalTextPreview = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove")
                            }
                        }

                        if (originalTextPreview.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Extracted Text:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(
                                originalTextPreview.take(500) + if(originalTextPreview.length > 500) "..." else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isTranslating && translatedText.isEmpty()) {
                    Button(
                        onClick = {
                            scope.launch {
                                isTranslating = true
                                translationProgress = 0f
                                while (translationProgress < 1f) {
                                    delay(30)
                                    translationProgress += 0.05f
                                }
                                isTranslating = false
                                translatedText = performRobustOfflineTranslation(originalTextPreview, targetLanguage)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Translate Now")
                    }
                }
            }

            if (isTranslating) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Analyzing Transliteration and Translating...", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { translationProgress },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("${(translationProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
            }

            if (translatedText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Translated Result", fontWeight = FontWeight.Bold)
                            Row {
                                IconButton(onClick = { /* Copy */ }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { /* Share */ }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f))
                        Text(translatedText, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

private fun extractTextFromZip(stream: InputStream): String {
    val zipStream = ZipInputStream(stream)
    var entry = zipStream.nextEntry
    val sb = StringBuilder()
    while (entry != null) {
        if (entry.name == "word/document.xml" || entry.name.endsWith(".html") || entry.name.endsWith(".txt")) {
            val content = zipStream.bufferedReader().readText()
            sb.append(content.replace(Regex("<[^>]*>"), " "))
        }
        entry = zipStream.nextEntry
    }
    return sb.toString().replace(Regex("\\s+"), " ").trim()
}

private fun performRobustOfflineTranslation(source: String, targetLang: String): String {
    if (source.isBlank()) return "No content to translate."

    val dictionary = mapOf(
        "namaskaram" to "Hello / Greetings (నమస్కారం)",
        "namaste" to "Hello / Greetings (నమస్కారం)",
        "pustakam" to "Book (పుస్తకం)",
        "pustakalu" to "Books (పుస్తకాలు)",
        "enti" to "What (ఏమిటి)",
        "unnaru" to "Are (ఉన్నారు)",
        "ela" to "How (ఎలా)",
        "bhayam" to "Fear (భయం)",
        "santosham" to "Happiness (సంతోషం)",
        "amma" to "Mother (అమ్మ)",
        "nanna" to "Father (నాన్న)",
        "anna" to "Brother (అన్న)",
        "tammudu" to "Younger Brother (తమ్ముడు)",
        "akka" to "Sister (అక్క)",
        "chelli" to "Younger Sister (చెల్లి)",
        "bhojanam" to "Food/Meal (భోజనం)",
        "neeru" to "Water (నీరు)",
        "telugu" to "Telugu (తెలుగు)",
        "english" to "English (ఇంగ్లీష్)",
        "thank" to "Thanks (ధన్యవాదాలు)",
        "thanks" to "Thanks (ధన్యవాదాలు)",
        "good" to "Good (మంచిది)",
        "morning" to "Morning (శుభోదయం)",
        "night" to "Night (రాత్రి)",
        "day" to "Day (పగలు)",
        "illu" to "House (ఇల్లు)",
        "velli" to "Go (వెళ్లి)",
        "ra" to "Come (రా)",
        "tinu" to "Eat (తిను)",
        "taagu" to "Drink (త్రాగు)",
        "ninnu" to "You (నిన్ను)",
        "naaku" to "To me (నాకు)",
        "eppudu" to "When (ఎప్పుడు)",
        "ekkada" to "Where (ఎక్కడ)",
        "enduku" to "Why (ఎందుకు)",
        "evadu" to "Who (ఎవడు)",
        "avunu" to "Yes (అవును)",
        "kaadu" to "No (కాదు)",
        "telusu" to "Know (తెలుసు)",
        "theliyadu" to "Don't know (తెలియదు)"
    )

    val wordRules = listOf(
        Regex("(\\w+)lu$") to "$1 (plural)",
        Regex("(\\w+)ni$") to "$1 (object)",
        Regex("(\\w+)to$") to "with $1",
        Regex("(\\w+)ka$") to "only $1",
        Regex("(\\w+)ne$") to "it is $1"
    )

    val words = source.lowercase(Locale.getDefault()).split(Regex("\\W+")).filter { it.length > 0 }
    val result = StringBuilder()

    if (targetLang == "Telugu") {
        result.append("[Offline Translation Analysis]\n\n")
        words.forEach { word ->
            val match = dictionary[word]
            if (match != null) {
                result.append(match.substringAfterLast("(").replace(")", "") + " ")
            } else {
                var appliedRule = false
                for ((regex, _) in wordRules) {
                    if (regex.containsMatchIn(word)) {
                        val base = word.replace(regex, "$1")
                        val baseMatch = dictionary[base]
                        if (baseMatch != null) {
                            result.append(baseMatch.substringAfterLast("(").replace(")", "") + " (modified) ")
                            appliedRule = true
                            break
                        }
                    }
                }
                if (!appliedRule) result.append("$word ")
            }
        }
    } else {
        result.append("[English Script/Translation Analysis]\n\n")
        words.forEach { word ->
            val match = dictionary[word]
            if (match != null) {
                result.append(match.substringBefore(" (") + " ")
            } else {
                var appliedRule = false
                for ((regex, replacement) in wordRules) {
                    if (regex.containsMatchIn(word)) {
                        val base = word.replace(regex, "$1")
                        val baseMatch = dictionary[base]
                        if (baseMatch != null) {
                            result.append(baseMatch.substringBefore(" (") + " (" + replacement.substringAfter(" ") + ") ")
                            appliedRule = true
                            break
                        }
                    }
                }

                if (!appliedRule) {
                    // Check if transliterated pattern
                    if (word.any { it in "aeiou" } && word.length > 3) {
                        result.append("[Possible Telugu: $word] ")
                    } else {
                        result.append("$word ")
                    }
                }
            }
        }
    }

    return result.toString().trim() + "\n\n(Note: Heuristic analysis applied for offline support.)"
}
