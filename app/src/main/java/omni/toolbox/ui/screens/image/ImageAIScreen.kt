package omni.toolbox.ui.screens.image

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.data.image.FilterEngine
import omni.toolbox.data.image.CyberpunkTransformation
import coil.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun ImageAIScreen(navController: NavHostController, title: String) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var resultBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
        resultBitmap = null
    }

    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedImageUri == null) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Select Image")
                }
            } else {
                AsyncImage(
                    model = resultBitmap ?: selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isProcessing) {
                    CircularProgressIndicator()
                    LaunchedEffect(selectedImageUri) {
                        val uri = selectedImageUri ?: return@LaunchedEffect
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val bitmap = BitmapFactory.decodeStream(inputStream)
                                if (bitmap != null) {
                                    val transformed = FilterEngine.applyTransformations(
                                        bitmap,
                                        listOf(CyberpunkTransformation())
                                    )
                                    resultBitmap = transformed
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        isProcessing = false
                    }
                } else {
                    Button(
                        onClick = { isProcessing = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = resultBitmap == null
                    ) {
                        Icon(Icons.Default.AutoFixHigh, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (resultBitmap == null) "Process with AI" else "Already Processed")
                    }
                }

                OutlinedButton(onClick = { selectedImageUri = null; resultBitmap = null }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Clear")
                }
            }
        }
    }
}
