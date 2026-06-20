package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AutomationRule
import com.example.ui.OmniViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Automation/Macros, 1: Calc Suite, 2: Unit Converter, 3: Widget

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Utilities & Automation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = activeTab) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("Macros", modifier = Modifier.padding(10.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Calculators", modifier = Modifier.padding(10.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("Converter", modifier = Modifier.padding(10.dp), fontSize = 11.sp)
                }
                Tab(selected = activeTab == 3, onClick = { activeTab = 3 }) {
                    Text("Widgets", modifier = Modifier.padding(10.dp), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                0 -> MacrosTab(viewModel)
                1 -> CalculatorsTab()
                2 -> UnitConverterTab()
                3 -> ProductivityWidgetsTab()
            }
        }
    }
}

@Composable
fun MacrosTab(viewModel: OmniViewModel) {
    val rules by viewModel.automationRules.collectAsState()
    var inputName by remember { mutableStateOf("") }
    var inputTrigger by remember { mutableStateOf("SHAKE") }
    var inputAction by remember { mutableStateOf("CLEAN_CACHE") }
    var simulationMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("AUTOMATION RULE CREATOR (ROOT-FREE)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Add New Custom Macro Rule", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    TextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Rule Description Name", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Trigger Source", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            val triggers = listOf("SHAKE", "TIMER", "BATTERY_LOW")
                            triggers.forEach { t ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { inputTrigger = t }.padding(vertical = 4.dp)) {
                                    RadioButton(selected = inputTrigger == t, onClick = { inputTrigger = t })
                                    Text(t, fontSize = 11.sp)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Macro Action", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            val actions = listOf("CLEAN_CACHE", "PASSWORD_GEN", "LOG_STATS", "SYNC_TASKS")
                            actions.forEach { a ->
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { inputAction = a }.padding(vertical = 4.dp)) {
                                    RadioButton(selected = inputAction == a, onClick = { inputAction = a })
                                    Text(a, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (inputName.isNotBlank()) {
                                viewModel.addAutomationRule(inputName, inputTrigger, inputAction)
                                inputName = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Add Macro Trigger", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            SectionHeader(title = "SIMULATE EVENTS (EMULATE ROOTLESS HANDLERS)")
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        simulationMessage = "SHAKE GESTURE DETECTED (M3 Agent simulation triggered!). Generated new key cipher."
                        viewModel.startDuplicateCleanerScan() // triggers background logging
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.RotateRight, contentDescription = "Shake")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trigger Shake", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        simulationMessage = "TIMER THRESHOLD REACHED: Running scheduled system task cache cleans..."
                        viewModel.startDuplicateCleanerScan()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(imageVector = Icons.Default.HourglassEmpty, contentDescription = "Timer")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Timer Tick", fontSize = 11.sp)
                }
            }
        }

        if (simulationMessage != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = "Macro run", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Macro Output logs:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(simulationMessage!!, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "SYSTEM AUTOMATION REGISTRY")
        }

        items(rules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (rule.triggerType) {
                            "SHAKE" -> Icons.Default.Vibration
                            "TIMER" -> Icons.Default.Schedule
                            else -> Icons.Default.Battery4Bar
                        },
                        contentDescription = "Trigger type icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rule.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Trigger: ${rule.triggerType} ➔ Action: ${rule.actionType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    IconButton(onClick = { viewModel.deleteRule(rule.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorsTab() {
    var isProgrammerMode by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TabRow(selectedTabIndex = if (isProgrammerMode) 1 else 0) {
            Tab(selected = !isProgrammerMode, onClick = { isProgrammerMode = false }) {
                Text("Standard Calc", modifier = Modifier.padding(8.dp), fontSize = 12.sp)
            }
            Tab(selected = isProgrammerMode, onClick = { isProgrammerMode = true }) {
                Text("Programmer Calc", modifier = Modifier.padding(8.dp), fontSize = 12.sp)
            }
        }

        if (!isProgrammerMode) {
            StandardCalculatorView()
        } else {
            ProgrammerCalculatorView()
        }
    }
}

@Composable
fun StandardCalculatorView() {
    var display by remember { mutableStateOf("0") }
    var operator by remember { mutableStateOf("") }
    var operand1 by remember { mutableStateOf(0.0) }
    var resetOnNext by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Screen Display
            Text(
                text = display,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Keys grid
            val keys = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("C", "0", "=", "+")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in keys) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (key in row) {
                            Button(
                                onClick = {
                                    when (key) {
                                        "C" -> {
                                            display = "0"
                                            operator = ""
                                            operand1 = 0.0
                                        }
                                        "+", "-", "*", "/" -> {
                                            operand1 = display.toDoubleOrNull() ?: 0.0
                                            operator = key
                                            resetOnNext = true
                                        }
                                        "=" -> {
                                            val operand2 = display.toDoubleOrNull() ?: 0.0
                                            val result = when (operator) {
                                                "+" -> operand1 + operand2
                                                "-" -> operand1 - operand2
                                                "*" -> operand1 * operand2
                                                "/" -> if (operand2 != 0.0) operand1 / operand2 else 0.0
                                                else -> operand2
                                            }
                                            display = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
                                            operator = ""
                                        }
                                        else -> {
                                            if (display == "0" || resetOnNext) {
                                                display = key
                                                resetOnNext = false
                                            } else {
                                                display += key
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (key in listOf("+", "-", "*", "/", "=")) MaterialTheme.colorScheme.primary
                                    else if (key == "C") MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                )
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (key in listOf("+", "-", "*", "/", "=")) Color.White
                                    else if (key == "C") Color.White
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProgrammerCalculatorView() {
    var decInput by remember { mutableStateOf("128") }
    val decimalVal = decInput.toIntOrNull() ?: 0

    val hexString = decimalVal.toString(16).uppercase()
    val binString = decimalVal.toString(2)
    val octString = decimalVal.toString(8)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("NUMERIC RADIX CONVERTER", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

            TextField(
                value = decInput,
                onValueChange = { decInput = it.filter { c -> c.isDigit() } },
                label = { Text("Decimal Integer Value", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(10.dp))

            RadixOutputRow(label = "HEXADECIMAL (HEX)", value = hexString)
            RadixOutputRow(label = "DECIMAL (DEC)", value = decimalVal.toString())
            RadixOutputRow(label = "OCTAL (OCT)", value = octString)
            RadixOutputRow(label = "BINARY (BIN)", value = binString)
        }
    }
}

@Composable
fun RadixOutputRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(
            text = value,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(8.dp)
        )
    }
}

@Composable
fun UnitConverterTab() {
    var sourceVal by remember { mutableStateOf("10.0") }
    var convertType by remember { mutableStateOf(0) } // 0: KM to Miles, 1: Celsius to F, 2: GB to MB

    val number = sourceVal.toDoubleOrNull() ?: 0.0
    val result = when (convertType) {
        0 -> number * 0.621371 // km to miles
        1 -> (number * 9 / 5) + 32 // c to f
        else -> number * 1024 // gb to mb
    }

    val resultString = String.format("%.2f", result)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("ENGINEERING UNIT CONVERTER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val list = listOf("KM ➔ Miles", "°C ➔ °F", "GB ➔ MB")
                list.forEachIndexed { index, item ->
                    FilterChip(
                        selected = convertType == index,
                        onClick = { convertType = index },
                        label = { Text(item, fontSize = 11.sp) }
                    )
                }
            }

            TextField(
                value = sourceVal,
                onValueChange = { sourceVal = it },
                label = { Text("Input Value to convert", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("CONVERSION OUTCOME", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)

            Text(
                text = "$sourceVal ${when(convertType){0->"KM";1->"°C";else->"GB"}} is equal to: $resultString ${when(convertType){0->"Miles";1->"°F";else->"MB"}}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            )
        }
    }
}

@Composable
fun ProductivityWidgetsTab() {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val sFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val dFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
            val cal = Calendar.getInstance()
            currentTime = sFormat.format(cal.time)
            currentDate = dFormat.format(cal.time)
            kotlinx.coroutines.delay(1000)
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("DAILY PRODUCTIVITY & TIME WIDGETS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
        }

        item {
            // Elegant Clock Dashboard Widget
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AGENT DYNAMIC CLOCK WIDGET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.8.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentDate,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        item {
            // Task counter widget
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("STUDIO FOCUS STATS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ACTIVE FOCUS SPRINT", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text("25 Minutes (Pomodoro)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("COMPLETED CYCLES", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                            Text("4 Sessions Today", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
