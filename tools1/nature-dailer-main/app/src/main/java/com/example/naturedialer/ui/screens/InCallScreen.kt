package com.example.naturedialer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturedialer.ui.theme.DialerTheme
import com.example.naturedialer.ui.theme.getNatureColors

@Composable
fun InCallScreen(
    number: String,
    theme: DialerTheme,
    onEndCall: () -> Unit
) {
    val natureColors = getNatureColors(theme)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(natureColors.bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Calling...",
                color = natureColors.accent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = number,
                color = natureColors.text,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Mock Caller Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(natureColors.surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(64.dp), tint = natureColors.accent)
        }

        // Call Controls
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CallControlButton(icon = Icons.Default.Notifications, label = "Mute", theme = theme)
                CallControlButton(icon = Icons.Default.Menu, label = "Keypad", theme = theme)
                CallControlButton(icon = Icons.Default.PlayArrow, label = "Speaker", theme = theme)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CallControlButton(icon = Icons.Default.Clear, label = "Add", theme = theme)
                CallControlButton(icon = Icons.Default.Face, label = "Video", theme = theme)
                CallControlButton(icon = Icons.Default.Call, label = "Record", theme = theme)
            }
        }

        // End Call Button
        Button(
            onClick = onEndCall,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(Icons.Default.Clear, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun CallControlButton(
    icon: ImageVector,
    label: String,
    theme: DialerTheme
) {
    val natureColors = getNatureColors(theme)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(56.dp).clip(CircleShape),
            color = natureColors.surface,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = natureColors.text)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = natureColors.text.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}
