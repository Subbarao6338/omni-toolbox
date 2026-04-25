package com.naturetools.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Pokemon(val id: Int, val name: String, val type: String)

val pokemonList = listOf(
    Pokemon(1, "Bulbasaur", "Grass/Poison"),
    Pokemon(2, "Ivysaur", "Grass/Poison"),
    Pokemon(3, "Venusaur", "Grass/Poison"),
    Pokemon(4, "Charmander", "Fire"),
    Pokemon(5, "Charmeleon", "Fire"),
    Pokemon(6, "Charizard", "Fire/Flying"),
    Pokemon(7, "Squirtle", "Water"),
    Pokemon(8, "Wartortle", "Water"),
    Pokemon(9, "Blastoise", "Water"),
    Pokemon(10, "Caterpie", "Bug"),
    Pokemon(11, "Metapod", "Bug"),
    Pokemon(12, "Butterfree", "Bug/Flying"),
    Pokemon(16, "Pidgey", "Normal/Flying"),
    Pokemon(17, "Pidgeotto", "Normal/Flying"),
    Pokemon(18, "Pidgeot", "Normal/Flying"),
    Pokemon(19, "Rattata", "Normal"),
    Pokemon(20, "Raticate", "Normal"),
    Pokemon(25, "Pikachu", "Electric"),
    Pokemon(26, "Raichu", "Electric"),
    Pokemon(133, "Eevee", "Normal"),
    Pokemon(134, "Vaporeon", "Water"),
    Pokemon(135, "Jolteon", "Electric"),
    Pokemon(136, "Flareon", "Fire"),
    Pokemon(143, "Snorlax", "Normal"),
    Pokemon(144, "Articuno", "Ice/Flying"),
    Pokemon(145, "Zapdos", "Electric/Flying"),
    Pokemon(146, "Moltres", "Fire/Flying"),
    Pokemon(147, "Dratini", "Dragon"),
    Pokemon(148, "Dragonair", "Dragon"),
    Pokemon(149, "Dragonite", "Dragon/Flying"),
    Pokemon(150, "Mewtwo", "Psychic"),
    Pokemon(151, "Mew", "Psychic"),
    Pokemon(152, "Chikorita", "Grass"),
    Pokemon(155, "Cyndaquil", "Fire"),
    Pokemon(158, "Totodile", "Water"),
    Pokemon(249, "Lugia", "Psychic/Flying"),
    Pokemon(250, "Ho-Oh", "Fire/Flying")
)

fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "grass" -> Color(0xFF7AC74C)
        "fire" -> Color(0xFFEE8130)
        "water" -> Color(0xFF6390F0)
        "bug" -> Color(0xFFA6B91A)
        "normal" -> Color(0xFFA8A77A)
        "poison" -> Color(0xFFA33EA1)
        "electric" -> Color(0xFFF7D02C)
        "ground" -> Color(0xFFE2BF65)
        "fairy" -> Color(0xFFD685AD)
        "fighting" -> Color(0xFFC22E28)
        "psychic" -> Color(0xFFF95587)
        "rock" -> Color(0xFFB6A136)
        "ghost" -> Color(0xFF735797)
        "ice" -> Color(0xFF96D9D6)
        "dragon" -> Color(0xFF6F35FC)
        "flying" -> Color(0xFFA98FF3)
        else -> Color.Gray
    }
}

@Composable
fun PokedexScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = pokemonList.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val externalResources = listOf(
        "Pokemon Portal" to "https://in.portal-pokemon.com/play/pokedex",
        "Pokemon Database" to "https://pokemondb.net/pokedex",
        "Official Pokedex" to "https://www.pokemon.com/us/pokedex"
    )

    ToolScreen(title = "Pokedex", onBack = { navController.popBackStack() }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search Pokemon...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = MaterialTheme.shapes.medium
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("External Resources", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    externalResources.take(2).forEach { (name, url) ->
                        ElevatedCard(
                            onClick = { navController.navigate("web?url=$url&showBar=false") },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                                Text(name, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                if (externalResources.size > 2) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        externalResources.drop(2).forEach { (name, url) ->
                            ElevatedCard(
                                onClick = { navController.navigate("web?url=$url&showBar=false") },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(name, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
                                }
                            }
                        }
                        if (externalResources.size % 2 != 0) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Pokemon List", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(filteredList) { pokemon ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#${pokemon.id.toString().padStart(3, '0')}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pokemon.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                pokemon.type.split("/").forEach { type ->
                                    Surface(
                                        color = getTypeColor(type),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = type,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
