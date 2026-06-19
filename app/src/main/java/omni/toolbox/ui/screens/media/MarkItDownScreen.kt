package omni.toolbox.ui.screens.media

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Scanner
import java.util.zip.ZipInputStream

@Composable
fun MarkItDownScreen(navController: NavHostController) {
    val context = LocalContext.current
    var selectedFiles by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        selectedFiles = it
    }
    var isProcessing by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf("") }
    var urlToConvert by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ToolScreen(title = "Batch Converter", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Transform, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Batch Document Converter", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Offline PDF, Office, Web & Media to Markdown", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedFiles.isEmpty()) {
                OutlinedTextField(
                    value = urlToConvert,
                    onValueChange = { urlToConvert = it },
                    label = { Text("Web URL to Markdown") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (urlToConvert.isNotEmpty()) {
                            IconButton(onClick = {
                                isProcessing = true
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        processingMessage = "Fetching URL..."
                                        val doc = Jsoup.connect(urlToConvert).get()
                                        val markdown = convertHtmlToMainMarkdown(doc.html())
                                        val outputDir = File(context.getExternalFilesDir(null), "MarkItDown")
                                        if (!outputDir.exists()) outputDir.mkdirs()
                                        val fileName = urlToConvert.substringAfterLast("/").ifEmpty { "index" }.substringBefore("?") + ".md"
                                        File(outputDir, fileName).writeText(markdown)
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "URL converted and saved to MarkItDown folder", Toast.LENGTH_LONG).show()
                                            urlToConvert = ""
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Download, contentDescription = "Convert URL")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("OR", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { launcher.launch("*/*") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Files to Convert")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    items(selectedFiles) { uri ->
                        Card(modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(uri.lastPathSegment ?: "Unknown File", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isProcessing) {
                    Text(processingMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
                } else {
                    Button(
                        onClick = {
                            isProcessing = true
                            scope.launch(Dispatchers.IO) {
                                try {
                                    processBatch(context, selectedFiles) { msg -> processingMessage = msg }
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Batch conversion complete!", Toast.LENGTH_LONG).show()
                                        selectedFiles = emptyList()
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                } finally {
                                    isProcessing = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Batch Conversion")
                    }

                    OutlinedButton(
                        onClick = { selectedFiles = emptyList() },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Clear Selection")
                    }
                }
            }
        }
    }
}

private suspend fun processBatch(context: Context, uris: List<Uri>, onUpdate: (String) -> Unit) {
    val outputDir = File(context.getExternalFilesDir(null), "MarkItDown")
    if (!outputDir.exists()) outputDir.mkdirs()

    uris.forEachIndexed { index, uri ->
        val fileName = uri.lastPathSegment ?: "file_$index"
        onUpdate("Processing ${index + 1}/${uris.size}: $fileName")

        try {
            val extension = fileName.substringAfterLast(".").lowercase()
            val inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream != null) {
                val outputFileName = fileName.substringBeforeLast(".") + ".md"
                val outputFile = File(outputDir, outputFileName)

                when (extension) {
                    "html", "htm" -> {
                        val html = inputStream.bufferedReader().use { it.readText() }
                        val markdown = convertHtmlToMainMarkdown(html)
                        outputFile.writeText(markdown)
                    }
                    "mhtml" -> {
                        val mhtml = inputStream.bufferedReader().use { it.readText() }
                        val html = extractHtmlFromMhtml(mhtml)
                        val markdown = convertHtmlToMainMarkdown(html)
                        outputFile.writeText(markdown)
                    }
                    "pdf" -> {
                        val document = PDDocument.load(inputStream)
                        val stripper = PDFTextStripper()
                        var text = stripper.getText(document)

                        if (text.trim().isEmpty()) {
                            onUpdate("Scanned PDF detected. Running OCR...")
                            val renderer = PDFRenderer(document)
                            val sb = StringBuilder()
                            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                            for (i in 0 until document.numberOfPages) {
                                val bitmap = renderer.renderImageWithDPI(i, 300f)
                                val image = InputImage.fromBitmap(bitmap, 0)
                                val result = recognizer.process(image).await()
                                sb.append(result.text).append("\n\n")
                            }
                            text = sb.toString()
                        }

                        document.close()
                        outputFile.writeText(formatPdfTextToMarkdown(text))
                    }
                    "docx" -> {
                        val text = extractTextFromDocx(inputStream)
                        outputFile.writeText(text)
                    }
                    "xlsx" -> {
                        val text = extractTextFromXlsx(inputStream)
                        outputFile.writeText(text)
                    }
                    "pptx" -> {
                        val text = extractTextFromPptx(inputStream)
                        outputFile.writeText(text)
                    }
                    "csv" -> {
                        val text = inputStream.bufferedReader().use { it.readText() }
                        outputFile.writeText(convertCsvToMarkdown(text))
                    }
                    "jpg", "jpeg", "png", "webp" -> {
                        val image = InputImage.fromFilePath(context, uri)
                        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        val result = recognizer.process(image).await()
                        outputFile.writeText("# OCR Result\n\n${result.text}")
                    }
                    "mp4", "mkv", "avi", "mov" -> {
                        extractAudioFromVideo(context, uri, outputDir, fileName.substringBeforeLast("."))
                    }
                    "mdx" -> {
                        val mdx = inputStream.bufferedReader().use { it.readText() }
                        outputFile.writeText(mdx) // Simple copy for now
                    }
                    "md" -> {
                        val md = inputStream.bufferedReader().use { it.readText() }
                        val mdxFile = File(outputDir, fileName.substringBeforeLast(".") + ".mdx")
                        mdxFile.writeText("---\ntitle: ${fileName}\n---\n\n$md")
                    }
                    else -> {
                        // Placeholder for other formats
                        delay(200)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        delay(100)
    }
}

private fun convertHtmlToMainMarkdown(html: String): String {
    val doc = Jsoup.parse(html)
    // Remove scripts and styles
    doc.select("script, style, nav, footer").remove()
    val body = doc.body()

    val sb = StringBuilder()

    fun processElement(element: org.jsoup.nodes.Element, depth: Int) {
        when (element.tagName()) {
            "h1" -> sb.append("\n# ").append(element.text()).append("\n\n")
            "h2" -> sb.append("\n## ").append(element.text()).append("\n\n")
            "h3" -> sb.append("\n### ").append(element.text()).append("\n\n")
            "h4" -> sb.append("\n#### ").append(element.text()).append("\n\n")
            "h5" -> sb.append("\n##### ").append(element.text()).append("\n\n")
            "h6" -> sb.append("\n###### ").append(element.text()).append("\n\n")
            "p" -> sb.append("\n").append(element.text()).append("\n\n")
            "br" -> sb.append("\n")
            "hr" -> sb.append("\n---\n\n")
            "b", "strong" -> sb.append("**").append(element.text()).append("**")
            "i", "em" -> sb.append("*").append(element.text()).append("*")
            "ul" -> {
                sb.append("\n")
                element.children().forEach { li ->
                    sb.append("  ".repeat(depth)).append("* ").append(li.text()).append("\n")
                }
                sb.append("\n")
            }
            "ol" -> {
                sb.append("\n")
                element.children().forEachIndexed { i, li ->
                    sb.append("  ".repeat(depth)).append("${i+1}. ").append(li.text()).append("\n")
                }
                sb.append("\n")
            }
            "img" -> {
                val alt = element.attr("alt")
                val src = element.attr("src")
                sb.append("\n![").append(alt).append("](").append(src).append(")\n\n")
            }
            "a" -> {
                val text = element.text()
                val href = element.attr("href")
                sb.append("[").append(text).append("](").append(href).append(")")
            }
            "code" -> sb.append("`").append(element.text()).append("`")
            "pre" -> sb.append("\n```\n").append(element.text()).append("\n```\n\n")
            "blockquote" -> sb.append("\n> ").append(element.text()).append("\n\n")
            "table" -> {
                sb.append("\n")
                element.select("tr").forEach { tr ->
                    sb.append("|")
                    tr.select("th, td").forEach { cell ->
                        sb.append(" ").append(cell.text()).append(" |")
                    }
                    sb.append("\n")
                }
                sb.append("\n")
            }
            "div", "span", "section", "article" -> {
                element.children().forEach { processElement(it, depth + 1) }
                if (element.tagName() != "span") sb.append("\n")
            }
            else -> {
                if (element.ownText().isNotEmpty()) {
                    sb.append(element.ownText())
                }
                element.children().forEach { processElement(it, depth) }
            }
        }
    }

    body.children().forEach { processElement(it, 0) }

    return sb.toString().replace(Regex("\\n{3,}"), "\n\n").trim()
}

private fun extractHtmlFromMhtml(mhtml: String): String {
    // Very basic MHTML to HTML extractor (finding the first HTML part)
    val htmlStartIndex = mhtml.indexOf("<html>", ignoreCase = true)
    val htmlEndIndex = mhtml.lastIndexOf("</html>", ignoreCase = true)

    return if (htmlStartIndex != -1 && htmlEndIndex != -1) {
        mhtml.substring(htmlStartIndex, htmlEndIndex + 7)
    } else {
        mhtml // Fallback
    }
}

private fun formatPdfTextToMarkdown(text: String): String {
    val sb = StringBuilder()
    text.lines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return@forEach

        // Simple heuristic for headers: Short lines with all caps or mostly caps
        if (trimmed.length < 60 && trimmed.count { it.isUpperCase() } > trimmed.length / 2) {
            sb.append("\n## ").append(trimmed).append("\n\n")
        } else if (trimmed.startsWith("•") || trimmed.startsWith("-")) {
            sb.append("* ").append(trimmed.substring(1).trim()).append("\n")
        } else {
            sb.append(trimmed).append("\n\n")
        }
    }
    return sb.toString().replace(Regex("\\n{3,}"), "\n\n").trim()
}

private fun extractTextFromDocx(inputStream: InputStream): String {
    val sb = StringBuilder()
    ZipInputStream(inputStream).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (entry.name == "word/document.xml") {
                val content = zip.bufferedReader().readText()
                // Simple XML tag stripping for DOCX (word/document.xml)
                // We'll look for <w:t> tags which contain the actual text
                val regex = Regex("<w:t[^>]*>(.*?)</w:t>")
                val matches = regex.findAll(content)
                matches.forEach { match ->
                    sb.append(match.groupValues[1])
                }
                // Also handle paragraphs to add newlines
                // This is a VERY simplified parser. A real one would be much more complex.
                // Re-parsing to add some basic structure:
                val structured = content.replace("</w:p>", "\n\n")
                    .replace(Regex("<[^>]+>"), "")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&quot;", "\"")
                return structured.trim()
            }
            entry = zip.nextEntry
        }
    }
    return sb.toString()
}

private fun extractTextFromXlsx(inputStream: InputStream): String {
    val sb = StringBuilder()
    var sharedStrings = listOf<String>()
    val sheets = mutableMapOf<String, String>()

    ZipInputStream(inputStream).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            when {
                entry.name == "xl/sharedStrings.xml" -> {
                    val content = zip.bufferedReader().readText()
                    val regex = Regex("<t[^>]*>(.*?)</t>")
                    sharedStrings = regex.findAll(content).map { it.groupValues[1] }.toList()
                }
                entry.name.startsWith("xl/worksheets/sheet") -> {
                    sheets[entry.name] = zip.bufferedReader().readText()
                }
            }
            entry = zip.nextEntry
        }
    }

    sheets.forEach { (name, content) ->
        sb.append("### ").append(name.substringAfterLast("/").substringBefore(".")).append("\n\n")
        val rows = Regex("<row[^>]*>(.*?)</row>").findAll(content)
        rows.forEach { rowMatch ->
            val cells = Regex("<c[^>]*t=\"s\"[^>]*><v>(.*?)</v></c>|<c[^>]*><v>(.*?)</v></c>").findAll(rowMatch.groupValues[1])
            sb.append("|")
            cells.forEach { cellMatch ->
                val sharedIndex = cellMatch.groupValues[1]
                val value = cellMatch.groupValues[2]
                if (sharedIndex.isNotEmpty()) {
                    val idx = sharedIndex.toIntOrNull() ?: -1
                    if (idx in sharedStrings.indices) {
                        sb.append(" ").append(sharedStrings[idx]).append(" |")
                    } else {
                        sb.append(" ").append(sharedIndex).append(" |")
                    }
                } else {
                    sb.append(" ").append(value).append(" |")
                }
            }
            sb.append("\n")
        }
        sb.append("\n")
    }

    return sb.toString().trim()
}

private fun extractAudioFromVideo(context: Context, videoUri: Uri, outputDir: File, baseName: String) {
    val extractor = MediaExtractor()
    try {
        context.contentResolver.openFileDescriptor(videoUri, "r")?.use { pfd ->
            extractor.setDataSource(pfd.fileDescriptor)

            var audioTrackIndex = -1
            var mimeType = ""
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("audio/") == true) {
                    audioTrackIndex = i
                    mimeType = mime
                    break
                }
            }

            if (audioTrackIndex != -1) {
                val extension = when {
                    mimeType.contains("mp3") -> "mp3"
                    mimeType.contains("aac") -> "m4a"
                    mimeType.contains("flac") -> "flac"
                    mimeType.contains("wav") -> "wav"
                    else -> "audio"
                }
                val outputFile = File(outputDir, "$baseName.$extension")
                extractor.selectTrack(audioTrackIndex)
                val outStream = FileOutputStream(outputFile)
                val buffer = java.nio.ByteBuffer.allocate(1024 * 1024)

                while (true) {
                    val sampleSize = extractor.readSampleData(buffer, 0)
                    if (sampleSize < 0) break

                    val bytes = ByteArray(sampleSize)
                    buffer.get(bytes)
                    outStream.write(bytes)
                    buffer.clear()
                    extractor.advance()
                }
                outStream.close()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        extractor.release()
    }
}

private fun convertCsvToMarkdown(csv: String): String {
    val sb = StringBuilder()
    val lines = csv.lines().filter { it.isNotBlank() }
    if (lines.isEmpty()) return ""

    lines.forEachIndexed { i, line ->
        val cols = line.split(",")
        sb.append("|")
        cols.forEach { col -> sb.append(" ").append(col.trim()).append(" |") }
        sb.append("\n")

        if (i == 0) {
            sb.append("|")
            cols.forEach { _ -> sb.append(" --- |") }
            sb.append("\n")
        }
    }

    return sb.toString().trim()
}

private fun extractTextFromPptx(inputStream: InputStream): String {
    val sb = StringBuilder()
    val slides = mutableMapOf<String, String>()

    ZipInputStream(inputStream).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (entry.name.startsWith("ppt/slides/slide") && entry.name.endsWith(".xml")) {
                slides[entry.name] = zip.bufferedReader().readText()
            }
            entry = zip.nextEntry
        }
    }

    slides.keys.sorted().forEach { key ->
        val content = slides[key] ?: ""
        sb.append("## Slide ").append(key.substringAfterLast("/").substringBefore(".")).append("\n\n")
        val regex = Regex("<a:t[^>]*>(.*?)</a:t>")
        val matches = regex.findAll(content)
        matches.forEach { match ->
            sb.append(match.groupValues[1]).append(" ")
        }
        sb.append("\n\n")
    }

    return sb.toString().trim()
}
