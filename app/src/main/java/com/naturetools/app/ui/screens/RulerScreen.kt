package com.naturetools.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun RulerScreen(navController: NavHostController) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val dpi = configuration.densityDpi.toFloat()
    val pxPerMm = dpi / 25.4f

    ToolScreen(
        title = "Ruler",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val height = size.height
                var currentMm = 0f
                while (currentMm * pxPerMm < height) {
                    val y = currentMm * pxPerMm
                    val isCm = currentMm % 10 == 0f
                    val isHalfCm = currentMm % 5 == 0f
                    val lineLength = when {
                        isCm -> 40.dp.toPx()
                        isHalfCm -> 30.dp.toPx()
                        else -> 20.dp.toPx()
                    }
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(lineLength, y),
                        strokeWidth = if (isCm) 2.dp.toPx() else 1.dp.toPx()
                    )
                    currentMm += 1f
                }
            }
            val maxCm = (configuration.screenHeightDp * density.density / pxPerMm / 10).toInt()
            for (i in 1..maxCm) {
                val yOffset = (i * 10 * pxPerMm / density.density).dp
                Text(
                    text = "$i",
                    fontSize = 12.sp,
                    modifier = Modifier.offset(x = 45.dp, y = yOffset - 8.dp)
                )
            }
            val pxPerInch = dpi
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                var currentInch = 0f
                while (currentInch * pxPerInch < height) {
                    val y = currentInch * pxPerInch
                    for (i in 0..7) {
                        val subY = y + (i * pxPerInch / 8f)
                        if (subY > height) break
                        val lineLength = when (i) {
                            0 -> 40.dp.toPx()
                            4 -> 30.dp.toPx()
                            2, 6 -> 25.dp.toPx()
                            else -> 15.dp.toPx()
                        }
                        drawLine(
                            color = Color.Gray,
                            start = Offset(width - lineLength, subY),
                            end = Offset(width, subY),
                            strokeWidth = if (i == 0) 2.dp.toPx() else 1.dp.toPx()
                        )
                    }
                    currentInch += 1f
                }
            }
            val maxInches = (configuration.screenHeightDp * density.density / pxPerInch).toInt()
            for (i in 1..maxInches) {
                val yOffset = (i * pxPerInch / density.density).dp
                Text(
                    text = "$i",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (-45).dp, y = yOffset - 8.dp)
                )
            }
            Text(
                text = "CM",
                modifier = Modifier.align(Alignment.TopStart).padding(start = 45.dp, top = 8.dp),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "INCH",
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 45.dp, top = 8.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
