package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import com.example.ui.SystemHealth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: OmniViewModel,
    onNavigateToSection: (String) -> Unit
) {
    val systemHealth by viewModel.systemHealth.collectAsState()
    val logs by viewModel.networkLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Construction,
                            contentDescription = "Toolbox icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Omni Toolbox",
                            fontWeight = FontWeight.Medium,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearLogs() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Terminal logs",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Real-time Diagnostic Monitor Card
            DiagnosticMonitorCard(health = systemHealth)

            // 2. Section Title
            Text(
                text = "UTILITY SUITE",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            // 3. Adaptive Grid of Categories
            val categories = remember {
                listOf(
                    CategoryItem("Cloud Sync", "Google Drive & Mega", Icons.Default.CloudSync, "sync", Color(0xFF29B6F6)),
                    CategoryItem("Contacts", "Manage People", Icons.Default.Contacts, "contacts", Color(0xFFEC407A)),
                    CategoryItem("Summarize", "AI Document Summary", Icons.Default.AutoAwesome, "summarize", Color(0xFFAB47BC)),
                    CategoryItem("Developer", "Specs & Hardware", Icons.Default.Terminal, "developer", Color(0xFF00E676)),
                    CategoryItem("Security", "Crypto & Password", Icons.Default.Security, "security", Color(0xFFEC407A)),
                    CategoryItem("Automation", "Rules & Macros", Icons.Default.FlashOn, "automation", Color(0xFFFFB74D)),
                    CategoryItem("Media", "Art, Audio & Games", Icons.Default.SportsEsports, "media_fun", Color(0xFFFF4081))
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories) { category ->
                    SquareCategoryCard(
                        category = category,
                        onClick = { onNavigateToSection(category.route) }
                    )
                }
            }

            // 4. Real-time Scrolling Logs Panel
            TerminalLogsPanel(logs = logs)
        }
    }
}

@Composable
fun DiagnosticMonitorCard(health: SystemHealth) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Material 3 style title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Background Tasks",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFF2E7D32), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Healthy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Grid Row (Minimalist spacing and style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricWidget(
                    title = "CPU LOAD",
                    value = "${String.format("%.1f", health.cpuLoad)}%",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Memory,
                    progress = health.cpuLoad / 100f
                )

                MetricWidget(
                    title = "RAM USED",
                    value = "${health.memoryUsedMb}MB",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.DeveloperBoard,
                    progress = health.memoryUsedMb.toFloat() / health.memoryMaxMb
                )

                MetricWidget(
                    title = "THERMAL",
                    value = "${String.format("%.1f", health.temperatureC)}°C",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Thermostat,
                    progress = (health.temperatureC - 20f) / 40f
                )

                MetricWidget(
                    title = "BATTERY",
                    value = "${health.batteryLevel}%",
                    color = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.BatteryChargingFull,
                    progress = health.batteryLevel / 100f
                )
            }
        }
    }
}

@Composable
fun RowScope.MetricWidget(
    title: String,
    value: String,
    color: Color,
    icon: ImageVector,
    progress: Float
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Minimalist fluid progress bar
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .width(44.dp)
                .height(4.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun SquareCategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.size(height = 140.dp, width = 150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(category.color.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = category.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    // ... keep original if needed or mark as unused
    // I decided to replace it in the grid with SquareCategoryCard.
    // I will leave it here as it might be used elsewhere.
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(category.color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = category.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Arrow right icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun TerminalLogsPanel(logs: List<String>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16181F)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Terminal Icon",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REAL-TIME DIAGNOSTIC LOGS",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "CONSOLE STDOUT",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(6.dp))

            if (logs.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "$ omnitool daemon listening...\nSelect a tool above to display logs.",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Displays the most recent logs first in visual terminal
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 0 until minOf(logs.size, 6)) {
                            Text(
                                text = "⚡ ${logs[i]}",
                                color = if (logs[i].startsWith("Error") || logs[i].startsWith("API Failure")) Color(0xFFEF5350)
                                        else if (logs[i].contains("Success") || logs[i].contains("finished") || logs[i].contains("started")) Color(0xFF00E676)
                                        else Color.White.copy(alpha = 0.82f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

data class CategoryItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)
