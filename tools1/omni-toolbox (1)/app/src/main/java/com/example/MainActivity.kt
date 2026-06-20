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
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WorkspaceNavHost()
                }
            }
        }
    }
}

@Composable
fun WorkspaceNavHost() {
    val navController = rememberNavController()
    val viewModel: OmniViewModel = viewModel()

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
    }
}
