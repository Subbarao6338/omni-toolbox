package omni.toolbox.ui.screens.science

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
import omni.toolbox.ui.components.ToolScreen

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
    Pokemon(13, "Weedle", "Bug/Poison"),
    Pokemon(14, "Kakuna", "Bug/Poison"),
    Pokemon(15, "Beedrill", "Bug/Poison"),
    Pokemon(16, "Pidgey", "Normal/Flying"),
    Pokemon(17, "Pidgeotto", "Normal/Flying"),
    Pokemon(18, "Pidgeot", "Normal/Flying"),
    Pokemon(19, "Rattata", "Normal"),
    Pokemon(20, "Raticate", "Normal"),
    Pokemon(21, "Spearow", "Normal/Flying"),
    Pokemon(22, "Fearow", "Normal/Flying"),
    Pokemon(23, "Ekans", "Poison"),
    Pokemon(24, "Arbok", "Poison"),
    Pokemon(25, "Pikachu", "Electric"),
    Pokemon(26, "Raichu", "Electric"),
    Pokemon(27, "Sandshrew", "Ground"),
    Pokemon(28, "Sandslash", "Ground"),
    Pokemon(29, "Nidoran♀", "Poison"),
    Pokemon(30, "Nidorina", "Poison"),
    Pokemon(31, "Nidoqueen", "Poison/Ground"),
    Pokemon(32, "Nidoran♂", "Poison"),
    Pokemon(33, "Nidorino", "Poison"),
    Pokemon(34, "Nidoking", "Poison/Ground"),
    Pokemon(35, "Clefairy", "Fairy"),
    Pokemon(36, "Clefable", "Fairy"),
    Pokemon(37, "Vulpix", "Fire"),
    Pokemon(38, "Ninetales", "Fire"),
    Pokemon(39, "Jigglypuff", "Normal/Fairy"),
    Pokemon(40, "Wigglytuff", "Normal/Fairy"),
    Pokemon(41, "Zubat", "Poison/Flying"),
    Pokemon(42, "Golbat", "Poison/Flying"),
    Pokemon(43, "Oddish", "Grass/Poison"),
    Pokemon(44, "Gloom", "Grass/Poison"),
    Pokemon(45, "Vileplume", "Grass/Poison"),
    Pokemon(46, "Paras", "Bug/Grass"),
    Pokemon(47, "Parasect", "Bug/Grass"),
    Pokemon(48, "Venonat", "Bug/Poison"),
    Pokemon(49, "Venomoth", "Bug/Poison"),
    Pokemon(50, "Diglett", "Ground"),
    Pokemon(51, "Dugtrio", "Ground"),
    Pokemon(52, "Meowth", "Normal"),
    Pokemon(53, "Persian", "Normal"),
    Pokemon(54, "Psyduck", "Water"),
    Pokemon(55, "Golduck", "Water"),
    Pokemon(56, "Mankey", "Fighting"),
    Pokemon(57, "Primeape", "Fighting"),
    Pokemon(58, "Growlithe", "Fire"),
    Pokemon(59, "Arcanine", "Fire"),
    Pokemon(60, "Poliwag", "Water"),
    Pokemon(61, "Poliwhirl", "Water"),
    Pokemon(62, "Poliwrath", "Water/Fighting"),
    Pokemon(63, "Abra", "Psychic"),
    Pokemon(64, "Kadabra", "Psychic"),
    Pokemon(65, "Alakazam", "Psychic"),
    Pokemon(66, "Machop", "Fighting"),
    Pokemon(67, "Machoke", "Fighting"),
    Pokemon(68, "Machamp", "Fighting"),
    Pokemon(69, "Bellsprout", "Grass/Poison"),
    Pokemon(70, "Weepinbell", "Grass/Poison"),
    Pokemon(71, "Victreebel", "Grass/Poison"),
    Pokemon(72, "Tentacool", "Water/Poison"),
    Pokemon(73, "Tentacruel", "Water/Poison"),
    Pokemon(74, "Geodude", "Rock/Ground"),
    Pokemon(75, "Graveler", "Rock/Ground"),
    Pokemon(76, "Golem", "Rock/Ground"),
    Pokemon(77, "Ponyta", "Fire"),
    Pokemon(78, "Rapidash", "Fire"),
    Pokemon(79, "Slowpoke", "Water/Psychic"),
    Pokemon(80, "Slowbro", "Water/Psychic"),
    Pokemon(81, "Magnemite", "Electric/Steel"),
    Pokemon(82, "Magneton", "Electric/Steel"),
    Pokemon(83, "Farfetch'd", "Normal/Flying"),
    Pokemon(84, "Doduo", "Normal/Flying"),
    Pokemon(85, "Dodrio", "Normal/Flying"),
    Pokemon(86, "Seel", "Water"),
    Pokemon(87, "Dewgong", "Water/Ice"),
    Pokemon(88, "Grimer", "Poison"),
    Pokemon(89, "Muk", "Poison"),
    Pokemon(90, "Shellder", "Water"),
    Pokemon(91, "Cloyster", "Water/Ice"),
    Pokemon(92, "Gastly", "Ghost/Poison"),
    Pokemon(93, "Haunter", "Ghost/Poison"),
    Pokemon(94, "Gengar", "Ghost/Poison"),
    Pokemon(95, "Onix", "Rock/Ground"),
    Pokemon(96, "Drowzee", "Psychic"),
    Pokemon(97, "Hypno", "Psychic"),
    Pokemon(98, "Krabby", "Water"),
    Pokemon(99, "Kingler", "Water"),
    Pokemon(100, "Voltorb", "Electric"),
    Pokemon(101, "Electrode", "Electric"),
    Pokemon(102, "Exeggcute", "Grass/Psychic"),
    Pokemon(103, "Exeggutor", "Grass/Psychic"),
    Pokemon(104, "Cubone", "Ground"),
    Pokemon(105, "Marowak", "Ground"),
    Pokemon(106, "Hitmonlee", "Fighting"),
    Pokemon(107, "Hitmonchan", "Fighting"),
    Pokemon(108, "Lickitung", "Normal"),
    Pokemon(109, "Koffing", "Poison"),
    Pokemon(110, "Weezing", "Poison"),
    Pokemon(111, "Rhyhorn", "Ground/Rock"),
    Pokemon(112, "Rhydon", "Ground/Rock"),
    Pokemon(113, "Chansey", "Normal"),
    Pokemon(114, "Tangela", "Grass"),
    Pokemon(115, "Kangaskhan", "Normal"),
    Pokemon(116, "Horsea", "Water"),
    Pokemon(117, "Seadra", "Water"),
    Pokemon(118, "Goldeen", "Water"),
    Pokemon(119, "Seaking", "Water"),
    Pokemon(120, "Staryu", "Water"),
    Pokemon(121, "Starmie", "Water/Psychic"),
    Pokemon(122, "Mr. Mime", "Psychic/Fairy"),
    Pokemon(123, "Scyther", "Bug/Flying"),
    Pokemon(124, "Jynx", "Ice/Psychic"),
    Pokemon(125, "Electabuzz", "Electric"),
    Pokemon(126, "Magmar", "Fire"),
    Pokemon(127, "Pinsir", "Bug"),
    Pokemon(128, "Tauros", "Normal"),
    Pokemon(129, "Magikarp", "Water"),
    Pokemon(130, "Gyarados", "Water/Flying"),
    Pokemon(131, "Lapras", "Water/Ice"),
    Pokemon(132, "Ditto", "Normal"),
    Pokemon(133, "Eevee", "Normal"),
    Pokemon(134, "Vaporeon", "Water"),
    Pokemon(135, "Jolteon", "Electric"),
    Pokemon(136, "Flareon", "Fire"),
    Pokemon(137, "Porygon", "Normal"),
    Pokemon(138, "Omanyte", "Rock/Water"),
    Pokemon(139, "Omastar", "Rock/Water"),
    Pokemon(140, "Kabuto", "Rock/Water"),
    Pokemon(141, "Kabutops", "Rock/Water"),
    Pokemon(142, "Aerodactyl", "Rock/Flying"),
    Pokemon(143, "Snorlax", "Normal"),
    Pokemon(144, "Articuno", "Ice/Flying"),
    Pokemon(145, "Zapdos", "Electric/Flying"),
    Pokemon(146, "Moltres", "Fire/Flying"),
    Pokemon(147, "Dratini", "Dragon"),
    Pokemon(148, "Dragonair", "Dragon"),
    Pokemon(149, "Dragonite", "Dragon/Flying"),
    Pokemon(150, "Mewtwo", "Psychic"),
    Pokemon(151, "Mew", "Psychic")
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
        "steel" -> Color(0xFFB7B7CE)
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
                Text("Pokemon List (Gen 1: 151)", style = MaterialTheme.typography.titleMedium)
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
