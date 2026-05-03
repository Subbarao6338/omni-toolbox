package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun TextDiffScreen(navController: NavHostController) {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var showDiff by remember { mutableStateOf(false) }

    ToolScreen(
        title = "Text Diff",
        onBack = { navController.popBackStack() },
        actions = {
            Button(onClick = { showDiff = !showDiff }) {
                Text(if (showDiff) "Edit" else "Compare")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (!showDiff) {
                OutlinedTextField(
                    value = text1,
                    onValueChange = { text1 = it },
                    label = { Text("Original Text") },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    label = { Text("Modified Text") },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                )
            } else {
                val diffs = computeDiff(text1, text2)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(diffs) { line ->
                        val bgColor = when {
                            line.startsWith("+") -> Color.Green.copy(alpha = 0.2f)
                            line.startsWith("-") -> Color.Red.copy(alpha = 0.2f)
                            else -> Color.Transparent
                        }
                        Text(
                            text = line,
                            modifier = Modifier.fillMaxWidth().background(bgColor).padding(horizontal = 4.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

private fun computeDiff(t1: String, t2: String): List<String> {
    val l1 = t1.lines()
    val l2 = t2.lines()
    val result = mutableListOf<String>()

    // Extremely simplified diff for illustration
    var i = 0
    var j = 0
    while (i < l1.size || j < l2.size) {
        when {
            i < l1.size && j < l2.size && l1[i] == l2[j] -> {
                result.add("  " + l1[i])
                i++
                j++
            }
            j < l2.size && (i >= l1.size || l2[j] !in l1.drop(i)) -> {
                result.add("+ " + l2[j])
                j++
            }
            i < l1.size -> {
                result.add("- " + l1[i])
                i++
            }
        }
    }
    return result
}
