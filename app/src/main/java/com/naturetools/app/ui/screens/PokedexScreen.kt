package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class Pokemon(val id: Int, val name: String, val type: String)

val pokemonList = listOf(
    Pokemon(1, "Bulbasaur", "Grass/Poison"),
    Pokemon(4, "Charmander", "Fire"),
    Pokemon(7, "Squirtle", "Water"),
    Pokemon(25, "Pikachu", "Electric"),
    Pokemon(133, "Eevee", "Normal"),
    Pokemon(143, "Snorlax", "Normal"),
    Pokemon(150, "Mewtwo", "Psychic"),
    Pokemon(151, "Mew", "Psychic")
)

@Composable
fun PokedexScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = pokemonList.filter { it.name.contains(searchQuery, ignoreCase = true) }

    ToolScreen(title = "Pokedex", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Pokemon...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredList) { pokemon ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("#${pokemon.id.toString().padStart(3, '0')}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(pokemon.name, style = MaterialTheme.typography.titleMedium)
                                Text(pokemon.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}
