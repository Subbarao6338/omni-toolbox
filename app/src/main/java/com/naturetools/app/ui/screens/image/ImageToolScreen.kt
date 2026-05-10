package com.naturetools.app.ui.screens.image

import com.naturetools.app.ui.screens.audio.AdjustmentSlider

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.naturetools.app.ui.components.ToolScreen

@Composable
fun ImageToolScreen(navController: NavHostController, title: String) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    ToolScreen(
        title = title,
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
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Processing Controls",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (title) {
                            "Smart Tools", "Image AI Tools" -> {
                                AdjustmentSlider("AI Upscale (x)", valueRange = 1f..4f, initialValue = 2f)
                                AdjustmentSlider("Denoise Strength")
                                AdjustmentSlider("Detail Enhancement")
                                AdjustmentSlider("Color Correction")
                            }
                            "Resize and Convert", "Format Conversion", "Resize Image" -> {
                                AdjustmentSlider("Quality", initialValue = 0.9f)
                                AdjustmentSlider("Scale Factor", valueRange = 0.1f..2.0f, initialValue = 1.0f)
                                Text("Output Format: JPEG/PNG/WEBP/AVIF")
                            }
                            "Compress Image", "Compress PDF" -> {
                                AdjustmentSlider("Target Compression Ratio")
                                AdjustmentSlider("Max Dimension (px)", valueRange = 100f..4000f, initialValue = 1920f)
                                AdjustmentSlider("Chroma Subsampling", initialValue = 0.5f)
                            }
                            "Crop", "Multi Crop" -> {
                                AdjustmentSlider("Aspect Ratio (X)", valueRange = 1f..16f, initialValue = 1f)
                                AdjustmentSlider("Aspect Ratio (Y)", valueRange = 1f..16f, initialValue = 1f)
                                AdjustmentSlider("Rotation Offset", valueRange = -45f..45f, initialValue = 0f)
                            }
                            "Filter", "Photo Filters" -> {
                                AdjustmentSlider("Filter Intensity")
                                AdjustmentSlider("Vignette")
                                AdjustmentSlider("Grain")
                                AdjustmentSlider("Temperature")
                            }
                            "Background Remover" -> {
                                AdjustmentSlider("Edge Smoothing")
                                AdjustmentSlider("Sensitivity")
                                AdjustmentSlider("Background Feather")
                            }
                            "Watermarking" -> {
                                AdjustmentSlider("Watermark Opacity")
                                AdjustmentSlider("Watermark Scale", valueRange = 0.1f..1.0f, initialValue = 0.3f)
                                AdjustmentSlider("Position X")
                                AdjustmentSlider("Position Y")
                            }
                            "Exif Viewer", "Metadata", "Edit EXIF" -> {
                                Text("ISO: 100 | Shutter: 1/500s | Aperture: f/2.8")
                                Text("Camera: Pixel 8 Pro | Location: San Francisco")
                                AdjustmentSlider("Strip Private Data (0:No, 1:Yes)", initialValue = 0f)
                            }
                            "PDF Tools", "Preview PDF", "Split PDF", "Merge PDF" -> {
                                AdjustmentSlider("DPI for Rendering", valueRange = 72f..600f, initialValue = 150f)
                                AdjustmentSlider("PDF Quality", initialValue = 0.8f)
                                AdjustmentSlider("Password Complexity", valueRange = 1f..5f, initialValue = 3f)
                            }
                            "OCR", "PDF to Text (OCR)" -> {
                                AdjustmentSlider("Recognition Confidence")
                                AdjustmentSlider("Binarization Threshold")
                                Text("Language: English (US)")
                            }
                            "Pixel Art Maker" -> {
                                AdjustmentSlider("Pixel Size", valueRange = 1f..50f, initialValue = 10f)
                                AdjustmentSlider("Color Palette Size", valueRange = 2f..256f, initialValue = 16f)
                                AdjustmentSlider("Dithering")
                            }
                            "ASCII Art" -> {
                                AdjustmentSlider("Character Density", valueRange = 10f..200f, initialValue = 80f)
                                AdjustmentSlider("Contrast Boost")
                                Text("Style: Braille / Standard")
                            }
                            else -> {
                                AdjustmentSlider("Intensity")
                                AdjustmentSlider("Sensitivity")
                                AdjustmentSlider("Threshold")
                            }
                        }

                        if (title == "Single Edit" || title == "Draw") {
                            AdjustmentSlider("Brightness", valueRange = -1f..1f, initialValue = 0f)
                            AdjustmentSlider("Contrast", valueRange = 0.5f..2.0f, initialValue = 1.0f)
                            AdjustmentSlider("Saturation", valueRange = 0f..2.0f, initialValue = 1.0f)
                            AdjustmentSlider("Exposure", valueRange = -2f..2f, initialValue = 0f)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Simulated Save */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Apply & Save Result")
                }

                OutlinedButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Text("Change Input Image")
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Select an image to start $title",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { launcher.launch("image/*") },
                            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                        ) {
                            Text("Pick Image")
                        }
                    }
                }
            }
        }
    }
}
