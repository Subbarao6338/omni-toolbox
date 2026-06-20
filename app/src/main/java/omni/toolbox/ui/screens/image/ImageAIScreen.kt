package omni.toolbox.ui.screens.image

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.data.image.FilterEngine
import omni.toolbox.data.image.BrightnessTransformation
import coil.compose.AsyncImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun ImageAIScreen(navController: NavHostController, title: String) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var resultUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
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
                    model = resultUri ?: selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isProcessing) {
                    CircularProgressIndicator()
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(2000)
                        isProcessing = false
                        resultUri = selectedImageUri // Simulate result
                    }
                } else {
                    Button(
                        onClick = { isProcessing = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoFixHigh, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Process with AI")
                    }
                }

                OutlinedButton(onClick = { selectedImageUri = null; resultUri = null }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("Clear")
                }
            }
        }
    }
}
