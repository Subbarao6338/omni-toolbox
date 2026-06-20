package com.nature.files.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.files.ui.theme.*

// Organic leaf shape for badges
private val LeafShape = GenericShape { size, _ ->
    moveTo(0f, size.height)
    quadraticBezierTo(size.width * 0.1f, size.height * 0.1f, size.width, 0f)
    quadraticBezierTo(size.width * 0.9f, size.height * 0.9f, 0f, size.height)
}

@Composable
fun LeafBadge(extension: String, modifier: Modifier = Modifier) {
    val color = when (extension.lowercase()) {
        "jpg", "png", "webp", "jpeg" -> ImageAutumnLeaf
        "mp4", "mkv", "avi" -> VideoClearWater
        "pdf" -> Clay
        "zip", "rar", "7z" -> ArchiveStoneMoss
        "txt", "doc", "docx", "kt", "java" -> DocumentFreshLeaf
        else -> CanopyGreen
    }

    Box(
        modifier = modifier
            .background(color, shape = LeafShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = extension.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
