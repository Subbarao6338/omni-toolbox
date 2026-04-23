package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Element(
    val symbol: String,
    val name: String,
    val number: Int,
    val color: Color
)

val elements = listOf(
    Element("H", "Hydrogen", 1, Color(0xFFBBDEFB)),
    Element("He", "Helium", 2, Color(0xFFFFCCBC)),
    Element("Li", "Lithium", 3, Color(0xFFFFCDD2)),
    Element("Be", "Beryllium", 4, Color(0xFFF8BBD0)),
    Element("B", "Boron", 5, Color(0xFFE1BEE7)),
    Element("C", "Carbon", 6, Color(0xFFD1C4E9)),
    Element("N", "Nitrogen", 7, Color(0xFFC5CAE9)),
    Element("O", "Oxygen", 8, Color(0xFFB3E5FC)),
    Element("F", "Fluorine", 9, Color(0xFFB2EBF2)),
    Element("Ne", "Neon", 10, Color(0xFFB2DFDB)),
    Element("Na", "Sodium", 11, Color(0xFFC8E6C9)),
    Element("Mg", "Magnesium", 12, Color(0xFFDCEDC8)),
    Element("Al", "Aluminum", 13, Color(0xFFF0F4C3)),
    Element("Si", "Silicon", 14, Color(0xFFFFF9C4)),
    Element("P", "Phosphorus", 15, Color(0xFFFFECB3)),
    Element("S", "Sulfur", 16, Color(0xFFFFE0B2)),
    Element("Cl", "Chlorine", 17, Color(0xFFFFCCBC)),
    Element("Ar", "Argon", 18, Color(0xFFD7CCC8)),
    Element("K", "Potassium", 19, Color(0xFFC8E6C9)),
    Element("Ca", "Calcium", 20, Color(0xFFDCEDC8)),
    Element("Sc", "Scandium", 21, Color(0xFFF0F4C3)),
    Element("Ti", "Titanium", 22, Color(0xFFFFF9C4)),
    Element("V", "Vanadium", 23, Color(0xFFFFECB3)),
    Element("Cr", "Chromium", 24, Color(0xFFFFE0B2)),
    Element("Mn", "Manganese", 25, Color(0xFFFFCCBC)),
    Element("Fe", "Iron", 26, Color(0xFFD7CCC8)),
    Element("Co", "Cobalt", 27, Color(0xFFC8E6C9)),
    Element("Ni", "Nickel", 28, Color(0xFFDCEDC8)),
    Element("Cu", "Copper", 29, Color(0xFFF0F4C3)),
    Element("Zn", "Zinc", 30, Color(0xFFFFF9C4)),
    Element("Ga", "Gallium", 31, Color(0xFFFFECB3)),
    Element("Ge", "Germanium", 32, Color(0xFFFFE0B2)),
    Element("As", "Arsenic", 33, Color(0xFFFFCCBC)),
    Element("Se", "Selenium", 34, Color(0xFFD7CCC8)),
    Element("Br", "Bromine", 35, Color(0xFFBBDEFB)),
    Element("Kr", "Krypton", 36, Color(0xFFFFCCBC))
)

@Composable
fun PeriodicTableScreen(navController: NavHostController) {
    ToolScreen(title = "Periodic Table", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Elements (1-36)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(elements) { element ->
                    ElementCard(element)
                }
            }
        }
    }
}

@Composable
fun ElementCard(element: Element) {
    Card(
        modifier = Modifier.aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = element.color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(element.number.toString(), style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
            Text(element.symbol, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(element.name, style = MaterialTheme.typography.labelSmall, maxLines = 1)
        }
    }
}
