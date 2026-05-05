package com.naturetools.app.ui.screens.physics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Formula(val name: String, val expression: String, val category: String)

@Composable
fun PhysicsFormulasScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }

    val formulas = remember {
        listOf(
            Formula("Newton's Second Law", "F = m * a", "Mechanics"),
            Formula("Kinetic Energy", "Ek = 1/2 * m * v²", "Mechanics"),
            Formula("Potential Energy", "Ep = m * g * h", "Mechanics"),
            Formula("Work Done", "W = F * d * cos(θ)", "Mechanics"),
            Formula("Power", "P = W / t", "Mechanics"),
            Formula("Ohm's Law", "V = I * R", "Electricity"),
            Formula("Electrical Power", "P = V * I", "Electricity"),
            Formula("Capacitance", "C = Q / V", "Electricity"),
            Formula("Ideal Gas Law", "P * V = n * R * T", "Thermodynamics"),
            Formula("Specific Heat Capacity", "Q = m * c * ΔT", "Thermodynamics"),
            Formula("Einstein's Mass-Energy", "E = m * c²", "Relativity"),
            Formula("Wave Equation", "v = f * λ", "Waves"),
            Formula("Photon Energy", "E = h * f", "Quantum")
        )
    }

    val filteredFormulas = formulas.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    ToolScreen(
        title = "Physics Formulas",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search formulas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredFormulas) { formula ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(formula.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(formula.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    formula.expression,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
