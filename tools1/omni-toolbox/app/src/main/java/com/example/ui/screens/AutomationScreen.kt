package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import kotlinx.coroutines.delay
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
    var subTab by remember { mutableStateOf(0) } // 0: Standard, 1: Programmer, 2: Scientific, 3: Mortgage, 4: Age

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ScrollableTabRow(
            selectedTabIndex = subTab,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            val tabs = listOf("Standard", "Programmer", "Scientific", "Mortgage", "Age")
            tabs.forEachIndexed { index, title ->
                Tab(selected = subTab == index, onClick = { subTab = index }) {
                    Text(title, modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (subTab) {
            0 -> StandardCalculatorView()
            1 -> ProgrammerCalculatorView()
            2 -> ScientificCalculatorView()
            3 -> MortgageCalculatorView()
            4 -> AgeCalculatorView()
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
            Text(
                text = "STANDARD CALCULATOR",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
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
    var bitwiseMessage by remember { mutableStateOf("") }
    val decimalVal = decInput.toIntOrNull() ?: 0

    val hexString = decimalVal.toString(16).uppercase()
    val binString = decimalVal.toString(2)
    val octString = decimalVal.toString(8)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("PROGRAMMER RADIX & BITWISE TOOL", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

            TextField(
                value = decInput,
                onValueChange = { decInput = it.filter { c -> c.isDigit() } },
                label = { Text("Decimal Value (DEC)", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(4.dp))

            RadixOutputRow(label = "HEXADECIMAL (HEX)", value = hexString)
            RadixOutputRow(label = "DECIMAL (DEC)", value = decimalVal.toString())
            RadixOutputRow(label = "OCTAL (OCT)", value = octString)
            RadixOutputRow(label = "BINARY (BIN)", value = binString)

            Spacer(modifier = Modifier.height(6.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Text("Bitwise Macro Simulations:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        val res = decimalVal and 0xFF
                        bitwiseMessage = "DEC ($decimalVal) AND 0xFF ➔ $res (BIN: ${res.toString(2)})"
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("AND 0xFF", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        val res = decimalVal or 0x0F
                        bitwiseMessage = "DEC ($decimalVal) OR 0x0F ➔ $res (BIN: ${res.toString(2)})"
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("OR 0x0F", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        val res = decimalVal shl 1
                        bitwiseMessage = "Shift Left 1 Bit (x2) ➔ $res (BIN: ${res.toString(2)})"
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("SHL 1", fontSize = 10.sp)
                }

                Button(
                    onClick = {
                        val res = decimalVal shr 1
                        bitwiseMessage = "Shift Right 1 Bit (/2) ➔ $res (BIN: ${res.toString(2)})"
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("SHR 1", fontSize = 10.sp)
                }
            }

            if (bitwiseMessage.isNotEmpty()) {
                Text(
                    text = bitwiseMessage,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun RadixOutputRow(label: String, value: String) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
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
fun ScientificCalculatorView() {
    var display by remember { mutableStateOf("0") }
    var resultText by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("SCIENTIFIC MATHEMATICAL ENGINE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

            OutlinedTextField(
                value = display,
                onValueChange = { display = it },
                label = { Text("Input Value (X)", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Scientific Functions:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            val r = kotlin.math.sin(Math.toRadians(v))
                            resultText = "sin($v°) = ${String.format("%.6f", r)}"
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("sin", fontSize = 11.sp) }

                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            val r = kotlin.math.cos(Math.toRadians(v))
                            resultText = "cos($v°) = ${String.format("%.6f", r)}"
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("cos", fontSize = 11.sp) }

                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            val r = kotlin.math.tan(Math.toRadians(v))
                            resultText = "tan($v°) = ${String.format("%.6f", r)}"
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("tan", fontSize = 11.sp) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            if (v > 0) {
                                val r = kotlin.math.log10(v)
                                resultText = "log10($v) = ${String.format("%.6f", r)}"
                            } else {
                                resultText = "Error: Input must be > 0"
                            }
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("log10", fontSize = 11.sp) }

                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            if (v > 0) {
                                val r = kotlin.math.ln(v)
                                resultText = "ln($v) = ${String.format("%.6f", r)}"
                            } else {
                                resultText = "Error: Input must be > 0"
                            }
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("ln", fontSize = 11.sp) }

                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            if (v >= 0) {
                                val r = kotlin.math.sqrt(v)
                                resultText = "√$v = ${String.format("%.6f", r)}"
                            } else {
                                resultText = "Error: Imaginary number!"
                            }
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("√x", fontSize = 11.sp) }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val v = display.toDoubleOrNull() ?: 0.0
                            val r = v * v
                            resultText = "$v² = $r"
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("x²", fontSize = 11.sp) }

                    Button(
                        onClick = {
                            display = Math.PI.toString()
                            resultText = "π loaded into input."
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("π", fontSize = 11.sp) }

                    Button(
                        onClick = {
                            display = Math.E.toString()
                            resultText = "Euler e loaded into input."
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) { Text("e", fontSize = 11.sp) }
                }
            }

            if (resultText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Text("ENGINE COMPUTATION OUTCOME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = resultText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MortgageCalculatorView() {
    var principalInput by remember { mutableStateOf("250000") }
    var interestInput by remember { mutableStateOf("4.5") }
    var yearsInput by remember { mutableStateOf("30") }

    var monthlyPaymentText by remember { mutableStateOf("") }
    var totalInterestText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("MORTGAGE / LOAN INTEREST CALC", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = principalInput,
                onValueChange = { principalInput = it.filter { c -> c.isDigit() } },
                label = { Text("Loan Principal ($)", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = interestInput,
                onValueChange = { interestInput = it },
                label = { Text("Annual Interest Rate (%)", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = yearsInput,
                onValueChange = { yearsInput = it.filter { c -> c.isDigit() } },
                label = { Text("Loan Term (Years)", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = {
                    val p = principalInput.toDoubleOrNull() ?: 0.0
                    val rate = (interestInput.toDoubleOrNull() ?: 0.0) / 100.0 / 12.0
                    val yrs = yearsInput.toIntOrNull() ?: 1
                    val n = yrs * 12

                    if (rate == 0.0) {
                        val simplePayment = if (n > 0) p / n else 0.0
                        monthlyPaymentText = "$${String.format("%.2f", simplePayment)} / Mo"
                        totalInterestText = "$0.00"
                    } else {
                        val termF = Math.pow(1.0 + rate, n.toDouble())
                        val monthly = (p * rate * termF) / (termF - 1)
                        val totalPayout = monthly * n
                        val totalInterest = totalPayout - p

                        monthlyPaymentText = "$${String.format("%.2f", monthly)} / Month"
                        totalInterestText = "$${String.format("%.2f", totalInterest)}"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Calculate Mortgage Payment")
            }

            if (monthlyPaymentText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("MONTHLY DUES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(monthlyPaymentText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOTAL INTEREST PAID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                            Text(totalInterestText, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AgeCalculatorView() {
    var dayInput by remember { mutableStateOf("15") }
    var monthInput by remember { mutableStateOf("10") }
    var yearInput by remember { mutableStateOf("1995") }

    var calculatedAgeText by remember { mutableStateOf("") }
    var calculatedCountdownText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("AGE & MILESTONE REALTIME TIMER", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dayInput,
                    onValueChange = { dayInput = it.take(2).filter { c -> c.isDigit() } },
                    label = { Text("Day", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = monthInput,
                    onValueChange = { monthInput = it.take(2).filter { c -> c.isDigit() } },
                    label = { Text("Month (1-12)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = yearInput,
                    onValueChange = { yearInput = it.take(4).filter { c -> c.isDigit() } },
                    label = { Text("Year (YYYY)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Button(
                onClick = {
                    val d = dayInput.toIntOrNull() ?: 1
                    val m = monthInput.toIntOrNull() ?: 1
                    val y = yearInput.toIntOrNull() ?: 1990

                    val current = Calendar.getInstance()
                    val birth = Calendar.getInstance().apply {
                        set(y, m - 1, d)
                    }

                    if (birth.after(current)) {
                        calculatedAgeText = "Birthdate cannot be in the future."
                        calculatedCountdownText = ""
                        return@Button
                    }

                    var years = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
                    var months = current.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
                    var days = current.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)

                    if (days < 0) {
                        months--
                        val prevMonth = (current.get(Calendar.MONTH) - 1 + 12) % 12
                        val tempCal = Calendar.getInstance().apply { set(Calendar.MONTH, prevMonth) }
                        days += tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    }
                    if (months < 0) {
                        years--
                        months += 12
                    }

                    calculatedAgeText = "$years Years, $months Months, and $days Days old"

                    // Next Birthday Countdown calculation
                    val nextBday = Calendar.getInstance().apply {
                        set(Calendar.MONTH, birth.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, birth.get(Calendar.DAY_OF_MONTH))
                        if (before(current) || equals(current)) {
                            add(Calendar.YEAR, 1)
                        }
                    }

                    val diffTime = nextBday.timeInMillis - current.timeInMillis
                    val diffDays = (diffTime / (1000 * 60 * 60 * 24)) + 1
                    calculatedCountdownText = "Only $diffDays days remaining until next birthday milestone!"
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Process Milestone Computation")
            }

            if (calculatedAgeText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Calculated Age:", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                    Text(calculatedAgeText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    if (calculatedCountdownText.isNotEmpty()) {
                        Text(calculatedCountdownText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun UnitConverterTab() {
    var sourceVal by remember { mutableStateOf("10.0") }
    var activeCategory by remember { mutableStateOf(0) } // 0: Length, 1: Temp, 2: Cyber Storage, 3: Weight
    var activeSubConversion by remember { mutableStateOf(0) }

    val number = sourceVal.toDoubleOrNull() ?: 1.0

    // Categories structure
    val structure = listOf(
        // Length
        Pair("Length", listOf("KM -> Miles", "Metres -> Feet", "Inches -> CM", "Yards -> Metres")),
        // Temp
        Pair("Temp", listOf("°C -> °F", "°F -> °C", "°C -> Kelvin", "Kelvin -> °C")),
        // Data
        Pair("Data Storage", listOf("GB -> MB", "TB -> GB", "MB -> KB", "Bytes -> KB")),
        // Weight
        Pair("Mass / Weight", listOf("Kg -> Lbs", "Lbs -> Kg", "Grams -> Ounces", "Stones -> Kg"))
    )

    // Compute Conversion Mathematical Outcome
    val categoryName = structure[activeCategory].first
    val modeName = structure[activeCategory].second[activeSubConversion]
    
    val resultAndUnits = when (categoryName) {
        "Length" -> {
            when (activeSubConversion) {
                0 -> Pair(number * 0.621371, "Miles")
                1 -> Pair(number * 3.28084, "Feet")
                2 -> Pair(number * 2.54, "CM")
                else -> Pair(number * 0.9144, "Metres")
            }
        }
        "Temp" -> {
            when (activeSubConversion) {
                0 -> Pair((number * 9 / 5) + 32, "°F")
                1 -> Pair((number - 32) * 5 / 9, "°C")
                2 -> Pair(number + 273.15, "Kelvin")
                else -> Pair(number - 273.15, "°C")
            }
        }
        "Data Storage" -> {
            when (activeSubConversion) {
                0 -> Pair(number * 1024, "MB")
                1 -> Pair(number * 1024, "GB")
                2 -> Pair(number * 1024, "KB")
                else -> Pair(number / 1024, "KB")
            }
        }
        else -> { // Mass Weight
            when (activeSubConversion) {
                0 -> Pair(number * 2.20462, "Lbs")
                1 -> Pair(number * 0.453592, "Kg")
                2 -> Pair(number * 0.035274, "Ounces")
                else -> Pair(number * 6.35029, "Kg")
            }
        }
    }

    val resultString = String.format("%.4f", resultAndUnits.first)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Scrollable category filterrow
        ScrollableTabRow(
            selectedTabIndex = activeCategory,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            structure.forEachIndexed { idx, pair ->
                Tab(selected = activeCategory == idx, onClick = { activeCategory = idx; activeSubConversion = 0 }) {
                    Text(pair.first, modifier = Modifier.padding(10.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("ENGINE PROCESS CONVERSION", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

                // Sub options chips row
                Text("Conversion Mode:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    structure[activeCategory].second.forEachIndexed { iSub, label ->
                        FilterChip(
                            selected = activeSubConversion == iSub,
                            onClick = { activeSubConversion = iSub },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                TextField(
                    value = sourceVal,
                    onValueChange = { sourceVal = it },
                    label = { Text("Input Value to Convert", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("OUTCOME CONVERSION SCALING", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)

                Text(
                    text = "$sourceVal ➔ $resultString ${resultAndUnits.second}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                )
            }
        }
    }
}

@Composable
fun ProductivityWidgetsTab() {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    // Stopwatch states
    var swRunning by remember { mutableStateOf(false) }
    var swTimeInMs by remember { mutableStateOf(0L) }
    var swLaps by remember { mutableStateOf(listOf<String>()) }

    // Timer countdown states
    var tSetMinutes by remember { mutableStateOf("1") }
    var tSetSeconds by remember { mutableStateOf("0") }
    var tRunning by remember { mutableStateOf(false) }
    var tTimeLeftMs by remember { mutableStateOf(0L) }
    var tInitialTimeMs by remember { mutableStateOf(0L) }

    // Live clock clock Tick
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

    // Stopwatch Thread Logic
    LaunchedEffect(swRunning) {
        if (swRunning) {
            val startTime = System.currentTimeMillis() - swTimeInMs
            while (swRunning) {
                swTimeInMs = System.currentTimeMillis() - startTime
                kotlinx.coroutines.delay(16)
            }
        }
    }

    // Timer Countdown thread logic
    LaunchedEffect(tRunning) {
        if (tRunning) {
            var lastTick = System.currentTimeMillis()
            while (tRunning && tTimeLeftMs > 0) {
                val now = System.currentTimeMillis()
                tTimeLeftMs = (tTimeLeftMs - (now - lastTick)).coerceAtLeast(0L)
                lastTick = now
                if (tTimeLeftMs <= 0L) {
                    tRunning = false
                }
                delay(50)
            }
        }
    }

    // Format Stopwatch Time Helper
    fun formatSw(ms: Long): String {
        val totalSecs = ms / 1000
        val minutes = totalSecs / 60
        val seconds = totalSecs % 60
        val millis = (ms % 1000) / 10
        return String.format("%02d:%02d.%02d", minutes, seconds, millis)
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("PRODUCTIVITY ACCESSORIES DECK", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
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
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ACTIVE DYNAMIC EMBEDDED SYSTEM CLOCK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.8.sp)
                    Spacer(modifier = Modifier.height(6.dp))
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
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // --- INTERACTIVE ACTIVE STOPWATCH ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("PRECISION STOPWATCH UTILITY", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

                    Text(
                        text = formatSw(swTimeInMs),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { swRunning = !swRunning },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (swRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(imageVector = if (swRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "StartPause")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (swRunning) "Pause" else "Start", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                if (swRunning) {
                                    swLaps = listOf("Lap ${swLaps.size + 1}: ${formatSw(swTimeInMs)}") + swLaps
                                }
                            },
                            enabled = swRunning,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Lap", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                swRunning = false
                                swTimeInMs = 0L
                                swLaps = emptyList()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Reset", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (swLaps.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            Text("Stopwatch Lap Records:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                            swLaps.forEach { lap ->
                                Text(lap, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        // --- COUNTDOWN TIMER UTILITY ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ACTIVE COUNTDOWN DAEMON TIMER", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)

                    if (!tRunning && tTimeLeftMs == 0L) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = tSetMinutes,
                                onValueChange = { tSetMinutes = it.filter { c -> c.isDigit() } },
                                label = { Text("Minutes", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            OutlinedTextField(
                                value = tSetSeconds,
                                onValueChange = { tSetSeconds = it.filter { c -> c.isDigit() } },
                                label = { Text("Seconds", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    } else {
                        // Display Active countdown timer beautifully
                        val tSecsTotal = tTimeLeftMs / 1000
                        val tMins = tSecsTotal / 60
                        val tSecs = tSecsTotal % 60
                        val displayTimer = String.format("%02d:%02d", tMins, tSecs)

                        val prg = if (tInitialTimeMs > 0L) tTimeLeftMs.toFloat() / tInitialTimeMs else 1f

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = displayTimer,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            LinearProgressIndicator(
                                progress = { prg },
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (tRunning) {
                                    tRunning = false
                                } else {
                                    if (tTimeLeftMs == 0L) {
                                        val m = tSetMinutes.toIntOrNull() ?: 0
                                        val s = tSetSeconds.toIntOrNull() ?: 0
                                        tInitialTimeMs = (m * 60 + s) * 1000L
                                        tTimeLeftMs = tInitialTimeMs
                                    }
                                    if (tTimeLeftMs > 0L) {
                                        tRunning = true
                                    }
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (tRunning) "Pause" else "Start Timer", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                tRunning = false
                                tTimeLeftMs = 0L
                                tInitialTimeMs = 0L
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text("Reset", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
