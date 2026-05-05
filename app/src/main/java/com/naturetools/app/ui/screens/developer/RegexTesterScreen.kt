package com.naturetools.app.ui.screens.developer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun RegexTesterScreen(navController: NavHostController) {
    var regexPattern by remember { mutableStateOf("") }
    var testString by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val highlightedResult = remember(regexPattern, testString) {
        if (regexPattern.isEmpty()) return@remember AnnotatedString(testString)
        try {
            val regex = Regex(regexPattern)
            error = null
            buildAnnotatedString {
                var lastIndex = 0
                regex.findAll(testString).forEach { match ->
                    append(testString.substring(lastIndex, match.range.first))
                    withStyle(style = SpanStyle(background = Color.Yellow.copy(alpha = 0.5f), color = Color.Black)) {
                        append(match.value)
                    }
                    lastIndex = match.range.last + 1
                }
                append(testString.substring(lastIndex))
            }
        } catch (e: Exception) {
            error = e.message
            AnnotatedString(testString)
        }
    }

    ToolScreen(title = "Regex Tester", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = regexPattern,
                onValueChange = { regexPattern = it },
                label = { Text("Regex Pattern") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                placeholder = { Text("e.g. [a-z]+") },
                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null) },
                singleLine = true
            )

            if (error != null) {
                Text(
                    text = error ?: "Invalid regex",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = testString,
                onValueChange = { testString = it },
                label = { Text("Test String") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                placeholder = { Text("Enter text to test against...") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Matches Highlighted", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = highlightedResult,
                    modifier = Modifier.padding(16.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
