package com.naturetools.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.naturetools.app.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NatureToolsApp()
        }
    }
}

data class Tool(
    val name: String,
    val icon: ImageVector,
    val route: String
)

val tools = listOf(
    Tool("Unit Converter", Icons.Default.SwapHoriz, "converter"),
    Tool("Calculator", Icons.Default.Calculate, "calculator"),
    Tool("Web Search", Icons.Default.Search, "web"),
    Tool("Compass", Icons.Default.Explore, "compass"),
    Tool("Light Meter", Icons.Default.LightMode, "light"),
    Tool("Note Pad", Icons.Default.NoteAlt, "note"),
    Tool("Level", Icons.Default.Architecture, "level"),
    Tool("Flashlight", Icons.Default.FlashlightOn, "flashlight"),
    Tool("Stopwatch", Icons.Default.Timer, "stopwatch")
)

@Composable
fun NatureToolsApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { HomeScreen(navController) }
        composable("converter") { UnitConverterScreen(navController) }
        composable("calculator") { CalculatorScreen(navController) }
        composable("web") { WebToolScreen(navController) }
        composable("compass") { CompassScreen(navController) }
        composable("light") { LightMeterScreen(navController) }
        composable("note") { NotePadScreen(navController) }
        composable("level") { LevelScreen(navController) }
        composable("flashlight") { FlashlightScreen(navController) }
        composable("stopwatch") { StopwatchScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Nature Tools") })
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding)
        ) {
            items(tools) { tool ->
                ElevatedCard(
                    onClick = { navController.navigate(tool.route) },
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            tool.icon,
                            contentDescription = tool.name,
                            modifier = Modifier.size(40.dp),
                            tint = LocalContentColor.current
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tool.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
