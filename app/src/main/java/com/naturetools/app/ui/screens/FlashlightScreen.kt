package com.naturetools.app.ui.screens

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun FlashlightScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraId = remember { cameraManager.cameraIdList.firstOrNull() }

    var isOn by remember { mutableStateOf(false) }
    var isSosMode by remember { mutableStateOf(false) }
    var isScreenLight by remember { mutableStateOf(false) }
    var screenBrightness by remember { mutableFloatStateOf(1f) }
    var screenColor by remember { mutableStateOf(androidx.compose.ui.graphics.Color.White) }

    fun setFlash(state: Boolean) {
        if (cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId, state)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(isSosMode) {
        if (isSosMode) {
            isOn = false // Reset standard ON
            val sosPattern = listOf(
                200L, 200L, 200L, 200L, 200L, 600L, // S: ...
                600L, 200L, 600L, 200L, 600L, 600L, // O: ---
                200L, 200L, 200L, 200L, 200L, 1000L // S: ...
            )
            while (isActive && isSosMode) {
                for (i in sosPattern.indices) {
                    val state = i % 2 == 0
                    setFlash(state)
                    delay(sosPattern[i])
                    if (!isSosMode) break
                }
            }
            setFlash(false)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            setFlash(false)
        }
    }

    ToolScreen(title = "Flashlight", onBack = { navController.popBackStack() }) { padding ->
        if (isScreenLight) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(screenColor.copy(alpha = screenBrightness))
                    .clickable { isScreenLight = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth().alpha(0.8f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Screen Light", style = MaterialTheme.typography.titleMedium)
                        Slider(value = screenBrightness, onValueChange = { screenBrightness = it })
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(Color.White, Color.Red, Color.Green, Color.Blue, Color.Yellow).forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(color, CircleShape)
                                        .clickable { screenColor = color }
                                )
                            }
                        }
                        Button(onClick = { isScreenLight = false }, modifier = Modifier.fillMaxWidth()) {
                            Text("Back to Flashlight")
                        }
                    }
                }
            }
        } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                if (isOn || isSosMode) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = if (isOn || isSosMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                if (isSosMode) "SOS BLINKING" else if (isOn) "ON" else "OFF",
                style = MaterialTheme.typography.headlineMedium,
                color = if (isSosMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(
                        checked = isOn,
                        onCheckedChange = {
                            isOn = it
                            if (it) isSosMode = false
                            setFlash(it)
                        }
                    )
                    Text("Flashlight", style = MaterialTheme.typography.labelMedium)
                }

                VerticalDivider(modifier = Modifier.height(48.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconButton(onClick = { isScreenLight = true }) {
                        Icon(Icons.Default.LightMode, contentDescription = "Screen Light")
                    }
                    Text("Screen", style = MaterialTheme.typography.labelMedium)
                }

                VerticalDivider(modifier = Modifier.height(48.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledIconToggleButton(
                        checked = isSosMode,
                        onCheckedChange = {
                            isSosMode = it
                            if (it) isOn = false
                        },
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            checkedContainerColor = MaterialTheme.colorScheme.error,
                            checkedContentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Icon(Icons.Default.Sos, contentDescription = "SOS")
                    }
                    Text("SOS Mode", style = MaterialTheme.typography.labelMedium)
                }
            }
            }
        }
    }
}
