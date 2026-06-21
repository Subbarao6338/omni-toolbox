package omni.toolbox.navigation

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
import omni.toolbox.model.Tool
import omni.toolbox.model.ToolProvider
import omni.toolbox.ui.screens.HomeScreen
import omni.toolbox.ui.components.ToolGroupScreen
import omni.toolbox.ui.screens.ai.*
import omni.toolbox.ui.screens.system.DashboardScreen
import omni.toolbox.ui.screens.system.DeveloperScreen
import omni.toolbox.ui.screens.system.PowerBenchScreen
import omni.toolbox.ui.screens.system.QuickTilesCreatorScreen
import omni.toolbox.ui.screens.astronomy.*
import omni.toolbox.ui.screens.audio.*
import omni.toolbox.ui.screens.calculation.*
import omni.toolbox.ui.screens.conversion.*
import omni.toolbox.ui.screens.developer.*
import omni.toolbox.ui.screens.health.*
import omni.toolbox.ui.screens.utility.PanchangamScreen
import omni.toolbox.ui.screens.image.*
import omni.toolbox.ui.screens.lifestyle.*
import omni.toolbox.ui.screens.media.*
import omni.toolbox.ui.screens.network.*
import omni.toolbox.ui.screens.physics.*
import omni.toolbox.ui.screens.productivity.*
import omni.toolbox.ui.screens.productivity.SecurityScreen
import omni.toolbox.ui.screens.science.*
import omni.toolbox.ui.screens.sensor.*
import omni.toolbox.ui.screens.survival.*
import omni.toolbox.ui.screens.system.*
import omni.toolbox.ui.screens.text.*
import omni.toolbox.ui.screens.utility.*
import omni.toolbox.viewmodel.OmniViewModel
import omni.toolbox.ui.screens.engineering.*
import omni.toolbox.ui.screens.photography.*
import omni.toolbox.ui.screens.music.*
import omni.toolbox.ui.screens.outdoor.*
import omni.toolbox.ui.screens.environment.*
import omni.toolbox.ui.screens.automotive.*
import omni.toolbox.ui.screens.social.*
import omni.toolbox.ui.screens.electronics.*
import omni.toolbox.ui.screens.data.*
import omni.toolbox.ui.screens.comm.*

@Composable
fun OmniToolboxApp(
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
            if (uri.scheme == "omnitoolbox") {
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
    composable(route = "calculator", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://calculator" })) { CalculatorScreen(navController) }
    composable(route = "compass", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://compass" })) { CompassScreen(navController) }
    composable(route = "note", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://note" })) { NotePadScreen(navController) }
    composable(route = "hub", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://hub" })) { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "nHub") }
    composable(route = "qr_scanner", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://qr_scanner" })) { QrScannerScreen(navController) }
    composable(route = "sos", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://sos" })) { EmergencySOSScreen(navController) }
    composable(route = "metronome", deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://metronome" })) { MetronomeScreen(navController) }

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
        deepLinks = listOf(navDeepLink { uriPattern = "omnitoolbox://media_grabber?url={url}" })
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
        listOf("ai_chat", "ai_summarizer", "ai_code", "ai_grammar", "ai_obj_detect", "ai_sentiment", "ai_text_ext", "ai_translate", "video_noise_remover").contains(route) -> ChatToolScreen(navController, tool.name, aiApiKey)

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
        route == "data_viz" -> DataVisualizerScreen(navController)
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
        route == "markitdown" -> MarkItDownScreen(navController)

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
        route == "weather_forecast" -> WeatherForecastScreen(navController)
        route == "tides" -> TidesScreen(navController)
        route == "survival_guide" -> SurvivalGuideScreen(navController)
        route == "beacon_nav" -> BeaconNavigationScreen(navController)
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

        route == "dialer" -> DialerScreen(navController)
        route == "call_history" -> HistoryScreen(navController)
        route == "contacts" -> ContactsScreen(navController)
        route == "messages" -> MessagesScreen(navController)
        route == "vocal_remover" || route == "ai_stems_splitter" -> VocalRemoverScreen(navController, tool.name)

        route == "plant_care" -> PlantCareScreen(navController)
        route == "fuel" -> FuelCostCalculatorScreen(navController)
        listOf("speedometer", "fuel_consumption", "car_maintenance").contains(route) -> AutomotiveToolScreen(navController, tool.name)

        listOf("image_color_picker", "image_palette", "color_palette_group").contains(route) -> ColorToolsScreen(navController)
        route == "image_bg_remover" || route == "image_ai_tools" -> ImageAIScreen(navController, tool.name)

        // --- 2. Shared Multi-Category Screens ---
        listOf("matrix_calc", "eq_solver", "fraction_calc", "truth_table", "binary_calc", "stats").contains(route) -> MathToolScreen(navController, tool.name)
        listOf("antenna_calc", "filter_design", "logic_gates", "pcb_trace", "resistor_code", "signal_gen_pro", "force_calc").contains(route) -> EngineeringToolScreen(navController, tool.name)
        listOf("ohms_law", "circuit_calc").contains(route) -> ElectronicsToolScreen(navController, tool.name)

        // --- 3. Web Tools (Dynamic URL mapping) ---
        route.startsWith("per_") || listOf("sec_adguard", "sec_nextdns", "sec_bitwarden", "sec_ente", "hub", "web").contains(route) -> {
             WebDispatcher(navController, tool)
        }

        // --- 4. Category-Based Fallback Screens ---
        else -> CategoryFallbackDispatcher(navController, tool, aiApiKey)
    }
}

@Composable
fun WebDispatcher(navController: NavHostController, tool: Tool) {
    val route = tool.route
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
    WebToolScreen(navController, initialUrl = url, showUrlBar = (route == "web"), title = tool.name)
}

@Composable
fun CategoryFallbackDispatcher(navController: NavHostController, tool: Tool, aiApiKey: String) {
    val route = tool.route
    val category = tool.category

    when (category) {
        "Games" -> GameToolScreen(navController, tool.name)
        "Network" -> NetworkToolScreen(navController, tool.name)
        "Finance" -> FinanceToolScreen(navController, tool.name)
        "Social" -> SocialToolScreen(navController, tool.name)
        "Media" -> MediaDispatcher(navController, tool)
        "Health" -> HealthScreen(navController, tool.name)
        "Device", "Security", "Utilities" -> SystemLabScreen(navController, tool.name)
        "Weather" -> EnvironmentToolScreen(navController, tool.name)
        "Travel" -> OutdoorToolScreen(navController, tool.name)
        "Documents" -> FileToolScreen(navController, tool.name)
        "DIY", "Education" -> EngineeringToolScreen(navController, tool.name)
        "Developer" -> DeveloperExpertScreen(navController, tool.name)
        "Data" -> if (route == "yaml_to_json") DeveloperExpertScreen(navController, tool.name) else FileToolScreen(navController, tool.name)
        "Web" -> WebToolScreen(navController, initialUrl = "https://www.google.com", title = tool.name)
        "Design" -> ColorToolsScreen(navController)
        "AI Tools" -> ChatToolScreen(navController, tool.name, aiApiKey)
        else -> DashboardScreen(navController)
    }
}

@Composable
fun MediaDispatcher(navController: NavHostController, tool: Tool) {
    val route = tool.route
    when {
        tool.route == "guitar_tuner" || tool.route == "chord_lib" -> MusicToolScreen(navController, tool.name)
        tool.route.startsWith("m_audio") || tool.route.startsWith("aud_") || tool.route.startsWith("ai_") ||
                listOf(
                    "m_3d_audio", "m_bass_booster", "m_echo_effect", "m_equalizer", "m_karaoke_maker",
                    "m_mute_audio", "m_reverse_audio", "m_ringtone_maker", "m_silence_remover",
                    "m_speech_to_text", "m_speed_changer", "m_text_to_speech", "m_voice_changer",
                    "m_volume_booster", "aud_master_pro", "audio_noise_remover", "echo_remover",
                    "reverb_remover", "vocal_autotuner", "add_sfx", "audio_loop",
                    "key_bpm_finder", "noise_generator", "record_audio", "silence_generator",
                    "sound_mastering", "wave_generator"
                ).contains(route) -> AudioToolScreen(navController, tool.name)
        tool.route.startsWith("video_") || tool.route.startsWith("vid_") ||
                listOf(
                    "frame_grabber", "m_video_to_audio", "mix_video_audio", "digital_magnifier",
                    "mirror_tool", "video_compress", "video_trim", "vid_edit_pro", "video_to_gif",
                    "vid_thumb", "video_delete", "video_flip", "video_loop", "video_reverse",
                    "video_sfx", "video_silence", "video_speed_changer", "video_splitter",
                    "video_stabilizer", "video_volume_booster", "video_merger"
                ).contains(route) -> AudioToolScreen(navController, tool.name, mimeType = "video/*")
        else -> ImageToolScreen(navController, tool.name)
    }
}
