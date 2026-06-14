package omni.toolbox.ui.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun CsvToJsonScreen(navController: NavHostController) {
    var csvInput by remember { mutableStateOf("") }
    var jsonOutput by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    ToolScreen(title = "CSV to JSON", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(scrollState)) {
            OutlinedTextField(
                value = csvInput,
                onValueChange = { csvInput = it },
                label = { Text("CSV Input (First row headers)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { jsonOutput = convertCsvToJson(csvInput) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convert")
            }

            if (jsonOutput.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("JSON Result", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(jsonOutput)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                        Text(jsonOutput, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

fun convertCsvToJson(csv: String): String {
    return try {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return "Invalid CSV. Need at least headers and one data row."
        val headers = lines[0].split(",").map { it.trim() }
        val jsonArray = JSONArray()

        for (i in 1 until lines.size) {
            val values = lines[i].split(",").map { it.trim() }
            val jsonObject = JSONObject()
            for (j in headers.indices) {
                if (j < values.size) {
                    jsonObject.put(headers[j], values[j])
                }
            }
            jsonArray.put(jsonObject)
        }
        jsonArray.toString(4)
    } catch (e: Exception) {
        "Error converting CSV: ${e.message}"
    }
}
