package com.naturetools.app.ui.screens.science

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Element(
    val symbol: String,
    val name: String,
    val number: Int,
    val weight: Double,
    val color: Color
)

val elements = listOf(
    Element("H", "Hydrogen", 1, 1.008, Color(0xFFBBDEFB)),
    Element("He", "Helium", 2, 4.0026, Color(0xFFFFCCBC)),
    Element("Li", "Lithium", 3, 6.94, Color(0xFFFFCDD2)),
    Element("Be", "Beryllium", 4, 9.0122, Color(0xFFF8BBD0)),
    Element("B", "Boron", 5, 10.81, Color(0xFFE1BEE7)),
    Element("C", "Carbon", 6, 12.011, Color(0xFFD1C4E9)),
    Element("N", "Nitrogen", 7, 14.007, Color(0xFFC5CAE9)),
    Element("O", "Oxygen", 8, 15.999, Color(0xFFB3E5FC)),
    Element("F", "Fluorine", 9, 18.998, Color(0xFFB2EBF2)),
    Element("Ne", "Neon", 10, 20.180, Color(0xFFB2DFDB)),
    Element("Na", "Sodium", 11, 22.990, Color(0xFFC8E6C9)),
    Element("Mg", "Magnesium", 12, 24.305, Color(0xFFDCEDC8)),
    Element("Al", "Aluminum", 13, 26.982, Color(0xFFF0F4C3)),
    Element("Si", "Silicon", 14, 28.085, Color(0xFFFFF9C4)),
    Element("P", "Phosphorus", 15, 30.974, Color(0xFFFFECB3)),
    Element("S", "Sulfur", 16, 32.06, Color(0xFFFFE0B2)),
    Element("Cl", "Chlorine", 17, 35.45, Color(0xFFFFCCBC)),
    Element("Ar", "Argon", 18, 39.948, Color(0xFFD7CCC8)),
    Element("K", "Potassium", 19, 39.098, Color(0xFFC8E6C9)),
    Element("Ca", "Calcium", 20, 40.078, Color(0xFFDCEDC8)),
    Element("Sc", "Scandium", 21, 44.956, Color(0xFFF0F4C3)),
    Element("Ti", "Titanium", 22, 47.867, Color(0xFFFFF9C4)),
    Element("V", "Vanadium", 23, 50.942, Color(0xFFFFECB3)),
    Element("Cr", "Chromium", 24, 51.996, Color(0xFFFFE0B2)),
    Element("Mn", "Manganese", 25, 54.938, Color(0xFFFFCCBC)),
    Element("Fe", "Iron", 26, 55.845, Color(0xFFD7CCC8)),
    Element("Co", "Cobalt", 27, 58.933, Color(0xFFC8E6C9)),
    Element("Ni", "Nickel", 28, 58.693, Color(0xFFDCEDC8)),
    Element("Cu", "Copper", 29, 63.546, Color(0xFFF0F4C3)),
    Element("Zn", "Zinc", 30, 65.38, Color(0xFFFFF9C4)),
    Element("Ga", "Gallium", 31, 69.723, Color(0xFFFFECB3)),
    Element("Ge", "Germanium", 32, 72.63, Color(0xFFFFE0B2)),
    Element("As", "Arsenic", 33, 74.922, Color(0xFFFFCCBC)),
    Element("Se", "Selenium", 34, 78.971, Color(0xFFD7CCC8)),
    Element("Br", "Bromine", 35, 79.904, Color(0xFFBBDEFB)),
    Element("Kr", "Krypton", 36, 83.798, Color(0xFFFFCCBC)),
    Element("Rb", "Rubidium", 37, 85.468, Color(0xFFC8E6C9)),
    Element("Sr", "Strontium", 38, 87.62, Color(0xFFDCEDC8)),
    Element("Y", "Yttrium", 39, 88.906, Color(0xFFF0F4C3)),
    Element("Zr", "Zirconium", 40, 91.224, Color(0xFFFFF9C4)),
    Element("Nb", "Niobium", 41, 92.906, Color(0xFFFFECB3)),
    Element("Mo", "Molybdenum", 42, 95.95, Color(0xFFFFE0B2)),
    Element("Tc", "Technetium", 43, 98.0, Color(0xFFFFCCBC)),
    Element("Ru", "Ruthenium", 44, 101.07, Color(0xFFD7CCC8)),
    Element("Rh", "Rhodium", 45, 102.91, Color(0xFFC8E6C9)),
    Element("Pd", "Palladium", 46, 106.42, Color(0xFFDCEDC8)),
    Element("Ag", "Silver", 47, 107.87, Color(0xFFF0F4C3)),
    Element("Cd", "Cadmium", 48, 112.41, Color(0xFFFFF9C4)),
    Element("In", "Indium", 49, 114.82, Color(0xFFFFECB3)),
    Element("Sn", "Tin", 50, 118.71, Color(0xFFFFE0B2)),
    Element("Sb", "Antimony", 51, 121.76, Color(0xFFFFCCBC)),
    Element("Te", "Tellurium", 52, 127.60, Color(0xFFD7CCC8)),
    Element("I", "Iodine", 53, 126.90, Color(0xFFBBDEFB)),
    Element("Xe", "Xenon", 54, 131.29, Color(0xFFFFCCBC)),
    Element("Cs", "Cesium", 55, 132.91, Color(0xFFC8E6C9)),
    Element("Ba", "Barium", 56, 137.33, Color(0xFFDCEDC8)),
    Element("La", "Lanthanum", 57, 138.91, Color(0xFFFFCDD2)),
    Element("Ce", "Cerium", 58, 140.12, Color(0xFFFFCDD2)),
    Element("Pr", "Praseodymium", 59, 140.91, Color(0xFFFFCDD2)),
    Element("Nd", "Neodymium", 60, 144.24, Color(0xFFFFCDD2)),
    Element("Pm", "Promethium", 61, 145.0, Color(0xFFFFCDD2)),
    Element("Sm", "Samarium", 62, 150.36, Color(0xFFFFCDD2)),
    Element("Eu", "Europium", 63, 151.96, Color(0xFFFFCDD2)),
    Element("Gd", "Gadolinium", 64, 157.25, Color(0xFFFFCDD2)),
    Element("Tb", "Terbium", 65, 158.93, Color(0xFFFFCDD2)),
    Element("Dy", "Dysprosium", 66, 162.50, Color(0xFFFFCDD2)),
    Element("Ho", "Holmium", 67, 164.93, Color(0xFFFFCDD2)),
    Element("Er", "Erbium", 68, 167.26, Color(0xFFFFCDD2)),
    Element("Tm", "Thulium", 69, 168.93, Color(0xFFFFCDD2)),
    Element("Yb", "Ytterbium", 70, 173.05, Color(0xFFFFCDD2)),
    Element("Lu", "Lutetium", 71, 174.97, Color(0xFFFFCDD2)),
    Element("Hf", "Hafnium", 72, 178.49, Color(0xFFFFF9C4)),
    Element("Ta", "Tantalum", 73, 180.95, Color(0xFFFFF9C4)),
    Element("W", "Tungsten", 74, 183.84, Color(0xFFFFF9C4)),
    Element("Re", "Rhenium", 75, 186.21, Color(0xFFFFF9C4)),
    Element("Os", "Osmium", 76, 190.23, Color(0xFFFFF9C4)),
    Element("Ir", "Iridium", 77, 192.22, Color(0xFFFFF9C4)),
    Element("Pt", "Platinum", 78, 195.08, Color(0xFFFFF9C4)),
    Element("Au", "Gold", 79, 196.97, Color(0xFFFFF9C4)),
    Element("Hg", "Mercury", 80, 200.59, Color(0xFFFFF9C4)),
    Element("Tl", "Thallium", 81, 204.38, Color(0xFFF0F4C3)),
    Element("Pb", "Lead", 82, 207.2, Color(0xFFF0F4C3)),
    Element("Bi", "Bismuth", 83, 208.98, Color(0xFFF0F4C3)),
    Element("Po", "Polonium", 84, 209.0, Color(0xFFF0F4C3)),
    Element("At", "Astatine", 85, 210.0, Color(0xFFB2EBF2)),
    Element("Rn", "Radon", 86, 222.0, Color(0xFFFFCCBC)),
    Element("Fr", "Francium", 87, 223.0, Color(0xFFC8E6C9)),
    Element("Ra", "Radium", 88, 226.0, Color(0xFFDCEDC8)),
    Element("Ac", "Actinium", 89, 227.0, Color(0xFFD1C4E9)),
    Element("Th", "Thorium", 90, 232.04, Color(0xFFD1C4E9)),
    Element("Pa", "Protactinium", 91, 231.04, Color(0xFFD1C4E9)),
    Element("U", "Uranium", 92, 238.03, Color(0xFFD1C4E9)),
    Element("Np", "Neptunium", 93, 237.0, Color(0xFFD1C4E9)),
    Element("Pu", "Plutonium", 94, 244.0, Color(0xFFD1C4E9)),
    Element("Am", "Americium", 95, 243.0, Color(0xFFD1C4E9)),
    Element("Cm", "Curium", 96, 247.0, Color(0xFFD1C4E9)),
    Element("Bk", "Berkelium", 97, 247.0, Color(0xFFD1C4E9)),
    Element("Cf", "Californium", 98, 251.0, Color(0xFFD1C4E9)),
    Element("Es", "Einsteinium", 99, 252.0, Color(0xFFD1C4E9)),
    Element("Fm", "Fermium", 100, 257.0, Color(0xFFD1C4E9)),
    Element("Md", "Mendelevium", 101, 258.0, Color(0xFFD1C4E9)),
    Element("No", "Nobelium", 102, 259.0, Color(0xFFD1C4E9)),
    Element("Lr", "Lawrencium", 103, 262.0, Color(0xFFD1C4E9)),
    Element("Rf", "Rutherfordium", 104, 267.0, Color(0xFFFFF9C4)),
    Element("Db", "Dubnium", 105, 270.0, Color(0xFFFFF9C4)),
    Element("Sg", "Seaborgium", 106, 271.0, Color(0xFFFFF9C4)),
    Element("Bh", "Bohrium", 107, 270.0, Color(0xFFFFF9C4)),
    Element("Hs", "Hassium", 108, 277.0, Color(0xFFFFF9C4)),
    Element("Mt", "Meitnerium", 109, 276.0, Color(0xFFFFF9C4)),
    Element("Ds", "Darmstadtium", 110, 281.0, Color(0xFFFFF9C4)),
    Element("Rg", "Roentgenium", 111, 280.0, Color(0xFFFFF9C4)),
    Element("Cn", "Copernicium", 112, 285.0, Color(0xFFFFF9C4)),
    Element("Nh", "Nihonium", 113, 284.0, Color(0xFFF0F4C3)),
    Element("Fl", "Flerovium", 114, 289.0, Color(0xFFF0F4C3)),
    Element("Mc", "Moscovium", 115, 288.0, Color(0xFFF0F4C3)),
    Element("Lv", "Livermorium", 116, 293.0, Color(0xFFF0F4C3)),
    Element("Ts", "Tennessine", 117, 294.0, Color(0xFFB2EBF2)),
    Element("Og", "Oganesson", 118, 294.0, Color(0xFFFFCCBC))
)

@Composable
fun PeriodicTableScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredElements = elements.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.symbol.contains(searchQuery, ignoreCase = true)
    }

    val externalResources = listOf(
        "Byju's" to "https://byjus.com/periodic-table/",
        "PTable" to "https://ptable.com/#Properties",
        "RSC" to "https://periodic-table.rsc.org/",
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search elements...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = MaterialTheme.shapes.medium
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("External Resources", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        externalResources.take(2).forEach { (name, url) ->
                            ExternalResourceCard(name, url, navController, Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        externalResources.drop(2).forEach { (name, url) ->
                            ExternalResourceCard(name, url, navController, Modifier.weight(1f))
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Elements (1-118)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(filteredElements) { element ->
                ElementCard(element)
            }
        }
    }
}

@Composable
fun ExternalResourceCard(name: String, url: String, navController: NavHostController, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = { navController.navigate("web?url=${Uri.encode(url)}&showBar=false") },
        modifier = modifier.height(50.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
            Text(name, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
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
            Text(element.number.toString(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), modifier = Modifier.align(Alignment.Start))
            Text(element.symbol, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold)
            Text(element.name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), maxLines = 1)
            Text(element.weight.toString(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp), maxLines = 1)
        }
    }
}
