package com.example.naturedialer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.naturedialer.ui.screens.*
import com.example.naturedialer.ui.theme.DialerTheme
import com.example.naturedialer.ui.theme.NatureDialerTheme
import com.example.naturedialer.ui.viewmodel.CallViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentTheme by remember { mutableStateOf(DialerTheme.FOREST) }
            val callViewModel: CallViewModel = viewModel()
            val ongoingCall by callViewModel.ongoingCall.collectAsState()

            NatureDialerTheme(dialerTheme = currentTheme) {
                if (ongoingCall != null) {
                    InCallScreen(
                        number = ongoingCall!!,
                        theme = currentTheme,
                        onEndCall = { callViewModel.endCall() }
                    )
                } else {
                    MainScreen(
                        currentTheme = currentTheme,
                        onThemeChange = { currentTheme = it },
                        onCall = { number -> callViewModel.startCall(number) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    currentTheme: DialerTheme,
    onThemeChange: (DialerTheme) -> Unit,
    onCall: (String) -> Unit
) {
    val navController = rememberNavController()
    val items = listOf("history", "stats", "contacts", "dialer", "settings")
    val icons = listOf(
        Icons.Default.DateRange,
        Icons.Default.ThumbUp,
        Icons.Default.Person,
        Icons.Default.Call,
        Icons.Default.Settings
    )
    val labels = listOf("Recent", "Stats", "Contacts", "Keypad", "Settings")

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = labels[index]) },
                        label = { Text(labels[index]) },
                        selected = currentRoute == item,
                        onClick = {
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dialer",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("history") { HistoryScreen(theme = currentTheme, onCall = onCall) }
            composable("stats") { StatsScreen(theme = currentTheme) }
            composable("contacts") { ContactsScreen(theme = currentTheme, onCall = onCall) }
            composable("dialer") { DialerScreen(theme = currentTheme, onCall = onCall) }
            composable("settings") {
                SettingsScreen(
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    currentTheme: DialerTheme,
    onThemeChange: (DialerTheme) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Theme Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        DialerTheme.entries.forEach { theme ->
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { onThemeChange(theme) }.padding(8.dp)
            ) {
                RadioButton(
                    selected = currentTheme == theme,
                    onClick = { onThemeChange(theme) }
                )
                Text(theme.name.lowercase().replaceFirstChar { it.uppercase() }, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
