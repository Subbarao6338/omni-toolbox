package omni.toolbox.ui.screens.developer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun TerminalScreen(navController: NavHostController) {
    var command by remember { mutableStateOf("") }
    val output = remember { mutableStateListOf<String>("Omni Terminal v1.0.0", "Type 'help' for commands.") }

    ToolScreen(title = "Terminal Emulator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color.Black).padding(8.dp)) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                output.forEach { line ->
                    Text(line, color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("$ ", color = Color.Green, fontFamily = FontFamily.Monospace)
                BasicTextField(
                    value = command,
                    onValueChange = { command = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Green, fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.Green),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = {
                        if (command.isNotBlank()) {
                            output.add("$ $command")
                            val response = when (command.trim().lowercase()) {
                                "help" -> "Available: help, ls, clear, whoami, uname, date"
                                "ls" -> "app/  data/  cache/  logs/"
                                "whoami" -> "omni_user"
                                "uname" -> "Linux omni-toolbox 5.10.0"
                                "date" -> java.util.Date().toString()
                                "clear" -> { output.clear(); "" }
                                else -> "Command not found: $command"
                            }
                            if (response.isNotEmpty()) output.add(response)
                            command = ""
                        }
                    })
                )
            }
        }
    }
}

@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle.Default,
    cursorBrush: androidx.compose.ui.graphics.Brush = androidx.compose.ui.graphics.SolidColor(Color.Black),
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        cursorBrush = cursorBrush,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true
    )
}
