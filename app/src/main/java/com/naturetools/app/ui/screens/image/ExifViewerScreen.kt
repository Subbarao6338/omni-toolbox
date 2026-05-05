package com.naturetools.app.ui.screens.image

import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun ExifViewerScreen(navController: NavHostController) {
    val context = LocalContext.current
    var exifData by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val exifInterface = ExifInterface(stream)
                    val tags = listOf(
                        ExifInterface.TAG_DATETIME,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_F_NUMBER,
                        ExifInterface.TAG_ISO_SPEED_RATINGS,
                        ExifInterface.TAG_FOCAL_LENGTH,
                        ExifInterface.TAG_GPS_LATITUDE,
                        ExifInterface.TAG_GPS_LONGITUDE
                    )
                    exifData = tags.map { tag -> tag to (exifInterface.getAttribute(tag) ?: "N/A") }
                }
            } catch (e: Exception) {
                exifData = listOf("Error" to "Could not read EXIF data")
            }
        }
    }

    ToolScreen(
        title = "EXIF Viewer",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (exifData.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exifData) { (tag, value) ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(tag, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(value, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No image selected", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
