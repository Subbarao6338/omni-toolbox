package com.example.naturedialer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturedialer.ui.theme.getNatureColors
import com.example.naturedialer.ui.theme.DialerTheme

@Composable
fun DialerScreen(
    theme: DialerTheme,
    onCall: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    val natureColors = getNatureColors(theme)
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(natureColors.bg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = phoneNumber,
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = natureColors.text,
                maxLines = 1
            )
            if (phoneNumber.isNotEmpty()) {
                IconButton(onClick = { phoneNumber = phoneNumber.dropLast(1) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = natureColors.text.copy(alpha = 0.6f))
                }
            }
        }

        // Keypad
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().height(400.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(keys) { key ->
                DialerButton(
                    text = key,
                    theme = theme,
                    onClick = { phoneNumber += key }
                )
            }
        }

        // Call Button
        Button(
            onClick = { if (phoneNumber.isNotEmpty()) onCall(phoneNumber) },
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(containerColor = natureColors.accent),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = natureColors.bg, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DialerButton(
    text: String,
    theme: DialerTheme,
    onClick: () -> Unit
) {
    val natureColors = getNatureColors(theme)
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable { onClick() },
        color = natureColors.dial,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 28.sp,
                color = natureColors.text,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
