package omni.toolbox.ui.screens.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

@Composable
fun VirtualTryOnScreen(navController: NavHostController, stableDiffusionUrl: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var clothUri by remember { mutableStateOf<Uri?>(null) }
    var humanUri by remember { mutableStateOf<Uri?>(null) }
    var resultBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val clothLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { clothUri = it }
    val humanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { humanUri = it }

    ToolScreen(title = "Virtual Try-On", onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Checkroom, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Try clothes on virtually using AI. Upload a cloth image and a human image.", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cloth Image", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    ImagePlaceholder(uri = clothUri, onClick = { clothLauncher.launch("image/*") })
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Human Image", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    ImagePlaceholder(uri = humanUri, onClick = { humanLauncher.launch("image/*") })
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (stableDiffusionUrl.isBlank()) {
                        Toast.makeText(context, "Please set Stable Diffusion API URL in Settings", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (clothUri == null || humanUri == null) {
                        Toast.makeText(context, "Please select both images", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isProcessing = true
                    scope.launch(Dispatchers.IO) {
                        try {
                            val client = OkHttpClient()

                            fun uriToBase64(uri: Uri): String {
                                val inputStream = context.contentResolver.openInputStream(uri)
                                val bytes = inputStream?.readBytes() ?: return ""
                                return Base64.encodeToString(bytes, Base64.DEFAULT)
                            }

                            val clothBase64 = uriToBase64(clothUri!!)
                            val humanBase64 = uriToBase64(humanUri!!)

                            // Based on typical Gradio/SD API for virtual try-on
                            val json = JSONObject().apply {
                                put("data", org.json.JSONArray().apply {
                                    put("data:image/jpeg;base64,$clothBase64")
                                    put("data:image/jpeg;base64,$humanBase64")
                                })
                            }

                            val body = json.toString().toRequestBody("application/json".toMediaType())
                            val request = Request.Builder()
                                .url("${stableDiffusionUrl.trimEnd('/')}/api/predict")
                                .post(body)
                                .build()

                            client.newCall(request).execute().use { response ->
                                if (response.isSuccessful) {
                                    val result = JSONObject(response.body?.string() ?: "")
                                    val dataArray = result.getJSONArray("data")
                                    if (dataArray.length() > 0) {
                                        val resultBase64 = dataArray.getString(0)
                                            .replace("data:image/jpeg;base64,", "")
                                            .replace("data:image/png;base64,", "")
                                        val imageBytes = Base64.decode(resultBase64, Base64.DEFAULT)
                                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                        withContext(Dispatchers.Main) {
                                            resultBitmap = bitmap
                                            Toast.makeText(context, "Try-on processed successfully", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    throw Exception("API Error: ${response.code}")
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        } finally {
                            withContext(Dispatchers.Main) {
                                isProcessing = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing && (clothUri != null && humanUri != null)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Start Virtual Try-On")
                }
            }

            if (resultBitmap != null) {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Result", style = MaterialTheme.typography.titleMedium)
                Card(modifier = Modifier.fillMaxWidth().height(400.dp).padding(top = 8.dp)) {
                    Image(
                        bitmap = resultBitmap!!.asImageBitmap(),
                        contentDescription = "Try-on Result",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun ImagePlaceholder(uri: Uri?, onClick: () -> Unit) {
    val context = LocalContext.current
    Surface(
        onClick = onClick,
        modifier = Modifier.size(150.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        if (uri != null) {
            val bitmap = remember(uri) {
                val inputStream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
