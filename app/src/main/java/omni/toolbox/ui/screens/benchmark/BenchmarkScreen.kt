package omni.toolbox.ui.screens.benchmark

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.OmniViewModel
import omni.toolbox.viewmodel.BenchmarkResult

@Composable
fun BenchmarkScreen(
    navController: NavHostController,
    viewModel: OmniViewModel
) {
    val isBenchmarking by viewModel.isBenchmarking
    val progress by viewModel.benchmarkProgress
    val status by viewModel.benchmarkStatus
    val lastResult by viewModel.lastBenchmarkResult
    val history by viewModel.benchmarkHistory

    ToolScreen(
        title = "PowerBench Engine",
        onBack = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Header Hero Panel
                item {
                    BenchmarkHeroCard(
                        isBenchmarking = isBenchmarking,
                        progress = progress,
                        status = status,
                        lastResult = lastResult,
                        onRun = { viewModel.runAllBenchmarks() }
                    )
                }

                if (lastResult != null) {
                    item {
                        Text(
                            text = "DETAILED SCORES",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        DetailedScoresGrid(result = lastResult!!)
                    }
                }

                item {
                    Text(
                        text = "HARDWARE PERFORMANCE RANKINGS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(history) { record ->
                    PerformanceRankingCard(
                        record = record,
                        isUserDevice = record.name.contains("This Device")
                    )
                }

                item {
                    BenchmarkInfoFooter()
                }
            }
        }
    }
}

@Composable
fun BenchmarkHeroCard(
    isBenchmarking: Boolean,
    progress: Float,
    status: String,
    lastResult: BenchmarkResult?,
    onRun: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SYSTEM PERFORMANCE PROFILER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gauge Circle or Score Display
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.Black.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Background visual concentric lines for speedometer aesthetic
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        color = Color.Green.copy(alpha = 0.03f),
                        radius = radius * 0.85f,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                if (isBenchmarking) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(0.85f),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "STRESSING CPU",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline,
                            letterSpacing = 0.5.sp
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val overallScore = if (lastResult != null) {
                            (lastResult.scoreCpu + lastResult.scoreGpu + lastResult.scoreMem + lastResult.scoreStorage) / 4
                        } else null

                        Text(
                            text = overallScore?.toString() ?: "N/A",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = if (overallScore != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Text(
                            text = lastResult?.rating?.uppercase() ?: "NOT RATED YET",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (lastResult != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Current status message
            Text(
                text = status,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().height(36.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRun,
                enabled = !isBenchmarking,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("run_benchmark_btn")
            ) {
                if (isBenchmarking) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("PROFILING ALL HARDWARE...")
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RUN DISPATCH BENCHMARK")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedScoresGrid(result: BenchmarkResult) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ScoreValueWidget(
                title = "CPU Multi-Core",
                score = result.scoreCpu,
                detail = "Primes finding arithmetic",
                icon = Icons.Default.Memory,
                tint = Color(0xFF00FF88),
                modifier = Modifier.weight(1f)
            )
            ScoreValueWidget(
                title = "GPU Blitting",
                score = result.scoreGpu,
                detail = "Fractal coordinate scaling",
                icon = Icons.Default.FilterVintage,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ScoreValueWidget(
                title = "Memory (RAM)",
                score = result.scoreMem,
                detail = "Floating bandwidth index",
                icon = Icons.Default.DeveloperBoard,
                tint = Color(0xFFFFB74D),
                modifier = Modifier.weight(1f)
            )
            ScoreValueWidget(
                title = "Disk Storage",
                score = result.scoreStorage,
                detail = "I/O read write loopback",
                icon = Icons.Default.Storage,
                tint = Color(0xFFFF4081),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScoreValueWidget(
    title: String,
    score: Int,
    detail: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.06f)
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(tint.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = score.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun PerformanceRankingCard(
    record: BenchmarkResult,
    isUserDevice: Boolean
) {
    val overallScore = (record.scoreCpu + record.scoreGpu + record.scoreMem + record.scoreStorage) / 4
    val maxRankScore = 13500 // normalization scale for simple visual graphs

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUserDevice) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isUserDevice) 1.5.dp else 1.dp,
            if (isUserDevice) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.05f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUserDevice) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUserDevice) Icons.Default.Bolt else Icons.Default.Devices,
                    contentDescription = null,
                    tint = if (isUserDevice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isUserDevice) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$overallScore pts",
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = if (isUserDevice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bar meter graph comparing scores
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (overallScore.toFloat() / maxRankScore).coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                if (isUserDevice) Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
                                else Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)))
                            )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = record.rating,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (isUserDevice) "Ran " + record.timestamp else record.timestamp,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun BenchmarkInfoFooter() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.05f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Understanding System Benchmarks",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "These numbers represent computed scores relative to performance loops running inside your Android OS virtual sandbox sandbox. Running stress tests compiles float points and verifies Sequential Read-Write (I/O) memory blocks. Disposing background tasks beforehand will yield a higher benchmark index.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
            )
        }
    }
}
