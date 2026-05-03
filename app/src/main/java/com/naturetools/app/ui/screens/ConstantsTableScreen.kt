package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class ScientificConstant(val name: String, val symbol: String, val value: String, val unit: String)

@Composable
fun ConstantsTableScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val constants = listOf(
        ScientificConstant("Speed of Light", "c", "299,792,458", "m/s"),
        ScientificConstant("Gravitational Constant", "G", "6.67430 × 10⁻¹¹", "m³/(kg·s²)"),
        ScientificConstant("Planck Constant", "h", "6.62607015 × 10⁻³⁴", "J·s"),
        ScientificConstant("Reduced Planck Constant", "ħ", "1.054571817 × 10⁻³⁴", "J·s"),
        ScientificConstant("Boltzmann Constant", "k", "1.380649 × 10⁻²³", "J/K"),
        ScientificConstant("Avogadro Constant", "Nₐ", "6.02214076 × 10²³", "mol⁻¹"),
        ScientificConstant("Elementary Charge", "e", "1.602176634 × 10⁻¹⁹", "C"),
        ScientificConstant("Gas Constant", "R", "8.314462618", "J/(mol·K)"),
        ScientificConstant("Standard Gravity", "g", "9.80665", "m/s²"),
        ScientificConstant("Electron Mass", "mₑ", "9.1093837 × 10⁻³¹", "kg"),
        ScientificConstant("Proton Mass", "mₚ", "1.6726219 × 10⁻²⁷", "kg"),
        ScientificConstant("Permittivity of Free Space", "ε₀", "8.8541878128 × 10⁻¹²", "F/m"),
        ScientificConstant("Permeability of Free Space", "μ₀", "1.256637062 × 10⁻⁶", "N/A²"),
        ScientificConstant("Golden Ratio", "φ", "1.6180339887", ""),
        ScientificConstant("Pi", "π", "3.1415926535", ""),
        ScientificConstant("Euler's Number", "e", "2.7182818284", "")
    ).filter { it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(searchQuery, ignoreCase = true) }

    ToolScreen(
        title = "Scientific Constants",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search constants...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(constants) { constant ->
                    ListItem(
                        headlineContent = { Text(constant.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("${constant.symbol} = ${constant.value} ${constant.unit}") },
                        trailingContent = {
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(constant.value))
                            }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Value")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
