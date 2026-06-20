package com.nature.files.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BreadcrumbBar(
    path: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = path.split("/").filter { it.isNotEmpty() }
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onNavigate("/") }) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }

        var currentPath = ""
        parts.forEach { part ->
            currentPath += "/$part"
            val thisPath = currentPath
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
            TextButton(onClick = { onNavigate(thisPath) }) {
                Text(part)
            }
        }
    }
}
