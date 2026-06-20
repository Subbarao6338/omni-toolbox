package com.nature.docs.ui.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nature.docs.data.FileVault
import com.nature.docs.ui.theme.BotanicalGreen
import com.nature.docs.ui.theme.ParchmentBg
import kotlinx.coroutines.launch
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditorView(bitmap: Bitmap, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var brightness by remember { mutableStateOf(0f) }
    var contrast by remember { mutableStateOf(1f) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Studio") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                val output = FileVault.getTempFile(context, ".jpg")
                                FileOutputStream(output).use { out ->
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                }
                                Toast.makeText(context, "Exported to ${output.name}", Toast.LENGTH_SHORT).show()
                                isSaving = false
                            }
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        else Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ParchmentBg)
            )
        },
        bottomBar = {
            AgedPaperCard(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                Column {
                    Text("Brightness", style = MaterialTheme.typography.labelSmall)
                    Slider(value = brightness, onValueChange = { brightness = it }, valueRange = -1f..1f, colors = SliderDefaults.colors(thumbColor = BotanicalGreen, activeTrackColor = BotanicalGreen))
                    Text("Contrast", style = MaterialTheme.typography.labelSmall)
                    Slider(value = contrast, onValueChange = { contrast = it }, valueRange = 0f..2f, colors = SliderDefaults.colors(thumbColor = BotanicalGreen, activeTrackColor = BotanicalGreen))
                }
            }
        }
    ) { padding ->
        LinenCanvas(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
