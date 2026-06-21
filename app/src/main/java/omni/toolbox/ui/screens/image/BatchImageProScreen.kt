package omni.toolbox.ui.screens.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.AdjustmentSlider
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun BatchImageProScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var sepia by remember { mutableStateOf(false) }
    var invert by remember { mutableStateOf(false) }
    var grayscale by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedUris = uris
    }

    ToolScreen(
        title = "Batch Image Pro",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (selectedUris.isNotEmpty()) {
                Text("${selectedUris.size} Images Selected", style = MaterialTheme.typography.titleMedium)
                LazyRow(modifier = Modifier.height(100.dp).padding(vertical = 8.dp)) {
                    items(selectedUris) { uri ->
                        Card(modifier = Modifier.padding(end = 8.dp)) {
                            androidx.compose.foundation.Image(
                                painter = coil.compose.rememberAsyncImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Filters", style = MaterialTheme.typography.titleMedium)
                        AdjustmentSlider("Brightness", -1f..1f, brightness) { brightness = it }
                        AdjustmentSlider("Contrast", 0f..2f, contrast) { contrast = it }
                        AdjustmentSlider("Saturation", 0f..2f, saturation) { saturation = it }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            FilterToggle("Sepia", sepia) { sepia = it }
                            FilterToggle("Invert", invert) { invert = it }
                            FilterToggle("Gray", grayscale) { grayscale = it }
                        }
                    }
                }

                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val outputDir = File(context.cacheDir, "batch_output")
                                if (!outputDir.exists()) outputDir.mkdirs()

                                selectedUris.forEachIndexed { index, uri ->
                                    val inputStream = context.contentResolver.openInputStream(uri)
                                    val original = BitmapFactory.decodeStream(inputStream)
                                    val result = ImageProcessor.applyFilters(
                                        original, brightness, contrast, saturation, 0f, sepia, invert, grayscale
                                    )
                                    val outFile = File(outputDir, "batch_${index}_${System.currentTimeMillis()}.jpg")
                                    FileOutputStream(outFile).use { out ->
                                        result.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Processed ${selectedUris.size} images to cache.", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply to All and Save")
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Icon(Icons.Default.AddPhotoAlternate, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Multiple Images")
                    }
                }
            }
        }
    }
}

@Composable
fun FilterToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
