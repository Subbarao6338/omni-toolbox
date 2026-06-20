package com.nature.docs.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nature.docs.ui.theme.BotanicalGreen

@Composable
fun Logo(modifier: Modifier = Modifier, partColor: Color = BotanicalGreen) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.size(64.dp)) {
            val path = Path().apply {
                moveTo(size.width / 2, size.height)
                cubicTo(size.width / 2, size.height / 2, size.width, size.height / 4, size.width / 2, 0f)
                cubicTo(0f, size.height / 4, size.width / 2, size.height / 2, size.width / 2, size.height)
            }
            drawPath(path, color = partColor, style = Stroke(width = 2.dp.toPx()))

            // Veins
            drawLine(partColor, Offset(size.width / 2, size.height * 0.8f), Offset(size.width / 2, size.height * 0.2f), strokeWidth = 1.dp.toPx())
        }
    }
}
