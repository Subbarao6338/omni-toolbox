package omni.toolbox.ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.util.UUID

@Composable
fun DeveloperExpertScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "Hex Viewer" -> HexViewer()
                "ASCII Table" -> AsciiTable()
                "Crontab Gen" -> CronHelper()
                "UUID Generator" -> UuidGenerator()
                "SQL Formatter" -> SqlFormatter()
                "Minifier" -> Minifier()
                "YAML to JSON" -> DataConverter("YAML to JSON")
                "XML to JSON" -> DataConverter("XML to JSON")
                "JSON to TypeScript" -> DataConverter("JSON to TS")
                "YAML ↔ JSON" -> DataConverter("YAML ↔ JSON")
                "XML ↔ JSON" -> DataConverter("XML ↔ JSON")
                else -> Text("Developer Utility for $title")
            }
        }
    }
}

@Composable
fun UuidGenerator() {
    var uuid by remember { mutableStateOf(UUID.randomUUID().toString()) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("UUID v4 Generator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = uuid, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { uuid = UUID.randomUUID().toString() }, modifier = Modifier.fillMaxWidth()) {
            Text("Generate New UUID")
        }
    }
}

@Composable
fun SqlFormatter() {
    var sqlInput by remember { mutableStateOf("SELECT * FROM users WHERE id = 1") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("SQL Formatter", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = sqlInput, onValueChange = { sqlInput = it }, modifier = Modifier.fillMaxWidth(), minLines = 5)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            sqlInput = sqlInput.replace(Regex("(?i)SELECT"), "SELECT")
                .replace(Regex("(?i)FROM"), "\nFROM")
                .replace(Regex("(?i)WHERE"), "\nWHERE")
                .replace(Regex("(?i)AND"), "\n  AND")
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Format SQL")
        }
    }
}

@Composable
fun CronHelper() {
    var cronExpr by remember { mutableStateOf("0 0 * * *") }

    val explanation = remember(cronExpr) {
        try {
            val parts = cronExpr.trim().split(Regex("\\s+"))
            if (parts.size < 5) "Invalid cron expression (needs 5 parts)"
            else {
                val (min, hour, dom, mon, dow) = parts
                val m = if (min == "*") "every minute" else "at minute $min"
                val h = if (hour == "*") "every hour" else "at hour $hour"
                val d = if (dom == "*") "every day" else "on day $dom of the month"
                val mo = if (mon == "*") "every month" else "in month $mon"
                val w = if (dow == "*") "every day of the week" else "on day $dow of the week"
                "Execution: $m, $h, $d, $mo, $w."
            }
        } catch (e: Exception) {
            "Error parsing expression"
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Cron Helper", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = cronExpr, onValueChange = { cronExpr = it }, label = { Text("Cron Expression") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = explanation,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun Minifier() {
    var codeInput by remember { mutableStateOf("function hello() {\n  console.log('world');\n}") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("JS/CSS Minifier", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = codeInput, onValueChange = { codeInput = it }, modifier = Modifier.fillMaxWidth(), minLines = 5)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            codeInput = codeInput.replace(Regex("\\s+"), " ").trim()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Minify Code")
        }
    }
}

@Composable
fun DataConverter(type: String) {
    var input by remember { mutableStateOf(when(type) {
        "YAML to JSON", "YAML ↔ JSON" -> "name: Omni Toolbox\nversion: 1.0"
        "XML to JSON", "XML ↔ JSON" -> "<tool><name>Omni Toolbox</name></tool>"
        "JSON to TS" -> "{\n  \"name\": \"Omni Toolbox\",\n  \"active\": true\n}"
        else -> ""
    }) }
    var output by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(type, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Input") }, modifier = Modifier.fillMaxWidth(), minLines = 5)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            output = when {
                type.contains("YAML") -> {
                    val lines = input.lines().filter { it.isNotBlank() }
                    val result = mutableListOf<String>()
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        if (trimmed.contains(":")) {
                            val parts = trimmed.split(":", limit = 2)
                            val key = parts[0].trim().removeSurrounding("\"")
                            val value = parts[1].trim().removeSurrounding("\"")
                            if (value.isNotEmpty()) {
                                result.add("\"$key\": \"$value\"")
                            } else {
                                result.add("\"$key\": null")
                            }
                        }
                    }
                    result.joinToString(",\n  ", "{\n  ", "\n}")
                }
                type.contains("XML") -> {
                    val tagRegex = Regex("<(\\w+)>([^<]+)</\\1>")
                    val matches = tagRegex.findAll(input)
                    matches.map { it.groupValues }.joinToString(",\n  ", "{\n  ", "\n}") { g ->
                        "\"${g[1]}\": \"${g[2]}\""
                    }
                }
                type.contains("TS") -> {
                    "interface Generated {\n" +
                    input.lines().filter { it.contains(":") }.joinToString("\n") { line ->
                        val parts = line.split(":", limit = 2)
                        val key = parts[0].trim().removeSurrounding("\"")
                        val value = parts[1].trim().removeSurrounding(",")
                        "  $key: ${if (value.startsWith("\"")) "string" else if (value == "true" || value == "false") "boolean" else "number"};"
                    } + "\n}"
                }
                else -> "Conversion logic for $type"
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Convert")
        }

        if (output.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = output, onValueChange = {}, label = { Text("Output") }, modifier = Modifier.fillMaxWidth(), minLines = 5, readOnly = true)
        }
    }
}

@Composable
fun HexViewer() {
    var textInput by remember { mutableStateOf("Omni Toolbox") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Hex Viewer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Input Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val hexString = textInput.toByteArray().joinToString(" ") {
            String.format("%02X", it)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = hexString,
                modifier = Modifier.padding(16.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AsciiTable() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("ASCII Table (Common)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Dec", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Hex", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                    Text("Char", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider()
                (32..126).forEach { i ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(i.toString(), modifier = Modifier.weight(1f))
                        Text(String.format("%02X", i), modifier = Modifier.weight(1f))
                        Text(i.toChar().toString(), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
