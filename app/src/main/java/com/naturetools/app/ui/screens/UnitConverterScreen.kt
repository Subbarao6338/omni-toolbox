package com.naturetools.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import com.naturetools.app.viewmodel.ConverterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(navController: NavHostController, viewModel: ConverterViewModel = viewModel()) {
    ToolScreen(title = "Unit Converter", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            ScrollableTabRow(
                selectedTabIndex = viewModel.selectedCategoryIndex,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                viewModel.categories.forEachIndexed { index, category ->
                    Tab(
                        selected = viewModel.selectedCategoryIndex == index,
                        onClick = { viewModel.onCategoryChange(index) },
                        text = { Text(category.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = viewModel.input,
                onValueChange = { viewModel.onInputChange(it) },
                label = { Text("Input Value") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            val currentUnits = remember(viewModel.selectedCategoryIndex) {
                viewModel.categories[viewModel.selectedCategoryIndex].units.map { it.name }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                UnitDropdown(
                    label = "From",
                    options = currentUnits,
                    selectedIndex = viewModel.fromUnitIndex,
                    onSelectionChange = { viewModel.onFromUnitChange(it) },
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.SwapVert,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                UnitDropdown(
                    label = "To",
                    options = currentUnits,
                    selectedIndex = viewModel.toUnitIndex,
                    onSelectionChange = { viewModel.onToUnitChange(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Result", style = MaterialTheme.typography.labelLarge)
                    Text(
                        viewModel.result.ifEmpty { "0" },
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        viewModel.categories[viewModel.selectedCategoryIndex].units[viewModel.toUnitIndex].name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = options[selectedIndex],
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectionChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}
