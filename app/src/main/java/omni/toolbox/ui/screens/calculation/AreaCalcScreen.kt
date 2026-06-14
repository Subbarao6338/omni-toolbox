package omni.toolbox.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.PI

@Composable
fun AreaCalcScreen(navController: NavHostController) {
    var selectedShape by remember { mutableStateOf("Rectangle") }
    var val1 by remember { mutableStateOf("") }; var val2 by remember { mutableStateOf("") }
    val result = remember(selectedShape, val1, val2) {
        val d1 = val1.toDoubleOrNull() ?: 0.0; val d2 = val2.toDoubleOrNull() ?: 0.0
        when (selectedShape) {
            "Rectangle" -> d1 * d2; "Circle" -> PI * d1 * d1; "Triangle" -> 0.5 * d1 * d2; "Square" -> d1 * d1; else -> 0.0
        }
    }
    ToolScreen(title = "Area Calculator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Row { listOf("Rectangle", "Circle", "Triangle").forEach { FilterChip(selected = selectedShape == it, onClick = { selectedShape = it }, label = { Text(it) }) } }
            OutlinedTextField(value = val1, onValueChange = { val1 = it }, label = { Text("Value 1") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Value 2") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) { Text("Result: ${String.format("%.4f", result)}", style = MaterialTheme.typography.headlineMedium) }
            }
        }
    }
}
