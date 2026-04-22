package com.naturetools.app.ui.screens

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun FlashlightScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraId = remember { cameraManager.cameraIdList.firstOrNull() }

    var isOn by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            if (isOn && cameraId != null) {
                try {
                    cameraManager.setTorchMode(cameraId, false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    ToolScreen(title = "Flashlight", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                if (isOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = if (isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(48.dp))
            Switch(
                checked = isOn,
                onCheckedChange = {
                    isOn = it
                    if (cameraId != null) {
                        try {
                            cameraManager.setTorchMode(cameraId, it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            Text(if (isOn) "ON" else "OFF", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
