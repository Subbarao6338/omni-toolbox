package omni.toolbox.ui.screens.electronics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun ElectronicsToolScreen(navController: NavHostController, title: String) {
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
            when (title) {
                "Ohm's Law" -> OhmsLawCalculator()
                "Circuit Calc" -> CircuitPowerCalculator()
                else -> Text("Utility for $title")
            }
        }
    }
}

@Composable
fun OhmsLawCalculator() {
    var voltage by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    var resistance by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Ohm's Law (V = I * R)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = voltage,
            onValueChange = { voltage = it },
            label = { Text("Voltage (V)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = current,
            onValueChange = { current = it },
            label = { Text("Current (A)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = resistance,
            onValueChange = { resistance = it },
            label = { Text("Resistance (Ω)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val v = voltage.toDoubleOrNull()
        val i = current.toDoubleOrNull()
        val r = resistance.toDoubleOrNull()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calculated Results:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    v != null && i != null && i != 0.0 -> Text("Resistance (R) = ${"%.4f".format(v / i)} Ω")
                    v != null && r != null && r != 0.0 -> Text("Current (I) = ${"%.4f".format(v / r)} A")
                    i != null && r != null -> Text("Voltage (V) = ${"%.4f".format(i * r)} V")
                    else -> Text("Enter exactly two values to calculate the missing one.")
                }

                if (v != null && i != null) {
                    Text("Power (P) = ${"%.4f".format(v * i)} W")
                } else if (i != null && r != null) {
                    Text("Power (P) = ${"%.4f".format(i * i * r)} W")
                } else if (v != null && r != null && r != 0.0) {
                    Text("Power (P) = ${"%.4f".format((v * v) / r)} W")
                }
            }
        }
    }
}

@Composable
fun CircuitPowerCalculator() {
    var power by remember { mutableStateOf("") }
    var voltage by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Power Triangle (P = V * I)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = power,
            onValueChange = { power = it },
            label = { Text("Power (W)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = voltage,
            onValueChange = { voltage = it },
            label = { Text("Voltage (V)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = current,
            onValueChange = { current = it },
            label = { Text("Current (A)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val p = power.toDoubleOrNull()
        val v = voltage.toDoubleOrNull()
        val i = current.toDoubleOrNull()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Calculated Results:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    v != null && i != null -> Text("Power (P) = ${"%.4f".format(v * i)} W")
                    p != null && v != null && v != 0.0 -> Text("Current (I) = ${"%.4f".format(p / v)} A")
                    p != null && i != null && i != 0.0 -> Text("Voltage (V) = ${"%.4f".format(p / i)} V")
                    else -> Text("Enter exactly two values to calculate the missing one.")
                }
            }
        }
    }
}
