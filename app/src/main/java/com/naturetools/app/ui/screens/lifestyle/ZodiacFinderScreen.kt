package com.naturetools.app.ui.screens.lifestyle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZodiacFinderScreen(navController: NavHostController) {
    var birthDate by remember { mutableStateOf(LocalDate.now()) }
    val showDatePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        birthDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                    }
                    showDatePicker.value = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ToolScreen(title = "Zodiac Finder", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Button(onClick = { showDatePicker.value = true }) { Text(birthDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))) }
            Card(modifier = Modifier.fillMaxWidth().padding(top = 32.dp)) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your Zodiac Sign", style = MaterialTheme.typography.labelLarge)
                    Text(getZodiacSign(birthDate.dayOfMonth, birthDate.monthValue), style = MaterialTheme.typography.displayMedium)
                }
            }
        }
    }
}

fun getZodiacSign(day: Int, month: Int): String {
    return when (month) {
        1 -> if (day < 20) "Capricorn" else "Aquarius"
        2 -> if (day < 19) "Aquarius" else "Pisces"
        3 -> if (day < 21) "Pisces" else "Aries"
        4 -> if (day < 20) "Aries" else "Taurus"
        5 -> if (day < 21) "Taurus" else "Gemini"
        6 -> if (day < 21) "Gemini" else "Cancer"
        7 -> if (day < 23) "Cancer" else "Leo"
        8 -> if (day < 23) "Leo" else "Virgo"
        9 -> if (day < 23) "Virgo" else "Libra"
        10 -> if (day < 23) "Libra" else "Scorpio"
        11 -> if (day < 22) "Scorpio" else "Sagittarius"
        12 -> if (day < 22) "Sagittarius" else "Capricorn"
        else -> "Unknown"
    }
}
