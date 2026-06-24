package omni.toolbox.ui.screens.ai

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun ImageGeneratorScreen(navController: NavHostController, aiApiKey: String = "", stableDiffusionUrl: String = "") {
    var prompt by remember { mutableStateOf("") }
    var seed by remember { mutableStateOf(0L) }
    var isGenerating by remember { mutableStateOf(false) }
    var generatedImage by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val scope = rememberCoroutineScope()

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
                    if (stableDiffusionUrl.isNotBlank()) {
                        isGenerating = true
                        scope.launch(Dispatchers.IO) {
                            try {
                                val client = OkHttpClient()
                                val json = JSONObject().apply {
                                    put("prompt", prompt)
                                    put("steps", 20)
                                }
                                val body = json.toString().toRequestBody("application/json".toMediaType())
                                val request = Request.Builder()
                                    .url("${stableDiffusionUrl.trimEnd('/')}/sdapi/v1/txt2img")
                                    .post(body)
                                    .build()

                                client.newCall(request).execute().use { response ->
                                    if (response.isSuccessful) {
                                        val result = JSONObject(response.body?.string() ?: "")
                                        val images = result.getJSONArray("images")
                                        if (images.length() > 0) {
                                            val base64Image = images.getString(0)
                                            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                            withContext(Dispatchers.Main) {
                                                generatedImage = bitmap
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                withContext(Dispatchers.Main) {
                                    isGenerating = false
                                }
                            }
                        }
                    } else {
                        generatedImage = null
                        seed = prompt.hashCode().toLong() + Random.nextLong()
                    }
                },
                enabled = !isGenerating && prompt.isNotBlank()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (stableDiffusionUrl.isBlank()) "Generate Offline (Local AI)" else "Generate via Stable Diffusion")
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
                    } else if (seed != 0L) {
                        val random = remember(seed) { Random(seed) }
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            repeat(50) {
                                drawCircle(
                                    color = Color(
                                        random.nextInt(256),
                                        random.nextInt(256),
                                        random.nextInt(256),
                                        random.nextInt(100, 200)
                                    ),
                                    radius = random.nextFloat() * 200f,
                                    center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height)
                                )
                            }
                        }
                    } else {
                        Text("No image generated yet", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            if (prompt.isEmpty()) {
                Text("Enter a prompt to generate abstract art offline", modifier = Modifier.padding(top = 16.dp))
            } else if (stableDiffusionUrl.isBlank()) {
                Text("Offline implementation using procedural generation based on: '$prompt'", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp))
            } else {
                Text("Generating via Stable Diffusion API at: $stableDiffusionUrl", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
