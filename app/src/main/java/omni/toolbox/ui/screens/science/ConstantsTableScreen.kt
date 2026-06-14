package omni.toolbox.ui.screens.science

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class Constant(val name: String, val symbol: String, val value: String)

@Composable
fun ConstantsTableScreen(navController: NavHostController) {
    val constants = listOf(
        Constant("Speed of Light", "c", "299,792,458 m/s"),
        Constant("Gravitational Constant", "G", "6.674 × 10⁻¹¹ m³/kg·s²"),
        Constant("Planck Constant", "h", "6.626 × 10⁻³⁴ J·s"),
        Constant("Elementary Charge", "e", "1.602 × 10⁻¹⁹ C"),
        Constant("Avogadro Constant", "Nₐ", "6.022 × 10²³ mol⁻¹"),
        Constant("Boltzmann Constant", "k", "1.381 × 10⁻²³ J/K"),
        Constant("Gas Constant", "R", "8.314 J/mol·K"),
        Constant("Stefan-Boltzmann Constant", "σ", "5.670 × 10⁻⁸ W/m²·K⁴"),
        Constant("Proton Mass", "mₚ", "1.672 × 10⁻²⁷ kg"),
        Constant("Electron Mass", "mₑ", "9.109 × 10⁻³¹ kg"),
        Constant("Permittivity of Free Space", "ε₀", "8.854 × 10⁻¹² F/m"),
        Constant("Permeability of Free Space", "μ₀", "1.256 × 10⁻⁶ H/m")
    )

    ToolScreen(
        title = "Physics Constants",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(constants) { item ->
                ListItem(
                    headlineContent = { Text(item.name) },
                    supportingContent = { Text(item.value) },
                    trailingContent = { Text(item.symbol, style = MaterialTheme.typography.titleMedium) }
                )
                HorizontalDivider()
            }
        }
    }
}
