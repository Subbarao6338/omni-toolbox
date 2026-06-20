package com.nature.docs.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioToolsView(onBack: () -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var amplitude by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                amplitude = (0..100).random().toFloat() / 100f // Simulating amplitude
                delay(100)
            }
        } else {
            amplitude = 0f
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Audio Tools") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = ParchmentBg))
        }
    ) { padding ->
        LinenCanvas(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                AgedPaperCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Text("Live Waveform", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(32.dp))
                    VineProgressBar(progress = amplitude, modifier = Modifier.fillMaxWidth().height(40.dp))
                }

                Spacer(Modifier.height(48.dp))

                FloatingActionButton(
                    onClick = { isRecording = !isRecording },
                    containerColor = if (isRecording) Terracotta else BotanicalGreen,
                    contentColor = Color.White
                ) {
                    Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = "Record")
                }
            }
        }
    }
}
