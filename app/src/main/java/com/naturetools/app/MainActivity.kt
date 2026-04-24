package com.naturetools.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import com.naturetools.app.ui.screens.*
import com.naturetools.app.ui.theme.NatureToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by rememberSaveable { mutableStateOf("system") }
            var dynamicColor by rememberSaveable { mutableStateOf(true) }

            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            NatureToolsTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NatureToolsApp(
                        themeMode = themeMode,
                        onThemeChange = { themeMode = it },
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = { dynamicColor = it }
                    )
                }
            }
        }
    }
}

data class Tool(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val category: String
)

val tools = listOf(
    Tool("Unit Converter", Icons.Default.SwapHoriz, "converter", "Conversion"),
    Tool("Calculator", Icons.Default.Calculate, "calculator", "Calculation"),
    Tool("BMI Calc", Icons.Default.AccessibilityNew, "bmi", "Calculation"),
    Tool("Tip Calc", Icons.Default.Receipt, "tip", "Calculation"),
    Tool("Web Search", Icons.Default.Search, "web", "Utility"),
    Tool("Compass", Icons.Default.Explore, "compass", "Sensors"),
    Tool("Light Meter", Icons.Default.LightMode, "light", "Sensors"),
    Tool("Metal Detector", Icons.Default.CompassCalibration, "metal", "Sensors"),
    Tool("Level", Icons.Default.Architecture, "level", "Sensors"),
    Tool("Note Pad", Icons.Default.NoteAlt, "note", "Productivity"),
    Tool("Flashlight", Icons.Default.FlashlightOn, "flashlight", "Utility"),
    Tool("Stopwatch", Icons.Default.Timer, "stopwatch", "Utility"),
    Tool("Battery", Icons.Default.BatteryFull, "battery", "System"),
    Tool("Device", Icons.Default.Info, "device", "System"),
    Tool("Prime Checker", Icons.Default.Filter7, "prime", "Science"),
    Tool("Random Gen", Icons.Default.Casino, "random", "Utility"),
    Tool("Periodic Table", Icons.Default.GridOn, "periodic_table", "Science"),
    Tool("Pokedex", Icons.Default.CatchingPokemon, "pokedex", "Science"),
    Tool("Base64 Tool", Icons.Default.Code, "base64", "Developer")
)

@Composable
fun NatureToolsApp(
    themeMode: String,
    onThemeChange: (String) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                dynamicColor = dynamicColor,
                onDynamicColorChange = onDynamicColorChange
            )
        }
        composable("converter") { UnitConverterScreen(navController) }
        composable("calculator") { CalculatorScreen(navController) }
        composable("bmi") { BMICalculatorScreen(navController) }
        composable("tip") { TipCalculatorScreen(navController) }
        composable("web") { WebToolScreen(navController) }
        composable("compass") { CompassScreen(navController) }
        composable("light") { LightMeterScreen(navController) }
        composable("metal") { MetalDetectorScreen(navController) }
        composable("note") { NotePadScreen(navController) }
        composable("level") { LevelScreen(navController) }
        composable("flashlight") { FlashlightScreen(navController) }
        composable("stopwatch") { StopwatchScreen(navController) }
        composable("battery") { BatteryScreen(navController) }
        composable("device") { DeviceScreen(navController) }
        composable("prime") { PrimeCheckerScreen(navController) }
        composable("random") { RandomGeneratorScreen(navController) }
        composable("periodic_table") { PeriodicTableScreen(navController) }
        composable("pokedex") { PokedexScreen(navController) }
        composable("base64") { Base64Screen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + tools.map { it.category }.distinct().sorted()

    val filteredTools = tools.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (it.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nature Tools") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { },
                active = false,
                onActiveChange = { },
                placeholder = { Text("Search tools...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {}

            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 16.dp,
                divider = {},
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTools) { tool ->
                    ElevatedCard(
                        onClick = { navController.navigate(tool.route) },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                tool.icon,
                                contentDescription = tool.name,
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                tool.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                tool.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
