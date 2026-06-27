package omni.toolbox.ui.screens.science

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class Country(
    val name: String,
    val capital: String,
    val population: String,
    val area: String,
    val region: String,
    val flag: String
)

@Composable
fun WorldMapScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val countries = remember {
        listOf(
            Country("Afghanistan", "Kabul", "41.1M", "652,230 km²", "Asia", "🇦🇫"),
            Country("Albania", "Tirana", "2.8M", "28,748 km²", "Europe", "🇦🇱"),
            Country("Algeria", "Algiers", "44.9M", "2,381,741 km²", "Africa", "🇩🇿"),
            Country("Andorra", "Andorra la Vella", "77k", "468 km²", "Europe", "🇦🇩"),
            Country("Angola", "Luanda", "35.6M", "1,246,700 km²", "Africa", "🇦🇴"),
            Country("Argentina", "Buenos Aires", "45.8M", "2,780,400 km²", "South America", "🇦🇷"),
            Country("Australia", "Canberra", "26.0M", "7,692,024 km²", "Oceania", "🇦🇺"),
            Country("Austria", "Vienna", "9.0M", "83,871 km²", "Europe", "🇦🇹"),
            Country("Belgium", "Brussels", "11.6M", "30,528 km²", "Europe", "🇧🇪"),
            Country("Brazil", "Brasília", "214.3M", "8,515,767 km²", "South America", "🇧🇷"),
            Country("Canada", "Ottawa", "38.2M", "9,984,670 km²", "North America", "🇨🇦"),
            Country("China", "Beijing", "1,412M", "9,596,961 km²", "Asia", "🇨🇳"),
            Country("Egypt", "Cairo", "111.0M", "1,002,450 km²", "Africa", "🇪🇬"),
            Country("France", "Paris", "67.8M", "551,695 km²", "Europe", "🇫🇷"),
            Country("Germany", "Berlin", "83.2M", "357,022 km²", "Europe", "🇩🇪"),
            Country("India", "New Delhi", "1,408M", "3,287,263 km²", "Asia", "🇮🇳"),
            Country("Indonesia", "Jakarta", "273.8M", "1,904,569 km²", "Asia", "🇮🇩"),
            Country("Italy", "Rome", "59.1M", "301,340 km²", "Europe", "🇮🇹"),
            Country("Japan", "Tokyo", "125.7M", "377,975 km²", "Asia", "🇯🇵"),
            Country("Mexico", "Mexico City", "126.7M", "1,964,375 km²", "North America", "🇲🇽"),
            Country("Nigeria", "Abuja", "213.4M", "923,768 km²", "Africa", "🇳🇬"),
            Country("Pakistan", "Islamabad", "231.4M", "881,913 km²", "Asia", "🇵🇰"),
            Country("Russia", "Moscow", "143.4M", "17,098,242 km²", "Europe/Asia", "🇷🇺"),
            Country("South Africa", "Pretoria", "59.4M", "1,221,037 km²", "Africa", "🇿🇦"),
            Country("Spain", "Madrid", "47.4M", "505,992 km²", "Europe", "🇪🇸"),
            Country("United Kingdom", "London", "67.3M", "242,495 km²", "Europe", "🇬🇧"),
            Country("United States", "Washington, D.C.", "331.9M", "9,833,517 km²", "North America", "🇺🇸"),
            Country("Vietnam", "Hanoi", "98.2M", "331,210 km²", "Asia", "🇻🇳")
        ).sortedBy { it.name }
    }

    val filteredCountries = countries.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.capital.contains(searchQuery, ignoreCase = true)
    }

    ToolScreen(
        title = "World Info",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Country or Capital") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredCountries) { country ->
                    CountryCard(country)
                }
            }
        }
    }
}

@Composable
fun CountryCard(country: Country) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(country.flag, fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(country.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(country.region, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                CountryInfoRow("Capital", country.capital)
                CountryInfoRow("Population", country.population)
                CountryInfoRow("Area", country.area)
            }
        }
    }
}

@Composable
fun CountryInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
