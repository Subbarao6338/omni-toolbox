package omni.toolbox.ui.screens.utility

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.math.*

@Composable
fun ProtractorScreen(navController: NavHostController) {
    var angle by remember { mutableFloatStateOf(45f) }

    ToolScreen(
        title = "Protractor",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${angle.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touch = change.position
                            val dx = touch.x - center.x
                            val dy = touch.y - center.y
                            var a = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (a < 0) a += 360f
                            angle = a
                        }
                    }
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 * 0.8f

                    // Draw outer circle
                    drawCircle(
                        color = onSurfaceColor.copy(alpha = 0.1f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw ticks
                    for (i in 0 until 360 step 5) {
                        val isMajor = i % 10 == 0
                        val length = if (isMajor) 15.dp.toPx() else 8.dp.toPx()
                        val rad = Math.toRadians(i.toDouble())
                        val start = Offset(
                            center.x + (radius - length) * cos(rad).toFloat(),
                            center.y + (radius - length) * sin(rad).toFloat()
                        )
                        val end = Offset(
                            center.x + radius * cos(rad).toFloat(),
                            center.y + radius * sin(rad).toFloat()
                        )
                        drawLine(
                            color = if (isMajor) onSurfaceColor else onSurfaceColor.copy(alpha = 0.5f),
                            start = start,
                            end = end,
                            strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                        )
                    }

                    // Draw needle
                    val needleRad = Math.toRadians(angle.toDouble())
                    val needleEnd = Offset(
                        center.x + radius * cos(needleRad).toFloat(),
                        center.y + radius * sin(needleRad).toFloat()
                    )
                    drawLine(
                        color = primaryColor,
                        start = center,
                        end = needleEnd,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = primaryColor,
                        radius = 8.dp.toPx(),
                        center = center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Drag to measure angle",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
