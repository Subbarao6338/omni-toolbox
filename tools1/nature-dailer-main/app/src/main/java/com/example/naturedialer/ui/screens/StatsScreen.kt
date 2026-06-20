package com.example.naturedialer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturedialer.ui.theme.DialerTheme
import com.example.naturedialer.ui.theme.getNatureColors

@Composable
fun StatsScreen(theme: DialerTheme) {
    val natureColors = getNatureColors(theme)

    Column(modifier = Modifier.fillMaxSize().background(natureColors.bg).padding(16.dp)) {
        Text(
            text = "Call Stats",
            color = natureColors.text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        StatCard("Total Calls", "128", theme)
        StatCard("Missed Calls", "12", theme)
        StatCard("Incoming", "74", theme)
        StatCard("Outgoing", "42", theme)
    }
}

@Composable
fun StatCard(label: String, value: String, theme: DialerTheme) {
    val natureColors = getNatureColors(theme)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(natureColors.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = natureColors.text.copy(alpha = 0.8f), fontSize = 16.sp)
        Text(text = value, color = natureColors.accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
