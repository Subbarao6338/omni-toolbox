package omni.toolbox.ui.screens.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.ui.components.AdjustmentSlider
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
fun MedicationReminderSystem() {
    var medicineName by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00") }
    val reminders = remember { mutableStateListOf<Pair<String, String>>() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Medicine Reminder", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            label = { Text("Medicine Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (medicineName.isNotBlank()) {
                    reminders.add(medicineName to time)
                    medicineName = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Reminder")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Scheduled Reminders", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (reminders.isEmpty()) {
            Text("No reminders set", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        } else {
            reminders.forEach { reminder ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(reminder.first, style = MaterialTheme.typography.bodyLarge)
                            Text(reminder.second, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { reminders.remove(reminder) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BmrCalculator() {
    var age by remember { mutableStateOf("30") }
    var weight by remember { mutableStateOf("70") }
    var height by remember { mutableStateOf("175") }
    var isMale by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("BMR Calculator (Mifflin-St Jeor)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isMale, onClick = { isMale = true })
            Text("Male")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = !isMale, onClick = { isMale = false })
            Text("Female")
        }

        val a = age.toDoubleOrNull() ?: 0.0
        val w = weight.toDoubleOrNull() ?: 0.0
        val h = height.toDoubleOrNull() ?: 0.0

        if (a > 0 && w > 0 && h > 0) {
            val bmr = (10 * w) + (6.25 * h) - (5 * a) + (if (isMale) 5 else -161)
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("BMR: ${bmr.toInt()} kcal/day", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
            }
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
                "Yoga Guide" -> {
                    Text("Current Pose: Mountain Pose", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Focus on your breath and stand tall.", style = MaterialTheme.typography.bodyMedium)
                }
                "Period Tracker" -> {
                    Text("Next period in 12 days", style = MaterialTheme.typography.titleMedium)
                    Text("Cycle: 28 days (Average)", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Last period: Oct 1 - Oct 5")
                }
                "Medication Tracker", "Medication reminder" -> {
                    MedicationReminderSystem()
                }
                "Sleep Cycle", "Sleep Cycle calculator", "sleep_tracker" -> SleepCycleCalculator()
                "BMR Calculator", "bmr" -> BmrCalculator()
                "BMI Calc", "bmi" -> {
                    var weight by remember { mutableStateOf("70") }
                    var height by remember { mutableStateOf("175") }
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
                    val w = weight.toDoubleOrNull() ?: 0.0
                    val h = height.toDoubleOrNull() ?: 0.0
                    if (w > 0 && h > 0) {
                        val bmi = w / (h/100 * h/100)
                        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text("BMI: ${"%.1f".format(bmi)}", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
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
