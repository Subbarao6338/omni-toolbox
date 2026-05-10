package com.naturetools.app.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.model.Tool
import com.naturetools.app.model.ToolProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    showCategoryCounts: Boolean,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var debouncedSearchQuery by remember { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedSearchQuery = searchQuery
    }

    val categoryCounts = remember(favorites) {
        derivedStateOf {
            val visibleTools = ToolProvider.tools.filter { it.isVisibleOnHome }
            val counts = visibleTools.groupingBy { it.category }.eachCount()
            counts + ("All" to visibleTools.size) + ("Favorites" to favorites.size)
        }
    }

    val categories = remember {
        derivedStateOf {
            listOf("All", "Favorites") + ToolProvider.tools.filter { it.isVisibleOnHome }.map { it.category }.distinct().sorted()
        }
    }

    val filteredTools = remember(debouncedSearchQuery, selectedCategory, favorites) {
        derivedStateOf {
            ToolProvider.tools.filter { tool ->
                val matchesSearch = tool.name.contains(debouncedSearchQuery, ignoreCase = true) ||
                        (tool.description?.contains(debouncedSearchQuery, ignoreCase = true) ?: false)

                val matchesCategory = when (selectedCategory) {
                    "All" -> tool.isVisibleOnHome
                    "Favorites" -> favorites.contains(tool.route)
                    else -> tool.category == selectedCategory && tool.isVisibleOnHome
                }

                matchesSearch && matchesCategory
            }.sortedBy { it.name }
        }
    }

    val context = LocalContext.current
    val recentPrefs = remember { context.getSharedPreferences("recent_tools", Context.MODE_PRIVATE) }
    var recentRoutes by remember {
        mutableStateOf(recentPrefs.getString("routes", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList())
    }

    fun onToolClick(route: String) {
        val newRecent = (listOf(route) + recentRoutes.filter { it != route }).take(10)
        recentRoutes = newRecent
        recentPrefs.edit().putString("routes", newRecent.joinToString(",")).apply()
        navController.navigate(route)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nature Tools", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { },
                active = false,
                onActiveChange = { },
                placeholder = { Text("Search tools & sub-tools...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {}

            if (recentRoutes.isNotEmpty()) {
                Text(
                    "Recent Tools",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    val recentTools = recentRoutes.mapNotNull { route -> ToolProvider.tools.find { it.route == route } }
                    items(recentTools, key = { it.route }) { tool ->
                        Surface(
                            onClick = { onToolClick(tool.route) },
                            shape = CircleShape,
                            color = tool.color.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tool.color.copy(alpha = 0.2f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(tool.icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = tool.color)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(tool.name, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            ScrollableTabRow(
                selectedTabIndex = categories.value.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 16.dp,
                divider = {},
                indicator = {},
                containerColor = Color.Transparent
            ) {
                categories.value.forEach { category ->
                    val count = categoryCounts.value[category] ?: 0
                    val label = if (showCategoryCounts) "$category ($count)" else category
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(label) },
                        modifier = Modifier.padding(horizontal = 4.dp).animateContentSize(),
                        shape = CircleShape
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredTools.value, key = { it.route }) { tool ->
                        ToolCard(
                            tool = tool,
                            isFavorite = favorites.contains(tool.route),
                            onToggleFavorite = { onToggleFavorite(tool.route) },
                            onClick = { onToolClick(tool.route) }
                        )
                    }
                }

                if (filteredTools.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No tools found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: Tool,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "tool_card_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tool_card_elevation"
    )

    ElevatedCard(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isPressed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.align(Alignment.TopEnd).size(32.dp).padding(4.dp)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    modifier = Modifier.size(16.dp),
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = tool.color.copy(alpha = 0.15f)
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    tool.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.5.sp),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 13.sp,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                if (tool.subToolRoutes != null) {
                    Text(
                        "${tool.subToolRoutes!!.size} tools",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
