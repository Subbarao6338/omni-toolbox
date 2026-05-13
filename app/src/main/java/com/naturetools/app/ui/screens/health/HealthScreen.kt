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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.ui.screens.audio.AdjustmentSlider
import com.naturetools.app.utils.PanchangamLogic
import java.time.LocalDate
import java.time.LocalDateTime
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
    var selectedTab by remember { mutableIntStateOf(0) }
    var dateStr by remember { mutableStateOf(LocalDate.now().toString()) }
    var timeStr by remember { mutableStateOf("10:30 AM") }
    var location by remember { mutableStateOf("Hyderabad, India") }

    var selectedRaashi by remember { mutableStateOf("Mesha") }
    var selectedNakshatra by remember { mutableStateOf("Ashwini") }

    val panchangamData = remember(dateStr, timeStr, selectedTab, selectedRaashi, selectedNakshatra) {
        if (selectedTab == 0) {
            try {
                val date = LocalDate.parse(dateStr)
                PanchangamLogic.getPanchangam(LocalDateTime.of(date, LocalTime.of(10, 30)))
            } catch (e: Exception) {
                PanchangamLogic.getPanchangam(LocalDateTime.now())
            }
        } else {
            // Simplified fallback for "By Name"
            PanchangamLogic.getPanchangam(LocalDateTime.now()).copy(
                raashi = "$selectedRaashi (రాశి)",
                nakshatram = "$selectedNakshatra (నక్షత్రం)",
                luckyNumber = when(selectedRaashi) {
                    "Mesha" -> "9, 1, 8"
                    "Vrushabha" -> "6, 2, 7"
                    "Midhuna" -> "5, 3, 6"
                    "Karka" -> "2, 7, 9"
                    "Simha" -> "1, 4, 9"
                    "Kanya" -> "5, 3, 6"
                    "Thula" -> "6, 2, 7"
                    "Vrushchika" -> "9, 1, 8"
                    "Dhanussu" -> "3, 5, 8"
                    "Makara" -> "8, 6, 7"
                    "Kumbha" -> "8, 4, 5"
                    "Meena" -> "3, 7, 9"
                    else -> "1, 5, 9"
                }
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("By DateTime") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("By Name") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            OutlinedTextField(value = dateStr, onValueChange = { dateStr = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = timeStr, onValueChange = { timeStr = it }, label = { Text("Time (HH:MM AM/PM)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
            PanchangamResult(
                samvatsaram = panchangamData.samvatsaram,
                ayana = panchangamData.ayana,
                rutu = panchangamData.rutu,
                maasam = panchangamData.maasam,
                paksha = panchangamData.paksha,
                tithi = panchangamData.tithi,
                nakshatram = panchangamData.nakshatram,
                raashi = panchangamData.raashi,
                luckyNumber = panchangamData.luckyNumber,
                luckyColor = panchangamData.luckyColor,
                luckyDay = panchangamData.luckyDay
            )
        } else {
            val raashis = listOf("Mesha", "Vrushabha", "Midhuna", "Karka", "Simha", "Kanya", "Thula", "Vrushchika", "Dhanussu", "Makara", "Kumbha", "Meena")
            val nakshatras = listOf("Ashwini", "Bharani", "Kruthika", "Rohini", "Mrigashira", "Arudra", "Punarvasu", "Pushyami", "Aslesha", "Makha", "Pubba", "Uttara", "Hastha", "Chitra", "Swathi", "Vishakha", "Anuradha", "Jyeshta", "Moola", "Poorvashada", "Uttarashada", "Shravanam", "Dhanishta", "Shathabhisham", "Poorvabhadra", "Uttarabhadra", "Revathi")

            var raashiExpanded by remember { mutableStateOf(false) }
            var nakshatraExpanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(onClick = { raashiExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Selected Raashi: $selectedRaashi")
                }
                DropdownMenu(expanded = raashiExpanded, onDismissRequest = { raashiExpanded = false }) {
                    raashis.forEach { r ->
                        DropdownMenuItem(text = { Text(r) }, onClick = { selectedRaashi = r; raashiExpanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                OutlinedButton(onClick = { nakshatraExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Selected Nakshatram: $selectedNakshatra")
                }
                DropdownMenu(expanded = nakshatraExpanded, onDismissRequest = { nakshatraExpanded = false }) {
                    nakshatras.forEach { n ->
                        DropdownMenuItem(text = { Text(n) }, onClick = { selectedNakshatra = n; nakshatraExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PanchangamResult(
                samvatsaram = "Krodhi (క్రోధి)",
                ayana = "Based on selection",
                rutu = "Based on selection",
                maasam = "Based on selection",
                paksha = "Shukla/Krishna",
                tithi = "Variable",
                nakshatram = "$selectedNakshatra (నక్షత్రం)",
                raashi = "$selectedRaashi (రాశి)",
                luckyNumber = when(selectedRaashi) {
                    "Mesha" -> "9, 1, 8"; "Vrushabha" -> "6, 2, 7"; "Midhuna" -> "5, 3, 6"; "Karka" -> "2, 7, 9"
                    "Simha" -> "1, 4, 9"; "Kanya" -> "5, 3, 6"; "Thula" -> "6, 2, 7"; "Vrushchika" -> "9, 1, 8"
                    "Dhanussu" -> "3, 5, 8"; "Makara" -> "8, 6, 7"; "Kumbha" -> "8, 4, 5"; "Meena" -> "3, 7, 9"
                    else -> "1, 5, 9"
                },
                luckyColor = "Themed to $selectedRaashi",
                luckyDay = "Auspicious for $selectedRaashi"
            )
        }
    }
}

@Composable
fun PanchangamResult(
    samvatsaram: String,
    ayana: String,
    rutu: String,
    maasam: String,
    paksha: String,
    tithi: String,
    nakshatram: String,
    raashi: String,
    luckyNumber: String,
    luckyColor: String,
    luckyDay: String
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Detailed Panchangam Info", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            PanchangamRow("Samvatsaram", samvatsaram)
            PanchangamRow("Ayana", ayana)
            PanchangamRow("Rutu", rutu)
            PanchangamRow("Maasam", maasam)
            PanchangamRow("Paksham", paksha)
            PanchangamRow("Tidhi", tithi)
            PanchangamRow("Nakshatram", nakshatram)
            PanchangamRow("Raashi", raashi)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Lucky Factors", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            PanchangamRow("Lucky Numbers", luckyNumber)
            PanchangamRow("Lucky Colors", luckyColor)
            PanchangamRow("Lucky Days", luckyDay)
        }
    }
}

@Composable
fun PanchangamRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
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
