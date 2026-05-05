package com.naturetools.app.ui.screens.system

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun RamInfoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    var totalRam by remember { mutableLongStateOf(0L) }
    var availableRam by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            activityManager.getMemoryInfo(memoryInfo)
            totalRam = memoryInfo.totalMem; availableRam = memoryInfo.availMem
            delay(2000)
        }
    }

    ToolScreen(title = "RAM Info", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total: ${totalRam / 1024 / 1024} MB", style = MaterialTheme.typography.headlineSmall)
            Text("Available: ${availableRam / 1024 / 1024} MB", style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(progress = { (totalRam - availableRam).toFloat() / totalRam.toFloat() }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
        }
    }
}
