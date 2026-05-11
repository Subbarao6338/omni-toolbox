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
    aiApiKey: String,
    onAiApiKeyChange: (String) -> Unit,
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
        composable("settings") { SettingsScreen(navController, themeMode, onThemeChange, dynamicColor, onDynamicColorChange, showCategoryCounts, onShowCategoryCountsChange, aiApiKey, onAiApiKeyChange) }

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
        ) { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "nHub") }

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
                    "AI Tools" -> {
                        if (listOf("ai_chat", "ai_summarizer", "ai_code", "ai_grammar", "ai_obj_detect", "ai_sentiment", "ai_text_ext", "ai_translate").contains(tool.route)) {
                            ChatToolScreen(navController, tool.name, aiApiKey)
                        } else if (tool.route == "ai_image") {
                            ImageGeneratorScreen(navController, aiApiKey)
                        } else {
                            AudioToolScreen(navController, tool.name)
                        }
                    }
                    "Media" -> {
                        if (tool.route == "guitar_tuner" || tool.route == "chord_lib") MusicToolScreen(navController, tool.name)
                        else if (tool.route == "voice_memo") VoiceMemoScreen(navController)
                        else if (tool.route == "gradient_gen") GradientGeneratorScreen(navController)
                        else if (tool.route == "exif_viewer") ExifViewerScreen(navController)
                        else if (tool.route == "media_grabber") MediaGrabberScreen(navController)
                        else if (tool.route == "profile_photo_maker") ProfilePhotoMakerScreen(navController)
                        else if (listOf("m_3d_audio", "m_audio_compressor", "m_audio_cutter", "m_audio_editor", "m_audio_joiner", "m_audio_mixer", "m_audio_normalizer", "m_audio_pan", "m_audio_pitch", "m_audio_splitter", "m_audio_tag_editor", "m_bass_booster", "m_echo_effect", "m_equalizer", "m_karaoke_maker", "m_mute_audio", "m_reverse_audio", "m_ringtone_maker", "m_silence_remover", "m_speech_to_text", "m_speed_changer", "m_text_to_speech", "m_voice_changer", "m_volume_booster").contains(tool.route)) AudioToolScreen(navController, tool.name)
                        else if (listOf("frame_grabber", "m_video_to_audio", "mix_video_audio", "vid_annotator", "vid_edit_pro", "vid_thumb", "video_compress", "video_delete", "video_flip", "video_loop", "video_reverse", "video_sfx", "video_silence", "video_speed_changer", "video_splitter", "video_stabilizer", "video_to_gif", "video_trim", "video_volume_booster").contains(tool.route)) AudioToolScreen(navController, tool.name, mimeType = "video/*")
                        else ImageToolScreen(navController, tool.name)
                    }
                    "Education" -> {
                        if (tool.route == "periodic_table") PeriodicTableScreen(navController)
                        else if (tool.route == "pokedex") PokedexScreen(navController)
                        else if (tool.route == "constants") ConstantsTableScreen(navController)
                        else if (tool.route == "physics_formulas") PhysicsFormulasScreen(navController)
                        else if (tool.route == "star_map" || tool.route == "constellations" || tool.route == "solar_system" || tool.route == "planet_finder") StarMapScreen(navController)
                        else if (tool.route == "prime") PrimeCheckerScreen(navController)
                        else if (listOf("matrix_calc", "eq_solver", "fraction_calc", "truth_table", "stats", "binary_calc", "sci_calc").contains(tool.route)) MathToolScreen(navController, tool.name)
                        else if (tool.route == "dna_viz") DataVisualizerScreen(navController)
                        else if (tool.route == "force_calc") EngineeringToolScreen(navController, tool.name)
                        else EngineeringToolScreen(navController, tool.name)
                    }
                    "Utilities" -> {
                        if (tool.route == "flashlight") FlashlightScreen(navController)
                        else if (tool.route == "qr_gen") QrGeneratorScreen(navController)
                        else if (tool.route == "qr_scanner") QrScannerScreen(navController)
                        else if (tool.route == "ruler" || tool.route == "protractor") RulerScreen(navController)
                        else if (tool.route == "vibration") VibrationTestScreen(navController)
                        else if (tool.route == "wifi_qr") WifiQrScreen(navController)
                        else if (tool.route == "checklist") ChecklistScreen(navController)
                        else if (tool.route == "pomodoro") PomodoroScreen(navController)
                        else if (tool.route == "task_board" || tool.route == "kanban") TaskBoardScreen(navController)
                        else if (tool.route == "time_logger") TimeLoggerScreen(navController)
                        else if (tool.route == "daily_journal" || tool.route == "note") NotePadScreen(navController)
                        else if (tool.route == "daily_quotes") DailyQuotesScreen(navController)
                        else if (tool.route == "clock") ClockScreen(navController)
                        else if (tool.route == "stopwatch") StopwatchScreen(navController)
                        else if (tool.route == "date_calc") DateCalculatorScreen(navController)
                        else if (listOf("ohms_law", "circuit_calc").contains(tool.route)) ElectronicsToolScreen(navController, tool.name)
                        else if (listOf("matrix_calc", "eq_solver", "fraction_calc", "truth_table", "sci_calc", "calculator", "binary_calc", "discount", "mortgage_calc", "tip", "area_calc").contains(tool.route)) MathToolScreen(navController, tool.name)
                        else if (tool.route == "unit_price" || tool.route == "unit_compare") UnitPriceCalculatorScreen(navController)
                        else if (tool.route == "base_conv") BaseConverterScreen(navController)
                        else if (listOf("converter", "torque_conv").contains(tool.route)) UnitConverterScreen(navController)
                        else if (listOf("antenna_calc", "filter_design", "logic_gates", "pcb_trace", "resistor_code", "signal_gen_pro").contains(tool.route)) EngineeringToolScreen(navController, tool.name)
                        else if (tool.route == "smart_hub") SystemLabScreen(navController, tool.name)
                        else if (tool.route == "tiles_widgets") TilesAndWidgetsScreen(navController)
                        else AudioToolScreen(navController, tool.name)
                    }
                    "Games" -> {
                        RandomGeneratorScreen(navController)
                    }
                    "Device" -> {
                        if (tool.route == "battery") BatteryScreen(navController)
                        else if (tool.route == "cpu_info") CpuInfoScreen(navController)
                        else if (tool.route == "device") DeviceScreen(navController)
                        else if (tool.route == "storage") StorageScreen(navController)
                        else if (tool.route == "app_info") AppInfoScreen(navController)
                        else if (tool.route == "sensors_list") SensorsListScreen(navController)
                        else if (listOf("spl_meter", "gforce_meter", "sensor_data").contains(tool.route)) SensorDataScreen(navController)
                        else if (listOf("light", "metal", "altimeter", "barometer", "level").contains(tool.route)) AltimeterScreen(navController)
                        else if (tool.route == "thermal_info" || tool.route == "device_id" || tool.route == "update_check" || tool.route == "process_manager") SystemLabScreen(navController, tool.name)
                        else SystemLabScreen(navController, tool.name)
                    }
                    "Documents" -> {
                        if (tool.route == "doc_scanner") FileToolScreen(navController, tool.name)
                        else if (tool.route == "csv_to_json") CsvToJsonScreen(navController)
                        else if (tool.route.startsWith("pdf_") || tool.route == "images_to_pdf") ImageToolScreen(navController, tool.name)
                        else FileToolScreen(navController, tool.name)
                    }
                    "Security" -> {
                        if (tool.route == "password_gen") PasswordGenScreen(navController)
                        else if (tool.route == "password_manager") PasswordManagerScreen(navController)
                        else if (tool.route == "app_locker" || tool.route == "privacy_check" || tool.route == "app_permissions" || tool.route == "perm_manager") SystemLabScreen(navController, tool.name)
                        else FileToolScreen(navController, tool.name)
                    }
                    "Web" -> {
                        if (tool.route.startsWith("per_")) {
                            WebToolScreen(navController, initialUrl = when(tool.route) {
                                "per_hub" -> "https://perchance.org/welcome"
                                "per_image" -> "https://perchance.org/ai-image-generator"
                                "per_story" -> "https://perchance.org/ai-story-generator"
                                "per_character" -> "https://perchance.org/ai-character-generator"
                                "per_image_pro" -> "https://perchance.org/image-generator-professional"
                                "per_text_gen" -> "https://perchance.org/ai-text-generator"
                                "per_text_rewrite" -> "https://perchance.org/ai-text-rewriter"
                                "per_necs_story" -> "https://perchance.org/necs-story"
                                else -> "https://perchance.org"
                            }, showUrlBar = false, title = tool.name)
                        } else if (tool.route == "hub") WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "nHub")
                        else if (tool.route == "media_grabber") MediaGrabberScreen(navController)
                        else if (tool.route == "meta_anal") MetaTagScreen(navController)
                        else if (tool.route == "web") WebToolScreen(navController, title = "Web Search")
                        else AudioToolScreen(navController, tool.name)
                    }
                    "Network" -> {
                        if (tool.route == "network_info") NetworkInfoScreen(navController)
                        else if (tool.route == "my_ip") MyIPScreen(navController)
                        else NetworkToolScreen(navController, tool.name)
                    }
                    "Data" -> {
                        if (tool.route == "csv_to_json") CsvToJsonScreen(navController)
                        else if (tool.route == "data_viz") DataVisualizerScreen(navController)
                        else if (tool.route == "json") JsonFormatterScreen(navController)
                        else if (tool.route == "stats") MathToolScreen(navController, tool.name)
                        else if (tool.route == "yaml_to_json") DeveloperExpertScreen(navController, tool.name)
                        else FileToolScreen(navController, tool.name)
                    }
                    "Finance" -> {
                        if (tool.route == "compound_interest") CompoundInterestScreen(navController)
                        else if (tool.route == "crypto_conv" || tool.route == "currency") CurrencyConverterScreen(navController)
                        else FinanceToolScreen(navController, tool.name)
                    }
                    "Health" -> {
                        if (tool.route == "water") WaterTrackerScreen(navController)
                        else if (tool.route == "step_counter") StepCounterScreen(navController)
                        else if (tool.route == "bmi") BMICalculatorScreen(navController)
                        else if (tool.route == "bmr") BmrCalculatorScreen(navController)
                        else if (tool.route == "habit_tracker") HabitTrackerScreen(navController)
                        else if (tool.route == "meditation") MeditationTimerScreen(navController)
                        else HealthScreen(navController, tool.name)
                    }
                    "Weather" -> {
                        EnvironmentToolScreen(navController, tool.name)
                    }
                    "Travel" -> {
                        if (tool.route == "travel_budget") TravelBudgetScreen(navController)
                        else if (tool.route == "world_map") WorldMapScreen(navController)
                        else if (tool.route == "world_clock") WorldClockScreen(navController)
                        else if (tool.route == "sos") EmergencySOSScreen(navController)
                        else if (tool.route == "signal_mirror") SignalMirrorScreen(navController)
                        else if (tool.route == "area_calc") MathToolScreen(navController, tool.name)
                        else OutdoorToolScreen(navController, tool.name)
                    }
                    "Developer" -> {
                        if (tool.route == "url_encoder") UrlEncoderScreen(navController)
                        else if (tool.route == "base64") Base64Screen(navController)
                        else if (tool.route == "jwt_tool") JwtToolScreen(navController)
                        else if (tool.route == "regex_tester") RegexTesterScreen(navController)
                        else if (tool.route == "markdown_preview") MarkdownPreviewScreen(navController)
                        else if (tool.route == "word_counter") WordCounterScreen(navController)
                        else if (tool.route == "case_converter") TextToolScreen(navController, "Case Converter")
                        else if (tool.route == "morse") MorseCodeScreen(navController)
                        else if (tool.route == "morse_decoder") MorseDecoderScreen(navController)
                        else if (tool.route == "lorem") TextToolScreen(navController, "Lorem Ipsum")
                        else if (tool.route == "anagram") TextToolScreen(navController, "Anagram Finder")
                        else if (tool.route == "text_diff") TextDiffScreen(navController)
                        else DeveloperExpertScreen(navController, tool.name)
                    }
                    "Design" -> {
                        ImageToolScreen(navController, tool.name)
                    }
                    "DIY" -> {
                        if (tool.route == "speedometer" || tool.route == "fuel_consumption" || tool.route == "car_maintenance") AutomotiveToolScreen(navController, tool.name)
                        else if (tool.route == "plant_care") PlantCareScreen(navController)
                        else if (tool.route == "fuel") FuelCostCalculatorScreen(navController)
                        else if (tool.route == "recipe_scaler") HealthScreen(navController, tool.name)
                        else EngineeringToolScreen(navController, tool.name)
                    }
                    "Social" -> {
                        if (tool.route == "profile_photo_maker") ProfilePhotoMakerScreen(navController)
                        else SocialToolScreen(navController, tool.name)
                    }
                    else -> AudioToolScreen(navController, tool.name)
                }
            }
        }
    }
}
