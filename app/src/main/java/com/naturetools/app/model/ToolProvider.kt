package com.naturetools.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Tool(
    val name: String,
    val icon: ImageVector,
    val route: String,
    val category: String,
    val color: Color = Color.Unspecified
)

object ToolProvider {
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

        // AI & Data
        Tool("Regex Tester", Icons.Default.Code, "regex_tester", "AI & Data", Color(0xFF673AB7)),
        Tool("Markdown Preview", Icons.Default.Description, "markdown_preview", "AI & Data", Color(0xFF3F51B5)),
        Tool("Network Details", Icons.Default.NetworkCheck, "network_info", "Network", Color(0xFF2196F3)),

        Tool("Advance Trim", Icons.Default.ContentCut, "advance_trim", "Audio Editor", Color(0xFFF44336)),
        Tool("Simple Trim", Icons.Default.ContentCut, "simple_trim", "Audio Editor", Color(0xFFF44336)),
        Tool("Two Wave Trim", Icons.Default.ContentCut, "two_wave_trim", "Audio Editor", Color(0xFFF44336)),
        Tool("MIX", Icons.Default.Tune, "mix_audio", "Audio Editor", Color(0xFF2196F3)),
        Tool("Merge", Icons.Default.CallMerge, "merge_audio", "Audio Editor", Color(0xFF4CAF50)),
        Tool("Tag Editor", Icons.Default.Badge, "tag_editor", "Audio Editor", Color(0xFFFF9800)),
        Tool("Convert", Icons.Default.Transform, "convert_audio", "Audio Editor", Color(0xFF9C27B0)),
        Tool("Compress", Icons.Default.Compress, "audio_compressor", "Audio Editor", Color(0xFF673AB7)),
        Tool("Simple Splitter", Icons.Default.AltRoute, "simple_splitter", "Audio Editor", Color(0xFF00BCD4)),
        Tool("Advance Splitter", Icons.Default.AltRoute, "advance_splitter", "Audio Editor", Color(0xFF00BCD4)),
        Tool("Channel Manipulation", Icons.Default.GraphicEq, "channel_manipulation", "Audio Editor", Color(0xFF00BCD4)),
        Tool("Silence Remover", Icons.Default.SpeakerNotesOff, "silence_remover", "Audio Editor", Color(0xFFF44336)),
        Tool("Effects", Icons.Default.BarChart, "audio_effects_main", "Audio Editor", Color(0xFF4CAF50)),
        Tool("Equalizer", Icons.Default.Equalizer, "equalizer", "Audio Editor", Color(0xFF8BC34A)),
        Tool("Normalize", Icons.Default.VolumeUp, "audio_normalizer", "Audio Editor", Color(0xFF795548)),
        Tool("Sound Mastering", Icons.Default.Insights, "sound_mastering", "Audio Editor", Color(0xFF3F51B5)),
        Tool("Voice Changer", Icons.Default.Face, "voice_changer", "Audio Editor", Color(0xFF673AB7)),
        Tool("Speed Changer", Icons.Default.FastForward, "speed_changer", "Audio Editor", Color(0xFF607D8B)),
        Tool("Add SFX", Icons.Default.AutoAwesome, "add_sfx", "Audio Editor", Color(0xFF3F51B5)),
        Tool("Reverse", Icons.Default.History, "reverse_audio", "Audio Editor", Color(0xFF795548)),

        Tool("8d Audio", Icons.Default.Headset, "eight_d_audio", "Audio Lab", Color(0xFFE91E63)),
        Tool("Volume Booster", Icons.Default.VolumeUp, "volume_booster", "Audio Lab", Color(0xFFFFC107)),
        Tool("Noise Generator", Icons.Default.GraphicEq, "noise_generator", "Audio Lab", Color(0xFF2196F3)),
        Tool("Wave Generator", Icons.Default.Waves, "wave_generator", "Audio Lab", Color(0xFF8BC34A)),
        Tool("Audio To Video", Icons.Default.Movie, "audio_to_video", "Audio Lab", Color(0xFF9C27B0)),
        Tool("Bass Booster", Icons.Default.Speaker, "bass_booster", "Audio Lab", Color(0xFF3F51B5)),
        Tool("Audio Echo", Icons.Default.SettingsBackupRestore, "audio_echo", "Audio Lab", Color(0xFF009688)),
        Tool("Audio Merger", Icons.Default.Layers, "audio_merger", "Audio Lab", Color(0xFF607D8B)),
        Tool("Silence Generator", Icons.Default.DoNotDisturbOn, "silence_generator", "Audio Lab", Color(0xFFFF5722)),
        Tool("Audio Loop", Icons.Default.Loop, "audio_loop", "Audio Lab", Color(0xFFF44336)),
        Tool("Text To Speech", Icons.Default.RecordVoiceOver, "text_to_speech", "Audio Lab", Color(0xFF009688)),
        Tool("Video To Audio", Icons.Default.VideoLibrary, "video_to_audio", "Audio Lab", Color(0xFFFFC107)),
        Tool("Pitch Changer", Icons.Default.Height, "pitch_changer", "Audio Lab", Color(0xFFFF5722)),

        Tool("Audio Noise Remover", Icons.Default.MicOff, "audio_noise_remover", "AI Editing Tools", Color(0xFF2196F3)),
        Tool("Video Noise Remover", Icons.Default.MovieFilter, "video_noise_remover", "AI Editing Tools", Color(0xFF9C27B0)),
        Tool("Vocal Remover", Icons.Default.PersonOff, "vocal_remover", "AI Editing Tools", Color(0xFFFF9800)),
        Tool("AI Stems Splitter", Icons.Default.AltRoute, "ai_stems_splitter", "AI Editing Tools", Color(0xFF00BCD4)),
        Tool("Vocal AutoTuner", Icons.Default.SettingsVoice, "vocal_autotuner", "AI Editing Tools", Color(0xFFE91E63)),
        Tool("AI Noise Remover", Icons.Default.Hearing, "ai_noise_remover", "AI Editing Tools", Color(0xFF4CAF50)),
        Tool("Echo Remover", Icons.Default.SettingsBackupRestore, "echo_remover", "AI Editing Tools", Color(0xFF009688)),
        Tool("Reverb Remover", Icons.Default.Waves, "reverb_remover", "AI Editing Tools", Color(0xFF607D8B)),
        Tool("Key BPM finder", Icons.Default.MusicNote, "key_bpm_finder", "AI Editing Tools", Color(0xFFF44336)),

        Tool("Record", Icons.Default.Mic, "record_audio", "Recording Tools", Color(0xFFE91E63)),
        Tool("Fun Recording", Icons.Default.Mood, "fun_record", "Recording Tools", Color(0xFFCDDC39)),
        Tool("Karaoke", Icons.Default.Mic, "karaoke_effect", "Recording Tools", Color(0xFFCDDC39)),
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
}
