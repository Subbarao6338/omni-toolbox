package com.nature.files.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nature.files.ui.theme.*

@Composable
fun NatureFolderIcon(folderName: String, modifier: Modifier = Modifier, customColor: String? = null) {
    val (icon, defaultColor) = when (folderName.lowercase()) {
        "documents" -> Icons.Default.Park to CanopyGreen // Oak representative
        "images" -> Icons.Default.FilterVintage to Color(0xFFFFB7C5) // Cherry Blossom
        "videos", "movies" -> Icons.Default.NaturePeople to LichenGrey // Weeping Willow
        "music" -> Icons.Default.Grass to CanopyGreen // Bamboo
        "downloads" -> Icons.Default.Eco to BarkBrown // Pine Cone
        "code" -> Icons.Default.Forest to LichenGrey // Fern
        "dcim", "pictures" -> Icons.Default.Yard to Clay // Garden
        "vault" -> Icons.Default.Lock to Clay // Secure Tree Knot
        "archives", "backups" -> Icons.Default.Terrain to LichenGrey // Baobab
        "system", "android" -> Icons.Default.WbTwilight to LichenGrey // Silver Birch
        "large", "huge" -> Icons.Default.Park to CanopyGreen // Sequoia
        "camera" -> Icons.Default.FilterVintage to Color(0xFFFFB7C5) // Cherry Blossom
        "screenshots" -> Icons.Default.Nightlight to Moonlight // Moonlight
        "temp", "cache" -> Icons.Default.Park to LichenGrey // Leaf Litter
        else -> Icons.Default.Folder to BarkBrown
    }

    val tint = customColor?.let {
        try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { defaultColor }
    } ?: defaultColor

    Icon(icon, contentDescription = null, modifier = modifier, tint = tint)
}
