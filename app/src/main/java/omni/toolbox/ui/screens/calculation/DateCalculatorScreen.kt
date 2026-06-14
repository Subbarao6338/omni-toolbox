package omni.toolbox.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculatorScreen(navController: NavHostController) {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val period = remember(startDate, endDate) {
        Period.between(startDate, endDate)
    }

    ToolScreen(title = "Date Tools", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Date Difference", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            DateRow(label = "Start Date", date = startDate) { showStartPicker = true }
            Spacer(modifier = Modifier.height(8.dp))
            DateRow(label = "End Date", date = endDate) { showEndPicker = true }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Difference", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${Math.abs(period.years)} Years, ${Math.abs(period.months)} Months, ${Math.abs(period.days)} Days",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            // Additional tools
            AgeCalculator()

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            TimestampConverter()
        }
    }

    if (showStartPicker) {
        DatePickerModal(initialDate = startDate, onDateSelected = { startDate = it; showStartPicker = false }, onDismiss = { showStartPicker = false })
    }
    if (showEndPicker) {
        DatePickerModal(initialDate = endDate, onDateSelected = { endDate = it; showEndPicker = false }, onDismiss = { showEndPicker = false })
    }
}

@Composable
fun AgeCalculator() {
    var birthYear by remember { mutableStateOf("1995") }
    var ageResult by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Age Calculator", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = birthYear, onValueChange = { birthYear = it }, label = { Text("Birth Year") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val year = birthYear.toIntOrNull() ?: 1995
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            ageResult = "You are ${currentYear - year} years old."
        }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text("Calculate Age")
        }
        if (ageResult.isNotEmpty()) {
            Text(ageResult, style = MaterialTheme.typography.bodyLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
    }
}

@Composable
fun TimestampConverter() {
    val currentTimestamp = System.currentTimeMillis()
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Timestamp Converter", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = currentTimestamp.toString(), onValueChange = {}, label = { Text("Unix Timestamp (ms)") }, modifier = Modifier.fillMaxWidth(), readOnly = true)
        Text("Human Readable: ${java.util.Date(currentTimestamp)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun DateRow(label: String, date: LocalDate, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Button(onClick = onClick) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(date.toString())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(initialDate: LocalDate, onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    val date = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(date)
                }
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) { DatePicker(state = datePickerState) }
}
