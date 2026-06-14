package omni.toolbox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.model.Tool
import omni.toolbox.model.ToolProvider
import omni.toolbox.ui.components.ToolScreen

@Composable
fun ToolGroupScreen(
    navController: NavHostController,
    groupRoute: String,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    val groupTool = remember(groupRoute) {
        ToolProvider.tools.find { it.route == groupRoute }
    }

    if (groupTool == null) {
        navController.popBackStack()
        return
    }

    val subTools = remember(groupTool) {
        groupTool.subToolRoutes?.mapNotNull { route ->
            ToolProvider.tools.find { it.route == route }
        }?.sortedBy { it.name } ?: emptyList()
    }

    ToolScreen(
        title = groupTool.name,
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { onToggleFavorite(groupTool.route) }) {
                Icon(
                    if (favorites.contains(groupTool.route)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (favorites.contains(groupTool.route)) Color.Red else MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            groupTool.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            subTools.forEach { tool ->
                SubToolCard(
                    tool = tool,
                    isFavorite = favorites.contains(tool.route),
                    onToggleFavorite = { onToggleFavorite(tool.route) }
                ) {
                    navController.navigate(tool.route)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubToolCard(
    tool: Tool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = tool.color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        tool.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = tool.color
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                tool.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
