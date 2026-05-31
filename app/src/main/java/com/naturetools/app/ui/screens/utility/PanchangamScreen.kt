package com.naturetools.app.ui.screens.utility

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.utils.PanchangamDetails
import com.naturetools.app.utils.PanchangamLogic
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun PanchangamScreen(navController: NavHostController) {
    ToolScreen(
        title = "Panchangam",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PanchangamTool()
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
                // Basic time parsing: assume HH:MM AM/PM
                val timeParts = timeStr.split(" ")
                val hms = timeParts[0].split(":")
                var hour = hms[0].toInt()
                val minute = hms[1].toInt()
                if (timeParts.size > 1 && timeParts[1].equals("PM", ignoreCase = true) && hour < 12) hour += 12
                if (timeParts.size > 1 && timeParts[1].equals("AM", ignoreCase = true) && hour == 12) hour = 0

                PanchangamLogic.getPanchangam(LocalDateTime.of(date, LocalTime.of(hour, minute)))
            } catch (e: Exception) {
                PanchangamLogic.getPanchangam(LocalDateTime.now())
            }
        } else {
            PanchangamDetails(
                samvatsaram = "Krodhi (తెలుగు సంవత్సరం)",
                ayana = "Uttarayana",
                rutu = "Vasanta",
                maasam = "Chaitra (మాసం)",
                paksha = "Shukla Paksha",
                tithi = "Padyami (తిథి)",
                nakshatram = "$selectedNakshatra (నక్షత్రం)",
                raashi = "$selectedRaashi (రాశి)",
                luckyNumber = PanchangamLogic.getLuckyNumber(selectedRaashi),
                luckyColor = PanchangamLogic.getLuckyColor(selectedRaashi),
                luckyDay = PanchangamLogic.getLuckyDay(selectedRaashi)
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
