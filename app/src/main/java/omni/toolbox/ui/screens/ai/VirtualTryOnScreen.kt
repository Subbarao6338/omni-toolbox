package omni.toolbox.ui.screens.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

@Composable
fun VirtualTryOnScreen(navController: NavHostController, stableDiffusionUrl: String = "") {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var clothImageUri by remember { mutableStateOf<Uri?>(null) }
    var humanImageUri by remember { mutableStateOf<Uri?>(null) }
    var resultImage by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val clothLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { clothImageUri = it }
    val humanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { humanImageUri = it }

    val client = remember {
        OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    ToolScreen(title = "Virtual Try-On", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cloth Image", style = MaterialTheme.typography.labelLarge)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (clothImageUri != null) {
                            AsyncImage(model = clothImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                        } else {
                            IconButton(onClick = { clothLauncher.launch("image/*") }) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                            }
                        }
                    }
                    if (clothImageUri != null) {
                        TextButton(onClick = { clothLauncher.launch("image/*") }) { Text("Change") }
                    }
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Human Image", style = MaterialTheme.typography.labelLarge)
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (humanImageUri != null) {
                            AsyncImage(model = humanImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                        } else {
                            IconButton(onClick = { humanLauncher.launch("image/*") }) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null)
                            }
                        }
                    }
                    if (humanImageUri != null) {
                        TextButton(onClick = { humanLauncher.launch("image/*") }) { Text("Change") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (stableDiffusionUrl.isBlank()) {
                        Toast.makeText(context, "Please configure API URL in Settings", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    isProcessing = true
                    scope.launch(Dispatchers.IO) {
                        try {
                            val clothBase64 = uriToBase64(context, clothImageUri!!)
                            val humanBase64 = uriToBase64(context, humanImageUri!!)

                            // This is a representative implementation.
                            // Virtual Try-on often uses specific extensions or scripts in SD WebUI.
                            // We simulate calling a hypothetical /sdapi/v1/virtual-try-on or similar if it existed,
                            // but usually it's img2img with ControlNet or specific extension API.
                            // For this task, we will follow the 'Stable Diffusion' API pattern.

                            val json = JSONObject().apply {
                                put("cloth_image", clothBase64)
                                put("human_image", humanBase64)
                            }

                            val body = json.toString().toRequestBody("application/json".toMediaType())
                            val request = Request.Builder()
                                .url("$stableDiffusionUrl/sdapi/v1/virtual-try-on")
                                .post(body)
                                .build()

                            client.newCall(request).execute().use { response ->
                                if (!response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Server error or API not supported", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val responseData = response.body?.string()
                                    if (responseData != null) {
                                        val jsonResponse = JSONObject(responseData)
                                        val imageStr = jsonResponse.optString("image", "")
                                        if (imageStr.isNotEmpty()) {
                                            val imageBytes = Base64.decode(imageStr, Base64.DEFAULT)
                                            resultImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                enabled = !isProcessing && clothImageUri != null && humanImageUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Run Virtual Try-On")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Result", style = MaterialTheme.typography.titleMedium)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(top = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (resultImage != null) {
                        Image(bitmap = resultImage!!.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                    } else {
                        Text("Result will appear here", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

fun uriToBase64(context: android.content.Context, uri: Uri): String {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes() ?: ByteArray(0)
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
