package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.OmniViewModel
import com.example.ui.screens.AICompanionScreen
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeveloperScreen
import com.example.ui.screens.DocsCrawlerScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.MediaHubScreen
import com.example.ui.screens.SocialMediaScreen
import com.example.ui.screens.StatsBackupScreen
import com.example.ui.screens.SyncScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.SummarizeScreen
import com.example.ui.screens.BenchmarkScreen
import com.example.ui.screens.HiddenSettingsScreen
import com.example.ui.screens.QuickTilesScreen
import com.example.ui.screens.FileManagerScreen
import com.example.ui.screens.WordRankScreen
import com.example.ui.screens.TeluguPanchangamScreen
import com.example.ui.screens.UtilitySuiteScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: OmniViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val useDarkTheme = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            MyApplicationTheme(darkTheme = useDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WorkspaceNavHost(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun WorkspaceNavHost(viewModel: OmniViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToSection = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable("developer") {
            DeveloperScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("docs") {
            DocsCrawlerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("security") {
            SecurityScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("automation") {
            AutomationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("ai_assistant") {
            AICompanionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("media_fun") {
            MediaHubScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("social_media") {
            SocialMediaScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("stats_backup") {
            StatsBackupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("sync") {
            SyncScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("contacts") {
            ContactsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("summarize") {
            SummarizeScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("benchmarks") {
            BenchmarkScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("hidden_settings") {
            HiddenSettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("quick_tiles") {
            QuickTilesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("file_manager") {
            FileManagerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("word_rank") {
            WordRankScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("panchangam") {
            TeluguPanchangamScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("utilities") {
            UtilitySuiteScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
