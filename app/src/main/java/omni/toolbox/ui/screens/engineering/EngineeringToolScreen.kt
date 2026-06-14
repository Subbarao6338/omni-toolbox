package omni.toolbox.ui.screens.engineering

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun EngineeringToolScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "Resistor Color Code" -> ResistorCalculator()
                "Logic Gates" -> LogicGateSim()
                "Antenna Calc" -> AntennaCalculator()
                "PCB Trace Width" -> PcbTraceCalculator()
                "Force Calculator" -> ForceCalculator()
                "Signal Gen", "Signal Generator" -> SignalGenerator()
                "Filter Designer" -> FilterDesigner()
                else -> {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Specialized engineering tool for $title")
                }
            }
        }
    }
}

@Composable
fun ResistorCalculator() {
    var band1 by remember { mutableStateOf(0) }
    var band2 by remember { mutableStateOf(0) }
    var multiplier by remember { mutableStateOf(1) }

    val colors = listOf(
        Color.Black, Color(0xFF8B4513), Color.Red, Color(0xFFFFA500),
        Color.Yellow, Color.Green, Color.Blue, Color(0xFF800080),
        Color.Gray, Color.White
    )

    val resistance = (band1 * 10 + band2) * Math.pow(10.0, multiplier.toDouble())

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Resistor Visualizer", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
                val center = size.height / 2
                drawRect(Color.LightGray, Offset(0f, center - 5f), Size(size.width, 10f))
                drawRect(Color(0xFFDEB887), Offset(size.width * 0.2f, 0f), Size(size.width * 0.6f, size.height))

                drawRect(colors[band1], Offset(size.width * 0.3f, 0f), Size(size.width * 0.05f, size.height))
                drawRect(colors[band2], Offset(size.width * 0.45f, 0f), Size(size.width * 0.05f, size.height))
                drawRect(colors[multiplier], Offset(size.width * 0.6f, 0f), Size(size.width * 0.05f, size.height))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Value: ${formatResistance(resistance)} Ω", style = MaterialTheme.typography.headlineMedium)
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    BandSelector("Band 1", band1) { band1 = it }
    BandSelector("Band 2", band2) { band2 = it }
    BandSelector("Multiplier", multiplier) { multiplier = it }
}

fun formatResistance(r: Double): String {
    return when {
        r >= 1_000_000 -> String.format("%.1f M", r / 1_000_000)
        r >= 1_000 -> String.format("%.1f k", r / 1_000)
        else -> r.toInt().toString()
    }
}

@Composable
fun BandSelector(label: String, selected: Int, onSelect: (Int) -> Unit) {
    Text(label, style = MaterialTheme.typography.labelLarge)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        (0..9).forEach { i ->
            FilterChip(
                selected = selected == i,
                onClick = { onSelect(i) },
                label = { Text(i.toString()) }
            )
        }
    }
}

@Composable
fun LogicGateSim() {
    var inputA by remember { mutableStateOf(false) }
    var inputB by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = inputA, onCheckedChange = { inputA = it })
            Text(" Input A")
            Spacer(modifier = Modifier.width(32.dp))
            Switch(checked = inputB, onCheckedChange = { inputB = it })
            Text(" Input B")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LogicResult("AND", inputA && inputB)
        LogicResult("OR", inputA || inputB)
        LogicResult("XOR", inputA xor inputB)
        LogicResult("NAND", !(inputA && inputB))
    }
}

@Composable
fun LogicResult(name: String, result: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name)
        Text(if (result) "HIGH (1)" else "LOW (0)", color = if (result) Color.Green else Color.Red)
    }
}

@Composable
fun AntennaCalculator() {
    var freq by remember { mutableStateOf("2400") }
    val f = freq.toDoubleOrNull() ?: 2400.0
    val waveLength = 299.79 / f // meters
    val halfWave = waveLength / 2 * 1000 // mm
    val quarterWave = waveLength / 4 * 1000 // mm

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
            value = freq,
            onValueChange = { freq = it },
            label = { Text("Frequency (MHz)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("Wavelength: ${String.format("%.4f", waveLength)} m")
        Text("Half-wave Dipole: ${String.format("%.2f", halfWave)} mm")
        Text("Quarter-wave Monopole: ${String.format("%.2f", quarterWave)} mm")
    }
}

@Composable
fun PcbTraceCalculator() {
    var current by remember { mutableStateOf("1.0") }
    var tempRise by remember { mutableStateOf("10") }
    val i = current.toDoubleOrNull() ?: 1.0
    val dt = tempRise.toDoubleOrNull() ?: 10.0
    // Simplified IPC-2221 internal trace
    val area = Math.pow(i / (0.024 * Math.pow(dt, 0.44)), 1.0/0.725)
    val width = area / 1.37 // for 1oz copper (35um)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(value = current, onValueChange = { current = it }, label = { Text("Current (Amps)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tempRise, onValueChange = { tempRise = it }, label = { Text("Temp Rise (°C)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Text("Required Trace Area: ${String.format("%.4f", area)} mil²")
        Text("Trace Width (1oz Cu): ${String.format("%.2f", width)} mils")
    }
}

@Composable
fun ForceCalculator() {
    var mass by remember { mutableStateOf("") }
    var accel by remember { mutableStateOf("") }
    val m = mass.toDoubleOrNull() ?: 0.0
    val a = accel.toDoubleOrNull() ?: 0.0

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(value = mass, onValueChange = { mass = it }, label = { Text("Mass (kg)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = accel, onValueChange = { accel = it }, label = { Text("Acceleration (m/s²)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Text("Force (F = ma): ${String.format("%.2f", m * a)} Newtons", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun SignalGenerator() {
    var frequency by remember { mutableFloatStateOf(440f) }
    var isRunning by remember { mutableStateOf(false) }
    var waveform by remember { mutableStateOf("Sine") }

    val sampleRate = 44100
    val bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)

    val audioTrack = remember {
        AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    DisposableEffect(Unit) { onDispose { isRunning = false; audioTrack.release() } }

    LaunchedEffect(isRunning, frequency, waveform) {
        if (isRunning) {
            withContext(Dispatchers.Default) {
                audioTrack.play()
                val samples = ShortArray(bufferSize)
                var angle = 0.0
                while (isActive && isRunning) {
                    for (i in samples.indices) {
                        val sample = when (waveform) {
                            "Sine" -> sin(angle)
                            "Square" -> if (sin(angle) >= 0) 1.0 else -1.0
                            "Triangle" -> (2.0 / PI) * Math.asin(sin(angle))
                            else -> sin(angle)
                        }
                        samples[i] = (sample * 16383.0).toInt().toShort()
                        angle += 2.0 * PI * frequency / sampleRate
                    }
                    audioTrack.write(samples, 0, bufferSize)
                }
                audioTrack.stop()
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Frequency: ${frequency.toInt()} Hz", style = MaterialTheme.typography.titleLarge)
        Slider(value = frequency, onValueChange = { frequency = it }, valueRange = 20f..20000f)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("Sine", "Square", "Triangle").forEach {
                FilterChip(selected = waveform == it, onClick = { waveform = it }, label = { Text(it) })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color.Red else MaterialTheme.colorScheme.primary)
        ) {
            Text(if (isRunning) "STOP SIGNAL" else "START SIGNAL")
        }
    }
}

@Composable
fun FilterDesigner() {
    var cutoffFreq by remember { mutableStateOf("1000") }
    var capacitor by remember { mutableStateOf("100") } // nF

    val f = cutoffFreq.toDoubleOrNull() ?: 1000.0
    val c = (capacitor.toDoubleOrNull() ?: 100.0) / 1e9
    val r = 1.0 / (2.0 * Math.PI * f * c)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("RC Filter Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = cutoffFreq, onValueChange = { cutoffFreq = it }, label = { Text("Cutoff Frequency (Hz)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = capacitor, onValueChange = { capacitor = it }, label = { Text("Capacitance (nF)") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Required Resistance (R):", style = MaterialTheme.typography.labelLarge)
                Text("${String.format("%.2f", r)} Ω", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
