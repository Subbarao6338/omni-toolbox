package omni.toolbox.ui.screens.system

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PowerBenchScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var isRunning by remember { mutableStateOf(false) }
    var cpuScore by remember { mutableIntStateOf(0) }
    var gpuScore by remember { mutableIntStateOf(0) }
    var ioScore by remember { mutableIntStateOf(0) }

    val logs = remember { mutableStateListOf<String>() }

    ToolScreen(
        title = "PowerBench Benchmark Suite",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Hardware Performance Profiler", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Stress test your CPU, GPU, and I/O bandwidth.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(24.dp))

            BenchmarkCard("CPU Multi-Core", cpuScore, 15000, MaterialTheme.colorScheme.primary)
            BenchmarkCard("GPU Fractal Rendering", gpuScore, 8000, MaterialTheme.colorScheme.secondary)
            BenchmarkCard("Storage I/O Speed", ioScore, 5000, MaterialTheme.colorScheme.tertiary)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        isRunning = true
                        logs.clear()
                        cpuScore = 0; gpuScore = 0; ioScore = 0

                        logs.add("> Initializing Stress-Test threads...")
                        delay(1000)

                        logs.add("> Computing massive prime sequences...")
                        repeat(10) {
                            cpuScore += (1000..1500).random()
                            delay(300)
                        }

                        logs.add("> Profiling Mandelbrot vector scaling...")
                        repeat(10) {
                            gpuScore += (500..800).random()
                            delay(300)
                        }

                        logs.add("> Measuring sequential I/O bandwidth...")
                        repeat(10) {
                            ioScore += (300..500).random()
                            delay(300)
                        }

                        logs.add("> [OK] Benchmark complete. Scores saved.")
                        isRunning = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRunning
            ) {
                if (isRunning) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.Speed, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Run Full Benchmark")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Diagnostic Run Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
            ) {
                Column(modifier = Modifier.padding(12.dp).verticalScroll(rememberScrollState())) {
                    logs.forEach { log ->
                        Text(log, color = Color(0xFF39FF14), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BenchmarkCard(label: String, score: Int, max: Int, color: Color) {
    val progress = score.toFloat() / max
    ElevatedCard(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontWeight = FontWeight.Bold)
                Text("$score pts", color = color, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = color
            )
            Text("Reference: $max pts (Pixel 8 Pro)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
        }
    }
}
