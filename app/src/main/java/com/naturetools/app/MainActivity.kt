package com.naturetools.app

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
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
    LaunchedEffect(intent) {
        intent?.data?.let { uri ->
            if (uri.scheme == "naturetools") {
                // We'll handle this in NatureToolsApp or via a global navigator if possible.
                // For now, let's just make sure the app is aware.
            }
        }
    }

            NatureToolsTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NatureToolsApp(
                intent = intent,
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
    onShowCategoryCountsChange: (Boolean) -> Unit,
    intent: Intent? = null
) {
    val navController = rememberNavController()

    LaunchedEffect(intent) {
        intent?.data?.let { uri ->
            if (uri.scheme == "naturetools") {
                val route = uri.host
                if (route != null) {
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }
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
        composable(
            route = "calculator",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://calculator" })
        ) { CalculatorScreen(navController) }
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
        composable(
            route = "compass",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://compass" })
        ) { CompassScreen(navController) }
        composable("light") { LightMeterScreen(navController) }
        composable("metal") { MetalDetectorScreen(navController) }
        composable(
            route = "note",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://note" })
        ) { NotePadScreen(navController) }
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
        composable(
            route = "hub",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://hub" })
        ) { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "Hub") }
        composable("media_grabber?url={url}", arguments = listOf(navArgument("url") { type = NavType.StringType; nullable = true; defaultValue = null })) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            MediaGrabberScreen(navController, initialUrl = url)
        }
        composable("loan_calc") { LoanCalculatorScreen(navController) }
        composable("compound_interest") { CompoundInterestScreen(navController) }
        composable("water") { WaterTrackerScreen(navController) }
        composable("bmr") { BmrCalculatorScreen(navController) }
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
        composable("ruler") { RulerScreen(navController) }
        composable("altimeter") { AltimeterScreen(navController) }
        composable("barometer") { BarometerScreen(navController) }
        composable("binaural") { BinauralBeatsScreen(navController) }
        composable("area_calc") { AreaCalcScreen(navController) }
        composable("volume_calc") { VolumeCalcScreen(navController) }
        composable("ram_info") { RamInfoScreen(navController) }
        composable(
            route = "qr_scanner",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://qr_scanner" })
        ) { QrScannerScreen(navController) }
        composable("zodiac") { ZodiacFinderScreen(navController) }
        composable("white_noise") { AudioToolScreen(navController, "White Noise") }

        // New Tools Screens
        composable("sci_calc") { ScientificCalculatorScreen(navController) }
        composable("device_id") { AudioToolScreen(navController, "Hardware ID") }
        composable("air_quality") { AudioToolScreen(navController, "Air Quality") }
        composable("uv_index") { AudioToolScreen(navController, "UV Index") }
        composable("habit_tracker") { HabitTrackerScreen(navController) }
        composable("meditation") { MeditationTimerScreen(navController) }
        composable("spl_meter") { SplMeterScreen(navController) }

        // Even more new tools
        composable("data_viz") { AudioToolScreen(navController, "Data Visualizer") }
        composable("ai_image") { AudioToolScreen(navController, "AI Image Gen") }
        composable("base_conv") { BaseConverterScreen(navController) }
        composable("constants") { AudioToolScreen(navController, "Constants Table") }
        composable("light_pollution") { AudioToolScreen(navController, "Light Pollution") }
        composable("tax_calc") { TaxCalculatorScreen(navController) }
        composable("calorie_calc") { CalorieCalculatorScreen(navController) }
        composable("exif_viewer") { ExifViewerScreen(navController) }
        composable("port_scanner") { PortScannerScreen(navController) }
        composable("pomodoro") { PomodoroScreen(navController) }
        composable("morse_decoder") { MorseDecoderScreen(navController) }
        composable("hash_gen") { HashGeneratorScreen(navController) }
        composable("sensors_list") { SensorsListScreen(navController) }
        composable("lorem") { LoremIpsumScreen(navController) }
        composable("vibration") { VibrationTestScreen(navController) }

        // Astronomy
        composable("star_map") { StarMapScreen(navController) }
        composable("constellations") { AudioToolScreen(navController, "Constellations") }

        // Survival
        composable("sos") { EmergencySOSScreen(navController) }
        composable("signal_mirror") { SignalMirrorScreen(navController) }

        // Physics
        composable("physics_formulas") { PhysicsFormulasScreen(navController) }

        // New Additions from ToolProvider expansion
        composable("ai_chat") { AudioToolScreen(navController, "AI Chat") }
        composable("ai_summarizer") { AudioToolScreen(navController, "AI Summarizer") }
        composable("mortgage_calc") { MortgageCalculatorScreen(navController) }
        composable("jwt_tool") { JwtToolScreen(navController) }
        composable("world_map") { WorldMapScreen(navController) }
        composable("moon_phase") { AudioToolScreen(navController, "Moon Phase") }
        composable("sleep_tracker") { AudioToolScreen(navController, "Sleep Tracker") }
        composable("daily_quotes") { DailyQuotesScreen(navController) }
        composable("plant_care") { AudioToolScreen(navController, "Plant Care") }
        composable("image_compress") { AudioToolScreen(navController, "Image Compressor") }
        composable("photo_filters") { AudioToolScreen(navController, "Photo Filters") }
        composable("dns_lookup") { AudioToolScreen(navController, "DNS Lookup") }
        composable("whois") { AudioToolScreen(navController, "Whois") }
        composable("task_board") { AudioToolScreen(navController, "Task Board") }
        composable("time_logger") { AudioToolScreen(navController, "Time Logger") }
        composable("voice_memo") { AudioToolScreen(navController, "Voice Memo") }
        composable("altimeter") { AudioToolScreen(navController, "Altimeter") }
        composable("barometer") { AudioToolScreen(navController, "Barometer") }
        composable("app_permissions") { AudioToolScreen(navController, "App Permissions") }
        composable("privacy_check") { AudioToolScreen(navController, "Privacy Check") }
        composable("app_info") { AudioToolScreen(navController, "App Info") }
        composable("update_check") { AudioToolScreen(navController, "Update Check") }
        composable("anagram") { AudioToolScreen(navController, "Anagram Finder") }
        composable("text_diff") { AudioToolScreen(navController, "Text Diff") }
        composable("ruler") { AudioToolScreen(navController, "Ruler") }
        composable("unit_price") { UnitPriceCalculatorScreen(navController) }
        composable("video_stabilizer") { AudioToolScreen(navController, "Video Stabilizer", "video/*") }
        composable("multi_image_resize") { AudioToolScreen(navController, "Multi Image Resize") }
        composable("currency_trends") { AudioToolScreen(navController, "Currency Trends") }

        // Audio Tools
        composable("m_audio_editor") { AudioToolScreen(navController, "Audio Editor") }
        composable("m_audio_cutter") { AudioToolScreen(navController, "Audio Cutter") }
        composable("m_audio_joiner") { AudioToolScreen(navController, "Audio Joiner") }
        composable("m_audio_mixer") { AudioToolScreen(navController, "Audio Mixer") }
        composable("m_audio_tag_editor") { AudioToolScreen(navController, "Audio Tag Editor") }
        composable("m_audio_compressor") { AudioToolScreen(navController, "Audio Compressor") }
        composable("m_audio_splitter") { AudioToolScreen(navController, "Audio Splitter") }
        composable("m_audio_normalizer") { AudioToolScreen(navController, "Audio Normalizer") }
        composable("m_volume_booster") { AudioToolScreen(navController, "Volume Booster") }
        composable("m_speed_changer") { AudioToolScreen(navController, "Speed Changer") }
        composable("m_audio_pitch") { AudioToolScreen(navController, "Pitch Changer") }
        composable("m_reverse_audio") { AudioToolScreen(navController, "Reverse Audio") }
        composable("m_bass_booster") { AudioToolScreen(navController, "Bass Booster") }
        composable("m_echo_effect") { AudioToolScreen(navController, "Echo Effect") }
        composable("m_3d_audio") { AudioToolScreen(navController, "3D Audio") }
        composable("m_audio_pan") { AudioToolScreen(navController, "Audio Pan") }
        composable("m_equalizer") { AudioToolScreen(navController, "Equalizer") }
        composable("m_mute_audio") { AudioToolScreen(navController, "Mute Audio") }
        composable("m_silence_remover") { AudioToolScreen(navController, "Silence Remover") }
        composable("m_voice_changer") { AudioToolScreen(navController, "Voice Changer") }
        composable("m_karaoke_maker") { AudioToolScreen(navController, "Karaoke Maker") }
        composable("m_ringtone_maker") { AudioToolScreen(navController, "Ringtone Maker") }
        composable("m_text_to_speech") { AudioToolScreen(navController, "Text to Speech") }
        composable("m_speech_to_text") { AudioToolScreen(navController, "Speech to Text") }
        composable("noise_generator") { AudioToolScreen(navController, "Noise Generator") }
        composable("wave_generator") { AudioToolScreen(navController, "Wave Generator") }
        composable("silence_generator") { AudioToolScreen(navController, "Silence Generator") }
        composable("audio_loop") { AudioToolScreen(navController, "Audio Loop") }
        composable("sound_mastering") { AudioToolScreen(navController, "Sound Mastering") }
        composable("add_sfx") { AudioToolScreen(navController, "Add SFX") }

        // Video Tools
        composable("video_trim") { AudioToolScreen(navController, "Video Editor", "video/*") }
        composable("m_video_to_audio") { AudioToolScreen(navController, "Video to Audio", "video/*") }
        composable("video_compress") { AudioToolScreen(navController, "Video Compressor", "video/*") }
        composable("video_reverse") { AudioToolScreen(navController, "Reverse Video", "video/*") }
        composable("video_splitter") { AudioToolScreen(navController, "Video Splitter", "video/*") }
        composable("mix_video_audio") { AudioToolScreen(navController, "Mix Video Audio", "video/*") }
        composable("video_speed_changer") { AudioToolScreen(navController, "Video Speed", "video/*") }
        composable("video_sfx") { AudioToolScreen(navController, "Video SFX", "video/*") }
        composable("video_to_gif") { AudioToolScreen(navController, "Video To GIF", "video/*") }
        composable("video_volume_booster") { AudioToolScreen(navController, "Video Volume", "video/*") }
        composable("video_delete") { AudioToolScreen(navController, "Delete Segment", "video/*") }
        composable("video_silence") { AudioToolScreen(navController, "Silence Video", "video/*") }
        composable("video_loop") { AudioToolScreen(navController, "Loop Video", "video/*") }

        // AI Tools
        composable("vocal_remover") { AudioToolScreen(navController, "Vocal Remover") }
        composable("ai_stems_splitter") { AudioToolScreen(navController, "AI Stems Splitter") }
        composable("vocal_autotuner") { AudioToolScreen(navController, "Vocal AutoTuner") }
        composable("ai_noise_remover") { AudioToolScreen(navController, "AI Noise Remover") }
        composable("echo_remover") { AudioToolScreen(navController, "Echo Remover") }
        composable("reverb_remover") { AudioToolScreen(navController, "Reverb Remover") }
        composable("key_bpm_finder") { AudioToolScreen(navController, "Key BPM Finder") }
        composable("audio_noise_remover") { AudioToolScreen(navController, "Audio Noise Remover") }
        composable("video_noise_remover") { AudioToolScreen(navController, "Video Noise Remover", "video/*") }

        // Recording Tools
        composable("record_audio") { AudioToolScreen(navController, "Record Audio") }
        composable("fun_record") { AudioToolScreen(navController, "Fun Recording") }
        composable("karaoke_effect") { AudioToolScreen(navController, "Karaoke Effect") }

        // Batch Processing
        composable("multi_mix") { AudioToolScreen(navController, "Multi Mix Audio") }
        composable("multi_convert") { AudioToolScreen(navController, "Multi Convert") }
        composable("multi_video_to_audio") { AudioToolScreen(navController, "Multi Video To Audio", "video/*") }
        composable("multi_volume_booster") { AudioToolScreen(navController, "Multi Volume Booster") }

        // Other Tools
        composable(
            route = "metronome",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://metronome" })
        ) { MetronomeScreen(navController) }
        composable("audio_info") { AudioToolScreen(navController, "Audio Info") }
        composable("video_info") { AudioToolScreen(navController, "Video Info") }
        composable("device_codec") { AudioToolScreen(navController, "Device Codec") }

        // Output
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
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

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

            AnimatedContent(
                targetState = filteredTools,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "tools_grid"
            ) { tools ->
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tools, key = { it.route }) { tool ->
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
}

@Composable
fun ToolCard(
    tool: Tool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "tool_card_scale"
    )

    ElevatedCard(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (tool.badge != com.naturetools.app.model.BadgeType.NONE) {
                val badgeIcon = when (tool.badge) {
                    com.naturetools.app.model.BadgeType.NEW -> Icons.Default.CardGiftcard
                    com.naturetools.app.model.BadgeType.PREMIUM -> Icons.Default.WorkspacePremium
                    else -> Icons.Default.Star
                }
                Icon(
                    badgeIcon,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp).size(14.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
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
