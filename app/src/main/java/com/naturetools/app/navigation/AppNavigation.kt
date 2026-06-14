package com.naturetools.app.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.naturetools.app.model.Tool
import com.naturetools.app.model.ToolProvider
import com.naturetools.app.ui.screens.HomeScreen
import com.naturetools.app.ui.screens.ai.*
import com.naturetools.app.ui.screens.system.DashboardScreen
import com.naturetools.app.ui.screens.system.DeveloperScreen
import com.naturetools.app.ui.screens.system.PowerBenchScreen
import com.naturetools.app.ui.screens.system.QuickTilesCreatorScreen
import com.naturetools.app.ui.screens.astronomy.*
import com.naturetools.app.ui.screens.audio.*
import com.naturetools.app.ui.screens.calculation.*
import com.naturetools.app.ui.screens.conversion.*
import com.naturetools.app.ui.screens.developer.*
import com.naturetools.app.ui.screens.health.*
import com.naturetools.app.ui.screens.utility.PanchangamScreen
import com.naturetools.app.ui.screens.image.*
import com.naturetools.app.ui.screens.lifestyle.*
import com.naturetools.app.ui.screens.media.*
import com.naturetools.app.ui.screens.network.*
import com.naturetools.app.ui.screens.physics.*
import com.naturetools.app.ui.screens.productivity.*
import com.naturetools.app.ui.screens.productivity.SecurityScreen
import com.naturetools.app.ui.screens.science.*
import com.naturetools.app.ui.screens.sensor.*
import com.naturetools.app.ui.screens.survival.*
import com.naturetools.app.ui.screens.system.*
import com.naturetools.app.ui.screens.text.*
import com.naturetools.app.ui.screens.utility.*
import com.naturetools.app.viewmodel.OmniViewModel
import com.naturetools.app.ui.screens.engineering.*
import com.naturetools.app.ui.screens.photography.*
import com.naturetools.app.ui.screens.music.*
import com.naturetools.app.ui.screens.outdoor.*
import com.naturetools.app.ui.screens.environment.*
import com.naturetools.app.ui.screens.automotive.*
import com.naturetools.app.ui.screens.social.*
import com.naturetools.app.ui.screens.electronics.*
import com.naturetools.app.ui.screens.data.*

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
    accentColor: Color?,
    onAccentColorChange: (Color?) -> Unit,
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
    val omniViewModel: OmniViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

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

    NavHost(navController = navController, startDestination = "home", modifier = Modifier.fillMaxSize()) {
        composable("home") {
            HomeScreen(
                navController,
                showCategoryCounts,
                favorites,
                onToggleFavorite = toggleFavorite
            )
        }
        composable("settings") {
            SettingsScreen(
                navController,
                themeMode, onThemeChange,
                dynamicColor, onDynamicColorChange,
                showCategoryCounts, onShowCategoryCountsChange,
                aiApiKey, onAiApiKeyChange,
                accentColor, onAccentColorChange
            )
        }

        composable("dashboard") { DashboardScreen(navController) }
        composable("developer_console") { DeveloperScreen(navController) }
        composable("cloud_sync") { SyncScreen(navController, omniViewModel) }
        composable("web_scraper") { DocsCrawlerScreen(navController) }
        composable("ai_companion") { AICompanionScreen(navController, aiApiKey) }
        composable("security_vault") { SecurityScreen(navController) }
        composable("power_bench") { PowerBenchScreen(navController) }
        composable("quick_tiles") { QuickTilesCreatorScreen(navController) }

            addSpecialRoutes(navController)

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

            // Individual tools mapping
            ToolProvider.tools.filter { it.subToolRoutes == null }.forEach { tool ->
                if (isSpecialRoute(tool.route)) return@forEach
                composable(tool.route) {
                    ToolScreenDispatcher(navController, tool, aiApiKey)
                }
            }
        }
}

fun NavGraphBuilder.addSpecialRoutes(navController: NavHostController) {
    composable(route = "calculator", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://calculator" })) { CalculatorScreen(navController) }
    composable(route = "compass", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://compass" })) { CompassScreen(navController) }
    composable(route = "note", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://note" })) { NotePadScreen(navController) }
    composable(route = "hub", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://hub" })) { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "nHub") }
    composable(route = "qr_scanner", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://qr_scanner" })) { QrScannerScreen(navController) }
    composable(route = "sos", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://sos" })) { EmergencySOSScreen(navController) }
    composable(route = "metronome", deepLinks = listOf(navDeepLink { uriPattern = "naturetools://metronome" })) { MetronomeScreen(navController) }

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
}

fun isSpecialRoute(route: String): Boolean {
    return listOf("calculator", "compass", "note", "hub", "qr_scanner", "sos", "metronome", "web").contains(route)
}

@Composable
fun ToolScreenDispatcher(navController: NavHostController, tool: Tool, aiApiKey: String) {
    val route = tool.route
    val category = tool.category

    when {
        // --- 1. Specialized Screens by Route (Direct Mapping) ---
        route == "ai_image" -> ImageGeneratorScreen(navController, aiApiKey)
        route == "ai_doc_translator" -> DocumentTranslatorScreen(navController, tool.name)
        route == "binaural" -> BinauralBeatsScreen(navController)
        route == "ping" -> PingScreen(navController)
        route == "port_scanner" -> PortScannerScreen(navController)
        route == "cipher_tools" -> HashGeneratorScreen(navController)
        route == "bpm" -> BpmCounterScreen(navController)
        route == "random" -> RandomGeneratorScreen(navController)
        listOf("ai_chat", "ai_summarizer", "ai_code", "ai_grammar", "ai_obj_detect", "ai_sentiment", "ai_text_ext", "ai_translate", "ai_noise_remover", "audio_noise_remover", "video_noise_remover", "echo_remover", "reverb_remover", "ai_stems_splitter", "vocal_remover", "vocal_autotuner", "aud_master_pro", "ai_voice_mimic").contains(route) -> ChatToolScreen(navController, tool.name, aiApiKey)

        route == "voice_memo" -> VoiceMemoScreen(navController)
        route == "exif_viewer" -> ExifViewerScreen(navController)
        route == "media_grabber" -> MediaGrabberScreen(navController, null)
        route == "file_conv" -> FileConverterScreen(navController)
        route == "profile_photo_maker" -> ProfilePhotoMakerScreen(navController)
        route == "gradient_gen" -> GradientGeneratorScreen(navController)

        route == "periodic_table" -> PeriodicTableScreen(navController)
        route == "pokedex" -> PokedexScreen(navController)
        route == "constants" -> ConstantsTableScreen(navController)
        route == "physics_formulas" -> PhysicsFormulasScreen(navController)
        listOf("star_map", "constellations", "solar_system", "planet_finder").contains(route) -> StarMapScreen(navController)
        route == "prime" -> PrimeCheckerScreen(navController)
        route == "unit_circle" -> UnitCircleScreen(navController)
        route == "dna_viz" -> DataVisualizerScreen(navController)

        route == "flashlight" -> FlashlightScreen(navController)
        route == "qr_gen" -> QrGeneratorScreen(navController)
        route == "vibration" -> VibrationTestScreen(navController)
        route == "wifi_qr" -> WifiQrScreen(navController)
        route == "checklist" -> ChecklistScreen(navController)
        route == "pomodoro" -> PomodoroScreen(navController)
        listOf("task_board", "kanban").contains(route) -> TaskBoardScreen(navController)
        route == "time_logger" -> TimeLoggerScreen(navController)
        route == "daily_journal" -> NotePadScreen(navController)
        route == "daily_quotes" -> DailyQuotesScreen(navController)
        route == "clock" -> ClockScreen(navController)
        route == "stopwatch" -> StopwatchScreen(navController)
        route == "date_calc" -> DateCalculatorScreen(navController)
        route == "area_calc" -> AreaCalcScreen(navController)
        route == "volume_calc" -> VolumeCalcScreen(navController)
        route == "discount" -> DiscountCalculatorScreen(navController)
        route == "tip" -> TipCalculatorScreen(navController)
        route == "mortgage_calc" -> MortgageCalculatorScreen(navController)
        route == "sci_calc" -> ScientificCalculatorScreen(navController)
        route == "tiles_widgets" -> TilesAndWidgetsScreen(navController)
        route == "panchangam" -> PanchangamScreen(navController)
        route == "ruler" -> RulerScreen(navController)
        route == "protractor" -> ProtractorScreen(navController)
        route == "zodiac" -> ZodiacFinderScreen(navController)
        route == "macro_splitter" -> MacroSplitterScreen(navController)
        route == "billing" -> BillingScreen(navController)

        listOf("unit_price", "unit_compare").contains(route) -> UnitPriceCalculatorScreen(navController)
        route == "base_conv" -> BaseConverterScreen(navController)
        listOf("converter", "torque_conv").contains(route) -> UnitConverterScreen(navController)

        route == "battery" -> BatteryScreen(navController)
        route == "ram_info" -> RamInfoScreen(navController)
        route == "cpu_info" -> CpuInfoScreen(navController)
        route == "device" -> DeviceScreen(navController)
        route == "storage" -> StorageScreen(navController)
        route == "app_info" -> AppInfoScreen(navController)
        route == "app_inspector" -> AppDecompilerScreen(navController)
        listOf("drawing_board", "signature_maker").contains(route) -> DrawingBoardScreen(navController, tool.name)
        route == "terminal" -> TerminalScreen(navController)
        route == "face_swap" -> FaceSwapScreen(navController)
        route == "sensors_list" -> SensorsListScreen(navController)
        route == "spl_meter" -> SensorDataScreen(navController)
        route == "sensor_data" -> SensorDataScreen(navController)
        route == "gforce_meter" -> SensorDataScreen(navController)
        route == "light" -> LightMeterScreen(navController)
        route == "metal" -> MetalDetectorScreen(navController)
        route == "altimeter" -> AltimeterScreen(navController)
        route == "barometer" -> BarometerScreen(navController)
        route == "level" -> LevelScreen(navController)

        route == "csv_to_json" -> CsvToJsonScreen(navController)
        route == "json" -> JsonFormatterScreen(navController)
        listOf("anomaly_detection", "data_profiling", "data_statistics", "data_visualisations", "synthetic_data_gen", "data_quality", "data_cleaning", "data_transformation").contains(route) -> DataToolScreen(navController, route, tool.name)
        route.startsWith("pdf_") || route == "images_to_pdf" -> PdfToolScreen(navController, tool.name)

        route == "docs_online" -> OnlineDocsScreen(navController)
        route == "password_gen" -> PasswordGenScreen(navController)
        route == "password_manager" -> PasswordManagerScreen(navController)

        route == "meta_anal" -> MetaTagScreen(navController)

        route == "network_info" -> NetworkInfoScreen(navController)
        route == "my_ip" -> MyIPScreen(navController)

        route == "compound_interest" -> CompoundInterestScreen(navController)
        route == "tax_calc" -> TaxCalculatorScreen(navController)
        route == "loan_calc" -> LoanCalculatorScreen(navController)
        listOf("crypto_conv", "currency").contains(route) -> CurrencyConverterScreen(navController)

        route == "water" -> WaterTrackerScreen(navController)
        route == "step_counter" -> StepCounterScreen(navController)
        route == "bmi" -> BMICalculatorScreen(navController)
        route == "bmr" -> BmrCalculatorScreen(navController)
        route == "calorie_calc" -> CalorieCalculatorScreen(navController)
        route == "habit_tracker" -> HabitTrackerScreen(navController)
        route == "meditation" -> MeditationTimerScreen(navController)
        listOf("heart_rate", "blood_pressure", "blood_sugar").contains(route) -> MedicalToolsScreen(navController, tool.name)

        route == "travel_budget" -> TravelBudgetScreen(navController)
        route == "world_map" -> WorldMapScreen(navController)
        route == "world_clock" -> WorldClockScreen(navController)
        route == "signal_mirror" -> SignalMirrorScreen(navController)
        route == "packing_list" -> PackingListScreen(navController)

        route == "url_encoder" -> UrlEncoderScreen(navController)
        route == "base64" -> Base64Screen(navController)
        route == "jwt_tool" -> JwtToolScreen(navController)
        route == "regex_tester" -> RegexTesterScreen(navController)
        route == "markdown_preview" -> MarkdownPreviewScreen(navController)
        route == "word_counter" -> WordCounterScreen(navController)
        route == "morse" -> MorseCodeScreen(navController)
        route == "morse_decoder" -> MorseDecoderScreen(navController)
        route == "text_diff" -> TextDiffScreen(navController)
        listOf("case_converter", "lorem", "anagram").contains(route) -> TextToolScreen(navController, tool.name)
        route == "word_rank_calc" -> WordRankScreen(navController)
        route == "panchangam" -> PanchangamScreen(navController)

        route == "plant_care" -> PlantCareScreen(navController)
        route == "fuel" -> FuelCostCalculatorScreen(navController)
        listOf("speedometer", "fuel_consumption", "car_maintenance").contains(route) -> AutomotiveToolScreen(navController, tool.name)

        listOf("image_color_picker", "image_palette", "color_palette_group").contains(route) -> ColorToolsScreen(navController)

        // --- 2. Shared Multi-Category Screens ---
        listOf("matrix_calc", "eq_solver", "fraction_calc", "truth_table", "binary_calc", "stats").contains(route) -> MathToolScreen(navController, tool.name)
        listOf("antenna_calc", "filter_design", "logic_gates", "pcb_trace", "resistor_code", "signal_gen_pro", "force_calc").contains(route) -> EngineeringToolScreen(navController, tool.name)
        listOf("ohms_law", "circuit_calc").contains(route) -> ElectronicsToolScreen(navController, tool.name)

        // --- 3. Web Tools (Dynamic URL mapping) ---
        route.startsWith("per_") || listOf("sec_adguard", "sec_nextdns", "sec_bitwarden", "sec_ente", "hub", "web").contains(route) -> {
             val url = when(route) {
                "per_hub" -> "https://perchance.org/welcome"
                "per_image" -> "https://perchance.org/ai-image-generator"
                "per_story" -> "https://perchance.org/ai-story-generator"
                "per_character" -> "https://perchance.org/ai-character-generator"
                "per_image_pro" -> "https://perchance.org/image-generator-professional"
                "per_text_gen" -> "https://perchance.org/ai-text-generator"
                "per_text_rewrite" -> "https://perchance.org/ai-text-rewriter"
                "per_necs_story" -> "https://perchance.org/necs-story"
                "sec_adguard" -> "https://adguard-dns.io/en/welcome.html"
                "sec_nextdns" -> "https://my.nextdns.io"
                "sec_bitwarden" -> "https://vault.bitwarden.com/"
                "sec_ente" -> "https://auth.ente.com/"
                "hub" -> "https://nhub-pi.vercel.app"
                "web" -> "https://www.google.com"
                else -> "https://perchance.org"
            }
            // If the route is 'web' without query params, use Google.
            // The NavHost handles routes with query params separately in addSpecialRoutes.
            WebToolScreen(navController, initialUrl = url, showUrlBar = (route == "web"), title = tool.name)
        }

        // --- 4. Category-Based Fallback Screens ---
        category == "Games" -> GameToolScreen(navController, tool.name)
        category == "Network" -> NetworkToolScreen(navController, tool.name)
        category == "Finance" -> FinanceToolScreen(navController, tool.name)
        category == "Social" -> SocialToolScreen(navController, tool.name)
        category == "Media" -> {
            if (tool.route == "guitar_tuner" || tool.route == "chord_lib") MusicToolScreen(navController, tool.name)
            else if (tool.route.startsWith("m_audio") || tool.route.startsWith("aud_") || listOf("m_3d_audio", "m_bass_booster", "m_echo_effect", "m_equalizer", "m_karaoke_maker", "m_mute_audio", "m_reverse_audio", "m_ringtone_maker", "m_silence_remover", "m_speech_to_text", "m_speed_changer", "m_text_to_speech", "m_voice_changer", "m_volume_booster").contains(route)) AudioToolScreen(navController, tool.name)
            else if (tool.route.startsWith("video_") || tool.route.startsWith("vid_") || listOf("frame_grabber", "m_video_to_audio", "mix_video_audio", "digital_magnifier", "mirror_tool", "video_compress", "video_trim", "vid_edit_pro", "video_to_gif", "vid_thumb", "video_delete", "video_flip", "video_loop", "video_reverse", "video_sfx", "video_silence", "video_speed_changer", "video_splitter", "video_stabilizer", "video_volume_booster").contains(route)) AudioToolScreen(navController, tool.name, mimeType = "video/*")
            else ImageToolScreen(navController, tool.name)
        }
        category == "Health" -> HealthScreen(navController, tool.name)
        category == "Device" -> if (route == "smart_hub") SystemLabScreen(navController, tool.name) else SystemLabScreen(navController, tool.name)
        category == "Security" -> SystemLabScreen(navController, tool.name)
        category == "Weather" -> EnvironmentToolScreen(navController, tool.name)
        category == "Travel" -> OutdoorToolScreen(navController, tool.name)
        category == "Documents" -> FileToolScreen(navController, tool.name)
        category == "DIY" -> EngineeringToolScreen(navController, tool.name)
        category == "Developer" -> DeveloperExpertScreen(navController, tool.name)
        category == "Data" -> if (route == "yaml_to_json") DeveloperExpertScreen(navController, tool.name) else FileToolScreen(navController, tool.name)

        else -> AudioToolScreen(navController, tool.name)
    }
}
