package com.naturetools.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class BadgeType {
    NONE, NEW, PREMIUM
}

data class Tool(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val category: String,
    val color: Color = Color.Unspecified,
    val badge: BadgeType = BadgeType.NONE
)

object ToolProvider {
    val tools = listOf(
        // AI & Data
        Tool("AI Chat", Icons.Default.Chat, "ai_chat", "AI & Data", Color(0xFF2196F3), BadgeType.NEW),
        Tool("AI Summarizer", Icons.Default.Summarize, "ai_summarizer", "AI & Data", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Data Visualizer", Icons.Default.BarChart, "data_viz", "AI & Data", Color(0xFF4CAF50)),
        Tool("Markdown Preview", Icons.Default.Description, "markdown_preview", "AI & Data", Color(0xFF3F51B5)),
        Tool("Regex Tester", Icons.Default.Code, "regex_tester", "AI & Data", Color(0xFF673AB7)),

        // AI Tools
        Tool("AI Image Gen", Icons.Default.Image, "ai_image", "AI Tools", Color(0xFFFFC107), BadgeType.NEW),
        Tool("AI Noise Remover", Icons.Default.Hearing, "ai_noise_remover", "AI Tools", Color(0xFF4CAF50)),
        Tool("AI Stems Splitter", Icons.AutoMirrored.Filled.AltRoute, "ai_stems_splitter", "AI Tools", Color(0xFF00BCD4)),
        Tool("Audio Noise Remover", Icons.Default.MicOff, "audio_noise_remover", "AI Tools", Color(0xFF2196F3)),
        Tool("Echo Remover", Icons.Default.SettingsBackupRestore, "echo_remover", "AI Tools", Color(0xFF009688)),
        Tool("Key BPM Finder", Icons.Default.MusicNote, "key_bpm_finder", "AI Tools", Color(0xFFF44336)),
        Tool("Reverb Remover", Icons.Default.Waves, "reverb_remover", "AI Tools", Color(0xFF607D8B)),
        Tool("Video Noise Remover", Icons.Default.MovieFilter, "video_noise_remover", "AI Tools", Color(0xFF9C27B0)),
        Tool("Vocal AutoTuner", Icons.Default.SettingsVoice, "vocal_autotuner", "AI Tools", Color(0xFFE91E63)),
        Tool("Vocal Remover", Icons.Default.PersonOff, "vocal_remover", "AI Tools", Color(0xFFFF9800)),

        // Audio Tools
        Tool("3D Audio", Icons.Default.Headset, "m_3d_audio", "Audio Tools", Color(0xFFE91E63)),
        Tool("Add SFX", Icons.Default.AutoAwesome, "add_sfx", "Audio Tools", Color(0xFF3F51B5)),
        Tool("Audio Compressor", Icons.Default.Compress, "m_audio_compressor", "Audio Tools", Color(0xFF673AB7)),
        Tool("Audio Cutter", Icons.Default.ContentCut, "m_audio_cutter", "Audio Tools", Color(0xFFF44336)),
        Tool("Audio Editor", Icons.Default.Edit, "m_audio_editor", "Audio Tools", Color(0xFFF44336)),
        Tool("Audio Joiner", Icons.Default.Link, "m_audio_joiner", "Audio Tools", Color(0xFF4CAF50)),
        Tool("Audio Loop", Icons.Default.Loop, "audio_loop", "Audio Tools", Color(0xFFF44336)),
        Tool("Audio Mixer", Icons.Default.Tune, "m_audio_mixer", "Audio Tools", Color(0xFF2196F3)),
        Tool("Audio Normalizer", Icons.AutoMirrored.Filled.VolumeUp, "m_audio_normalizer", "Audio Tools", Color(0xFF795548)),
        Tool("Audio Pan", Icons.AutoMirrored.Filled.AltRoute, "m_audio_pan", "Audio Tools", Color(0xFF00BCD4)),
        Tool("Audio Splitter", Icons.AutoMirrored.Filled.AltRoute, "m_audio_splitter", "Audio Tools", Color(0xFF00BCD4)),
        Tool("Audio Tag Editor", Icons.AutoMirrored.Filled.Label, "m_audio_tag_editor", "Audio Tools", Color(0xFFFF9800)),
        Tool("Bass Booster", Icons.Default.Speaker, "m_bass_booster", "Audio Tools", Color(0xFF3F51B5)),
        Tool("Echo Effect", Icons.Default.SettingsBackupRestore, "m_echo_effect", "Audio Tools", Color(0xFF009688)),
        Tool("Equalizer", Icons.Default.Equalizer, "m_equalizer", "Audio Tools", Color(0xFF8BC34A)),
        Tool("Karaoke Maker", Icons.Default.Mic, "m_karaoke_maker", "Audio Tools", Color(0xFFCDDC39)),
        Tool("Mute Audio", Icons.AutoMirrored.Filled.VolumeOff, "m_mute_audio", "Audio Tools", Color(0xFF9E9E9E)),
        Tool("Noise Generator", Icons.Default.GraphicEq, "noise_generator", "Audio Tools", Color(0xFF2196F3)),
        Tool("Pitch Changer", Icons.Default.Height, "m_audio_pitch", "Audio Tools", Color(0xFFFF5722)),
        Tool("Reverse Audio", Icons.Default.History, "m_reverse_audio", "Audio Tools", Color(0xFF795548)),
        Tool("Ringtone Maker", Icons.Default.Notifications, "m_ringtone_maker", "Audio Tools", Color(0xFFFF5722)),
        Tool("Silence Generator", Icons.Default.DoNotDisturbOn, "silence_generator", "Audio Tools", Color(0xFFFF5722)),
        Tool("Silence Remover", Icons.Default.SpeakerNotesOff, "m_silence_remover", "Audio Tools", Color(0xFFF44336)),
        Tool("Sound Mastering", Icons.Default.Insights, "sound_mastering", "Audio Tools", Color(0xFF3F51B5)),
        Tool("Speech to Text", Icons.Default.Mic, "m_speech_to_text", "Audio Tools", Color(0xFFE91E63)),
        Tool("Speed Changer", Icons.Default.FastForward, "m_speed_changer", "Audio Tools", Color(0xFF607D8B)),
        Tool("Text to Speech", Icons.Default.RecordVoiceOver, "m_text_to_speech", "Audio Tools", Color(0xFF009688)),
        Tool("Voice Changer", Icons.Default.Face, "m_voice_changer", "Audio Tools", Color(0xFF673AB7)),
        Tool("Volume Booster", Icons.AutoMirrored.Filled.VolumeUp, "m_volume_booster", "Audio Tools", Color(0xFFFFC107)),
        Tool("Wave Generator", Icons.Default.Waves, "wave_generator", "Audio Tools", Color(0xFF8BC34A)),

        // Batch Processing
        Tool("Multi Convert", Icons.Default.Autorenew, "multi_convert", "Batch Processing", Color(0xFF4CAF50)),
        Tool("Multi Image Resize", Icons.Default.PhotoSizeSelectLarge, "multi_image_resize", "Batch Processing", Color(0xFF8BC34A), BadgeType.NEW),
        Tool("Multi Mix Audio", Icons.Default.Tune, "multi_mix", "Batch Processing", Color(0xFF2196F3)),
        Tool("Multi Video To Audio", Icons.Default.MusicVideo, "multi_video_to_audio", "Batch Processing", Color(0xFFFF9800)),
        Tool("Multi Volume Booster", Icons.AutoMirrored.Filled.VolumeUp, "multi_volume_booster", "Batch Processing", Color(0xFF9C27B0)),

        // Calculation
        Tool("Calculator", Icons.Default.Calculate, "calculator", "Calculation", Color(0xFF607D8B)),
        Tool("Date Calc", Icons.Default.CalendarToday, "date_calc", "Calculation", Color(0xFF9E9E9E)),
        Tool("Discount Calc", Icons.Default.Percent, "discount", "Calculation", Color(0xFFF44336)),
        Tool("Fuel Cost", Icons.Default.LocalGasStation, "fuel", "Calculation", Color(0xFF9C27B0)),
        Tool("Mortgage Calc", Icons.Default.Home, "mortgage_calc", "Calculation", Color(0xFF795548), BadgeType.NEW),
        Tool("Scientific Calc", Icons.Default.Functions, "sci_calc", "Calculation", Color(0xFF2196F3)),
        Tool("Tip Calc", Icons.Default.Receipt, "tip", "Calculation", Color(0xFFE91E63)),

        // Conversion
        Tool("Base Converter", Icons.Default.Numbers, "base_conv", "Conversion", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Currency", Icons.Default.CurrencyExchange, "currency", "Conversion", Color(0xFF03A9F4)),
        Tool("Unit Converter", Icons.Default.SwapHoriz, "converter", "Conversion", Color(0xFF2196F3)),

        // Developer
        Tool("Base64 Tool", Icons.Default.Code, "base64", "Developer", Color(0xFF3F51B5)),
        Tool("JSON Format", Icons.Default.DataObject, "json", "Developer", Color(0xFF2196F3)),
        Tool("JWT Tool", Icons.Default.Key, "jwt_tool", "Developer", Color(0xFFFF5722), BadgeType.NEW),
        Tool("URL Encoder", Icons.Default.Link, "url_encoder", "Developer", Color(0xFF03A9F4)),

        // Education
        Tool("Prime Checker", Icons.Default.Filter7, "prime", "Education", Color(0xFFFF9800)),
        Tool("World Map", Icons.Default.Map, "world_map", "Education", Color(0xFF4CAF50), BadgeType.NEW),

        // Environment
        Tool("Air Quality", Icons.Default.Air, "air_quality", "Environment", Color(0xFF00BCD4)),
        Tool("Light Pollution", Icons.Default.NightsStay, "light_pollution", "Environment", Color(0xFF3F51B5)),
        Tool("Moon Phase", Icons.Default.Brightness3, "moon_phase", "Environment", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("UV Index", Icons.Default.WbSunny, "uv_index", "Environment", Color(0xFFFF9800)),

        // Finance
        Tool("Compound Interest", Icons.AutoMirrored.Filled.TrendingUp, "compound_interest", "Finance", Color(0xFF3F51B5)),
        Tool("Currency Trends", Icons.Default.Timeline, "currency_trends", "Finance", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Loan Calculator", Icons.Default.AccountBalance, "loan_calc", "Finance", Color(0xFF673AB7)),
        Tool("Tax Calculator", Icons.Default.MoneyOff, "tax_calc", "Finance", Color(0xFFF44336)),

        // Health
        Tool("BMI Calc", Icons.Default.AccessibilityNew, "bmi", "Health", Color(0xFFFF9800)),
        Tool("Calorie Calc", Icons.Default.Restaurant, "calorie_calc", "Health", Color(0xFFE91E63)),
        Tool("BMR Calculator", Icons.Default.Calculate, "bmr", "Health", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Sleep Tracker", Icons.Default.Bedtime, "sleep_tracker", "Health", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Step Counter", Icons.AutoMirrored.Filled.DirectionsRun, "step_counter", "Health", Color(0xFFFF5722)),
        Tool("Water Tracker", Icons.Default.LocalDrink, "water", "Health", Color(0xFF795548)),

        // Lifestyle
        Tool("Daily Quotes", Icons.Default.FormatQuote, "daily_quotes", "Lifestyle", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Habit Tracker", Icons.Default.EventRepeat, "habit_tracker", "Lifestyle", Color(0xFF4CAF50)),
        Tool("Meditation Timer", Icons.Default.SelfImprovement, "meditation", "Lifestyle", Color(0xFF9C27B0)),
        Tool("Plant Care", Icons.Default.Eco, "plant_care", "Lifestyle", Color(0xFF4CAF50), BadgeType.NEW),

        // Image Toolbox
        Tool("AI Tools", Icons.Default.Psychology, "image_ai_tools", "Image Toolbox", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Background Remover", Icons.Default.LayersClear, "image_bg_remover", "Image Toolbox", Color(0xFFF44336), BadgeType.NEW),
        Tool("Base64 Tools", Icons.Default.Code, "image_base64", "Image Toolbox", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Collage Maker", Icons.Default.AutoAwesomeMosaic, "image_collage", "Image Toolbox", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Color Picker", Icons.Default.Palette, "image_color_picker", "Image Toolbox", Color(0xFFCDDC39), BadgeType.NEW),
        Tool("Compare", Icons.Default.Compare, "image_compare", "Image Toolbox", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Crop", Icons.Default.Crop, "image_crop", "Image Toolbox", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Delete EXIF", Icons.Default.NoPhotography, "image_delete_exif", "Image Toolbox", Color(0xFFF44336), BadgeType.NEW),
        Tool("Draw", Icons.Default.Brush, "image_draw", "Image Toolbox", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Edit EXIF", Icons.Default.CameraAlt, "image_edit_exif", "Image Toolbox", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Filter", Icons.Default.FilterHdr, "image_filter", "Image Toolbox", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Format Conversion", Icons.Default.Transform, "image_format_conv", "Image Toolbox", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Image Cutting", Icons.Default.GridOn, "image_cutting", "Image Toolbox", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Image Preview", Icons.Default.Image, "image_preview", "Image Toolbox", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Image Stacking", Icons.Default.Layers, "image_stacking", "Image Toolbox", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Image Stitching", Icons.Default.ViewArray, "image_stitching", "Image Toolbox", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Images to SVG", Icons.Default.Architecture, "image_to_svg", "Image Toolbox", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Markup Layers", Icons.Default.DashboardCustomize, "image_markup", "Image Toolbox", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Noise Generation", Icons.Default.BlurOn, "image_noise_gen", "Image Toolbox", Color(0xFF9E9E9E), BadgeType.NEW),
        Tool("OCR", Icons.Default.ManageSearch, "image_ocr", "Image Toolbox", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Palette Tools", Icons.Default.ColorLens, "image_palette", "Image Toolbox", Color(0xFF009688), BadgeType.NEW),
        Tool("Resize and Convert", Icons.Default.PhotoSizeSelectLarge, "image_resize_conv", "Image Toolbox", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Resize by Limits", Icons.Default.AspectRatio, "image_resize_limits", "Image Toolbox", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Resize by Weight", Icons.Default.Scale, "image_resize_weight", "Image Toolbox", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Single Edit", Icons.Default.Edit, "image_single_edit", "Image Toolbox", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Wallpapers Export", Icons.Default.Wallpaper, "image_wallpapers", "Image Toolbox", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Watermarking", Icons.Default.BrandingWatermark, "image_watermark", "Image Toolbox", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Web Image Loading", Icons.Default.CloudDownload, "image_web_load", "Image Toolbox", Color(0xFF03A9F4), BadgeType.NEW),
        Tool("Mask Filter", Icons.Default.Texture, "image_mask_filter", "Image Toolbox", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Draw on background", Icons.Default.Brush, "image_draw_bg", "Image Toolbox", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Layers on image", Icons.Default.Layers, "image_layers_img", "Image Toolbox", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Layers on background", Icons.Default.Layers, "image_layers_bg", "Image Toolbox", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Open project", Icons.Default.FolderOpen, "image_open_project", "Image Toolbox", Color(0xFF607D8B), BadgeType.NEW),

        // Media
        Tool("Color Picker", Icons.Default.Palette, "color_picker", "Media", Color(0xFFCDDC39)),
        Tool("Exif Viewer", Icons.Default.CameraAlt, "exif_viewer", "Media", Color(0xFF607D8B)),
        Tool("Gradient Gen", Icons.Default.Gradient, "gradient_gen", "Media", Color(0xFFFFEB3B)),
        Tool("Image Compressor", Icons.Default.Compress, "image_compress", "Media", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Media Grabber", Icons.Default.Download, "media_grabber", "Media", Color(0xFFFFC107)),
        Tool("Photo Filters", Icons.Default.Filter, "photo_filters", "Media", Color(0xFFE91E63), BadgeType.NEW),

        // Network
        Tool("DNS Lookup", Icons.Default.Dns, "dns_lookup", "Network", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("My IP", Icons.Default.Public, "my_ip", "Network", Color(0xFF00BCD4)),
        Tool("Network Details", Icons.Default.NetworkCheck, "network_info", "Network", Color(0xFF2196F3)),
        Tool("Ping", Icons.Default.SettingsEthernet, "ping", "Network", Color(0xFF009688)),
        Tool("Port Scanner", Icons.Default.Search, "port_scanner", "Network", Color(0xFFF44336)),
        Tool("Whois", Icons.Default.QuestionMark, "whois", "Network", Color(0xFF607D8B), BadgeType.NEW),

        // Other Tools
        Tool("Audio Info", Icons.Default.AudioFile, "audio_info", "Other Tools", Color(0xFF795548), BadgeType.NEW),
        Tool("Device Codec", Icons.Default.PermDeviceInformation, "device_codec", "Other Tools", Color(0xFF3F51B5), BadgeType.PREMIUM),
        Tool("Metronome", Icons.Default.AvTimer, "metronome", "Other Tools", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Video Info", Icons.Default.VideoFile, "video_info", "Other Tools", Color(0xFF673AB7), BadgeType.NEW),

        // Output
        Tool("Audio Output", Icons.Default.LibraryMusic, "audio_output", "Output", Color(0xFF2196F3)),
        Tool("Video Output", Icons.Default.VideoLibrary, "video_output", "Output", Color(0xFF4CAF50)),

        // Productivity
        Tool("Checklist", Icons.Default.Checklist, "checklist", "Productivity", Color(0xFFCDDC39)),
        Tool("Note Pad", Icons.Default.NoteAlt, "note", "Productivity", Color(0xFF8BC34A)),
        Tool("Pomodoro", Icons.Default.HourglassEmpty, "pomodoro", "Productivity", Color(0xFFF44336)),
        Tool("Task Board", Icons.Default.Dashboard, "task_board", "Productivity", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Time Logger", Icons.Default.HistoryToggleOff, "time_logger", "Productivity", Color(0xFF673AB7), BadgeType.NEW),

        // Recording Tools
        Tool("Fun Recording", Icons.Default.Mood, "fun_record", "Recording Tools", Color(0xFFCDDC39)),
        Tool("Karaoke Effect", Icons.Default.Mic, "karaoke_effect", "Recording Tools", Color(0xFFCDDC39)),
        Tool("Record Audio", Icons.Default.Mic, "record_audio", "Recording Tools", Color(0xFFE91E63)),
        Tool("Voice Memo", Icons.Default.SettingsVoice, "voice_memo", "Recording Tools", Color(0xFF2196F3), BadgeType.NEW),

        // Sensors
        Tool("Altimeter", Icons.Default.Landscape, "altimeter", "Sensors", Color(0xFF795548), BadgeType.NEW),
        Tool("Barometer", Icons.Default.Compress, "barometer", "Sensors", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Compass", Icons.Default.Explore, "compass", "Sensors", Color(0xFF2196F3)),
        Tool("Level", Icons.Default.Architecture, "level", "Sensors", Color(0xFF03A9F4)),
        Tool("Light Meter", Icons.Default.LightMode, "light", "Sensors", Color(0xFF00BCD4)),
        Tool("Metal Detector", Icons.Default.CompassCalibration, "metal", "Sensors", Color(0xFF009688)),
        Tool("Sensor Data", Icons.Default.SettingsInputComponent, "sensor_data", "Sensors", Color(0xFF4CAF50)),
        Tool("SPL Meter", Icons.Default.VolumeUp, "spl_meter", "Sensors", Color(0xFFF44336)),

        // Hardware
        Tool("Battery", Icons.Default.BatteryFull, "battery", "Hardware", Color(0xFFFF5722)),
        Tool("CPU Info", Icons.Default.Memory, "cpu_info", "Hardware", Color(0xFF795548)),
        Tool("Device Info", Icons.Default.Info, "device", "Hardware", Color(0xFF607D8B)),
        Tool("Hardware ID", Icons.Default.PermDeviceInformation, "device_id", "Hardware", Color(0xFF607D8B)),
        Tool("Sensors List", Icons.Default.List, "sensors_list", "Hardware", Color(0xFF4CAF50)),
        Tool("Storage", Icons.Default.Storage, "storage", "Hardware", Color(0xFF9E9E9E)),

        // Privacy
        Tool("App Permissions", Icons.Default.Security, "app_permissions", "Privacy", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Privacy Check", Icons.Default.PrivacyTip, "privacy_check", "Privacy", Color(0xFFF44336), BadgeType.NEW),

        // Science
        Tool("Base Converter", Icons.Default.Numbers, "base_conv", "Science", Color(0xFF673AB7)),
        Tool("Constants Table", Icons.Default.Functions, "constants", "Science", Color(0xFFF44336)),
        Tool("Periodic Table", Icons.Default.GridOn, "periodic_table", "Science", Color(0xFFFFEB3B)),
        Tool("Pokedex", Icons.Default.CatchingPokemon, "pokedex", "Science", Color(0xFFFFC107)),

        // Astronomy
        Tool("Star Map", Icons.Default.AutoAwesome, "star_map", "Astronomy", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Constellations", Icons.Default.Stars, "constellations", "Astronomy", Color(0xFF673AB7)),

        // Survival
        Tool("Emergency SOS", Icons.Default.Sos, "sos", "Survival", Color(0xFFF44336), BadgeType.NEW),
        Tool("Signal Mirror", Icons.Default.FlashlightOn, "signal_mirror", "Survival", Color(0xFFFFC107)),

        // Physics
        Tool("Formula Sheet", Icons.Default.Functions, "physics_formulas", "Physics", Color(0xFF4CAF50), BadgeType.NEW),

        // Security
        Tool("Hash Generator", Icons.Default.Fingerprint, "hash_gen", "Security", Color(0xFF607D8B)),
        Tool("Password Manager", Icons.Default.Password, "password_manager", "Security", Color(0xFF4CAF50)),

        // System
        Tool("App Info", Icons.Default.Apps, "app_info", "System", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Update Check", Icons.Default.SystemUpdate, "update_check", "System", Color(0xFF4CAF50), BadgeType.NEW),

        // Text
        Tool("Anagram Finder", Icons.Default.SortByAlpha, "anagram", "Text", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Case Converter", Icons.Default.TextFields, "case_converter", "Text", Color(0xFF9C27B0)),
        Tool("Lorem Ipsum", Icons.Default.Notes, "lorem", "Text", Color(0xFF607D8B)),
        Tool("Morse Code", Icons.Default.Language, "morse", "Text", Color(0xFF673AB7)),
        Tool("Morse Decoder", Icons.Default.Language, "morse_decoder", "Text", Color(0xFF03A9F4), BadgeType.NEW),
        Tool("Text Diff", Icons.Default.Difference, "text_diff", "Text", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Word Counter", Icons.Default.Abc, "word_counter", "Text", Color(0xFFE91E63)),

        // Utility
        Tool("BPM Counter", Icons.Default.Favorite, "bpm", "Utility", Color(0xFF3F51B5)),
        Tool("Clock", Icons.Default.Schedule, "clock", "Utility", Color(0xFF2196F3)),
        Tool("Flashlight", Icons.Default.FlashlightOn, "flashlight", "Utility", Color(0xFFF44336)),
        Tool("Hub", Icons.Default.Hub, "hub", "Utility", Color(0xFF00BCD4)),
        Tool("QR Generator", Icons.Default.QrCode, "qr_gen", "Utility", Color(0xFF8BC34A)),
        Tool("Random Gen", Icons.Default.Casino, "random", "Utility", Color(0xFFE91E63)),
        Tool("Ruler", Icons.Default.Straighten, "ruler", "Utility", Color(0xFF9E9E9E), BadgeType.NEW),
        Tool("Stopwatch", Icons.Default.Timer, "stopwatch", "Utility", Color(0xFF9C27B0)),
        Tool("Unit Price Calc", Icons.Default.PriceCheck, "unit_price", "Utility", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Vibration Test", Icons.Default.Vibration, "vibration", "Utility", Color(0xFFFF5722)),
        Tool("Web Search", Icons.Default.Search, "web", "Utility", Color(0xFF009688)),
        Tool("World Clock", Icons.Default.Public, "world_clock", "Utility", Color(0xFF673AB7)),

        // Video Tools
        Tool("Delete Segment", Icons.Default.Delete, "video_delete", "Video Tools", Color(0xFFF44336)),
        Tool("Loop Video", Icons.Default.Loop, "video_loop", "Video Tools", Color(0xFFFF5722)),
        Tool("Mix Video Audio", Icons.Default.Tune, "mix_video_audio", "Video Tools", Color(0xFF2196F3)),
        Tool("Reverse Video", Icons.Default.History, "video_reverse", "Video Tools", Color(0xFF795548)),
        Tool("Silence Video", Icons.AutoMirrored.Filled.VolumeOff, "video_silence", "Video Tools", Color(0xFF9E9E9E)),
        Tool("Video Compressor", Icons.Default.Compress, "video_compress", "Video Tools", Color(0xFF673AB7)),
        Tool("Video Editor", Icons.Default.Edit, "video_trim", "Video Tools", Color(0xFFF44336)),
        Tool("Video SFX", Icons.Default.AutoAwesome, "video_sfx", "Video Tools", Color(0xFF3F51B5)),
        Tool("Video Speed", Icons.Default.FastForward, "video_speed_changer", "Video Tools", Color(0xFF607D8B)),
        Tool("Video Splitter", Icons.AutoMirrored.Filled.AltRoute, "video_splitter", "Video Tools", Color(0xFF00BCD4)),
        Tool("Video Stabilizer", Icons.Default.Camera, "video_stabilizer", "Video Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Video to Audio", Icons.Default.VideoLibrary, "m_video_to_audio", "Video Tools", Color(0xFFFFC107)),
        Tool("Video To GIF", Icons.Default.Gif, "video_to_gif", "Video Tools", Color(0xFFE91E63)),
        Tool("Video Volume", Icons.AutoMirrored.Filled.VolumeUp, "video_volume_booster", "Video Tools", Color(0xFFFFC107))
    )
}
