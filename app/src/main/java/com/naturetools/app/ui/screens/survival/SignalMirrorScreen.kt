package com.naturetools.app.ui.screens.survival

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun SignalMirrorScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isMaxBrightness by remember { mutableStateOf(false) }

    DisposableEffect(isMaxBrightness) {
        if (isMaxBrightness && activity != null) {
            val layoutParams = activity.window.attributes
            val originalBrightness = layoutParams.screenBrightness
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            activity.window.attributes = layoutParams
            onDispose {
                layoutParams.screenBrightness = originalBrightness
                activity.window.attributes = layoutParams
            }
        } else {
            onDispose { }
        }
    }

    ToolScreen(
        title = "Signal Mirror",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isMaxBrightness) Color.White else MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    "Use your screen as a signaling device by reflecting sunlight or using maximum brightness.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isMaxBrightness) Color.Black else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(48.dp))
                Switch(
                    checked = isMaxBrightness,
                    onCheckedChange = { isMaxBrightness = it }
                )
                Text(
                    "Maximum Brightness",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isMaxBrightness) Color.Black else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
