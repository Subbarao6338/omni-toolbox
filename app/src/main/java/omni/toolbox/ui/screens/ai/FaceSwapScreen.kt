package omni.toolbox.ui.screens.ai

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.data.image.FilterEngine
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FaceSwapScreen(navController: NavHostController) {
    val context = LocalContext.current
    var sourceImageUri by remember { mutableStateOf<Uri?>(null) }
    var targetImageUri by remember { mutableStateOf<Uri?>(null) }
    var resultBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val sourceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { sourceImageUri = it }
    val targetLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { targetImageUri = it }

    ToolScreen(title = "Face Swap AI", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ImagePickerCard("Source Face", sourceImageUri, { sourceLauncher.launch("image/*") }, Modifier.weight(1f))
                ImagePickerCard("Target Image", targetImageUri, { targetLauncher.launch("image/*") }, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        isProcessing = true
                        withContext(Dispatchers.IO) {
                            try {
                                val inputStream = context.contentResolver.openInputStream(targetImageUri!!)
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                if (bitmap != null) {
                                    // Actual transformation (using Vintage as a placeholder for a "swap" look)
                                    val result = FilterEngine.applyTransformations(
                                        bitmap,
                                        listOf(omni.toolbox.data.image.ColorMatrixTransformation(android.graphics.ColorMatrix(omni.toolbox.data.image.ImageFilters.Vintage)))
                                    )
                                    resultBitmap = result
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        isProcessing = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing && sourceImageUri != null && targetImageUri != null
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Swap Faces")
                }
            }

            if (resultBitmap != null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Result", style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth().height(300.dp).padding(vertical = 8.dp)) {
                    AsyncImage(
                        model = resultBitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Button(onClick = { /* Save result */ }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Result")
                }
            }
        }
    }
}

@Composable
fun ImagePickerCard(label: String, uri: Uri?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.height(150.dp)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (uri != null) {
                AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
