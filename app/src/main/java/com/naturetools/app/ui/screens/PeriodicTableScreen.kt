package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
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
    Element("Kr", "Krypton", 36, Color(0xFFFFCCBC)),
    Element("Rb", "Rubidium", 37, Color(0xFFC8E6C9)),
    Element("Sr", "Strontium", 38, Color(0xFFDCEDC8)),
    Element("Y", "Yttrium", 39, Color(0xFFF0F4C3)),
    Element("Zr", "Zirconium", 40, Color(0xFFFFF9C4)),
    Element("Nb", "Niobium", 41, Color(0xFFFFECB3)),
    Element("Mo", "Molybdenum", 42, Color(0xFFFFE0B2)),
    Element("Tc", "Technetium", 43, Color(0xFFFFCCBC)),
    Element("Ru", "Ruthenium", 44, Color(0xFFD7CCC8)),
    Element("Rh", "Rhodium", 45, Color(0xFFC8E6C9)),
    Element("Pd", "Palladium", 46, Color(0xFFDCEDC8)),
    Element("Ag", "Silver", 47, Color(0xFFF0F4C3)),
    Element("Cd", "Cadmium", 48, Color(0xFFFFF9C4)),
    Element("In", "Indium", 49, Color(0xFFFFECB3)),
    Element("Sn", "Tin", 50, Color(0xFFFFE0B2)),
    Element("Sb", "Antimony", 51, Color(0xFFFFCCBC)),
    Element("Te", "Tellurium", 52, Color(0xFFD7CCC8)),
    Element("I", "Iodine", 53, Color(0xFFBBDEFB)),
    Element("Xe", "Xenon", 54, Color(0xFFFFCCBC))
)

@Composable
fun PeriodicTableScreen(navController: NavHostController) {
    val externalResources = listOf(
        "Byju's Periodic Table" to "https://byjus.com/periodic-table/",
        "PTable" to "https://ptable.com/#Properties",
        "Royal Society of Chemistry" to "https://periodic-table.rsc.org/",
        "PubChem" to "https://pubchem.ncbi.nlm.nih.gov/periodic-table/"
    )

    ToolScreen(title = "Periodic Table", onBack = { navController.popBackStack() }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(padding)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Elements (1-54)", style = MaterialTheme.typography.titleMedium)
            }

            items(elements) { element ->
                ElementCard(element)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("External Resources", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(externalResources.size, span = { GridItemSpan(maxLineSpan) }) { index ->
                val (name, url) = externalResources[index]
                OutlinedCard(
                    onClick = { navController.navigate("web?url=$url") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
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
