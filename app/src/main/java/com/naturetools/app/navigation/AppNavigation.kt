package com.naturetools.app.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.naturetools.app.model.ToolProvider
import com.naturetools.app.ui.screens.HomeScreen
import com.naturetools.app.ui.screens.ai.*
import com.naturetools.app.ui.screens.astronomy.*
import com.naturetools.app.ui.screens.audio.*
import com.naturetools.app.ui.screens.calculation.*
import com.naturetools.app.ui.screens.conversion.*
import com.naturetools.app.ui.screens.developer.*
import com.naturetools.app.ui.screens.health.*
import com.naturetools.app.ui.screens.image.*
import com.naturetools.app.ui.screens.lifestyle.*
import com.naturetools.app.ui.screens.media.*
import com.naturetools.app.ui.screens.network.*
import com.naturetools.app.ui.screens.physics.*
import com.naturetools.app.ui.screens.productivity.*
import com.naturetools.app.ui.screens.science.*
import com.naturetools.app.ui.screens.sensor.*
import com.naturetools.app.ui.screens.survival.*
import com.naturetools.app.ui.screens.system.*
import com.naturetools.app.ui.screens.text.*
import com.naturetools.app.ui.screens.utility.*
import com.naturetools.app.ui.screens.engineering.*
import com.naturetools.app.ui.screens.photography.*
import com.naturetools.app.ui.screens.music.*
import com.naturetools.app.ui.screens.outdoor.*
import com.naturetools.app.ui.screens.environment.*
import com.naturetools.app.ui.screens.automotive.*
import com.naturetools.app.ui.screens.social.*
import com.naturetools.app.ui.screens.electronics.*

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
        intent?.getStringExtra("route")?.let { route ->
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
        intent?.data?.let { uri ->
            if (uri.scheme == "naturetools") {
                val host = uri.host
                val path = uri.path
                val route = if (path != null && path.length > 1) "$host$path" else host
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

    val toggleFavorite: (String) -> Unit = { route ->
        val newFavorites = if (favorites.contains(route)) {
            favorites - route
        } else {
            favorites + route
        }
        favorites = newFavorites
        prefs.edit().putStringSet("favorites", newFavorites).apply()
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController,
                showCategoryCounts,
                favorites,
                onToggleFavorite = toggleFavorite
            )
        }
        composable("settings") { SettingsScreen(navController, themeMode, onThemeChange, dynamicColor, onDynamicColorChange, showCategoryCounts, onShowCategoryCountsChange) }

        // Special routes with arguments or deep links
        composable(
            route = "calculator",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://calculator" })
        ) { CalculatorScreen(navController) }

        composable(
            route = "compass",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://compass" })
        ) { CompassScreen(navController) }

        composable(
            route = "note",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://note" })
        ) { NotePadScreen(navController) }

        composable(
            route = "hub",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://hub" })
        ) { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "Hub") }

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
            route = "media_grabber?url={url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType; nullable = true; defaultValue = null }),
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://media_grabber?url={url}" })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            MediaGrabberScreen(navController, initialUrl = url)
        }

        composable(
            route = "qr_scanner",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://qr_scanner" })
        ) { QrScannerScreen(navController) }

        composable(
            route = "sos",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://sos" })
        ) { EmergencySOSScreen(navController) }

        composable(
            route = "metronome",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://metronome" })
        ) { MetronomeScreen(navController) }

        // Group Tools mapping
        ToolProvider.tools.filter { it.subToolRoutes != null }.forEach { groupTool ->
            composable(groupTool.route) {
                if (groupTool.route == "perchance_tools") {
                    PerchanceHubScreen(navController, favorites, toggleFavorite)
                } else {
                    ToolGroupScreen(navController, groupTool.route, favorites, toggleFavorite)
                }
            }
        }

        // Generic and specific mappings for individual tools
        ToolProvider.tools.filter { it.subToolRoutes == null }.forEach { tool ->
            // Skip routes already handled by special cases above
            if (listOf("calculator", "compass", "note", "hub", "qr_scanner", "sos", "metronome").contains(tool.route)) return@forEach

            composable(tool.route) {
                when (tool.category) {
                    "Smart Utilities" -> {
                        if (listOf("ai_chat", "ai_summarizer", "ai_code", "ai_grammar", "ai_obj_detect", "ai_sentiment", "ai_text_ext", "ai_translate", "csv_to_json").contains(tool.route)) {
                            ChatToolScreen(navController, tool.name)
                        } else if (tool.route == "data_viz") {
                            DataVisualizerScreen(navController)
                        } else if (tool.route == "markdown_preview") {
                            MarkdownPreviewScreen(navController)
                        } else if (tool.route == "ai_image") {
                            ImageGeneratorScreen(navController)
                        } else if (tool.route == "regex_tester") {
                            RegexTesterScreen(navController)
                        } else {
                            AudioToolScreen(navController, tool.name)
                        }
                    }
                    "Audio & Music" -> {
                        if (tool.route == "guitar_tuner") MusicToolScreen(navController, tool.name)
                        else if (tool.route == "chord_lib") MusicToolScreen(navController, tool.name)
                        else if (tool.route == "voice_memo") VoiceMemoScreen(navController)
                        else AudioToolScreen(navController, tool.name)
                    }
                    "Video & Media" -> {
                        if (tool.route.startsWith("per_")) {
                            WebToolScreen(navController, initialUrl = when(tool.route) {
                                "per_hub" -> "https://perchance.org/welcome"
                                "per_image" -> "https://perchance.org/ai-image-generator"
                                "per_story" -> "https://perchance.org/ai-story-generator"
                                "per_character" -> "https://perchance.org/ai-character-generator"
                                else -> "https://perchance.org"
                            }, showUrlBar = false, title = tool.name)
                        } else if (tool.route == "media_grabber" || tool.route == "media_grab_pro") {
                            MediaGrabberScreen(navController)
                        } else {
                            AudioToolScreen(navController, tool.name, mimeType = "video/*")
                        }
                    }
                    "Image Tools" -> {
                        if (tool.route == "gradient_gen") GradientGeneratorScreen(navController)
                        else if (tool.route == "exif_viewer") ExifViewerScreen(navController)
                        else ImageToolScreen(navController, tool.name)
                    }
                    "Files & Documents" -> {
                        FileToolScreen(navController, tool.name)
                    }
                    "Finance & Crypto" -> {
                        if (listOf("compound_interest", "compound_pro").contains(tool.route)) CompoundInterestScreen(navController)
                        else if (listOf("loan_calc", "dividend_calc", "inflation_calc", "roi_calc", "salary_calc", "stock_profit", "expense_tracker").contains(tool.route)) LoanCalculatorScreen(navController)
                        else if (tool.route == "crypto_conv") CurrencyConverterScreen(navController)
                        else FinanceToolScreen(navController, tool.name)
                    }
                    "Health & Lifestyle" -> {
                        if (tool.route == "water") WaterTrackerScreen(navController)
                        else if (tool.route == "step_counter") StepCounterScreen(navController)
                        else if (tool.route == "bmi") BMICalculatorScreen(navController)
                        else if (tool.route == "bmr") BmrCalculatorScreen(navController)
                        else if (tool.route == "habit_tracker") HabitTrackerScreen(navController)
                        else if (tool.route == "meditation") MeditationTimerScreen(navController)
                        else if (tool.route == "daily_journal") NotePadScreen(navController)
                        else if (tool.route == "daily_quotes") DailyQuotesScreen(navController)
                        else if (tool.route == "plant_care") PlantCareScreen(navController)
                        else HealthScreen(navController, tool.name)
                    }
                    "Science & Education" -> {
                        if (tool.route == "periodic_table") PeriodicTableScreen(navController)
                        else if (tool.route == "pokedex") PokedexScreen(navController)
                        else if (tool.route == "constants") ConstantsTableScreen(navController)
                        else if (tool.route == "world_map") WorldMapScreen(navController)
                        else if (tool.route == "physics_formulas") PhysicsFormulasScreen(navController)
                        else if (tool.route == "star_map") StarMapScreen(navController)
                        else if (tool.route == "prime") PrimeCheckerScreen(navController)
                        else if (listOf("matrix_calc", "eq_solver", "fraction_calc", "truth_table", "stats_pro").contains(tool.route)) MathToolScreen(navController, tool.name)
                        else EngineeringToolScreen(navController, tool.name)
                    }
                    "Engineering" -> {
                        if (listOf("ohms_law", "circuit_calc").contains(tool.route)) ElectronicsToolScreen(navController, tool.name)
                        else EngineeringToolScreen(navController, tool.name)
                    }
                    "Outdoor & Nature" -> {
                        if (tool.route == "signal_mirror") SignalMirrorScreen(navController)
                        else if (tool.route == "altitude_graph") SensorDataScreen(navController)
                        else if (listOf("air_quality", "uv_index", "light_pollution", "moon_phase", "rain_radar").contains(tool.route)) EnvironmentToolScreen(navController, tool.name)
                        else OutdoorToolScreen(navController, tool.name)
                    }
                    "System & Sensors" -> {
                        if (tool.route == "battery") BatteryScreen(navController)
                        else if (tool.route == "cpu_info") CpuInfoScreen(navController)
                        else if (tool.route == "device") DeviceScreen(navController)
                        else if (tool.route == "storage") StorageScreen(navController)
                        else if (tool.route == "app_info") AppInfoScreen(navController)
                        else if (tool.route == "sensors_list") SensorsListScreen(navController)
                        else if (tool.route == "ram_info") RamInfoScreen(navController)
                        else if (listOf("spl_meter", "gforce_meter", "sensor_data").contains(tool.route)) SensorDataScreen(navController)
                        else if (listOf("light", "metal", "altimeter", "barometer", "level").contains(tool.route)) AltimeterScreen(navController) // Uses generic sensor handler
                        else SystemLabScreen(navController, tool.name)
                    }
                    "Productivity" -> {
                        if (tool.route == "checklist") ChecklistScreen(navController)
                        else if (tool.route == "pomodoro") PomodoroScreen(navController)
                        else if (tool.route == "task_board" || tool.route == "kanban") TaskBoardScreen(navController)
                        else if (tool.route == "time_logger") TimeLoggerScreen(navController)
                        else if (tool.route == "password_manager") PasswordManagerScreen(navController)
                        else NotePadScreen(navController)
                    }
                    "Developer" -> {
                        if (tool.route == "json") JsonFormatterScreen(navController)
                        else if (tool.route == "url_encoder") UrlEncoderScreen(navController)
                        else if (tool.route == "base64") Base64Screen(navController)
                        else if (tool.route == "jwt_tool") JwtToolScreen(navController)
                        else DeveloperExpertScreen(navController, tool.name)
                    }
                    "Text" -> {
                        if (tool.route == "word_counter") WordCounterScreen(navController)
                        else if (tool.route == "case_converter") CaseConverterScreen(navController)
                        else if (tool.route == "morse") MorseCodeScreen(navController)
                        else if (tool.route == "morse_decoder") MorseDecoderScreen(navController)
                        else if (tool.route == "lorem") LoremIpsumScreen(navController)
                        else if (tool.route == "text_diff") TextDiffScreen(navController)
                        else WordCounterScreen(navController, tool.name)
                    }
                    "Calculation" -> {
                        if (tool.route == "date_calc") DateCalculatorScreen(navController)
                        else if (tool.route == "fuel") FuelCostCalculatorScreen(navController)
                        else if (tool.route == "unit_price") UnitPriceCalculatorScreen(navController)
                        else MathToolScreen(navController, tool.name)
                    }
                    "Conversion" -> {
                        if (tool.route == "currency") CurrencyConverterScreen(navController)
                        else if (tool.route == "base_conv") BaseConverterScreen(navController)
                        else UnitConverterScreen(navController)
                    }
                    "Network" -> {
                        if (tool.route == "network_info") NetworkInfoScreen(navController)
                        else if (tool.route == "my_ip") MyIPScreen(navController)
                        else NetworkToolScreen(navController, tool.name)
                    }
                    "Utility & Misc" -> {
                        if (tool.route == "flashlight") FlashlightScreen(navController)
                        else if (tool.route == "stopwatch") StopwatchScreen(navController)
                        else if (tool.route == "clock") ClockScreen(navController)
                        else if (tool.route == "world_clock") WorldClockScreen(navController)
                        else if (tool.route == "qr_gen") QrGeneratorScreen(navController)
                        else if (tool.route == "ruler" || tool.route == "protractor") RulerScreen(navController)
                        else if (tool.route == "vibration") VibrationTestScreen(navController)
                        else if (tool.route == "bpm") BpmCounterScreen(navController)
                        else if (listOf("random", "coin_flip", "dice_roller", "number_guessing", "memory_game").contains(tool.route)) RandomGeneratorScreen(navController)
                        else if (tool.route == "speedometer" || tool.route == "fuel_consumption" || tool.route == "car_maintenance") AutomotiveToolScreen(navController, tool.name)
                        else if (tool.route == "social_preview" || tool.route == "bio_linker") SocialToolScreen(navController, tool.name)
                        else if (tool.route == "tiles_widgets") TilesAndWidgetsScreen(navController)
                        else AudioToolScreen(navController, tool.name)
                    }
                    else -> AudioToolScreen(navController, tool.name)
                }
            }
        }
    }
}
