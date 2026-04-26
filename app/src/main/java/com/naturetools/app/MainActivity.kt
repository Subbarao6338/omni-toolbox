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

data class Tool(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val category: String,
    val color: Color = Color.Unspecified
)

val tools = listOf(
    // Text
    Tool("Case Converter", Icons.Default.TextFields, "case_converter", "Text", Color(0xFF9C27B0)),
    Tool("Word Counter", Icons.Default.Abc, "word_counter", "Text", Color(0xFFE91E63)),
    Tool("Morse Code", Icons.Default.Language, "morse", "Text", Color(0xFF673AB7)),
    Tool("Base64 Tool", Icons.Default.Code, "base64", "Developer", Color(0xFF3F51B5)),
    Tool("JSON Format", Icons.Default.DataObject, "json", "Developer", Color(0xFF2196F3)),
    Tool("URL Encoder", Icons.Default.Link, "url_encoder", "Developer", Color(0xFF03A9F4)),

    // Network
    Tool("My IP", Icons.Default.Public, "my_ip", "Network", Color(0xFF00BCD4)),
    Tool("Ping", Icons.Default.SettingsEthernet, "ping", "Network", Color(0xFF009688)),

    // Security
    Tool("Password Manager", Icons.Default.Password, "password_manager", "Security", Color(0xFF4CAF50)),
    Tool("QR Generator", Icons.Default.QrCode, "qr_gen", "Utility", Color(0xFF8BC34A)),

    // Graphics
    Tool("Color Picker", Icons.Default.Palette, "color_picker", "Media", Color(0xFFCDDC39)),
    Tool("Gradient Gen", Icons.Default.Gradient, "gradient_gen", "Media", Color(0xFFFFEB3B)),
    Tool("Media Grabber", Icons.Default.Download, "media_grabber", "Media", Color(0xFFFFC107)),

    // Health
    Tool("BMI Calc", Icons.Default.AccessibilityNew, "bmi", "Health", Color(0xFFFF9800)),
    Tool("Step Counter", Icons.Default.DirectionsRun, "step_counter", "Health", Color(0xFFFF5722)),
    Tool("Water Tracker", Icons.Default.LocalDrink, "water", "Health", Color(0xFF795548)),

    // Calculation
    Tool("Calculator", Icons.Default.Calculate, "calculator", "Calculation", Color(0xFF607D8B)),
    Tool("Date Calc", Icons.Default.CalendarToday, "date_calc", "Calculation", Color(0xFF9E9E9E)),
    Tool("Discount Calc", Icons.Default.Percent, "discount", "Calculation", Color(0xFFF44336)),
    Tool("Tip Calc", Icons.Default.Receipt, "tip", "Calculation", Color(0xFFE91E63)),
    Tool("Fuel Cost", Icons.Default.LocalGasStation, "fuel", "Calculation", Color(0xFF9C27B0)),

    // Finance
    Tool("Loan Calculator", Icons.Default.AccountBalance, "loan_calc", "Finance", Color(0xFF673AB7)),
    Tool("Compound Interest", Icons.Default.TrendingUp, "compound_interest", "Finance", Color(0xFF3F51B5)),

    // Sensors
    Tool("Compass", Icons.Default.Explore, "compass", "Sensors", Color(0xFF2196F3)),
    Tool("Level", Icons.Default.Architecture, "level", "Sensors", Color(0xFF03A9F4)),
    Tool("Light Meter", Icons.Default.LightMode, "light", "Sensors", Color(0xFF00BCD4)),
    Tool("Metal Detector", Icons.Default.CompassCalibration, "metal", "Sensors", Color(0xFF009688)),
    Tool("Sensor Data", Icons.Default.SettingsInputComponent, "sensor_data", "Sensors", Color(0xFF4CAF50)),

    // Productivity
    Tool("Note Pad", Icons.Default.NoteAlt, "note", "Productivity", Color(0xFF8BC34A)),
    Tool("Checklist", Icons.Default.Checklist, "checklist", "Productivity", Color(0xFFCDDC39)),

    // Education
    Tool("Periodic Table", Icons.Default.GridOn, "periodic_table", "Education", Color(0xFFFFEB3B)),
    Tool("Pokedex", Icons.Default.CatchingPokemon, "pokedex", "Education", Color(0xFFFFC107)),
    Tool("Prime Checker", Icons.Default.Filter7, "prime", "Education", Color(0xFFFF9800)),

    // System
    Tool("Battery", Icons.Default.BatteryFull, "battery", "System", Color(0xFFFF5722)),
    Tool("CPU Info", Icons.Default.Memory, "cpu_info", "System", Color(0xFF795548)),
    Tool("Device", Icons.Default.Info, "device", "System", Color(0xFF607D8B)),
    Tool("Storage", Icons.Default.Storage, "storage", "System", Color(0xFF9E9E9E)),

    // Utility
    Tool("Flashlight", Icons.Default.FlashlightOn, "flashlight", "Utility", Color(0xFFF44336)),
    Tool("Random Gen", Icons.Default.Casino, "random", "Utility", Color(0xFFE91E63)),
    Tool("Stopwatch", Icons.Default.Timer, "stopwatch", "Utility", Color(0xFF9C27B0)),
    Tool("Clock", Icons.Default.Schedule, "clock", "Utility", Color(0xFF2196F3)),
    Tool("World Clock", Icons.Default.Public, "world_clock", "Utility", Color(0xFF673AB7)),
    Tool("BPM Counter", Icons.Default.Favorite, "bpm", "Utility", Color(0xFF3F51B5)),
    Tool("Unit Converter", Icons.Default.SwapHoriz, "converter", "Conversion", Color(0xFF2196F3)),
    Tool("Currency", Icons.Default.CurrencyExchange, "currency", "Conversion", Color(0xFF03A9F4)),
    Tool("Hub", Icons.Default.Hub, "hub", "Utility", Color(0xFF00BCD4)),
    Tool("Web Search", Icons.Default.Search, "web", "Utility", Color(0xFF009688)),
    Tool("Trim Audio", Icons.Default.ContentCut, "trim_audio", "Audio Editor", Color(0xFFF44336)),
    Tool("Mix Audio", Icons.Default.Merge, "mix_audio", "Audio Editor", Color(0xFF2196F3)),
    Tool("Merge Audio", Icons.Default.Merge, "merge_audio", "Audio Editor", Color(0xFF4CAF50)),
    Tool("Tag Editor", Icons.Default.Label, "tag_editor", "Audio Editor", Color(0xFFFF9800)),
    Tool("Convert Audio", Icons.Default.Transform, "convert_audio", "Audio Editor", Color(0xFF9C27B0)),
    Tool("Record Audio", Icons.Default.Mic, "record_audio", "Audio Editor", Color(0xFFE91E63)),
    Tool("Split Audio", Icons.Default.VerticalSplit, "split_audio", "Audio Editor", Color(0xFF00BCD4)),
    Tool("Reverse Audio", Icons.Default.SettingsBackupRestore, "reverse_audio", "Audio Editor", Color(0xFF795548)),
    Tool("Voice Changer", Icons.Default.Face, "voice_changer", "Audio Editor", Color(0xFF673AB7)),
    Tool("Add SFX", Icons.Default.MusicNote, "add_sfx", "Audio Editor", Color(0xFF3F51B5)),
    Tool("Text To Speech", Icons.Default.RecordVoiceOver, "text_to_speech", "Audio Editor", Color(0xFF009688)),
    Tool("Video To Audio", Icons.Default.VideoLibrary, "video_to_audio", "Audio Editor", Color(0xFFFFC107)),
    Tool("Karaoke Effect", Icons.Default.Mic, "karaoke_effect", "Audio Editor", Color(0xFFCDDC39)),
    Tool("Equalizer", Icons.Default.Equalizer, "equalizer", "Audio Editor", Color(0xFF8BC34A)),
    Tool("Speed Changer", Icons.Default.Speed, "speed_changer", "Audio Editor", Color(0xFF607D8B)),
    Tool("Pitch Changer", Icons.Default.Height, "pitch_changer", "Audio Editor", Color(0xFFFF5722)),
    Tool("Silence Remover", Icons.Default.SpeakerNotesOff, "silence_remover", "Audio Lab", Color(0xFFF44336)),
    Tool("Noise Remover", Icons.Default.MicOff, "noise_remover", "Audio Lab", Color(0xFF2196F3)),
    Tool("Audio Effects", Icons.Default.AutoAwesome, "audio_effects_main", "Audio Lab", Color(0xFF4CAF50)),
    Tool("Vocal Remover", Icons.Default.PersonOff, "vocal_remover", "Audio Lab", Color(0xFFFF9800)),
    Tool("Audio To Video", Icons.Default.Movie, "audio_to_video", "Audio Lab", Color(0xFF9C27B0)),
    Tool("8D Audio", Icons.Default.Speaker, "eight_d_audio", "Audio Lab", Color(0xFFE91E63)),
    Tool("Channel Manipulation", Icons.Default.Tune, "channel_manipulation", "Audio Lab", Color(0xFF00BCD4)),
    Tool("Audio Normalizer", Icons.Default.GraphicEq, "audio_normalizer", "Audio Lab", Color(0xFF795548)),
    Tool("Audio Compressor", Icons.Default.Settings, "audio_compressor", "Audio Lab", Color(0xFF673AB7)),
    Tool("Bass Booster", Icons.Default.Speaker, "bass_booster", "Audio Lab", Color(0xFF3F51B5)),
    Tool("Audio Echo", Icons.Default.SettingsBackupRestore, "audio_echo", "Audio Lab", Color(0xFF009688)),
    Tool("Volume Booster", Icons.Default.VolumeUp, "volume_booster", "Audio Lab", Color(0xFFFFC107)),
    Tool("Fun Record", Icons.Default.Mood, "fun_record", "Audio Lab", Color(0xFFCDDC39)),
    Tool("Wave Generator", Icons.Default.Waves, "wave_generator", "Audio Lab", Color(0xFF8BC34A)),
    Tool("Audio Merger", Icons.Default.Layers, "audio_merger", "Audio Lab", Color(0xFF607D8B)),
    Tool("Silence Generator", Icons.Default.DoNotDisturbOn, "silence_generator", "Audio Lab", Color(0xFFFF5722)),
    Tool("Audio Loop", Icons.Default.Loop, "audio_loop", "Audio Lab", Color(0xFFF44336)),
    Tool("Multi Mix Audio", Icons.Default.LibraryMusic, "multi_mix", "Batch Processing", Color(0xFF2196F3)),
    Tool("Multi Convert", Icons.Default.Refresh, "multi_convert", "Batch Processing", Color(0xFF4CAF50)),
    Tool("Multi Video To Audio", Icons.Default.VideoLibrary, "multi_video_to_audio", "Batch Processing", Color(0xFFFF9800)),
    Tool("Multi Volume Booster", Icons.Default.VolumeUp, "multi_volume_booster", "Batch Processing", Color(0xFF9C27B0)),
    Tool("Text To Speech ", Icons.Default.RecordVoiceOver, "text_to_speech_other", "Other Tools", Color(0xFF009688)),
    Tool("Speech To Text", Icons.Default.Hearing, "speech_to_text", "Other Tools", Color(0xFFE91E63)),
    Tool("Metronome", Icons.Default.Timer, "metronome", "Other Tools", Color(0xFF00BCD4)),
    Tool("Audio Media Info", Icons.Default.Info, "audio_info", "Other Tools", Color(0xFF795548)),
    Tool("Video Media Info", Icons.Default.Movie, "video_info", "Other Tools", Color(0xFF673AB7)),
    Tool("Device Codec", Icons.Default.PermDeviceInformation, "device_codec", "Other Tools", Color(0xFF3F51B5)),
    Tool("Audio Output", Icons.Default.Folder, "audio_output", "Output", Color(0xFF2196F3)),
    Tool("Video Output", Icons.Default.Folder, "video_output", "Output", Color(0xFF4CAF50))
)

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
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, showCategoryCounts) }
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
fun HomeScreen(navController: NavHostController, showCategoryCounts: Boolean) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categoryCounts = remember {
        val counts = tools.groupingBy { it.category }.eachCount()
        counts + ("All" to tools.size)
    }

    val categories = remember {
        listOf("All") + tools.map { it.category }.distinct().sorted()
    }

    val filteredTools = remember(searchQuery, selectedCategory) {
        tools.filter {
            (selectedCategory == "All" || it.category == selectedCategory) &&
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
                    ToolCard(tool) { navController.navigate(tool.route) }
                }
            }
        }
    }
}

@Composable
fun ToolCard(tool: Tool, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
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
