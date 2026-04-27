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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.saveable.rememberSaveable
import com.naturetools.app.model.Tool
import com.naturetools.app.model.ToolProvider
import com.naturetools.app.ui.screens.*
import com.naturetools.app.ui.screens.AudioToolScreen
import com.naturetools.app.ui.theme.NatureToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        setContent {
            var themeMode by rememberSaveable { mutableStateOf(prefs.getString("theme_mode", "system") ?: "system") }
            var dynamicColor by rememberSaveable { mutableStateOf(prefs.getBoolean("dynamic_color", true)) }
            var showCategoryCounts by rememberSaveable { mutableStateOf(prefs.getBoolean("show_category_counts", true)) }
            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            NatureToolsTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
                        },
                        showCategoryCounts = showCategoryCounts,
                        onShowCategoryCountsChange = {
                            showCategoryCounts = it
                            prefs.edit().putBoolean("show_category_counts", it).apply()
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun NatureToolsApp(
    themeMode: String,
    onThemeChange: (String) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    showCategoryCounts: Boolean,
    onShowCategoryCountsChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var favorites by remember {
        mutableStateOf(prefs.getStringSet("favorites", emptySet<String>()) ?: emptySet<String>())
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController,
                showCategoryCounts,
                favorites,
                onToggleFavorite = { route ->
                    val newFavorites = if (favorites.contains(route)) {
                        favorites - route
                    } else {
                        favorites + route
                    }
                    favorites = newFavorites
                    prefs.edit().putStringSet("favorites", newFavorites).apply()
                }
            )
        }
        composable("settings") { SettingsScreen(navController, themeMode, onThemeChange, dynamicColor, onDynamicColorChange, showCategoryCounts, onShowCategoryCountsChange) }
        composable("converter") { UnitConverterScreen(navController) }
        composable("currency") { CurrencyConverterScreen(navController) }
        composable("calculator") { CalculatorScreen(navController) }
        composable("bmi") { BMICalculatorScreen(navController) }
        composable("tip") { TipCalculatorScreen(navController) }
        composable("discount") { DiscountCalculatorScreen(navController) }
        composable("web?url={url}&showBar={showBar}&title={title}", arguments = listOf(
            navArgument("url") { type = NavType.StringType; nullable = true; defaultValue = null },
            navArgument("showBar") { type = NavType.BoolType; defaultValue = true },
            navArgument("title") { type = NavType.StringType; defaultValue = "Web Search" }
        )) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            val showBar = backStackEntry.arguments?.getBoolean("showBar") ?: true
            val title = backStackEntry.arguments?.getString("title") ?: "Web Search"
            WebToolScreen(navController, initialUrl = url, showUrlBar = showBar, title = title)
        }
        composable("compass") { CompassScreen(navController) }
        composable("light") { LightMeterScreen(navController) }
        composable("metal") { MetalDetectorScreen(navController) }
        composable("note") { NotePadScreen(navController) }
        composable("checklist") { ChecklistScreen(navController) }
        composable("level") { LevelScreen(navController) }
        composable("flashlight") { FlashlightScreen(navController) }
        composable("stopwatch") { StopwatchScreen(navController) }
        composable("clock") { ClockScreen(navController) }
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
        composable("date_calc") { DateCalculatorScreen(navController) }
        composable("bpm") { BpmCounterScreen(navController) }
        composable("fuel") { FuelCostCalculatorScreen(navController) }
        composable("hub") { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "Hub") }
        composable("media_grabber?url={url}", arguments = listOf(navArgument("url") { type = NavType.StringType; nullable = true; defaultValue = null })) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            MediaGrabberScreen(navController, initialUrl = url)
        }
        composable("loan_calc") { LoanCalculatorScreen(navController) }
        composable("compound_interest") { CompoundInterestScreen(navController) }
        composable("water") { WaterTrackerScreen(navController) }
        composable("step_counter") { StepCounterScreen(navController) }
        composable("qr_gen") { QrGeneratorScreen(navController) }
        composable("cpu_info") { CpuInfoScreen(navController) }
        composable("regex_tester") { RegexTesterScreen(navController) }
        composable("markdown_preview") { MarkdownPreviewScreen(navController) }
        composable("network_info") { NetworkInfoScreen(navController) }
        composable("case_converter") { CaseConverterScreen(navController) }
        composable("word_counter") { WordCounterScreen(navController) }
        composable("my_ip") { MyIPScreen(navController) }
        composable("ping") { PingScreen(navController) }
        composable("password_manager") { PasswordManagerScreen(navController) }
        composable("gradient_gen") { GradientGeneratorScreen(navController) }
        composable("trim_audio") { AudioToolScreen(navController, "Trim Audio") }
        composable("mix_audio") { AudioToolScreen(navController, "Mix Audio") }
        composable("merge_audio") { AudioToolScreen(navController, "Merge Audio") }
        composable("tag_editor") { AudioToolScreen(navController, "Tag Editor") }
        composable("convert_audio") { AudioToolScreen(navController, "Convert Audio") }
        composable("record_audio") { AudioToolScreen(navController, "Record Audio") }
        composable("split_audio") { AudioToolScreen(navController, "Split Audio") }
        composable("reverse_audio") { AudioToolScreen(navController, "Reverse Audio") }
        composable("voice_changer") { AudioToolScreen(navController, "Voice Changer") }
        composable("add_sfx") { AudioToolScreen(navController, "Add SFX") }
        composable("text_to_speech") { AudioToolScreen(navController, "Text To Speech") }
        composable("video_to_audio") { AudioToolScreen(navController, "Video To Audio") }
        composable("karaoke_effect") { AudioToolScreen(navController, "Karaoke Effect") }
        composable("equalizer") { AudioToolScreen(navController, "Equalizer") }
        composable("speed_changer") { AudioToolScreen(navController, "Speed Changer") }
        composable("pitch_changer") { AudioToolScreen(navController, "Pitch Changer") }
        composable("silence_remover") { AudioToolScreen(navController, "Silence Remover") }
        composable("noise_remover") { AudioToolScreen(navController, "Noise Remover") }
        composable("audio_effects_main") { AudioToolScreen(navController, "Audio Effects") }
        composable("vocal_remover") { AudioToolScreen(navController, "Vocal Remover") }
        composable("audio_to_video") { AudioToolScreen(navController, "Audio To Video") }
        composable("eight_d_audio") { AudioToolScreen(navController, "8D Audio") }
        composable("channel_manipulation") { AudioToolScreen(navController, "Channel Manipulation") }
        composable("audio_normalizer") { AudioToolScreen(navController, "Audio Normalizer") }
        composable("audio_compressor") { AudioToolScreen(navController, "Audio Compressor") }
        composable("bass_booster") { AudioToolScreen(navController, "Bass Booster") }
        composable("audio_echo") { AudioToolScreen(navController, "Audio Echo") }
        composable("volume_booster") { AudioToolScreen(navController, "Volume Booster") }
        composable("fun_record") { AudioToolScreen(navController, "Fun Record") }
        composable("wave_generator") { AudioToolScreen(navController, "Wave Generator") }
        composable("audio_merger") { AudioToolScreen(navController, "Audio Merger") }
        composable("silence_generator") { AudioToolScreen(navController, "Silence Generator") }
        composable("audio_loop") { AudioToolScreen(navController, "Audio Loop") }
        composable("multi_mix") { AudioToolScreen(navController, "Multi Mix Audio") }
        composable("multi_convert") { AudioToolScreen(navController, "Multi Convert") }
        composable("multi_video_to_audio") { AudioToolScreen(navController, "Multi Video To Audio") }
        composable("multi_volume_booster") { AudioToolScreen(navController, "Multi Volume Booster") }
        composable("text_to_speech_other") { AudioToolScreen(navController, "Text To Speech ") }
        composable("speech_to_text") { AudioToolScreen(navController, "Speech To Text") }
        composable("metronome") { AudioToolScreen(navController, "Metronome") }
        composable("audio_info") { AudioToolScreen(navController, "Audio Media Info") }
        composable("video_info") { AudioToolScreen(navController, "Video Media Info") }
        composable("device_codec") { AudioToolScreen(navController, "Device Codec") }
        composable("audio_output") { AudioToolScreen(navController, "Audio Output") }
        composable("video_output") { AudioToolScreen(navController, "Video Output") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    showCategoryCounts: Boolean,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categoryCounts = remember(favorites) {
        val counts = ToolProvider.tools.groupingBy { it.category }.eachCount()
        counts + ("All" to ToolProvider.tools.size) + ("Favorites" to favorites.size)
    }

    val categories = remember {
        listOf("All", "Favorites") + ToolProvider.tools.map { it.category }.distinct().sorted()
    }

    val filteredTools = remember(searchQuery, selectedCategory, favorites) {
        ToolProvider.tools.filter {
            (selectedCategory == "All" || (selectedCategory == "Favorites" && favorites.contains(it.route)) || it.category == selectedCategory) &&
            (it.name.contains(searchQuery, ignoreCase = true))
        }.sortedBy { it.name }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nature Tools", fontWeight = FontWeight.Bold) },
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {}

            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                edgePadding = 16.dp,
                divider = {},
                indicator = {},
                containerColor = Color.Transparent
            ) {
                categories.forEach { category ->
                    val count = categoryCounts[category] ?: 0
                    val label = if (showCategoryCounts) "$category ($count)" else category
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(label) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        shape = CircleShape
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredTools) { tool ->
                    ToolCard(
                        tool = tool,
                        isFavorite = favorites.contains(tool.route),
                        onToggleFavorite = { onToggleFavorite(tool.route) },
                        onClick = { navController.navigate(tool.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: Tool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp).padding(4.dp)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    modifier = Modifier.size(16.dp),
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = tool.color.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            tool.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = tool.color
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    tool.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
