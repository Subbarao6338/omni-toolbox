package com.naturetools.app.ui.screens.media

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.model.ToolProvider
import com.naturetools.app.ui.components.ToolScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerchanceHubScreen(
    navController: NavHostController,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    val perchanceTools = remember {
        ToolProvider.tools.filter { it.route.startsWith("per_") }
    }

    val scrollState = rememberScrollState()

    ToolScreen(
        title = "Perchance AI",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(28.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp).size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Creative Playground",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Discover a world of specialized AI generators powered by Perchance community.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "Popular Generators",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            perchanceTools.forEachIndexed { index, tool ->
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { 50 * (index + 1) }) + fadeIn()
                ) {
                    PerchanceToolCard(
                        tool = tool,
                        isFavorite = favorites.contains(tool.route),
                        onToggleFavorite = { onToggleFavorite(tool.route) },
                        onClick = { navController.navigate(tool.route) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Cloud,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "These experimental tools require internet access to connect with Perchance.org.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PerchanceToolCard(
    tool: com.naturetools.app.model.Tool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    @Suppress("UNUSED_VARIABLE")
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isFavorite) {
            CardDefaults.outlinedCardBorder(enabled = true).copy(
                brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
            )
        } else {
            CardDefaults.outlinedCardBorder(enabled = true)
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = tool.color.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        tool.icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = tool.color
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tool.name.replace("Perchance ", "").replace("AI ", ""),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    tool.description ?: "Creative AI generation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            IconButton(
                onClick = {
                    onToggleFavorite()
                }
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
