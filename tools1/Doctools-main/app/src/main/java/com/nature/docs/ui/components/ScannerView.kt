package com.nature.docs.ui.components

import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.nature.docs.ui.theme.BotanicalGreen
import com.nature.docs.ui.theme.ParchmentBg
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerView(onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nature Scanner") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = ParchmentBg))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val capture = imageCapture ?: return@FloatingActionButton
                    val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                    capture.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            Log.d("Scanner", "Image saved: ${file.absolutePath}")
                        }
                        override fun onError(exc: ImageCaptureException) {
                            Log.e("Scanner", "Photo capture failed: ${exc.message}", exc)
                        }
                    })
                },
                containerColor = BotanicalGreen
            ) {
                Icon(Icons.Default.Camera, contentDescription = "Capture", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        imageCapture = ImageCapture.Builder().build()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                        } catch (exc: Exception) {
                            Log.e("Scanner", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            BotanicalBorder(modifier = Modifier.fillMaxSize().padding(32.dp)) { }
        }
    }
}
