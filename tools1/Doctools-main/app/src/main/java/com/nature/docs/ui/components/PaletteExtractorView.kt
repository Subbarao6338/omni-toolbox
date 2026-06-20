package com.nature.docs.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.data.image.PaletteExtractor
import com.nature.docs.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PaletteExtractorView(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var colors by remember { mutableStateOf<List<String>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }

    val pickLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedUri = it
            isProcessing = true
            scope.launch {
                val inputStream = context.contentResolver.openInputStream(it)
                val b = BitmapFactory.decodeStream(inputStream)
                bitmap = b
                if (b != null) {
                    colors = PaletteExtractor.extract(b)
                }
                isProcessing = false
            }
        }
    }

    LinenCanvas(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = InkBrown)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Palette Extractor", style = MaterialTheme.typography.titleLarge, color = InkBrown)
                        Text("IDENTIFY NATURAL COLORS", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen, letterSpacing = 1.sp)
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                if (bitmap == null) {
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AgedPaperCard(modifier = Modifier.size(280.dp).clickable { pickLauncher.launch("image/*") }) {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Eco, null, tint = BotanicalGreen, modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("SELECT SPECIMEN", style = MaterialTheme.typography.headlineMedium, color = InkBrown)
                                Text("Pick an image to extract its soul", style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.6f))
                            }
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            AgedPaperCard(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        item(span = { GridItemSpan(2) }) {
                            Text("EXTRACTED PALETTE", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.4f))
                        }

                        items(colors) { hex ->
                            AgedPaperCard(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(32.dp).background(Color(android.graphics.Color.parseColor(hex)), CircleShape))
                                    Spacer(Modifier.width(12.dp))
                                    Text(hex, style = MaterialTheme.typography.bodyLarge, color = InkBrown)
                                    Spacer(Modifier.weight(1f))
                                    IconButton(onClick = {
                                        clipboardManager.setText(AnnotatedString(hex))
                                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.ContentCopy, null, tint = BotanicalGreen, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        item(span = { GridItemSpan(2) }) {
                            Spacer(Modifier.height(80.dp))
                        }
                    }

                    Button(
                        onClick = { pickLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen)
                    ) {
                        Text("NEW SPECIMEN", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            if (isProcessing) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.2f)), contentAlignment = Alignment.Center) {
                    VineProgressBar(0.5f, Modifier.padding(32.dp))
                }
            }
        }
    }
}
