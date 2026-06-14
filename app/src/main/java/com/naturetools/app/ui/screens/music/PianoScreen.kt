package com.naturetools.app.ui.screens.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun PianoScreen(navController: NavHostController) {
    val keys = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val octaves = 2
    val allKeys = mutableListOf<String>()
    repeat(octaves) { o ->
        keys.forEach { k -> allKeys.add("$k${o + 4}") }
    }

    ToolScreen(title = "Pocket Piano", onBack = { navController.popBackStack() }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(allKeys) { key ->
                    val isBlack = key.contains("#")
                    PianoKey(key, isBlack)
                }
            }
        }
    }
}

@Composable
fun PianoKey(note: String, isBlack: Boolean) {
    Box(
        modifier = Modifier
            .width(if (isBlack) 40.dp else 60.dp)
            .fillMaxHeight(if (isBlack) 0.6f else 1f)
            .padding(horizontal = 2.dp)
            .background(if (isBlack) Color.Black else Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
            .clickable { /* Play note */ }
            .then(if (!isBlack) Modifier.background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)) else Modifier),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (!isBlack) {
            Text(note, style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}
