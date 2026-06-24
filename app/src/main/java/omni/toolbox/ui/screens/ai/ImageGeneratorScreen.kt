package omni.toolbox.ui.screens.ai

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import omni.toolbox.ui.components.ToolScreen
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@Composable
fun ImageGeneratorScreen(navController: NavHostController, aiApiKey: String = "", stableDiffusionUrl: String = "") {
    var prompt by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedImage by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val client = remember {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    ToolScreen(
        title = "AI Image Generator",
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
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Enter prompt for generation") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (stableDiffusionUrl.isBlank()) {
                        Toast.makeText(context, "Please configure Stable Diffusion URL in Settings", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    isGenerating = true
                    scope.launch(Dispatchers.IO) {
                        try {
                            val json = JSONObject().apply {
                                put("prompt", prompt)
                                put("steps", 20)
                                put("width", 512)
                                put("height", 512)
                            }
                            val body = json.toString().toRequestBody("application/json".toMediaType())
                            val request = Request.Builder()
                                .url("$stableDiffusionUrl/sdapi/v1/txt2img")
                                .post(body)
                                .build()

                            client.newCall(request).execute().use { response ->
                                if (!response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "Error: ${response.code}", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    val responseData = response.body?.string()
                                    if (responseData != null) {
                                        val jsonResponse = JSONObject(responseData)
                                        val images = jsonResponse.getJSONArray("images")
                                        if (images.length() > 0) {
                                            val base64Image = images.getString(0)
                                            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                            generatedImage = bitmap
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        } finally {
                            isGenerating = false
                        }
                    }
                },
                enabled = !isGenerating && prompt.isNotBlank()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Generate Image (SD API)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (generatedImage != null) {
                        Image(
                            bitmap = generatedImage!!.asImageBitmap(),
                            contentDescription = "Generated Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else if (isGenerating) {
                        Text("Generating...")
                    } else {
                        Text("Image will appear here")
                    }
                }
            }

            if (stableDiffusionUrl.isEmpty()) {
                Text(
                    "Stable Diffusion URL not set. Please go to Settings to configure it.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
