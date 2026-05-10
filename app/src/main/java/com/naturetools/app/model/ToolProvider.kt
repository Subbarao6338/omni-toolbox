package com.naturetools.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class BadgeType { NONE, NEW, PREMIUM }

data class Tool(val name: String, val icon: ImageVector, val route: String, val category: String, val color: Color = Color.Unspecified, val badge: BadgeType = BadgeType.NONE)

object ToolProvider {
    val tools = listOf(
        // AI & Data
        Tool("AI Chat", Icons.AutoMirrored.Filled.Chat, "ai_chat", "AI Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("AI Code Assistant", Icons.Default.Code, "ai_code", "AI Tools", Color(0xFF673AB7), BadgeType.NEW),
        Tool("AI Summarizer", Icons.Default.Summarize, "ai_summarizer", "AI Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("CSV to JSON", Icons.Default.Transform, "csv_to_json", "AI Tools", Color(0xFF009688), BadgeType.NEW),
        Tool("Data Visualizer", Icons.Default.BarChart, "data_viz", "AI Tools", Color(0xFF4CAF50), BadgeType.NONE),
        Tool("Markdown Preview", Icons.Default.Description, "markdown_preview", "AI Tools", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Regex Tester", Icons.Default.Code, "regex_tester", "AI Tools", Color(0xFF673AB7), BadgeType.NONE),

        // AI Advanced
        Tool("AI Grammar", Icons.Default.Spellcheck, "ai_grammar", "AI Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("AI Object Detect", Icons.Default.CenterFocusStrong, "ai_obj_detect", "AI Tools", Color(0xFF673AB7), BadgeType.NEW),
        Tool("AI Sentiment", Icons.Default.Mood, "ai_sentiment", "AI Tools", Color(0xFFFFC107), BadgeType.NEW),
        Tool("AI Text Extractor", Icons.Default.Scanner, "ai_text_ext", "AI Tools", Color(0xFFE91E63), BadgeType.NEW),
        Tool("AI Translation", Icons.Default.Translate, "ai_translate", "AI Tools", Color(0xFF2196F3), BadgeType.NEW),

        // AI Tools
        Tool("AI Image Gen", Icons.Default.Image, "ai_image", "AI Tools", Color(0xFFFFC107), BadgeType.NEW),
        Tool("AI Noise Remover", Icons.Default.Hearing, "ai_noise_remover", "AI Tools", Color(0xFF4CAF50), BadgeType.NONE),
        Tool("AI Stems Splitter", Icons.AutoMirrored.Filled.AltRoute, "ai_stems_splitter", "AI Tools", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("AI Voice Mimic", Icons.Default.RecordVoiceOver, "ai_voice_mimic", "AI Tools", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Audio Noise Remover", Icons.Default.MicOff, "audio_noise_remover", "AI Tools", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Echo Remover", Icons.Default.SettingsBackupRestore, "echo_remover", "AI Tools", Color(0xFF009688), BadgeType.NONE),
        Tool("Key BPM Finder", Icons.Default.MusicNote, "key_bpm_finder", "AI Tools", Color(0xFFF44336), BadgeType.NONE),
        Tool("Reverb Remover", Icons.Default.Waves, "reverb_remover", "AI Tools", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Video Noise Remover", Icons.Default.MovieFilter, "video_noise_remover", "AI Tools", Color(0xFF9C27B0), BadgeType.NONE),
        Tool("Vocal AutoTuner", Icons.Default.SettingsVoice, "vocal_autotuner", "AI Tools", Color(0xFFE91E63), BadgeType.NONE),
        Tool("Vocal Remover", Icons.Default.PersonOff, "vocal_remover", "AI Tools", Color(0xFFFF9800), BadgeType.NONE),

        // Astronomy
        Tool("Constellations", Icons.Default.Stars, "constellations", "Science & Education", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Planet Finder", Icons.Default.BrightnessHigh, "planet_finder", "Science & Education", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Star Map", Icons.Default.AutoAwesome, "star_map", "Science & Education", Color(0xFF3F51B5), BadgeType.NEW),

        // Audio Tools
        Tool("3D Audio", Icons.Default.Headset, "m_3d_audio", "Audio & Music", Color(0xFFE91E63), BadgeType.NONE),
        Tool("Add Audio to Video", Icons.Default.LibraryMusic, "aud_to_vid", "Audio & Music", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Add SFX", Icons.Default.AutoAwesome, "add_sfx", "Audio & Music", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Audio Compressor", Icons.Default.Compress, "m_audio_compressor", "Audio & Music", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Audio Converter", Icons.Default.Transform, "aud_conv", "Audio & Music", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Audio Cutter", Icons.Default.ContentCut, "m_audio_cutter", "Audio & Music", Color(0xFFF44336), BadgeType.NONE),
        Tool("Audio Editor", Icons.Default.Edit, "m_audio_editor", "Audio & Music", Color(0xFFF44336), BadgeType.NONE),
        Tool("Audio Equalizer", Icons.Default.Equalizer, "aud_eq_v2", "Audio & Music", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Audio Info", Icons.Default.Info, "aud_info_v2", "Audio & Music", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Audio Joiner", Icons.Default.Link, "m_audio_joiner", "Audio & Music", Color(0xFF4CAF50), BadgeType.NONE),
        Tool("Audio Loop", Icons.Default.Loop, "audio_loop", "Audio & Music", Color(0xFFF44336), BadgeType.NONE),
        Tool("Audio Mixer", Icons.Default.Tune, "m_audio_mixer", "Audio & Music", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Audio Normalizer", Icons.AutoMirrored.Filled.VolumeUp, "m_audio_normalizer", "Audio & Music", Color(0xFF795548), BadgeType.NONE),
        Tool("Audio Pan", Icons.AutoMirrored.Filled.AltRoute, "m_audio_pan", "Audio & Music", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("Audio Reverb", Icons.Default.Waves, "audio_reverb", "Audio & Music", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Audio Splitter", Icons.AutoMirrored.Filled.AltRoute, "m_audio_splitter", "Audio & Music", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("Audio Tag Editor", Icons.AutoMirrored.Filled.Label, "m_audio_tag_editor", "Audio & Music", Color(0xFFFF9800), BadgeType.NONE),
        Tool("Bass Booster", Icons.Default.Speaker, "m_bass_booster", "Audio & Music", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Echo Effect", Icons.Default.SettingsBackupRestore, "m_echo_effect", "Audio & Music", Color(0xFF009688), BadgeType.NONE),
        Tool("Equalizer", Icons.Default.Equalizer, "m_equalizer", "Audio & Music", Color(0xFF8BC34A), BadgeType.NONE),
        Tool("Freq Generator", Icons.Default.GraphicEq, "freq_gen", "Audio & Music", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Karaoke Maker", Icons.Default.Mic, "m_karaoke_maker", "Audio & Music", Color(0xFFCDDC39), BadgeType.NONE),
        Tool("Mute Audio", Icons.AutoMirrored.Filled.VolumeOff, "m_mute_audio", "Audio & Music", Color(0xFF9E9E9E), BadgeType.NONE),
        Tool("Noise Generator", Icons.Default.GraphicEq, "noise_generator", "Audio & Music", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Pitch Changer", Icons.Default.Height, "m_audio_pitch", "Audio & Music", Color(0xFFFF5722), BadgeType.NONE),
        Tool("Reverse Audio", Icons.Default.History, "m_reverse_audio", "Audio & Music", Color(0xFF795548), BadgeType.NONE),
        Tool("Ringtone Maker", Icons.Default.Notifications, "m_ringtone_maker", "Audio & Music", Color(0xFFFF5722), BadgeType.NONE),
        Tool("Silence Generator", Icons.Default.DoNotDisturbOn, "silence_generator", "Audio & Music", Color(0xFFFF5722), BadgeType.NONE),
        Tool("Silence Remover", Icons.Default.SpeakerNotesOff, "m_silence_remover", "Audio & Music", Color(0xFFF44336), BadgeType.NONE),
        Tool("Sound Mastering", Icons.Default.Insights, "sound_mastering", "Audio & Music", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Speech to Text", Icons.Default.Mic, "m_speech_to_text", "Audio & Music", Color(0xFFE91E63), BadgeType.NONE),
        Tool("Speed Changer", Icons.Default.FastForward, "m_speed_changer", "Audio & Music", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Stereo Widener", Icons.Default.SettingsInputComponent, "stereo_widener", "Audio & Music", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Text to Speech", Icons.Default.RecordVoiceOver, "m_text_to_speech", "Audio & Music", Color(0xFF009688), BadgeType.NONE),
        Tool("Voice Changer", Icons.Default.Face, "m_voice_changer", "Audio & Music", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Volume Booster", Icons.AutoMirrored.Filled.VolumeUp, "m_volume_booster", "Audio & Music", Color(0xFFFFC107), BadgeType.NONE),
        Tool("Wave Generator", Icons.Default.Waves, "wave_generator", "Audio & Music", Color(0xFF8BC34A), BadgeType.NONE),

        // Advanced Media
        Tool("Video To Audio Pro", Icons.Default.AudioFile, "vid_to_aud_pro", "Video & Media", Color(0xFFFF9800), BadgeType.PREMIUM),
        Tool("Batch Image Pro", Icons.Default.Collections, "batch_img_pro", "Video & Media", Color(0xFF4CAF50), BadgeType.PREMIUM),
        Tool("Media Grabber Pro", Icons.Default.CloudDownload, "media_grab_pro", "Video & Media", Color(0xFF2196F3), BadgeType.PREMIUM),
        Tool("Video Editor Pro", Icons.Default.MovieFilter, "vid_edit_pro", "Video & Media", Color(0xFFE91E63), BadgeType.PREMIUM),
        Tool("Audio Mastering Pro", Icons.Default.GraphicEq, "aud_master_pro", "Video & Media", Color(0xFF673AB7), BadgeType.PREMIUM),

        // Automotive
        Tool("Car Maintenance", Icons.Default.Build, "car_maintenance", "Utility & Misc", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Fuel Consumption", Icons.Default.LocalGasStation, "fuel_consumption", "Utility & Misc", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Speedometer", Icons.Default.Speed, "speedometer", "Utility & Misc", Color(0xFFF44336), BadgeType.NEW),

        // Batch Processing
        Tool("Multi Convert", Icons.Default.Autorenew, "multi_convert", "Video & Media", Color(0xFF4CAF50), BadgeType.NONE),
        Tool("Multi Crop", Icons.Default.Crop, "multi_crop", "Video & Media", Color(0xFFF44336), BadgeType.NEW),
        Tool("Multi Image Resize", Icons.Default.PhotoSizeSelectLarge, "multi_image_resize", "Video & Media", Color(0xFF8BC34A), BadgeType.NEW),
        Tool("Multi Mix Audio", Icons.Default.Tune, "multi_mix", "Video & Media", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Multi Video To Audio", Icons.Default.MusicVideo, "multi_video_to_audio", "Video & Media", Color(0xFFFF9800), BadgeType.NONE),
        Tool("Multi Volume Booster", Icons.AutoMirrored.Filled.VolumeUp, "multi_volume_booster", "Video & Media", Color(0xFF9C27B0), BadgeType.NONE),

        // Calculation
        Tool("Binary Calc", Icons.Default.Numbers, "binary_calc", "Calculation", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Calculator", Icons.Default.Calculate, "calculator", "Calculation", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Date Calc", Icons.Default.CalendarToday, "date_calc", "Calculation", Color(0xFF9E9E9E), BadgeType.NONE),
        Tool("Discount Calc", Icons.Default.Percent, "discount", "Calculation", Color(0xFFF44336), BadgeType.NONE),
        Tool("Fuel Cost", Icons.Default.LocalGasStation, "fuel", "Calculation", Color(0xFF9C27B0), BadgeType.NONE),
        Tool("Mortgage Calc", Icons.Default.Home, "mortgage_calc", "Calculation", Color(0xFF795548), BadgeType.NEW),
        Tool("Scientific Calc", Icons.Default.Functions, "sci_calc", "Calculation", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Tip Calc", Icons.Default.Receipt, "tip", "Calculation", Color(0xFFE91E63), BadgeType.NONE),

        // Conversion
        Tool("Base Converter", Icons.Default.Numbers, "base_conv", "Conversion", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Currency", Icons.Default.CurrencyExchange, "currency", "Conversion", Color(0xFF03A9F4), BadgeType.NONE),
        Tool("Torque Converter", Icons.Default.SyncAlt, "torque_conv", "Conversion", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Unit Converter", Icons.Default.SwapHoriz, "converter", "Conversion", Color(0xFF2196F3), BadgeType.NONE),

        // Cryptocurrency
        Tool("Coin Tracker", Icons.Default.MonetizationOn, "coin_tracker", "Finance & Crypto", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Crypto Converter", Icons.Default.CurrencyExchange, "crypto_conv", "Finance & Crypto", Color(0xFFF44336), BadgeType.NEW),
        Tool("NFT Viewer", Icons.Default.Token, "nft_viewer", "Finance & Crypto", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Wallet Explorer", Icons.Default.AccountBalanceWallet, "wallet_explorer", "Finance & Crypto", Color(0xFF673AB7), BadgeType.NEW),

        // Developer
        Tool("Base64 Tool", Icons.Default.Code, "base64", "Developer", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("JSON Format", Icons.Default.DataObject, "json", "Developer", Color(0xFF2196F3), BadgeType.NONE),
        Tool("JWT Tool", Icons.Default.Key, "jwt_tool", "Developer", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Subnet Calc", Icons.Default.SettingsEthernet, "subnet_calc", "Developer", Color(0xFF009688), BadgeType.NEW),
        Tool("URL Encoder", Icons.Default.Link, "url_encoder", "Developer", Color(0xFF03A9F4), BadgeType.NONE),
        Tool("YAML to JSON", Icons.Default.Transform, "yaml_to_json", "Developer", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Hex Viewer", Icons.Default.Numbers, "hex_viewer", "Developer", Color(0xFF607D8B), BadgeType.NEW),
        Tool("ASCII Table", Icons.Default.Notes, "ascii_table", "Developer", Color(0xFF9E9E9E), BadgeType.NEW),
        Tool("Binary Pro", Icons.Default.CodeOff, "binary_pro", "Developer", Color(0xFF795548), BadgeType.PREMIUM),
        Tool("Regex Pro", Icons.Default.Rule, "regex_pro", "Developer", Color(0xFF3F51B5), BadgeType.PREMIUM),

        // Developer Pro
        Tool("Color Converter Pro", Icons.Default.Palette, "color_conv_pro", "Developer", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Crontab Gen", Icons.Default.Schedule, "crontab_gen", "Developer", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Port Checker", Icons.Default.Dns, "port_checker", "Developer", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("SQL Formatter", Icons.Default.Storage, "sql_format", "Developer", Color(0xFF00BCD4), BadgeType.NEW),

        // Education
        Tool("Prime Checker", Icons.Default.Filter7, "prime", "Science & Education", Color(0xFFFF9800), BadgeType.NONE),
        Tool("Solar System", Icons.Default.Public, "solar_system", "Science & Education", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("World Map", Icons.Default.Map, "world_map", "Science & Education", Color(0xFF4CAF50), BadgeType.NEW),

        // Electronics
        Tool("Circuit Calc", Icons.Default.Memory, "circuit_calc", "Engineering", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Ohm's Law", Icons.Default.ElectricalServices, "ohms_law", "Engineering", Color(0xFFFF9800), BadgeType.NEW),

        // Engineering
        Tool("Antenna Calc", Icons.Default.SettingsInputAntenna, "antenna_calc", "Engineering", Color(0xFF795548), BadgeType.NEW),
        Tool("Filter Designer", Icons.Default.FilterList, "filter_design", "Engineering", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Logic Gates", Icons.Default.SettingsInputComponent, "logic_gates", "Engineering", Color(0xFF607D8B), BadgeType.NEW),
        Tool("PCB Trace Width", Icons.Default.Straighten, "pcb_trace", "Engineering", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Resistor Color Code", Icons.Default.Architecture, "resistor_code", "Engineering", Color(0xFF795548), BadgeType.NEW),
        Tool("Signal Gen Pro", Icons.Default.GraphicEq, "signal_gen_pro", "Engineering", Color(0xFFE91E63), BadgeType.NEW),

        // Environment
        Tool("Air Quality", Icons.Default.Air, "air_quality", "Outdoor & Nature", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("Light Pollution", Icons.Default.NightsStay, "light_pollution", "Outdoor & Nature", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Moon Phase", Icons.Default.Brightness3, "moon_phase", "Outdoor & Nature", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Rain Radar", Icons.Default.Water, "rain_radar", "Outdoor & Nature", Color(0xFF2196F3), BadgeType.NEW),
        Tool("UV Index", Icons.Default.WbSunny, "uv_index", "Outdoor & Nature", Color(0xFFFF9800), BadgeType.NONE),

        // Files
        Tool("Duplicate Finder", Icons.Default.ContentCopy, "duplicate_finder", "Files & Documents", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("File Explorer", Icons.Default.Folder, "file_explorer", "Files & Documents", Color(0xFF607D8B), BadgeType.NEW),
        Tool("File Shredder", Icons.Default.DeleteForever, "file_shredder", "Files & Documents", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Storage Cleaner", Icons.Default.CleaningServices, "storage_cleaner", "Files & Documents", Color(0xFFF44336), BadgeType.NEW),

        Tool("Zip/Unzip", Icons.Default.FolderZip, "zip_unzip", "Files & Documents", Color(0xFFFF9800), BadgeType.NEW),

        // Finance
        Tool("Compound Interest", Icons.AutoMirrored.Filled.TrendingUp, "compound_interest", "Finance & Crypto", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Currency Trends", Icons.Default.Timeline, "currency_trends", "Finance & Crypto", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Expense Tracker", Icons.Default.AccountBalanceWallet, "expense_tracker", "Finance & Crypto", Color(0xFF009688), BadgeType.NEW),
        Tool("Loan Calculator", Icons.Default.AccountBalance, "loan_calc", "Finance & Crypto", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Tax Calculator", Icons.Default.MoneyOff, "tax_calc", "Finance & Crypto", Color(0xFFF44336), BadgeType.NONE),

        // Finance Pro
        Tool("Dividend Calc", Icons.Default.Payments, "dividend_calc", "Finance & Crypto", Color(0xFF8BC34A), BadgeType.NEW),
        Tool("Inflation Calc", Icons.Default.MoneyOff, "inflation_calc", "Finance & Crypto", Color(0xFFF44336), BadgeType.NEW),
        Tool("ROI Calculator", Icons.AutoMirrored.Filled.ShowChart, "roi_calc", "Finance & Crypto", Color(0xFF009688), BadgeType.NEW),
        Tool("Salary Calc", Icons.Default.Work, "salary_calc", "Finance & Crypto", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Stock Profit", Icons.AutoMirrored.Filled.TrendingUp, "stock_profit", "Finance & Crypto", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("SIP Calculator", Icons.Default.PieChart, "sip_calc", "Finance & Crypto", Color(0xFF009688), BadgeType.NEW),
        Tool("GST Calculator", Icons.Default.RequestQuote, "gst_calc", "Finance & Crypto", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Retirement Planner", Icons.Default.EventAvailable, "retirement_planner", "Finance & Crypto", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Compound Interest Pro", Icons.Default.TrendingUp, "compound_pro", "Finance & Crypto", Color(0xFF4CAF50), BadgeType.PREMIUM),
        Tool("Loan EMI Pro", Icons.Default.CreditCard, "loan_emi_pro", "Finance & Crypto", Color(0xFFF44336), BadgeType.PREMIUM),
        Tool("Unit Price Pro", Icons.Default.PriceChange, "unit_price_pro", "Finance & Crypto", Color(0xFFFFC107), BadgeType.PREMIUM),

        // Games
        Tool("Coin Flip", Icons.Default.Paid, "coin_flip", "Utility & Misc", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Dice Roller", Icons.Default.Casino, "dice_roller", "Utility & Misc", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Memory Game", Icons.Default.Extension, "memory_game", "Utility & Misc", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Number Guessing", Icons.Default.QuestionMark, "number_guessing", "Utility & Misc", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Tic Tac Toe", Icons.Default.Close, "tic_tac_toe", "Utility & Misc", Color(0xFF4CAF50), BadgeType.NEW),

        // Hardware
        Tool("Battery", Icons.Default.BatteryFull, "battery", "System & Sensors", Color(0xFFFF5722), BadgeType.NONE),
        Tool("CPU Info", Icons.Default.Memory, "cpu_info", "System & Sensors", Color(0xFF795548), BadgeType.NONE),
        Tool("Device Info", Icons.Default.Info, "device", "System & Sensors", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Hardware ID", Icons.Default.PermDeviceInformation, "device_id", "System & Sensors", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Sensors List", Icons.AutoMirrored.Filled.List, "sensors_list", "System & Sensors", Color(0xFF4CAF50), BadgeType.NONE),
        Tool("Storage", Icons.Default.Storage, "storage", "System & Sensors", Color(0xFF9E9E9E), BadgeType.NONE),
        Tool("Thermal Info", Icons.Default.DeviceThermostat, "thermal_info", "System & Sensors", Color(0xFFF44336), BadgeType.NEW),

        // Health
        Tool("BMI Calc", Icons.Default.AccessibilityNew, "bmi", "Health & Lifestyle", Color(0xFFFF9800), BadgeType.NONE),
        Tool("BMR Calculator", Icons.Default.Calculate, "bmr", "Health & Lifestyle", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Calorie Calc", Icons.Default.Restaurant, "calorie_calc", "Health & Lifestyle", Color(0xFFE91E63), BadgeType.NONE),
        Tool("Eye Exercise", Icons.Default.Visibility, "eye_exercise", "Health & Lifestyle", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Medication Tracker", Icons.Default.MedicalServices, "medication_tracker", "Health & Lifestyle", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Posture Checker", Icons.Default.Accessibility, "posture_check", "Health & Lifestyle", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Sleep Tracker", Icons.Default.Bedtime, "sleep_tracker", "Health & Lifestyle", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Step Counter", Icons.AutoMirrored.Filled.DirectionsRun, "step_counter", "Health & Lifestyle", Color(0xFFFF5722), BadgeType.NONE),
        Tool("Stretching Guide", Icons.Default.SelfImprovement, "stretch_guide", "Health & Lifestyle", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Water Reminder", Icons.Default.NotificationsActive, "water_reminder", "Health & Lifestyle", Color(0xFF03A9F4), BadgeType.NEW),
        Tool("Water Tracker", Icons.Default.LocalDrink, "water", "Health & Lifestyle", Color(0xFF795548), BadgeType.NONE),
        Tool("Yoga Guide", Icons.Default.SelfImprovement, "yoga_guide", "Health & Lifestyle", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Period Tracker", Icons.Default.CalendarMonth, "period_tracker", "Health & Lifestyle", Color(0xFFE91E63), BadgeType.NEW),

        // Image Toolbox
        Tool("AI Tools", Icons.Default.Psychology, "image_ai_tools", "Image Tools", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("APNG Tools", Icons.Default.Animation, "apng_tools", "Image Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Background Remover", Icons.Default.LayersClear, "image_bg_remover", "Image Tools", Color(0xFFF44336), BadgeType.NEW),
        Tool("Base64 Tools", Icons.Default.Code, "image_base64", "Image Tools", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Collage Maker", Icons.Default.AutoAwesomeMosaic, "image_collage", "Image Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Color & Palette Tools", Icons.Default.Palette, "color_palette_tools", "Image Tools", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Color Picker", Icons.Default.Palette, "image_color_picker", "Image Tools", Color(0xFFCDDC39), BadgeType.NEW),
        Tool("Compare", Icons.Default.Compare, "image_compare", "Image Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Crop", Icons.Default.Crop, "image_crop", "Image Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Delete EXIF", Icons.Default.NoPhotography, "image_delete_exif", "Image Tools", Color(0xFFF44336), BadgeType.NEW),
        Tool("Draw", Icons.Default.Brush, "image_draw", "Image Tools", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Draw on background", Icons.Default.Brush, "image_draw_bg", "Image Tools", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Edit EXIF", Icons.Default.CameraAlt, "image_edit_exif", "Image Tools", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Filter", Icons.Default.FilterHdr, "image_filter", "Image Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Format Conversion", Icons.Default.Transform, "image_format_conv", "Image Tools", Color(0xFF673AB7), BadgeType.NEW),
        Tool("GIF Tools", Icons.Default.Animation, "gif_tools", "Image Tools", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Image Cutting", Icons.Default.GridOn, "image_cutting", "Image Tools", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Image Preview", Icons.Default.Image, "image_preview", "Image Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Image Stacking", Icons.Default.Layers, "image_stacking", "Image Tools", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Image Stitching", Icons.Default.ViewArray, "image_stitching", "Image Tools", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Images to SVG", Icons.Default.Architecture, "image_to_svg", "Image Tools", Color(0xFFE91E63), BadgeType.NEW),
        Tool("JXL Tools", Icons.Default.Animation, "jxl_tools", "Image Tools", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Layers on background", Icons.Default.Layers, "image_layers_bg", "Image Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Layers on image", Icons.Default.Layers, "image_layers_img", "Image Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Markup Layers", Icons.Default.DashboardCustomize, "image_markup", "Image Tools", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Mask Filter", Icons.Default.Texture, "image_mask_filter", "Image Tools", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Noise Generation", Icons.Default.BlurOn, "image_noise_gen", "Image Tools", Color(0xFF9E9E9E), BadgeType.NEW),
        Tool("OCR", Icons.AutoMirrored.Filled.ManageSearch, "image_ocr", "Image Tools", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Open project", Icons.Default.FolderOpen, "image_open_project", "Image Tools", Color(0xFF607D8B), BadgeType.NEW),
        Tool("PDF Tools", Icons.Default.PictureAsPdf, "pdf_tools", "Image Tools", Color(0xFFF44336), BadgeType.NEW),
        Tool("Palette Tools", Icons.Default.ColorLens, "image_palette", "Image Tools", Color(0xFF009688), BadgeType.NEW),
        Tool("Pixel Art Maker", Icons.Default.Grid4x4, "pixel_art", "Image Tools", Color(0xFFCDDC39), BadgeType.NEW),
        Tool("Resize and Convert", Icons.Default.PhotoSizeSelectLarge, "image_resize_conv", "Image Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Resize by Limits", Icons.Default.AspectRatio, "image_resize_limits", "Image Tools", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Resize by Weight", Icons.Default.Scale, "image_resize_weight", "Image Tools", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Single Edit", Icons.Default.Edit, "image_single_edit", "Image Tools", Color(0xFFE91E63), BadgeType.NEW),
        Tool("WEBP Tools", Icons.Default.Animation, "webp_tools", "Image Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Wallpapers Export", Icons.Default.Wallpaper, "image_wallpapers", "Image Tools", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Watermarking", Icons.AutoMirrored.Filled.BrandingWatermark, "image_watermark", "Image Tools", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Web Image Loading", Icons.Default.CloudDownload, "image_web_load", "Image Tools", Color(0xFF03A9F4), BadgeType.NEW),

        // IoT & Smart Home
        Tool("Device Discovery", Icons.Default.Search, "device_discovery", "Engineering", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("MQTT Tester", Icons.Default.NetworkCheck, "mqtt_tester", "Engineering", Color(0xFF2196F3), BadgeType.NEW),
        Tool("WIFI Analyzer", Icons.Default.Wifi, "wifi_anal", "Engineering", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Wake On LAN", Icons.Default.SettingsPower, "wake_on_lan", "Engineering", Color(0xFFF44336), BadgeType.NONE),
        Tool("Smart Hub", Icons.Default.Hub, "smart_hub", "Engineering", Color(0xFF00BCD4), BadgeType.NEW),

        // Kitchen
        Tool("Egg Timer", Icons.Default.Timer, "egg_timer", "Health & Lifestyle", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Recipe Scaler", Icons.Default.Scale, "recipe_scaler", "Health & Lifestyle", Color(0xFF4CAF50), BadgeType.NEW),

        // Lifestyle
        Tool("Daily Journal", Icons.Default.EditNote, "daily_journal", "Health & Lifestyle", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Daily Quotes", Icons.Default.FormatQuote, "daily_quotes", "Health & Lifestyle", Color(0xFFFFC107), BadgeType.NEW),
        Tool("Habit Tracker", Icons.Default.EventRepeat, "habit_tracker", "Health & Lifestyle", Color(0xFF4CAF50), BadgeType.NONE),
        Tool("Meditation Timer", Icons.Default.SelfImprovement, "meditation", "Health & Lifestyle", Color(0xFF9C27B0), BadgeType.NONE),
        Tool("Plant Care", Icons.Default.Eco, "plant_care", "Health & Lifestyle", Color(0xFF4CAF50), BadgeType.NEW),

        // Math & Logic
        Tool("Matrix Calc", Icons.Default.Grid4x4, "matrix_calc", "Science & Education", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Equation Solver", Icons.Default.Functions, "eq_solver", "Science & Education", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Fraction Calc", Icons.Default.Percent, "fraction_calc", "Science & Education", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Truth Table Gen", Icons.Default.ListAlt, "truth_table", "Science & Education", Color(0xFF009688), BadgeType.NEW),
        Tool("Statistics Pro", Icons.Default.BarChart, "stats_pro", "Science & Education", Color(0xFF4CAF50), BadgeType.NEW),

        // Media

        Tool("Document Scanner", Icons.Default.Scanner, "doc_scanner", "Video & Media", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("Exif Viewer", Icons.Default.CameraAlt, "exif_viewer", "Video & Media", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Gradient Gen", Icons.Default.Gradient, "gradient_gen", "Video & Media", Color(0xFFFFEB3B), BadgeType.NONE),
        Tool("Image Compressor", Icons.Default.Compress, "image_compress", "Video & Media", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Media Grabber", Icons.Default.Download, "media_grabber", "Video & Media", Color(0xFFFFC107), BadgeType.NONE),
        Tool("Photo Filters", Icons.Default.Filter, "photo_filters", "Video & Media", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Video Frame Grabber", Icons.Default.CropOriginal, "frame_grabber", "Video & Media", Color(0xFF009688), BadgeType.NEW),

        // Music
        Tool("Chord Library", Icons.Default.LibraryMusic, "chord_lib", "Audio & Music", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Guitar Tuner", Icons.Default.MusicNote, "guitar_tuner", "Audio & Music", Color(0xFFE91E63), BadgeType.NEW),
        Tool("Metronome", Icons.Default.AvTimer, "metronome", "Audio & Music", Color(0xFF00BCD4), BadgeType.NONE),

        // Navigation
        Tool("Altimeter Pro", Icons.Default.Landscape, "altimeter_pro", "Outdoor & Nature", Color(0xFF795548), BadgeType.NEW),
        Tool("Altitude Graph", Icons.AutoMirrored.Filled.ShowChart, "altitude_graph", "Outdoor & Nature", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Area Calculator", Icons.Default.SquareFoot, "area_calc_pro", "Outdoor & Nature", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Compass Pro", Icons.Default.Explore, "compass_pro", "Outdoor & Nature", Color(0xFF2196F3), BadgeType.NEW),
        Tool("GPS Status", Icons.Default.GpsFixed, "gps_status", "Outdoor & Nature", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Route Planner", Icons.Default.Directions, "route_planner", "Outdoor & Nature", Color(0xFF2196F3), BadgeType.NEW),

        // Network
        Tool("DNS Lookup", Icons.Default.Dns, "dns_lookup", "Network", Color(0xFF3F51B5), BadgeType.NEW),
        Tool("My IP", Icons.Default.Public, "my_ip", "Network", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("Network Details", Icons.Default.NetworkCheck, "network_info", "Network", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Ping", Icons.Default.SettingsEthernet, "ping", "Network", Color(0xFF009688), BadgeType.NONE),
        Tool("Port Scanner", Icons.Default.Search, "port_scanner", "Network", Color(0xFFF44336), BadgeType.NONE),
        Tool("Speed Test", Icons.Default.Speed, "speed_test", "Network", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Whois", Icons.Default.QuestionMark, "whois", "Network", Color(0xFF607D8B), BadgeType.NEW),

        // Outdoor
        Tool("Campfire Guide", Icons.Default.LocalFireDepartment, "campfire_guide", "Outdoor & Nature", Color(0xFFFF9800), BadgeType.NEW),
        Tool("Hiking Trails", Icons.AutoMirrored.Filled.DirectionsRun, "hiking_trails", "Outdoor & Nature", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Knots Guide", Icons.Default.InvertColors, "knots_guide", "Outdoor & Nature", Color(0xFF795548), BadgeType.NEW),
        Tool("Weather Forecast", Icons.Default.WbCloudy, "weather_forecast", "Outdoor & Nature", Color(0xFF03A9F4), BadgeType.NEW),

        // PDF & Document Tools
        Tool("Document Viewer", Icons.Default.Preview, "pdf_view_v2", "Files & Documents", Color(0xFFF44336), BadgeType.NONE),
        Tool("Extract Images", Icons.Default.Image, "pdf_extract_v2", "Files & Documents", Color(0xFFF44336), BadgeType.NONE),
        Tool("Lock/Unlock PDF", Icons.Default.Lock, "pdf_security", "Files & Documents", Color(0xFFF44336), BadgeType.NONE),
        Tool("Split PDFs/Document", Icons.AutoMirrored.Filled.CallSplit, "pdf_split_v2", "Files & Documents", Color(0xFFF44336), BadgeType.NONE),

        // Photography
        Tool("Depth of Field", Icons.Default.FilterHdr, "dof_calc", "Image Tools", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Exposure Calculator", Icons.Default.Camera, "exposure_calc", "Image Tools", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Golden Hour", Icons.Default.WbSunny, "golden_hour", "Image Tools", Color(0xFFFF9800), BadgeType.NEW),

        // Physics
        Tool("Force Calculator", Icons.Default.Speed, "force_calc", "Science & Education", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Formula Sheet", Icons.Default.Functions, "physics_formulas", "Science & Education", Color(0xFF4CAF50), BadgeType.NEW),

        // Privacy
        Tool("App Permissions", Icons.Default.Security, "app_permissions", "Security & Privacy", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Permission Manager", Icons.Default.ManageAccounts, "perm_manager", "Security & Privacy", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Privacy Check", Icons.Default.PrivacyTip, "privacy_check", "Security & Privacy", Color(0xFFF44336), BadgeType.NEW),

        // Productivity
        Tool("Checklist", Icons.Default.Checklist, "checklist", "Productivity", Color(0xFFCDDC39), BadgeType.NONE),
        Tool("Kanban Board", Icons.Default.ViewWeek, "kanban", "Productivity", Color(0xFF009688), BadgeType.NEW),
        Tool("Note Pad", Icons.Default.NoteAlt, "note", "Productivity", Color(0xFF8BC34A), BadgeType.NONE),
        Tool("Pomodoro", Icons.Default.HourglassEmpty, "pomodoro", "Productivity", Color(0xFFF44336), BadgeType.NONE),
        Tool("Task Board", Icons.Default.Dashboard, "task_board", "Productivity", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Time Logger", Icons.Default.HistoryToggleOff, "time_logger", "Productivity", Color(0xFF673AB7), BadgeType.NEW),

        // Recording Tools
        Tool("Record Audio", Icons.Default.Mic, "record_audio", "Audio & Music", Color(0xFFE91E63), BadgeType.NONE),
        Tool("Voice Memo", Icons.Default.SettingsVoice, "voice_memo", "Audio & Music", Color(0xFF2196F3), BadgeType.NEW),

        // Science
        Tool("Constants Table", Icons.Default.Functions, "constants", "Science & Education", Color(0xFFF44336), BadgeType.NONE),
        Tool("DNA Visualizer", Icons.Default.Hub, "dna_viz", "Science & Education", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Periodic Table", Icons.Default.GridOn, "periodic_table", "Science & Education", Color(0xFFFFEB3B), BadgeType.NONE),
        Tool("Pokedex", Icons.Default.CatchingPokemon, "pokedex", "Science & Education", Color(0xFFFFC107), BadgeType.NONE),

        // Security
        Tool("App Locker", Icons.Default.Lock, "app_locker", "Security & Privacy", Color(0xFFF44336), BadgeType.NEW),
        Tool("Cipher", Icons.Default.Security, "cipher_tools", "Security & Privacy", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Hash Generator", Icons.Default.Fingerprint, "hash_gen", "Security & Privacy", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Password Manager", Icons.Default.Password, "password_manager", "Security & Privacy", Color(0xFF4CAF50), BadgeType.NONE),

        // Sensors
        Tool("Altimeter", Icons.Default.Landscape, "altimeter", "System & Sensors", Color(0xFF795548), BadgeType.NEW),
        Tool("Barometer", Icons.Default.Compress, "barometer", "System & Sensors", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Compass", Icons.Default.Explore, "compass", "System & Sensors", Color(0xFF2196F3), BadgeType.NONE),
        Tool("G-Force Meter", Icons.Default.Speed, "gforce_meter", "System & Sensors", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Level", Icons.Default.Architecture, "level", "System & Sensors", Color(0xFF03A9F4), BadgeType.NONE),
        Tool("Light Meter", Icons.Default.LightMode, "light", "System & Sensors", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("Metal Detector", Icons.Default.CompassCalibration, "metal", "System & Sensors", Color(0xFF009688), BadgeType.NONE),
        Tool("SPL Meter", Icons.AutoMirrored.Filled.VolumeUp, "spl_meter", "System & Sensors", Color(0xFFF44336), BadgeType.NONE),
        Tool("Sensor Data", Icons.Default.SettingsInputComponent, "sensor_data", "System & Sensors", Color(0xFF4CAF50), BadgeType.NONE),

        // Social
        Tool("Bio Linker", Icons.Default.Link, "bio_linker", "Utility & Misc", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Social Preview", Icons.Default.Share, "social_preview", "Utility & Misc", Color(0xFFE91E63), BadgeType.NEW),

        // Survival
        Tool("Emergency SOS", Icons.Default.Sos, "sos", "Outdoor & Nature", Color(0xFFF44336), BadgeType.NEW),
        Tool("Signal Mirror", Icons.Default.FlashlightOn, "signal_mirror", "Outdoor & Nature", Color(0xFFFFC107), BadgeType.NONE),

        // System
        Tool("App Info", Icons.Default.Apps, "app_info", "System & Sensors", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Process Manager", Icons.Default.Dns, "process_manager", "System & Sensors", Color(0xFF607D8B), BadgeType.NEW),
        Tool("System Lab", Icons.Default.Science, "system_lab", "System & Sensors", Color(0xFF9C27B0), BadgeType.NEW),
        Tool("Update Check", Icons.Default.SystemUpdate, "update_check", "System & Sensors", Color(0xFF4CAF50), BadgeType.NEW),

        // Text
        Tool("ASCII Art", Icons.Default.Texture, "ascii_art", "Text", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Anagram Finder", Icons.Default.SortByAlpha, "anagram", "Text", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Case Converter", Icons.Default.TextFields, "case_converter", "Text", Color(0xFF9C27B0), BadgeType.NONE),
        Tool("Lorem Ipsum", Icons.AutoMirrored.Filled.Notes, "lorem", "Text", Color(0xFF607D8B), BadgeType.NONE),
        Tool("Morse Code", Icons.Default.Language, "morse", "Text", Color(0xFF673AB7), BadgeType.NONE),
        Tool("Morse Decoder", Icons.Default.Language, "morse_decoder", "Text", Color(0xFF03A9F4), BadgeType.NEW),
        Tool("Text Diff", Icons.Default.Difference, "text_diff", "Text", Color(0xFFFF5722), BadgeType.NEW),
        Tool("Word Counter", Icons.Default.Abc, "word_counter", "Text", Color(0xFFE91E63), BadgeType.NONE),

        // Utility
        Tool("BPM Counter", Icons.Default.Favorite, "bpm", "Utility & Misc", Color(0xFF3F51B5), BadgeType.NONE),
        Tool("Clock", Icons.Default.Schedule, "clock", "Utility & Misc", Color(0xFF2196F3), BadgeType.NONE),
        Tool("Flashlight", Icons.Default.FlashlightOn, "flashlight", "Utility & Misc", Color(0xFFF44336), BadgeType.NONE),
        Tool("Hub", Icons.Default.Hub, "hub", "Utility & Misc", Color(0xFF00BCD4), BadgeType.NONE),
        Tool("Protractor", Icons.Default.Architecture, "protractor", "Utility & Misc", Color(0xFFCDDC39), BadgeType.NEW),
        Tool("QR Generator", Icons.Default.QrCode, "qr_gen", "Utility & Misc", Color(0xFF8BC34A), BadgeType.NONE),
        Tool("Random Gen", Icons.Default.Casino, "random", "Utility & Misc", Color(0xFFE91E63), BadgeType.NONE),
        Tool("Ruler", Icons.Default.Straighten, "ruler", "Utility & Misc", Color(0xFF9E9E9E), BadgeType.NEW),
        Tool("Stopwatch", Icons.Default.Timer, "stopwatch", "Utility & Misc", Color(0xFF9C27B0), BadgeType.NONE),
        Tool("Unit Price Calc", Icons.Default.PriceCheck, "unit_price", "Utility & Misc", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Vibration Test", Icons.Default.Vibration, "vibration", "Utility & Misc", Color(0xFFFF5722), BadgeType.NONE),
        Tool("Web Search", Icons.Default.Search, "web", "Utility & Misc", Color(0xFF009688), BadgeType.NONE),
        Tool("World Clock", Icons.Default.Public, "world_clock", "Utility & Misc", Color(0xFF673AB7), BadgeType.NONE),

        // Video Tools
        Tool("Delete Segment", Icons.Default.Delete, "video_delete", "Video & Media", Color(0xFFF44336)),
        Tool("Loop Video", Icons.Default.Loop, "video_loop", "Video & Media", Color(0xFFFF5722)),
        Tool("Mix Video Audio", Icons.Default.Tune, "mix_video_audio", "Video & Media", Color(0xFF2196F3)),
        Tool("Reverse Video", Icons.Default.History, "video_reverse", "Video & Media", Color(0xFF795548)),
        Tool("Silence Video", Icons.AutoMirrored.Filled.VolumeOff, "video_silence", "Video & Media", Color(0xFF9E9E9E)),
        Tool("Video Compressor", Icons.Default.Compress, "video_compress", "Video & Media", Color(0xFF673AB7)),
        Tool("Video Editor", Icons.Default.Edit, "video_trim", "Video & Media", Color(0xFFF44336)),
        Tool("Video SFX", Icons.Default.AutoAwesome, "video_sfx", "Video & Media", Color(0xFF3F51B5)),
        Tool("Video Speed", Icons.Default.FastForward, "video_speed_changer", "Video & Media", Color(0xFF607D8B)),
        Tool("Video Splitter", Icons.AutoMirrored.Filled.AltRoute, "video_splitter", "Video & Media", Color(0xFF00BCD4)),
        Tool("Video Stabilizer", Icons.Default.Camera, "video_stabilizer", "Video & Media", Color(0xFF4CAF50), BadgeType.NEW),
        Tool("Video to Audio", Icons.Default.VideoLibrary, "m_video_to_audio", "Video & Media", Color(0xFFFFC107)),
        Tool("Video To GIF", Icons.Default.Gif, "video_to_gif", "Video & Media", Color(0xFFE91E63)),
        Tool("Video Volume", Icons.AutoMirrored.Filled.VolumeUp, "video_volume_booster", "Video & Media", Color(0xFFFFC107)),
        Tool("Video Flip", Icons.Default.Flip, "video_flip", "Video & Media", Color(0xFF607D8B), BadgeType.NEW),
        Tool("Thumbnail Extractor", Icons.Default.Image, "vid_thumb", "Video & Media", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Video Frame Annotator", Icons.Default.Edit, "vid_annotator", "Video & Media", Color(0xFF2196F3), BadgeType.NEW),
        Tool("Tiles & Widgets", Icons.Default.Widgets, "tiles_widgets", "Utility & Misc", Color(0xFF607D8B), BadgeType.NEW)
    )
}
