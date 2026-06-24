package omni.toolbox.ui.screens.automation

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
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.OmniViewModel
import omni.toolbox.viewmodel.AutomationRule

@Composable
fun AutomationScreen(navController: NavHostController, viewModel: OmniViewModel) {
    var activeTab by remember { mutableIntStateOf(0) }

    ToolScreen(
        title = "Daily Utilities & Automation",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    Text("Logs", modifier = Modifier.padding(10.dp), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                0 -> MacrosTab(viewModel)
                1 -> AutomationCalculatorsTab()
                2 -> AutomationConverterTab()
                3 -> AutomationLogsTab(viewModel)
            }
        }
    }
}

@Composable
fun MacrosTab(viewModel: OmniViewModel) {
    val rules = viewModel.automationRules.value
    var inputName by remember { mutableStateOf("") }
    var inputTrigger by remember { mutableStateOf("SHAKE") }
    var inputAction by remember { mutableStateOf("CLEAN_CACHE") }
    var simulationMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("AUTOMATION RULE CREATOR (ROOT-FREE)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Add New Custom Macro Rule", style = MaterialTheme.typography.titleSmall)

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
            Text("SIMULATE EVENTS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 16.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        simulationMessage = "SHAKE GESTURE DETECTED (Simulation triggered!). Generated new key cipher."
                        viewModel.addLog(simulationMessage!!)
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
                        viewModel.addLog(simulationMessage!!)
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
            Text("SYSTEM AUTOMATION REGISTRY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(top = 16.dp))
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
                        Text("Trigger: ${rule.triggerType} \u2794 Action: ${rule.actionType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
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
fun AutomationCalculatorsTab() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Automation Utility Calculators", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Standard Calculator", fontWeight = FontWeight.Bold)
                Text("Radix & Bitwise Tool", fontWeight = FontWeight.Bold)
                Text("Scientific Math Engine", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AutomationConverterTab() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Automation Unit Converters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Length & Temperature", fontWeight = FontWeight.Bold)
                Text("Cyber Storage & Weight", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AutomationLogsTab(viewModel: OmniViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Active Automation Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth().height(400.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                viewModel.syncLogs.forEach { log ->
                    Text(
                        text = log.message,
                        color = Color(0xFF00FF88),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
