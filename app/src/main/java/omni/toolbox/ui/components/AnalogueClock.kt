package omni.toolbox.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogueClock(
    modifier: Modifier = Modifier,
    hourHandColor: Color = MaterialTheme.colorScheme.primary,
    minuteHandColor: Color = MaterialTheme.colorScheme.secondary,
    secondHandColor: Color = MaterialTheme.colorScheme.tertiary,
    circleColor: Color = MaterialTheme.colorScheme.outlineVariant
) {
    var time by remember { mutableStateOf(LocalTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            time = LocalTime.now()
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(modifier = modifier.size(200.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Draw clock circle
            drawCircle(
                color = circleColor,
                radius = radius,
                center = center,
                style = Stroke(width = 4.dp.toPx())
            )

            // Draw hour marks
            for (i in 0 until 12) {
                val angle = i * 30 * (Math.PI / 180)
                val start = Offset(
                    (center.x + (radius - 15.dp.toPx()) * cos(angle)).toFloat(),
                    (center.y + (radius - 15.dp.toPx()) * sin(angle)).toFloat()
                )
                val end = Offset(
                    (center.x + radius * cos(angle)).toFloat(),
                    (center.y + radius * sin(angle)).toFloat()
                )
                drawLine(
                    color = circleColor,
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Hour hand
            val hourAngle = (time.hour % 12 + time.minute / 60f) * 30 * (Math.PI / 180) - Math.PI / 2
            drawLine(
                color = hourHandColor,
                start = center,
                end = Offset(
                    (center.x + radius * 0.5f * cos(hourAngle)).toFloat(),
                    (center.y + radius * 0.5f * sin(hourAngle)).toFloat()
                ),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Minute hand
            val minuteAngle = (time.minute + time.second / 60f) * 6 * (Math.PI / 180) - Math.PI / 2
            drawLine(
                color = minuteHandColor,
                start = center,
                end = Offset(
                    (center.x + radius * 0.7f * cos(minuteAngle)).toFloat(),
                    (center.y + radius * 0.7f * sin(minuteAngle)).toFloat()
                ),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second hand
            val secondAngle = time.second * 6 * (Math.PI / 180) - Math.PI / 2
            drawLine(
                color = secondHandColor,
                start = center,
                end = Offset(
                    (center.x + radius * 0.9f * cos(secondAngle)).toFloat(),
                    (center.y + radius * 0.9f * sin(secondAngle)).toFloat()
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center dot
            drawCircle(
                color = hourHandColor,
                radius = 5.dp.toPx(),
                center = center
            )
        }
    }
}
