package omni.toolbox.ui.screens.system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTilesCreatorScreen(navController: NavHostController) {
    var tileName by remember { mutableStateOf("Quick Cleaner") }

    ToolScreen(title = "Quick Tiles Creator", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Custom Settings Shade Tiles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Create custom toggles for your Android notification shade.", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = tileName,
                onValueChange = { tileName = it },
                label = { Text("Tile Label") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Action Binding", style = MaterialTheme.typography.labelLarge)
            var expanded by remember { mutableStateOf(false) }
            var selectedAction by remember { mutableStateOf("Toggle Dev Settings") }
            val actions = listOf("Toggle Dev Settings", "Launch App Info", "Clean Cache", "Run Macro", "SOS Signal")

            Box {
                OutlinedTextField(
                    value = selectedAction,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { IconButton(onClick = { expanded = true }) { Icon(Icons.Default.ArrowDropDown, contentDescription = null) } }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    actions.forEach { action ->
                        DropdownMenuItem(text = { Text(action) }, onClick = { selectedAction = action; expanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Shade Preview", style = MaterialTheme.typography.labelLarge)
            Card(
                modifier = Modifier.padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tileName, color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = { /* Create */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Register Tile to System")
            }
        }
    }
}
