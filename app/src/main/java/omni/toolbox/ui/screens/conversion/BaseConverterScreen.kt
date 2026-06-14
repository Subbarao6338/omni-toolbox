package omni.toolbox.ui.screens.conversion

import androidx.compose.foundation.layout.*
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
import java.util.*

@Composable
fun BaseConverterScreen(navController: NavHostController) {
    var input by remember { mutableStateOf("") }
    var inputBase by remember { mutableIntStateOf(10) }
    val clipboardManager = LocalClipboardManager.current

    val bases = listOf(2, 8, 10, 16)

    ToolScreen(
        title = "Base Converter",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Input Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("From Base:", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                bases.forEach { base ->
                    FilterChip(
                        selected = inputBase == base,
                        onClick = { inputBase = base },
                        label = { Text("Base $base") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            bases.forEach { base ->
                val converted = try {
                    if (input.isEmpty()) ""
                    else {
                        val num = input.toLong(inputBase)
                        num.toString(base).uppercase(Locale.US)
                    }
                } catch (e: Exception) {
                    "Invalid"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Base $base", style = MaterialTheme.typography.labelSmall)
                            Text(converted, style = MaterialTheme.typography.bodyLarge)
                        }
                        if (converted.isNotEmpty() && converted != "Invalid") {
                            IconButton(onClick = { clipboardManager.setText(AnnotatedString(converted)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                            }
                        }
                    }
                }
            }
        }
    }
}
