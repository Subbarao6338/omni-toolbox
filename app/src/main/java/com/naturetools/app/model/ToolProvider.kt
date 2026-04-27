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
        Tool("Network Details", Icons.Default.NetworkCheck, "network_info", "Network", Color(0xFF2196F3)),

        // Security
        Tool("Password Manager", Icons.Default.Password, "password_manager", "Security", Color(0xFF4CAF50)),
        Tool("QR Generator", Icons.Default.QrCode, "qr_gen", "Utility", Color(0xFF8BC34A)),

        // Media (Graphics)
        Tool("Color Picker", Icons.Default.Palette, "color_picker", "Media", Color(0xFFCDDC39)),
        Tool("Gradient Gen", Icons.Default.Gradient, "gradient_gen", "Media", Color(0xFFFFEB3B)),
        Tool("Media Grabber", Icons.Default.Download, "media_grabber", "Media", Color(0xFFFFC107)),

        // Health
        Tool("BMI Calc", Icons.Default.AccessibilityNew, "bmi", "Health", Color(0xFFFF9800)),
        Tool("Step Counter", Icons.AutoMirrored.Filled.DirectionsRun, "step_counter", "Health", Color(0xFFFF5722)),
        Tool("Water Tracker", Icons.Default.LocalDrink, "water", "Health", Color(0xFF795548)),

        // Calculation
        Tool("Calculator", Icons.Default.Calculate, "calculator", "Calculation", Color(0xFF607D8B)),
        Tool("Date Calc", Icons.Default.CalendarToday, "date_calc", "Calculation", Color(0xFF9E9E9E)),
        Tool("Discount Calc", Icons.Default.Percent, "discount", "Calculation", Color(0xFFF44336)),
        Tool("Tip Calc", Icons.Default.Receipt, "tip", "Calculation", Color(0xFFE91E63)),
        Tool("Fuel Cost", Icons.Default.LocalGasStation, "fuel", "Calculation", Color(0xFF9C27B0)),

        // Finance
        Tool("Loan Calculator", Icons.Default.AccountBalance, "loan_calc", "Finance", Color(0xFF673AB7)),
        Tool("Compound Interest", Icons.AutoMirrored.Filled.TrendingUp, "compound_interest", "Finance", Color(0xFF3F51B5)),

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

        // Audio Tools
        Tool("Audio Editor", Icons.Default.Edit, "m_audio_editor", "Audio Tools", Color(0xFFF44336)),
        Tool("Audio Cutter", Icons.Default.ContentCut, "m_audio_cutter", "Audio Tools", Color(0xFFF44336)),
        Tool("Audio Joiner", Icons.Default.Link, "m_audio_joiner", "Audio Tools", Color(0xFF4CAF50)),
        Tool("Audio Mixer", Icons.Default.Tune, "m_audio_mixer", "Audio Tools", Color(0xFF2196F3)),
        Tool("Audio Tag Editor", Icons.AutoMirrored.Filled.Label, "m_audio_tag_editor", "Audio Tools", Color(0xFFFF9800)),
        Tool("Audio Compressor", Icons.Default.Compress, "m_audio_compressor", "Audio Tools", Color(0xFF673AB7)),
        Tool("Audio Splitter", Icons.AutoMirrored.Filled.AltRoute, "m_audio_splitter", "Audio Tools", Color(0xFF00BCD4)),
        Tool("Audio Normalizer", Icons.AutoMirrored.Filled.VolumeUp, "m_audio_normalizer", "Audio Tools", Color(0xFF795548)),
        Tool("Volume Booster", Icons.AutoMirrored.Filled.VolumeUp, "m_volume_booster", "Audio Tools", Color(0xFFFFC107)),
        Tool("Speed Changer", Icons.Default.FastForward, "m_speed_changer", "Audio Tools", Color(0xFF607D8B)),
        Tool("Pitch Changer", Icons.Default.Height, "m_audio_pitch", "Audio Tools", Color(0xFFFF5722)),
        Tool("Reverse Audio", Icons.Default.History, "m_reverse_audio", "Audio Tools", Color(0xFF795548)),
        Tool("Bass Booster", Icons.Default.Speaker, "m_bass_booster", "Audio Tools", Color(0xFF3F51B5)),
        Tool("Echo Effect", Icons.Default.SettingsBackupRestore, "m_echo_effect", "Audio Tools", Color(0xFF009688)),
        Tool("3D Audio", Icons.Default.Headset, "m_3d_audio", "Audio Tools", Color(0xFFE91E63)),
        Tool("Audio Pan", Icons.AutoMirrored.Filled.AltRoute, "m_audio_pan", "Audio Tools", Color(0xFF00BCD4)),
        Tool("Equalizer", Icons.Default.Equalizer, "m_equalizer", "Audio Tools", Color(0xFF8BC34A)),
        Tool("Mute Audio", Icons.AutoMirrored.Filled.VolumeOff, "m_mute_audio", "Audio Tools", Color(0xFF9E9E9E)),
        Tool("Silence Remover", Icons.Default.SpeakerNotesOff, "m_silence_remover", "Audio Tools", Color(0xFFF44336)),
        Tool("Voice Changer", Icons.Default.Face, "m_voice_changer", "Audio Tools", Color(0xFF673AB7)),
        Tool("Karaoke Maker", Icons.Default.Mic, "m_karaoke_maker", "Audio Tools", Color(0xFFCDDC39)),
        Tool("Ringtone Maker", Icons.Default.Notifications, "m_ringtone_maker", "Audio Tools", Color(0xFFFF5722)),
        Tool("Text to Speech", Icons.Default.RecordVoiceOver, "m_text_to_speech", "Audio Tools", Color(0xFF009688)),
        Tool("Speech to Text", Icons.Default.Mic, "m_speech_to_text", "Audio Tools", Color(0xFFE91E63)),
        Tool("Noise Generator", Icons.Default.GraphicEq, "noise_generator", "Audio Tools", Color(0xFF2196F3)),
        Tool("Wave Generator", Icons.Default.Waves, "wave_generator", "Audio Tools", Color(0xFF8BC34A)),
        Tool("Silence Generator", Icons.Default.DoNotDisturbOn, "silence_generator", "Audio Tools", Color(0xFFFF5722)),
        Tool("Audio Loop", Icons.Default.Loop, "audio_loop", "Audio Tools", Color(0xFFF44336)),
        Tool("Sound Mastering", Icons.Default.Insights, "sound_mastering", "Audio Tools", Color(0xFF3F51B5)),
        Tool("Add SFX", Icons.Default.AutoAwesome, "add_sfx", "Audio Tools", Color(0xFF3F51B5)),

        // Video Tools
        Tool("Video Editor", Icons.Default.Edit, "video_trim", "Video Tools", Color(0xFFF44336)),
        Tool("Video to Audio", Icons.Default.VideoLibrary, "m_video_to_audio", "Video Tools", Color(0xFFFFC107)),
        Tool("Video Compressor", Icons.Default.Compress, "video_compress", "Video Tools", Color(0xFF673AB7)),
        Tool("Reverse Video", Icons.Default.History, "video_reverse", "Video Tools", Color(0xFF795548)),
        Tool("Video Splitter", Icons.AutoMirrored.Filled.AltRoute, "video_splitter", "Video Tools", Color(0xFF00BCD4)),
        Tool("Mix Video Audio", Icons.Default.Tune, "mix_video_audio", "Video Tools", Color(0xFF2196F3)),
        Tool("Video Speed", Icons.Default.FastForward, "video_speed_changer", "Video Tools", Color(0xFF607D8B)),
        Tool("Video SFX", Icons.Default.AutoAwesome, "video_sfx", "Video Tools", Color(0xFF3F51B5)),
        Tool("Video To GIF", Icons.Default.Gif, "video_to_gif", "Video Tools", Color(0xFFE91E63)),
        Tool("Video Volume", Icons.AutoMirrored.Filled.VolumeUp, "video_volume_booster", "Video Tools", Color(0xFFFFC107)),
        Tool("Delete Segment", Icons.Default.Delete, "video_delete", "Video Tools", Color(0xFFF44336)),
        Tool("Silence Video", Icons.AutoMirrored.Filled.VolumeOff, "video_silence", "Video Tools", Color(0xFF9E9E9E)),
        Tool("Loop Video", Icons.Default.Loop, "video_loop", "Video Tools", Color(0xFFFF5722)),

        // AI Tools
        Tool("Vocal Remover", Icons.Default.PersonOff, "vocal_remover", "AI Tools", Color(0xFFFF9800)),
        Tool("AI Stems Splitter", Icons.AutoMirrored.Filled.AltRoute, "ai_stems_splitter", "AI Tools", Color(0xFF00BCD4)),
        Tool("Vocal AutoTuner", Icons.Default.SettingsVoice, "vocal_autotuner", "AI Tools", Color(0xFFE91E63)),
        Tool("AI Noise Remover", Icons.Default.Hearing, "ai_noise_remover", "AI Tools", Color(0xFF4CAF50)),
        Tool("Echo Remover", Icons.Default.SettingsBackupRestore, "echo_remover", "AI Tools", Color(0xFF009688)),
        Tool("Reverb Remover", Icons.Default.Waves, "reverb_remover", "AI Tools", Color(0xFF607D8B)),
        Tool("Key BPM Finder", Icons.Default.MusicNote, "key_bpm_finder", "AI Tools", Color(0xFFF44336)),
        Tool("Audio Noise Remover", Icons.Default.MicOff, "audio_noise_remover", "AI Tools", Color(0xFF2196F3)),
        Tool("Video Noise Remover", Icons.Default.MovieFilter, "video_noise_remover", "AI Tools", Color(0xFF9C27B0)),

        // Recording Tools
        Tool("Record Audio", Icons.Default.Mic, "record_audio", "Recording Tools", Color(0xFFE91E63)),
        Tool("Fun Recording", Icons.Default.Mood, "fun_record", "Recording Tools", Color(0xFFCDDC39)),
        Tool("Karaoke Effect", Icons.Default.Mic, "karaoke_effect", "Recording Tools", Color(0xFFCDDC39)),

        // Batch Processing
        Tool("Multi Mix Audio", Icons.Default.Tune, "multi_mix", "Batch Processing", Color(0xFF2196F3)),
        Tool("Multi Convert", Icons.Default.Autorenew, "multi_convert", "Batch Processing", Color(0xFF4CAF50)),
        Tool("Multi Video To Audio", Icons.Default.MusicVideo, "multi_video_to_audio", "Batch Processing", Color(0xFFFF9800)),
        Tool("Multi Volume Booster", Icons.AutoMirrored.Filled.VolumeUp, "multi_volume_booster", "Batch Processing", Color(0xFF9C27B0)),

        // Other Tools
        Tool("Metronome", Icons.Default.AvTimer, "metronome", "Other Tools", Color(0xFF00BCD4), BadgeType.NEW),
        Tool("Audio Info", Icons.Default.AudioFile, "audio_info", "Other Tools", Color(0xFF795548), BadgeType.NEW),
        Tool("Video Info", Icons.Default.VideoFile, "video_info", "Other Tools", Color(0xFF673AB7), BadgeType.NEW),
        Tool("Device Codec", Icons.Default.PermDeviceInformation, "device_codec", "Other Tools", Color(0xFF3F51B5), BadgeType.PREMIUM),

        // Output
        Tool("Audio Output", Icons.Default.LibraryMusic, "audio_output", "Output", Color(0xFF2196F3)),
        Tool("Video Output", Icons.Default.VideoLibrary, "video_output", "Output", Color(0xFF4CAF50))
    )
}
