package com.naturetools.app.ui.screens.survival

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun EmergencySOSScreen(navController: NavHostController) {
    val context = LocalContext.current
    var isSosActive by remember { mutableStateOf(false) }
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    val cameraId = remember { cameraManager.cameraIdList.firstOrNull { id ->
        cameraManager.getCameraCharacteristics(id).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
    } }

    val infiniteTransition = rememberInfiniteTransition(label = "sos_bg")
    val bgColor by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Black,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_color"
    )

    LaunchedEffect(isSosActive) {
        if (isSosActive && cameraId != null) {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            try {
                while (isActive) {
                    // S: ...
                    repeat(3) {
                        cameraManager.setTorchMode(cameraId, true)
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
                        delay(200)
                        cameraManager.setTorchMode(cameraId, false)
                        delay(200)
                    }
                    delay(400)
                    // O: ---
                    repeat(3) {
                        cameraManager.setTorchMode(cameraId, true)
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 600)
                        delay(600)
                        cameraManager.setTorchMode(cameraId, false)
                        delay(200)
                    }
                    delay(400)
                    // S: ...
                    repeat(3) {
                        cameraManager.setTorchMode(cameraId, true)
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
                        delay(200)
                        cameraManager.setTorchMode(cameraId, false)
                        delay(200)
                    }
                    delay(2000)
                }
            } finally {
                cameraManager.setTorchMode(cameraId, false)
                toneGenerator.release()
            }
        }
    }

    ToolScreen(
        title = "Emergency SOS",
        onBack = {
            isSosActive = false
            navController.popBackStack()
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isSosActive) bgColor else MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Sos,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = if (isSosActive) Color.White else Color.Red
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { isSosActive = !isSosActive },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSosActive) Color.White else Color.Red,
                        contentColor = if (isSosActive) Color.Red else Color.White
                    ),
                    modifier = Modifier.size(200.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text(
                        if (isSosActive) "STOP" else "START SOS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
