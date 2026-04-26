package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun AudioToolScreen(navController: NavHostController, title: String) {
    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$title is coming soon!",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This tool is under development.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
