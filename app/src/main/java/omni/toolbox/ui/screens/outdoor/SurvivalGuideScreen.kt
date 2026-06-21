package omni.toolbox.ui.screens.outdoor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

data class SurvivalChapter(
    val title: String,
    val icon: ImageVector,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurvivalGuideScreen(navController: NavHostController) {
    val chapters = listOf(
        SurvivalChapter("Overview", Icons.Default.Info, "General survival principles and safety rules."),
        SurvivalChapter("Shelter", Icons.Default.Home, "How to build various types of shelters using natural materials."),
        SurvivalChapter("Fire", Icons.Default.LocalFireDepartment, "Techniques for starting and maintaining a fire in different conditions."),
        SurvivalChapter("Water", Icons.Default.WaterDrop, "Finding, collecting, and purifying water in the wild."),
        SurvivalChapter("Food", Icons.Default.Restaurant, "Identifying edible plants and basic trapping/fishing techniques."),
        SurvivalChapter("Navigation", Icons.Default.Explore, "Using natural signs and simple tools for navigation."),
        SurvivalChapter("Medical", Icons.Default.MedicalServices, "Basic first aid and natural remedies for common injuries."),
        SurvivalChapter("Weather", Icons.Default.Cloud, "Predicting weather changes using environmental observations.")
    )

    var selectedChapter by remember { mutableStateOf<SurvivalChapter?>(null) }

    ToolScreen(
        title = "Survival Guide",
        onBack = {
            if (selectedChapter != null) selectedChapter = null
            else navController.popBackStack()
        }
    ) { padding ->
        if (selectedChapter == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chapters.forEach { chapter ->
                    Card(
                        onClick = { selectedChapter = chapter },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            headlineContent = { Text(chapter.title, fontWeight = FontWeight.Bold) },
                            leadingContent = { Icon(chapter.icon, null, tint = MaterialTheme.colorScheme.primary) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, null) }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(selectedChapter!!.icon, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(selectedChapter!!.title, style = MaterialTheme.typography.headlineMedium)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = selectedChapter!!.content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                )

                // Add extended content based on chapter
                Spacer(modifier = Modifier.height(16.dp))
                ChapterDetail(selectedChapter!!.title)
            }
        }
    }
}

@Composable
fun ChapterDetail(title: String) {
    val details = when(title) {
        "Fire" -> listOf("The Fire Triangle: Heat, Fuel, Oxygen", "Tinder, Kindling, and Fuel Wood", "Bow Drill Technique", "Reflector Fires")
        "Water" -> listOf("Transpiration bags", "Solar stills", "Filtering through charcoal", "Boiling times")
        "Shelter" -> listOf("Lean-to shelter", "A-frame shelter", "Debris hut", "Site selection")
        else -> listOf("Preparation is key", "Keep a calm mind", "Signal for help", "Conserve energy")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Key Topics:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        details.forEach { detail ->
            Row(verticalAlignment = Alignment.Top) {
                Text("• ", style = MaterialTheme.typography.bodyLarge)
                Text(detail, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
