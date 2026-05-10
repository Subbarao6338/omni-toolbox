package com.naturetools.app.ui.screens.media

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class PerchanceTool(val name: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val description: String)

@Composable
fun PerchanceHubScreen(navController: NavHostController) {
    val perchanceTools = listOf(
        PerchanceTool("Image Generator", "per_image", Icons.Default.Image, "Create stunning AI images"),
        PerchanceTool("Story Writer", "per_story", Icons.Default.AutoAwesome, "Generate creative stories"),
        PerchanceTool("Character Maker", "per_character", Icons.Default.Person, "Build unique AI characters"),
        PerchanceTool("General Hub", "per_hub", Icons.Default.Hub, "Explore all Perchance tools")
    )

    ToolScreen(
        title = "Perchance AI Hub",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                "Welcome to Perchance AI Hub",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Integrated access to Perchance's powerful AI creative tools.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(perchanceTools) { tool ->
                    ElevatedCard(
                        onClick = { navController.navigate(tool.route) },
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(tool.icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(tool.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            Text(tool.description, fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("These tools require an internet connection as they use the Perchance.org cloud platform.", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
