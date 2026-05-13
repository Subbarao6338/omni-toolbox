package com.naturetools.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuroraBackground(enabled: Boolean, content: @Composable () -> Unit) {
    if (!enabled) {
        content()
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF004D40),
                            Color(0xFF1A237E),
                            Color(0xFF4A148C),
                            Color(0xFF004D40)
                        ),
                        start = androidx.compose.ui.geometry.Offset(shift, 0f),
                        end = androidx.compose.ui.geometry.Offset(shift + 500f, 1000f)
                    )
                )
                .blur(80.dp)
        )
        content()
    }
}

@Composable
fun Modifier.glassEffect(enabled: Boolean): Modifier {
    return if (enabled) {
        this.then(
            Modifier
                .background(Color.White.copy(alpha = 0.15f))
                .blur(20.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    } else {
        this
    }
}
