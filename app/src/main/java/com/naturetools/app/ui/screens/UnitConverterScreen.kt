package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.viewmodel.ConverterViewModel

@Composable
fun UnitConverterScreen(navController: NavHostController, viewModel: ConverterViewModel = viewModel()) {
    ToolScreen(title = "Unit Converter", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            TabRow(selectedTabIndex = viewModel.selectedTab) {
                viewModel.tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = viewModel.selectedTab == index,
                        onClick = { viewModel.selectedTab = index; viewModel.onInputChange(viewModel.input) },
                        text = { Text(title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.input,
                onValueChange = { viewModel.onInputChange(it) },
                label = { Text("Input Value") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Result", style = MaterialTheme.typography.labelLarge)
                    Text(viewModel.result, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
