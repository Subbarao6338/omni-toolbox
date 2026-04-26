package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AudioToolScreen(navController: NavHostController, title: String) {
    AudioBaseScreen(navController = navController, title = title) { columnScope, uri ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$title features for this file are coming soon!",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Processing: ${uri?.toString() ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
