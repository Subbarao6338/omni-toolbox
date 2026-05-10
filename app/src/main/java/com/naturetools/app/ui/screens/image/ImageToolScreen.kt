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
                        .height(300.dp),
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
                            text = "Image Adjustments",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (title) {
                            "Smart Tools", "Offline Tools" -> {
                                Text("Offline AI enhancement selected. Using local model for optimization.")
                                AdjustmentSlider("Model Strength")
                                AdjustmentSlider("Denoise")
                            }
                            "Crop" -> Text("Select aspect ratio and drag to crop.")
                            "Filter" -> Text("Select a filter to apply.")
                            "Resize" -> Text("Enter new dimensions or percentage.")
                            "Compress PDFs/Document", "Compress Image", "Compress PDF" -> {
                                AdjustmentSlider("Quality", initialValue = 0.8f)
                                AdjustmentSlider("Target Size (KB)", valueRange = 10f..5000f, initialValue = 500f)
                            }
                            "Merge PDFs/Document", "Merge PDF" -> {
                                Text("Select multiple files to merge.")
                                AdjustmentSlider("Overlap/Spacing")
                            }
                            "Split PDFs/Document", "Remove/Delete Pages", "Split PDF", "Delete Pages", "Extract Pages" -> {
                                Text("Select pages to extract or remove.")
                                AdjustmentSlider("Page Range Start")
                                AdjustmentSlider("Page Range End")
                            }
                            "Rotate Pages/PDF", "Rotate & Flip", "Rotate Pages" -> {
                                AdjustmentSlider("Rotation Angle", valueRange = 0f..360f, initialValue = 0f)
                                Text("Choose Flip orientation below.")
                            }
                            "Crop Pages", "Crop Image" -> {
                                AdjustmentSlider("Top Margin", initialValue = 0f)
                                AdjustmentSlider("Bottom Margin", initialValue = 0f)
                                AdjustmentSlider("Left Margin", initialValue = 0f)
                                AdjustmentSlider("Right Margin", initialValue = 0f)
                            }
                            "Image → PDF", "Images to PDF" -> {
                                Text("Converting selected images to a PDF document.")
                                AdjustmentSlider("Page Margin", initialValue = 20f, valueRange = 0f..100f)
                            }
                            "Extract Images" -> {
                                Text("Extracting embedded images from the document.")
                                AdjustmentSlider("Detection Sensitivity")
                            }
                            "Document Info", "View Metadata", "Metadata / Edit Props", "Strip Metadata" -> {
                                Text("Metadata and technical details of the file.")
                            }
                            "ICO Converter", "SVG Converter", "AVIF Converter", "WEBP Converter", "GIF Converter", "HEIC Converter", "Convert Format", "Resize Image" -> {
                                Text("Select target format and quality.")
                                AdjustmentSlider("Quality", initialValue = 0.9f)
                            }
                            "Lock PDF", "Unlock PDF" -> {
                                Text("Enter password in the dialog after clicking apply.")
                                AdjustmentSlider("Security Level", valueRange = 1f..5f, initialValue = 3f)
                            }
                            "Add Watermark" -> {
                                AdjustmentSlider("Opacity", initialValue = 0.5f)
                                AdjustmentSlider("Watermark Scale", initialValue = 1.0f, valueRange = 0.1f..3.0f)
                            }
                            "Add Text", "Add Images" -> {
                                AdjustmentSlider("Position X", valueRange = 0f..1000f)
                                AdjustmentSlider("Position Y", valueRange = 0f..1000f)
                            }
                            "Page Numbers" -> {
                                AdjustmentSlider("Font Size", valueRange = 6f..72f, initialValue = 12f)
                                AdjustmentSlider("Vertical Offset", valueRange = 0f..100f, initialValue = 20f)
                            }
                            "HTML → PDF", "Excel to PDF", "Text to PDF" -> {
                                Text("Select the source file to start conversion.")
                            }
                            else -> Text("Processing options for $title. Tap the button below to process your document.")
                        }

                        if (title == "Single Edit" || title == "Painter") {
                            AdjustmentSlider("Brightness")
                            AdjustmentSlider("Contrast")
                            AdjustmentSlider("Saturation")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Simulated Save */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply and Save")
                }

                OutlinedButton(
                    onClick = { selectedImageUri = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose Different Image")
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Select an image to start $title")
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { launcher.launch("image/*") }) {
                            Text("Pick Image")
                        }
                    }
                }
            }
        }
    }
}
