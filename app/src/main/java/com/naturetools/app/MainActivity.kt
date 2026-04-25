package com.naturetools.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import com.naturetools.app.ui.screens.*
import com.naturetools.app.ui.theme.NatureToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        setContent {
            var themeMode by rememberSaveable {
                mutableStateOf(prefs.getString("theme_mode", "system") ?: "system")
            }
            var dynamicColor by rememberSaveable {
                mutableStateOf(prefs.getBoolean("dynamic_color", true))
            }

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
                        onThemeChange = {
                            themeMode = it
                            prefs.edit().putString("theme_mode", it).apply()
                        },
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = {
                            dynamicColor = it
                            prefs.edit().putBoolean("dynamic_color", it).apply()
                        }
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
    Tool("Currency", Icons.Default.CurrencyExchange, "currency", "Conversion"),
    Tool("Calculator", Icons.Default.Calculate, "calculator", "Calculation"),
    Tool("BMI Calc", Icons.Default.AccessibilityNew, "bmi", "Calculation"),
    Tool("Tip Calc", Icons.Default.Receipt, "tip", "Calculation"),
    Tool("Discount Calc", Icons.Default.Percent, "discount", "Calculation"),
    Tool("Web Search", Icons.Default.Search, "web", "Utility"),
    Tool("Compass", Icons.Default.Explore, "compass", "Sensors"),
    Tool("Light Meter", Icons.Default.LightMode, "light", "Sensors"),
    Tool("Metal Detector", Icons.Default.CompassCalibration, "metal", "Sensors"),
    Tool("Level", Icons.Default.Architecture, "level", "Sensors"),
    Tool("Sensor Data", Icons.Default.SettingsInputComponent, "sensor_data", "Sensors"),
    Tool("Note Pad", Icons.Default.NoteAlt, "note", "Productivity"),
    Tool("Checklist", Icons.Default.Checklist, "checklist", "Productivity"),
    Tool("Flashlight", Icons.Default.FlashlightOn, "flashlight", "Utility"),
    Tool("Stopwatch", Icons.Default.Timer, "stopwatch", "Utility"),
    Tool("World Clock", Icons.Default.Public, "world_clock", "Utility"),
    Tool("Battery", Icons.Default.BatteryFull, "battery", "System"),
    Tool("Device", Icons.Default.Info, "device", "System"),
    Tool("Storage", Icons.Default.Storage, "storage", "System"),
    Tool("Prime Checker", Icons.Default.Filter7, "prime", "Science"),
    Tool("Random Gen", Icons.Default.Casino, "random", "Utility"),
    Tool("Periodic Table", Icons.Default.GridOn, "periodic_table", "Science"),
    Tool("Pokedex", Icons.Default.CatchingPokemon, "pokedex", "Science"),
    Tool("Morse Code", Icons.Default.Language, "morse", "Science"),
    Tool("Base64 Tool", Icons.Default.Code, "base64", "Developer"),
    Tool("JSON Format", Icons.Default.DataObject, "json", "Developer"),
    Tool("URL Encoder", Icons.Default.Link, "url_encoder", "Developer"),
    Tool("Color Picker", Icons.Default.Palette, "color_picker", "Media")
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
        composable("currency") { CurrencyConverterScreen(navController) }
        composable("calculator") { CalculatorScreen(navController) }
        composable("bmi") { BMICalculatorScreen(navController) }
        composable("tip") { TipCalculatorScreen(navController) }
        composable("discount") { DiscountCalculatorScreen(navController) }
        composable(
            "web?url={url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            WebToolScreen(navController, initialUrl = url)
        }
        composable("compass") { CompassScreen(navController) }
        composable("light") { LightMeterScreen(navController) }
        composable("metal") { MetalDetectorScreen(navController) }
        composable("note") { NotePadScreen(navController) }
        composable("checklist") { ChecklistScreen(navController) }
        composable("level") { LevelScreen(navController) }
        composable("flashlight") { FlashlightScreen(navController) }
        composable("stopwatch") { StopwatchScreen(navController) }
        composable("world_clock") { WorldClockScreen(navController) }
        composable("battery") { BatteryScreen(navController) }
        composable("device") { DeviceScreen(navController) }
        composable("storage") { StorageScreen(navController) }
        composable("prime") { PrimeCheckerScreen(navController) }
        composable("random") { RandomGeneratorScreen(navController) }
        composable("periodic_table") { PeriodicTableScreen(navController) }
        composable("pokedex") { PokedexScreen(navController) }
        composable("morse") { MorseCodeScreen(navController) }
        composable("base64") { Base64Screen(navController) }
        composable("json") { JsonFormatterScreen(navController) }
        composable("url_encoder") { UrlEncoderScreen(navController) }
        composable("sensor_data") { SensorDataScreen(navController) }
        composable("color_picker") { ColorPickerScreen(navController) }
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
                        modifier = Modifier.padding(horizontal = 4.dp),
                        shape = CircleShape
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTools) { tool ->
                    ElevatedCard(
                        onClick = { navController.navigate(tool.route) },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                tool.icon,
                                contentDescription = tool.name,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                tool.name,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
