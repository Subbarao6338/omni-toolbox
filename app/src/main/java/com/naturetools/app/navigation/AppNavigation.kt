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
        composable("water_reminder") { AudioToolScreen(navController, "Water Reminder") }
        composable("medication_tracker") { AudioToolScreen(navController, "Medication Tracker") }
        composable("compass_pro") { CompassScreen(navController) }
        composable("altimeter_pro") { AltimeterScreen(navController) }
        composable("gps_status") { NetworkToolScreen(navController, "GPS Status") }
        composable("area_calc_pro") { AreaCalcScreen(navController) }
        composable("coin_tracker") { NetworkToolScreen(navController, "Coin Tracker") }
        composable("crypto_conv") { CurrencyConverterScreen(navController) }
        composable("wallet_explorer") { NetworkToolScreen(navController, "Wallet Explorer") }
        composable("coin_flip") { RandomGeneratorScreen(navController) }
        composable("dice_roller") { RandomGeneratorScreen(navController) }
        composable("number_guessing") { RandomGeneratorScreen(navController) }
        composable("tic_tac_toe") { AudioToolScreen(navController, "Tic Tac Toe") }
        composable("file_explorer") { FileToolScreen(navController, "File Explorer") }
        composable("storage_cleaner") { FileToolScreen(navController, "Storage Cleaner") }
        composable("zip_unzip") { FileToolScreen(navController, "Zip/Unzip") }
        composable("duplicate_finder") { FileToolScreen(navController, "Duplicate Finder") }
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
        composable("date_calc") { DateCalculatorScreen(navController) }
        composable("bpm") { BpmCounterScreen(navController) }
        composable("fuel") { FuelCostCalculatorScreen(navController) }
        composable(
            route = "hub",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://hub" })
        ) { WebToolScreen(navController, initialUrl = "https://nhub-pi.vercel.app", showUrlBar = false, title = "Hub") }
        composable(
            route = "media_grabber?url={url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType; nullable = true; defaultValue = null }),
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://media_grabber?url={url}" })
        ) { backStackEntry ->
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
        composable("ping") { NetworkToolScreen(navController, "Ping") }
        composable("password_manager") { PasswordManagerScreen(navController) }
        composable("gradient_gen") { GradientGeneratorScreen(navController) }
        composable("ruler") { RulerScreen(navController) }
        composable("altimeter") { AltimeterScreen(navController) }
        composable("barometer") { BarometerScreen(navController) }
        composable(
            route = "qr_scanner",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://qr_scanner" })
        ) { QrScannerScreen(navController) }

        composable("sci_calc") { ScientificCalculatorScreen(navController) }
        composable("device_id") { AudioToolScreen(navController, "Hardware ID") }
        composable("air_quality") { EnvironmentToolScreen(navController, "Air Quality") }
        composable("uv_index") { EnvironmentToolScreen(navController, "UV Index") }
        composable("habit_tracker") { HabitTrackerScreen(navController) }
        composable("meditation") { MeditationTimerScreen(navController) }
        composable("spl_meter") { SplMeterScreen(navController) }
        composable("data_viz") { DataVisualizerScreen(navController) }
        composable("ai_image") { ImageGeneratorScreen(navController) }
        composable("base_conv") { BaseConverterScreen(navController) }
        composable("constants") { ConstantsTableScreen(navController) }
        composable("light_pollution") { EnvironmentToolScreen(navController, "Light Pollution") }
        composable("tax_calc") { TaxCalculatorScreen(navController) }
        composable("calorie_calc") { CalorieCalculatorScreen(navController) }
        composable("exif_viewer") { ExifViewerScreen(navController) }
        composable("port_scanner") { NetworkToolScreen(navController, "Port Scanner") }
        composable("pomodoro") { PomodoroScreen(navController) }
        composable("morse_decoder") { MorseDecoderScreen(navController) }
        composable("hash_gen") { HashGeneratorScreen(navController) }
        composable("sensors_list") { SensorsListScreen(navController) }
        composable("lorem") { LoremIpsumScreen(navController) }
        composable("vibration") { VibrationTestScreen(navController) }
        composable("star_map") { StarMapScreen(navController) }
        composable("constellations") { EngineeringToolScreen(navController, "Constellations") }
        composable(
            route = "sos",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://sos" })
        ) { EmergencySOSScreen(navController) }
        composable("signal_mirror") { SignalMirrorScreen(navController) }
        composable("physics_formulas") { PhysicsFormulasScreen(navController) }
        composable("ai_chat") { ChatToolScreen(navController, "Smart Chat") }
        composable("ai_summarizer") { ChatToolScreen(navController, "Text Summarizer") }
        composable("mortgage_calc") { MortgageCalculatorScreen(navController) }
        composable("jwt_tool") { JwtToolScreen(navController) }
        composable("world_map") { WorldMapScreen(navController) }
        composable("moon_phase") { EnvironmentToolScreen(navController, "Moon Phase") }
        composable("sleep_tracker") { HealthScreen(navController, "Sleep Tracker") }
        composable("daily_quotes") { DailyQuotesScreen(navController) }
        composable("plant_care") { PlantCareScreen(navController) }
        composable("image_compress") { ImageToolScreen(navController, "Image Compressor") }
        composable("photo_filters") { ImageToolScreen(navController, "Photo Filters") }
        composable("dns_lookup") { NetworkToolScreen(navController, "DNS Lookup") }
        composable("whois") { NetworkToolScreen(navController, "Whois") }
        composable("task_board") { TaskBoardScreen(navController) }
        composable("time_logger") { TimeLoggerScreen(navController) }
        composable("voice_memo") { VoiceMemoScreen(navController) }
        composable("app_permissions") { SystemLabScreen(navController, "App Permissions") }
        composable("privacy_check") { SystemLabScreen(navController, "Privacy Check") }
        composable("app_info") { AppInfoScreen(navController) }
        composable("update_check") { SystemLabScreen(navController, "Update Check") }
        composable("anagram") { EngineeringToolScreen(navController, "Anagram Finder") }
        composable("text_diff") { TextDiffScreen(navController) }
        composable("resistor_code") { EngineeringToolScreen(navController, "Resistor Color Code") }
        composable("logic_gates") { EngineeringToolScreen(navController, "Logic Gates") }
        composable("exposure_calc") { PhotographyToolScreen(navController, "Exposure Calculator") }
        composable("dof_calc") { PhotographyToolScreen(navController, "Depth of Field") }
        composable("guitar_tuner") { MusicToolScreen(navController, "Guitar Tuner") }
        composable("unit_price") { UnitPriceCalculatorScreen(navController) }
        composable("freq_gen") { AudioToolScreen(navController, "Freq Generator") }
        composable("stereo_widener") { AudioToolScreen(navController, "Stereo Widener") }
        composable("subnet_calc") { NetworkToolScreen(navController, "Subnet Calc") }
        composable("route_planner") { NetworkToolScreen(navController, "Route Planner") }
        composable("video_stabilizer") { AudioToolScreen(navController, "Video Stabilizer", "video/*") }
        composable("multi_image_resize") { ImageToolScreen(navController, "Multi Image Resize") }
        composable("currency_trends") { AudioToolScreen(navController, "Currency Trends") }

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

        composable("vocal_remover") { AudioToolScreen(navController, "Vocal Remover") }
        composable("ai_stems_splitter") { AudioToolScreen(navController, "Stems Splitter") }
        composable("vocal_autotuner") { AudioToolScreen(navController, "Vocal AutoTuner") }
        composable("ai_noise_remover") { AudioToolScreen(navController, "Noise Remover") }
        composable("echo_remover") { AudioToolScreen(navController, "Echo Remover") }
        composable("reverb_remover") { AudioToolScreen(navController, "Reverb Remover") }
        composable("key_bpm_finder") { AudioToolScreen(navController, "Key BPM Finder") }
        composable("audio_noise_remover") { AudioToolScreen(navController, "Audio Noise Remover") }
        composable("video_noise_remover") { AudioToolScreen(navController, "Video Noise Remover", "video/*") }

        composable("record_audio") { AudioToolScreen(navController, "Record Audio") }

        composable("multi_mix") { AudioToolScreen(navController, "Multi Mix Audio") }
        composable("multi_convert") { AudioToolScreen(navController, "Multi Convert") }
        composable("multi_video_to_audio") { AudioToolScreen(navController, "Multi Video To Audio", "video/*") }
        composable("multi_volume_booster") { AudioToolScreen(navController, "Multi Volume Booster") }

        composable(
            route = "metronome",
            deepLinks = listOf(navDeepLink { uriPattern = "naturetools://metronome" })
        ) { MetronomeScreen(navController) }

        composable("image_single_edit") { ImageToolScreen(navController, "Single Edit") }
        composable("image_resize_conv") { ImageToolScreen(navController, "Resize and Convert") }
        composable("image_format_conv") { ImageToolScreen(navController, "Format Conversion") }
        composable("image_crop") { ImageToolScreen(navController, "Crop") }
        composable("image_cutting") { ImageToolScreen(navController, "Image Cutting") }
        composable("image_resize_weight") { ImageToolScreen(navController, "Resize by Weight") }
        composable("image_resize_limits") { ImageToolScreen(navController, "Resize by Limits") }
        composable("image_edit_exif") { ImageToolScreen(navController, "Edit EXIF") }
        composable("image_delete_exif") { ImageToolScreen(navController, "Delete EXIF") }
        composable("image_ai_tools") { ImageToolScreen(navController, "Smart Tools") }
        composable("image_bg_remover") { ImageToolScreen(navController, "Background Remover") }
        composable("image_collage") { ImageToolScreen(navController, "Collage Maker") }
        composable("image_draw") { ImageToolScreen(navController, "Draw") }
        composable("image_filter") { ImageToolScreen(navController, "Filter") }
        composable("image_stacking") { ImageToolScreen(navController, "Image Stacking") }
        composable("image_stitching") { ImageToolScreen(navController, "Image Stitching") }
        composable("image_markup") { ImageToolScreen(navController, "Markup Layers") }
        composable("image_noise_gen") { ImageToolScreen(navController, "Noise Generation") }
        composable("image_watermark") { ImageToolScreen(navController, "Watermarking") }
        composable("image_compare") { ImageToolScreen(navController, "Compare") }
        composable("image_wallpapers") { ImageToolScreen(navController, "Wallpapers Export") }
        composable("image_to_svg") { ImageToolScreen(navController, "Images to SVG") }
        composable("image_web_load") { ImageToolScreen(navController, "Web Image Loading") }
        composable("image_ocr") { ImageToolScreen(navController, "OCR") }
        composable("image_preview") { ImageToolScreen(navController, "Image Preview") }
        composable("image_base64") { ImageToolScreen(navController, "Base64 Tools") }
        composable("image_palette") { ImageToolScreen(navController, "Palette Tools") }
        composable("image_color_picker") { ImageToolScreen(navController, "Color Picker") }
        composable("image_mask_filter") { ImageToolScreen(navController, "Mask Filter") }
        composable("image_draw_bg") { ImageToolScreen(navController, "Draw on background") }
        composable("image_layers_img") { ImageToolScreen(navController, "Layers on image") }
        composable("image_layers_bg") { ImageToolScreen(navController, "Layers on background") }
        composable("image_open_project") { ImageToolScreen(navController, "Open project") }

        composable("image_to_webp") { ImageToolScreen(navController, "Images to WEBP") }
        composable("webp_to_images") { ImageToolScreen(navController, "WEBP to images") }
        composable("image_to_apng") { ImageToolScreen(navController, "Images to APNG") }
        composable("apng_to_images") { ImageToolScreen(navController, "APNG to images") }
        composable("apng_to_jxl") { ImageToolScreen(navController, "APNG to JXL") }
        composable("pdf_preview") { ImageToolScreen(navController, "Preview PDF") }
        composable("images_to_pdf") { ImageToolScreen(navController, "Images to PDF") }
        composable("pdf_to_images") { ImageToolScreen(navController, "PDF to Images") }
        composable("pdf_merge") { ImageToolScreen(navController, "Merge PDF") }
        composable("pdf_split") { ImageToolScreen(navController, "Split PDF") }
        composable("pdf_remove_pages") { ImageToolScreen(navController, "Remove PDF pages") }
        composable("pdf_rotate") { ImageToolScreen(navController, "Rotate PDF") }
        composable("pdf_rearrange") { ImageToolScreen(navController, "Rearrange PDF") }
        composable("pdf_crop") { ImageToolScreen(navController, "Crop PDF") }
        composable("pdf_page_numbers") { ImageToolScreen(navController, "Page Numbers") }
        composable("pdf_watermark") { ImageToolScreen(navController, "Watermarking") }
        composable("pdf_signature") { ImageToolScreen(navController, "Signature") }
        composable("pdf_compress") { ImageToolScreen(navController, "Compress PDF") }
        composable("pdf_flatten") { ImageToolScreen(navController, "Flatten PDF") }
        composable("pdf_print") { ImageToolScreen(navController, "Print PDF") }
        composable("pdf_grayscale") { ImageToolScreen(navController, "Grayscale") }
        composable("pdf_repair") { ImageToolScreen(navController, "Repair PDF") }
        composable("pdf_protect") { ImageToolScreen(navController, "Protect PDF") }
        composable("pdf_unlock") { ImageToolScreen(navController, "Unlock PDF") }
        composable("pdf_metadata") { ImageToolScreen(navController, "Metadata") }
        composable("pdf_extract_images") { ImageToolScreen(navController, "Extract Images") }
        composable("pdf_ocr") { ImageToolScreen(navController, "PDF to Text (OCR)") }
        composable("pdf_zip") { ImageToolScreen(navController, "Zip PDF") }
        composable("jpeg_to_jxl") { ImageToolScreen(navController, "JPEG to JXL") }
        composable("jxl_to_jpeg") { ImageToolScreen(navController, "JXL to JPEG") }
        composable("jxl_to_images") { ImageToolScreen(navController, "JXL to Images") }
        composable("images_to_jxl") { ImageToolScreen(navController, "Images to JXL") }
        composable("images_to_gif") { ImageToolScreen(navController, "Images to GIF") }
        composable("gif_to_images") { ImageToolScreen(navController, "GIF to images") }
        composable("gif_to_jxl") { ImageToolScreen(navController, "GIF to JXL") }
        composable("gif_to_webp") { ImageToolScreen(navController, "GIF to WEBP") }
        composable("color_info") { ImageToolScreen(navController, "Color Info") }
        composable("color_mixing") { ImageToolScreen(navController, "Color Mixing") }
        composable("color_harmonies") { ImageToolScreen(navController, "Color Harmonies") }
        composable("color_shading") { ImageToolScreen(navController, "Color Shading") }
        composable("image_histogram") { ImageToolScreen(navController, "Histogram") }
        composable("generate_palette") { ImageToolScreen(navController, "Generate Palette") }
        composable("material_you_palette") { ImageToolScreen(navController, "Material You") }
        composable("edit_palette") { ImageToolScreen(navController, "Edit Palette") }

        composable("webp_tools") {
            AnimatedImageToolGroupScreen(
                navController = navController,
                title = "WEBP Tools",
                subTools = listOf(
                    SubTool("Images to WEBP", "Convert batch of images to WEBP file", Icons.Default.Transform, "image_to_webp"),
                    SubTool("WEBP to images", "Convert WEBP file to batch of pictures", Icons.Default.Collections, "webp_to_images")
                )
            )
        }
        composable("apng_tools") {
            AnimatedImageToolGroupScreen(
                navController = navController,
                title = "APNG Tools",
                subTools = listOf(
                    SubTool("Images to APNG", "Convert batch of images to APNG file", Icons.Default.Transform, "image_to_apng"),
                    SubTool("APNG to images", "Convert APNG file to batch of pictures", Icons.Default.Collections, "apng_to_images"),
                    SubTool("APNG to JXL", "Convert APNG images to JXL animated pictures", Icons.Default.Animation, "apng_to_jxl")
                )
            )
        }


        composable("pdf_tools") {
            AnimatedImageToolGroupScreen(
                navController = navController,
                title = "PDF Tools",
                subTools = listOf(
                    SubTool("Preview PDF", "View PDF files directly within the app", Icons.Default.Preview, "pdf_preview"),
                    SubTool("Images to PDF", "Pack a selection of images into a single PDF file", Icons.Default.PictureAsPdf, "images_to_pdf"),
                    SubTool("PDF to Images", "Convert PDF pages into individual image files", Icons.Default.Collections, "pdf_to_images"),
                    SubTool("Merge PDF", "Combine multiple PDF files into one document", Icons.Default.Merge, "pdf_merge"),
                    SubTool("Split PDF", "Extract specific pages from a PDF", Icons.AutoMirrored.Filled.CallSplit, "pdf_split"),
                    SubTool("Remove PDF pages", "Delete unwanted pages from a document", Icons.Default.Delete, "pdf_remove_pages"),
                    SubTool("Rotate PDF", "Permanently fix the orientation of PDF pages", Icons.AutoMirrored.Filled.RotateRight, "pdf_rotate"),
                    SubTool("Rearrange PDF", "Reorder pages using drag-and-drop functionality", Icons.Default.Reorder, "pdf_rearrange"),
                    SubTool("Crop PDF", "Trim PDF pages to specific boundaries", Icons.Default.Crop, "pdf_crop"),
                    SubTool("Page Numbers", "Automatically add numbering to document pages", Icons.Default.FormatListNumbered, "pdf_page_numbers"),
                    SubTool("Watermarking", "Overlay custom text for branding or security", Icons.AutoMirrored.Filled.BrandingWatermark, "pdf_watermark"),
                    SubTool("Signature", "Add electronic signatures to any document", Icons.Default.Draw, "pdf_signature"),
                    SubTool("Compress PDF", "Optimize and reduce file size", Icons.Default.Compress, "pdf_compress"),
                    SubTool("Flatten PDF", "Make PDFs unmodifiable", Icons.Default.LayersClear, "pdf_flatten"),
                    SubTool("Print PDF", "Prepare documents for printing", Icons.Default.Print, "pdf_print"),
                    SubTool("Grayscale", "Convert PDF images to black and white", Icons.Default.ColorLens, "pdf_grayscale"),
                    SubTool("Repair PDF", "Attempt to fix corrupted or unreadable documents", Icons.Default.Build, "pdf_repair"),
                    SubTool("Protect PDF", "Secure documents with strong encryption", Icons.Default.Lock, "pdf_protect"),
                    SubTool("Unlock PDF", "Remove password protection from files", Icons.Default.LockOpen, "pdf_unlock"),
                    SubTool("Metadata", "Edit document properties", Icons.Default.Info, "pdf_metadata"),
                    SubTool("Extract Images", "Save images embedded in PDFs", Icons.Default.Image, "pdf_extract_images"),
                    SubTool("PDF to Text (OCR)", "Extract plain text from PDF documents", Icons.Default.TextFields, "pdf_ocr"),
                    SubTool("Zip PDF", "Split documents and pack them into a zip archive", Icons.Default.FolderZip, "pdf_zip")
                )
            )
        }

        composable("jxl_tools") {
            AnimatedImageToolGroupScreen(
                navController = navController,
                title = "JXL Tools",
                subTools = listOf(
                    SubTool("JPEG to JXL", "Lossless transcoding from JPEG to JXL", Icons.Default.Transform, "jpeg_to_jxl"),
                    SubTool("JXL to JPEG", "Lossless transcoding from JXL back to JPEG", Icons.Default.Transform, "jxl_to_jpeg"),
                    SubTool("JXL to Images", "Convert JXL animations into a batch of pictures", Icons.Default.Collections, "jxl_to_images"),
                    SubTool("Images to JXL", "Convert a batch of pictures into a JXL animation", Icons.Default.Animation, "images_to_jxl")
                )
            )
        }

        composable("gif_tools") {
            AnimatedImageToolGroupScreen(
                navController = navController,
                title = "GIF Tools",
                subTools = listOf(
                    SubTool("Images to GIF", "Convert a batch of images to a GIF file", Icons.Default.Animation, "images_to_gif"),
                    SubTool("GIF to images", "Convert a GIF file into individual frames", Icons.Default.Collections, "gif_to_images"),
                    SubTool("GIF to JXL", "Convert GIF images to JXL animated pictures", Icons.Default.Transform, "gif_to_jxl"),
                    SubTool("GIF to WEBP", "Convert GIF images to WEBP animated pictures", Icons.Default.Transform, "gif_to_webp")
                )
            )
        }

        composable("color_palette_tools") {
            AnimatedImageToolGroupScreen(
                navController = navController,
                title = "Color & Palette Tools",
                subTools = listOf(
                    SubTool("Color Info", "Provides detailed data for a selected color", Icons.Default.Info, "color_info"),
                    SubTool("Color Mixing", "Blend colors together", Icons.Default.InvertColors, "color_mixing"),
                    SubTool("Color Harmonies", "Generate color schemes", Icons.Default.Palette, "color_harmonies"),
                    SubTool("Color Shading", "Create shades and tints", Icons.Default.Gradient, "color_shading"),
                    SubTool("Histogram", "View color distribution of an image", Icons.Default.BarChart, "image_histogram"),
                    SubTool("Generate Palette", "Create palette from an image", Icons.Default.ColorLens, "generate_palette"),
                    SubTool("Material You", "Create Material You palette from an image", Icons.Default.AutoAwesome, "material_you_palette"),
                    SubTool("Edit Palette", "Export or import palettes", Icons.Default.Edit, "edit_palette")
                )
            )
        }

        composable("pdf_view_v2") { ImageToolScreen(navController, "Preview PDF") }
        composable("pdf_split_v2") { ImageToolScreen(navController, "Split PDF") }
        composable("pdf_security") { ImageToolScreen(navController, "Protect PDF") }
        composable("pdf_extract_v2") { ImageToolScreen(navController, "Extract Images") }




        composable("doc_scanner") { ImageToolScreen(navController, "Document Scanner") }
        composable("cipher_tools") { FileToolScreen(navController, "Cipher") }
        composable("ascii_art") { ImageToolScreen(navController, "ASCII Art") }


        // New tools from ToolProvider
        composable("ai_code") { ChatToolScreen(navController, "Code Helper") }
        composable("csv_to_json") { ChatToolScreen(navController, "CSV to JSON") }
        composable("ai_voice_mimic") { AudioToolScreen(navController, "Voice Mimic") }
        composable("audio_reverb") { AudioToolScreen(navController, "Audio Reverb") }
        composable("multi_crop") { ImageToolScreen(navController, "Multi Crop") }
        composable("binary_calc") { ScientificCalculatorScreen(navController) }
        composable("torque_conv") { UnitConverterScreen(navController) }
        composable("yaml_to_json") { JsonFormatterScreen(navController) }
        composable("solar_system") { OutdoorToolScreen(navController, "Solar System Explorer") }
        composable("rain_radar") { EnvironmentToolScreen(navController, "Rain Radar") }
        composable("pcb_trace") { EngineeringToolScreen(navController, "PCB Trace Width") }
        composable("golden_hour") { PhotographyToolScreen(navController, "Golden Hour") }
        composable("chord_lib") { MusicToolScreen(navController, "Chord Library") }
        composable("hiking_trails") { OutdoorToolScreen(navController, "Hiking Trails") }
        composable("campfire_guide") { OutdoorToolScreen(navController, "Campfire Guide") }
        composable("weather_forecast") { OutdoorToolScreen(navController, "Weather Forecast") }
        composable("knots_guide") { OutdoorToolScreen(navController, "Knots Guide") }
        composable("expense_tracker") { LoanCalculatorScreen(navController) }
        composable("yoga_guide") { HealthScreen(navController, "Yoga Guide") }
        composable("altitude_graph") { SensorDataScreen(navController) }
        composable("nft_viewer") { NetworkToolScreen(navController, "NFT Viewer") }
        composable("memory_game") { RandomGeneratorScreen(navController) }
        composable("file_shredder") { FileToolScreen(navController, "File Shredder") }
        composable("daily_journal") { NotePadScreen(navController) }
        composable("pixel_art") { ImageToolScreen(navController, "Pixel Art Maker") }
        composable("frame_grabber") { AudioToolScreen(navController, "Video Frame Grabber", "video/*") }
        composable("speed_test") { NetworkToolScreen(navController, "Speed Test") }
        composable("kanban") { TaskBoardScreen(navController) }
        composable("gforce_meter") { SensorDataScreen(navController) }
        composable("thermal_info") { DeviceScreen(navController) }
        composable("perm_manager") { SystemLabScreen(navController, "Permission Manager") }
        composable("dna_viz") { EngineeringToolScreen(navController, "DNA Visualizer") }
        composable("planet_finder") { OutdoorToolScreen(navController, "Planet Finder") }
        composable("force_calc") { ScientificCalculatorScreen(navController) }
        composable("app_locker") { SystemLabScreen(navController, "App Locker") }
        composable("process_manager") { SystemLabScreen(navController, "Process Manager") }
        composable("protractor") { RulerScreen(navController) }
        composable("video_flip") { AudioToolScreen(navController, "Video Flip", "video/*") }
        composable("vid_to_aud_pro") { AudioToolScreen(navController, "Video To Audio Pro", "video/*") }
        composable("batch_img_pro") { ImageToolScreen(navController, "Batch Image Pro") }
        composable("media_grab_pro") { MediaGrabberScreen(navController) }
        composable("vid_edit_pro") { AudioToolScreen(navController, "Video Editor Pro", "video/*") }
        composable("aud_master_pro") { AudioToolScreen(navController, "Audio Mastering Pro") }

        composable("perchance_tools") { PerchanceHubScreen(navController) }
        composable("per_hub") { WebToolScreen(navController, "https://perchance.org/welcome", false, "Perchance Hub") }
        composable("per_image") { WebToolScreen(navController, "https://perchance.org/ai-image-generator", false, "Perchance Image Generator") }
        composable("per_story") { WebToolScreen(navController, "https://perchance.org/ai-story-generator", false, "Perchance Story Generator") }
        composable("per_character") { WebToolScreen(navController, "https://perchance.org/ai-character-generator", false, "Perchance Character Generator") }

        // Automotive
        composable("speedometer") { AutomotiveToolScreen(navController, "Speedometer") }
        composable("fuel_consumption") { AutomotiveToolScreen(navController, "Fuel Consumption") }
        composable("car_maintenance") { AutomotiveToolScreen(navController, "Car Maintenance") }

        // Social
        composable("social_preview") { SocialToolScreen(navController, "Social Preview") }
        composable("bio_linker") { SocialToolScreen(navController, "Bio Linker") }

        // Electronics
        composable("ohms_law") { ElectronicsToolScreen(navController, "Ohm's Law") }
        composable("circuit_calc") { ElectronicsToolScreen(navController, "Circuit Calc") }

        composable("system_lab") { SystemLabScreen(navController, "System Lab") }

        // Science

        // Kitchen
        composable("egg_timer") { StopwatchScreen(navController) }
        composable("recipe_scaler") { UnitPriceCalculatorScreen(navController) }

        // Document Creation & Conversion

        // Document Organization & Editing

        // Optimization & Enhancement

        // Security & Metadata

        // Specialized & View Tools

        // 2. Image Tools

        // 3. Video Tools
        composable("vid_annotator") { AudioToolScreen(navController, "Video Frame Annotator", "video/*") }
        composable("vid_thumb") { AudioToolScreen(navController, "Thumbnail Extractor", "video/*") }

        // 4. Audio Tools
        composable("aud_conv") { AudioToolScreen(navController, "Audio Converter") }
        composable("aud_info_v2") { AudioToolScreen(navController, "Audio Info") }
        composable("aud_eq_v2") { AudioToolScreen(navController, "Audio Equalizer") }
        composable("aud_to_vid") { AudioToolScreen(navController, "Add Audio to Video") }

        // 5. Text Tools
        composable("ai_grammar") { ChatToolScreen(navController, "Grammar Checker") }
        composable("ai_obj_detect") { ChatToolScreen(navController, "Object Detector") }
        composable("ai_sentiment") { ChatToolScreen(navController, "Sentiment Analysis") }
        composable("ai_text_ext") { ChatToolScreen(navController, "Text Extractor") }
        composable("ai_translate") { ChatToolScreen(navController, "Translator") }
        composable("color_conv_pro") { FileToolScreen(navController, "Color Converter Pro") }
        composable("crontab_gen") { FileToolScreen(navController, "Crontab Gen") }
        composable("port_checker") { FileToolScreen(navController, "Port Checker") }
        composable("sql_format") { FileToolScreen(navController, "SQL Formatter") }
        composable("antenna_calc") { EngineeringToolScreen(navController, "Antenna Calc") }
        composable("filter_design") { EngineeringToolScreen(navController, "Filter Designer") }
        composable("signal_gen_pro") { EngineeringToolScreen(navController, "Signal Gen Pro") }
        composable("dividend_calc") { LoanCalculatorScreen(navController) }
        composable("inflation_calc") { LoanCalculatorScreen(navController) }
        composable("roi_calc") { LoanCalculatorScreen(navController) }
        composable("salary_calc") { LoanCalculatorScreen(navController) }
        composable("stock_profit") { LoanCalculatorScreen(navController) }
        composable("eye_exercise") { HealthScreen(navController, "Eye Exercise") }
        composable("posture_check") { HealthScreen(navController, "Posture Checker") }
        composable("stretch_guide") { HealthScreen(navController, "Stretching Guide") }
        composable("wifi_anal") { NetworkToolScreen(navController, "WIFI Analyzer") }
        composable("wake_on_lan") { NetworkToolScreen(navController, "Wake On LAN") }

        composable("device_discovery") { NetworkToolScreen(navController, "Device Discovery") }
        composable("mqtt_tester") { NetworkToolScreen(navController, "MQTT Tester") }
        composable("smart_hub") { SystemLabScreen(navController, "Smart Hub") }

        // Math & Logic
        composable("matrix_calc") { MathToolScreen(navController, "Matrix Calc") }
        composable("eq_solver") { MathToolScreen(navController, "Equation Solver") }
        composable("fraction_calc") { MathToolScreen(navController, "Fraction Calc") }
        composable("truth_table") { MathToolScreen(navController, "Truth Table Gen") }
        composable("stats_pro") { MathToolScreen(navController, "Statistics Pro") }

        // Developer Expert (mapping some developer tools to new screen)
        composable("hex_viewer") { DeveloperExpertScreen(navController, "Hex Viewer") }
        composable("ascii_table") { DeveloperExpertScreen(navController, "ASCII Table") }
        composable("binary_pro") { DeveloperExpertScreen(navController, "Binary Pro") }
        composable("regex_pro") { DeveloperExpertScreen(navController, "Regex Pro") }

        // Finance Pro
        composable("sip_calc") { FinanceToolScreen(navController, "SIP Calculator") }
        composable("gst_calc") { FinanceToolScreen(navController, "GST Calculator") }
        composable("retirement_planner") { FinanceToolScreen(navController, "Retirement Planner") }
        composable("compound_pro") { FinanceToolScreen(navController, "Compound Interest Pro") }
        composable("loan_emi_pro") { FinanceToolScreen(navController, "Loan EMI Pro") }
        composable("unit_price_pro") { FinanceToolScreen(navController, "Unit Price Pro") }

        // Health Plus
        composable("period_tracker") { HealthScreen(navController, "Period Tracker") }
            composable("tiles_widgets") { TilesAndWidgetsScreen(navController) }
}
}