package com.naturetools.app.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.screens.audio.AdjustmentSlider
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun BmrCalculator() {
    var weight by remember { mutableStateOf("70") }
    var height by remember { mutableStateOf("175") }
    var age by remember { mutableStateOf("25") }
    var isMale by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("BMR Calculator (Mifflin-St Jeor)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isMale, onClick = { isMale = true })
            Text("Male")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = !isMale, onClick = { isMale = false })
            Text("Female")
        }
        val w = weight.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0
        val a = age.toIntOrNull() ?: 0
        val bmr = if (isMale) 10 * w + 6.25 * h - 5 * a + 5 else 10 * w + 6.25 * h - 5 * a - 161
        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("BMR: ${bmr.toInt()} kcal/day", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun CalorieNeedsCalc() {
    var bmrInput by remember { mutableStateOf("1600") }
    val bmr = bmrInput.toDoubleOrNull() ?: 0.0
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Calorie Needs Estimation", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = bmrInput, onValueChange = { bmrInput = it }, label = { Text("BMR (kcal)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        val levels = listOf(
            "Sedentary" to 1.2,
            "Lightly Active" to 1.375,
            "Moderately Active" to 1.55,
            "Very Active" to 1.725,
            "Extra Active" to 1.9
        )
        levels.forEach { (label, multiplier) ->
            ListItem(
                headlineContent = { Text(label) },
                trailingContent = { Text("${(bmr * multiplier).toInt()} kcal") },
                supportingContent = { Text("Multiplier: $multiplier") }
            )
        }
    }
}

@Composable
fun SleepCycleCalculator() {
    var wakeTimeStr by remember { mutableStateOf("07:00") }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Sleep Cycle (90-min cycles)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = wakeTimeStr,
            onValueChange = { wakeTimeStr = it },
            label = { Text("Wake up time (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        val wakeTime = try { LocalTime.parse(wakeTimeStr, formatter) } catch (e: Exception) { null }

        if (wakeTime != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("To wake up refreshed, go to bed at:")
            // 9h (6 cycles), 7.5h (5 cycles), 6h (4 cycles)
            val suggestedTimes = listOf(wakeTime.minusMinutes(540), wakeTime.minusMinutes(450), wakeTime.minusMinutes(360))
            suggestedTimes.forEach { time ->
                ListItem(
                    headlineContent = { Text(time.format(DateTimeFormatter.ofPattern("hh:mm a"))) },
                    supportingContent = {
                        val hours = java.time.Duration.between(time, wakeTime).let { if (it.isNegative) it.plusDays(1) else it }.toHours()
                        Text("$hours hours of sleep")
                    }
                )
            }
        }
    }
}

@Composable
fun PanchangamTool() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Traditional Panchangam", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        val data = listOf("Tithi: Shukla Paksha", "Nakshatra: Rohini", "Yoga: Vyatipata", "Karana: Vanija", "Rasi: Vrishabha")
        data.forEach { item ->
            ListItem(headlineContent = { Text(item) })
        }
    }
}

@Composable
fun MacroSplitterTool() {
    var calories by remember { mutableStateOf("2000") }
    val cals = calories.toDoubleOrNull() ?: 2000.0

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Macro Splitter (40/30/30)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Daily Calories") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        val macros = listOf(
            "Carbs (40%)" to (cals * 0.4 / 4),
            "Protein (30%)" to (cals * 0.3 / 4),
            "Fats (30%)" to (cals * 0.3 / 9)
        )

        macros.forEach { (label, grams) ->
            ListItem(headlineContent = { Text(label) }, trailingContent = { Text("${grams.toInt()}g") })
        }
    }
}

@Composable
fun HealthScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.HealthAndSafety,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (title) {
                "Sleep Tracker" -> {
                    Text("Sleep Quality: 85%", style = MaterialTheme.typography.titleMedium)
                    Text("Last night: 7h 45m", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Optimal Cycle: 11:00 PM to 7:00 AM")
                }
                "Macro Splitter" -> {
                    MacroSplitterTool()
                }
                "Yoga Guide" -> {
                    Text("Current Pose: Mountain Pose", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Focus on your breath and stand tall.", style = MaterialTheme.typography.bodyMedium)
                }
                "BMR Calculator" -> BmrCalculator()
                "Calorie needs", "Calorie Calc" -> CalorieNeedsCalc()
                "Sleep Cycle", "Sleep Cycle calculator" -> SleepCycleCalculator()
                "Panchangam" -> PanchangamTool()
                else -> {
                    Text("Health monitoring for $title")
                    AdjustmentSlider("Reminder Frequency", initialValue = 0.5f)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* Specialized action */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Session")
            }
        }
    }
}
