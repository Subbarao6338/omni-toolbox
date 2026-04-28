package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.random.Random

@Composable
fun DailyQuotesScreen(navController: NavHostController) {
    val quotes = listOf(
        "Look deep into nature, and then you will understand everything better." to "Albert Einstein",
        "The best thing one can do when it's raining is to let it rain." to "Henry Wadsworth Longfellow",
        "Nature does not hurry, yet everything is accomplished." to "Lao Tzu",
        "In every walk with nature one receives far more than he seeks." to "John Muir",
        "The Earth has music for those who listen." to "George Santayana",
        "Adopt the pace of nature: her secret is patience." to "Ralph Waldo Emerson"
    )
    val quote = remember { quotes[Random.nextInt(quotes.size)] }

    ToolScreen(
        title = "Daily Quote",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\"${quote.first}\"",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "— ${quote.second}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
