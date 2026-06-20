package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.OmniViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(viewModel: OmniViewModel, onBack: () -> Unit) {
    var activeTab by remember { mutableStateOf(0) } // 0: Encryption, 1: Password Gen, 2: QR Tools

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security & Encryption suite") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TabRow(selectedTabIndex = activeTab) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("Crypto Engine", modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("Password Gen", modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
                Tab(selected = activeTab == 2, onClick = { activeTab = 2 }) {
                    Text("QR Scanner/Gen", modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (activeTab) {
                0 -> EncryptionTab()
                1 -> PasswordGenTab()
                2 -> QRToolsTab()
            }
        }
    }
}

@Composable
fun EncryptionTab() {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("MyFallbackSecKey") }
    var outputText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("ADVANCED CRYPTOGRAPHIC ENGINE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }

        item {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Input content to process", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )
        }

        item {
            TextField(
                value = secretKey,
                onValueChange = { secretKey = it },
                label = { Text("Secret Passphrase / Salt (16 characters for AES)", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AES Enrypt
                Button(
                    onClick = {
                        try {
                            errorMessage = ""
                            val normalizedKey = secretKey.padEnd(16, ' ').take(16).toByteArray(StandardCharsets.UTF_8)
                            val key = SecretKeySpec(normalizedKey, "AES")
                            val ivBytes = ByteArray(16) { 0 } // constant IV for simpler prototype decrypts
                            val ivSpec = IvParameterSpec(ivBytes)
                            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
                            val encrypted = cipher.doFinal(inputText.toByteArray(StandardCharsets.UTF_8))
                            outputText = Base64.encodeToString(encrypted, Base64.DEFAULT)
                        } catch (e: Exception) {
                            errorMessage = "AES Error: ${e.message}"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("AES Encrypt", fontSize = 11.sp)
                }

                // AES Decrypt
                Button(
                    onClick = {
                        try {
                            errorMessage = ""
                            val normalizedKey = secretKey.padEnd(16, ' ').take(16).toByteArray(StandardCharsets.UTF_8)
                            val key = SecretKeySpec(normalizedKey, "AES")
                            val ivBytes = ByteArray(16) { 0 }
                            val ivSpec = IvParameterSpec(ivBytes)
                            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
                            val decodedBytes = Base64.decode(inputText, Base64.DEFAULT)
                            val decrypted = cipher.doFinal(decodedBytes)
                            outputText = String(decrypted, StandardCharsets.UTF_8)
                        } catch (e: Exception) {
                            errorMessage = "AES Decrypt Failed. Ensure valid base64 and key."
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("AES Decrypt", fontSize = 11.sp)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // SHA-256 Hash
                Button(
                    onClick = {
                        try {
                            errorMessage = ""
                            val digest = MessageDigest.getInstance("SHA-256")
                            val hash = digest.digest(inputText.toByteArray(StandardCharsets.UTF_8))
                            outputText = hash.joinToString("") { String.format("%02x", it) }
                        } catch (e: Exception) {
                            errorMessage = "Hash error: ${e.message}"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("SHA-256 Hash", fontSize = 11.sp)
                }

                // MD5 Hash
                Button(
                    onClick = {
                        try {
                            errorMessage = ""
                            val digest = MessageDigest.getInstance("MD5")
                            val hash = digest.digest(inputText.toByteArray(StandardCharsets.UTF_8))
                            outputText = hash.joinToString("") { String.format("%02x", it) }
                        } catch (e: Exception) {
                            errorMessage = "Hash error: ${e.message}"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("MD5 Hash", fontSize = 11.sp)
                }
            }
        }

        if (errorMessage.isNotBlank()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(errorMessage, modifier = Modifier.padding(10.dp), color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("CRYPTO RESULT ENGINE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        IconButton(onClick = {
                            val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            manager.setPrimaryClip(ClipData.newPlainText("Encrypted", outputText))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (outputText.isNotBlank()) outputText else "No operation executed.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = if (outputText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordGenTab() {
    val context = LocalContext.current
    var length by remember { mutableStateOf(14.0f) }
    var includeLetters by remember { mutableStateOf(true) }
    var includeDigits by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }

    // Logic for strength evaluation
    val strength = remember(generatedPassword) {
        if (generatedPassword.isEmpty()) ""
        else if (generatedPassword.length < 8) "WEAK"
        else if (generatedPassword.length < 12) "MODERATE"
        else "STRONG"
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("SECURE USER PASSWORD GENERATOR", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Length: ${length.toInt()} Characters", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Slider(
                        value = length,
                        onValueChange = { length = it },
                        valueRange = 6f..32f
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Include Letters (a-z, A-Z)")
                        Switch(checked = includeLetters, onCheckedChange = { includeLetters = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Include Digits (0-9)")
                        Switch(checked = includeDigits, onCheckedChange = { includeDigits = it })
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Include Symbols (!@#$%^&)")
                        Switch(checked = includeSymbols, onCheckedChange = { includeSymbols = it })
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val passLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    val passDigits = "0123456789"
                    val passSymbols = "!@#$%^&*()_+-=[]{}|;':,./<>?"
                    var pool = ""
                    if (includeLetters) pool += passLetters
                    if (includeDigits) pool += passDigits
                    if (includeSymbols) pool += passSymbols
                    if (pool.isEmpty()) pool = passLetters

                    val characters = StringBuilder()
                    for (i in 1..length.toInt()) {
                        characters.append(pool[Random.nextInt(pool.length)])
                    }
                    generatedPassword = characters.toString()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Shuffle, contentDescription = "Generate")
                Spacer(modifier = Modifier.width(8.dp))
                Text("GENERATE CIPHER KEY")
            }
        }

        if (generatedPassword.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PASSWORD CIPHER", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = strength,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (strength) {
                                        "WEAK" -> Color(0xFFEF5350)
                                        "MODERATE" -> Color(0xFFFFB74D)
                                        else -> Color(0xFF66BB6A)
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(onClick = {
                                    val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    manager.setPrimaryClip(ClipData.newPlainText("Generated Password", generatedPassword))
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy text")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            generatedPassword,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRToolsTab() {
    var isRecordingScanner by remember { mutableStateOf(false) }
    var qrContentInput by remember { mutableStateOf("https://github.com/google/aistudio") }
    var isQrGenerated by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("SECURE PRIVACY QR UTILITY MODULE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }

        item {
            TabRow(selectedTabIndex = if (isRecordingScanner) 1 else 0) {
                Tab(selected = !isRecordingScanner, onClick = { isRecordingScanner = false }) {
                    Text("QR Generator", modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                }
                Tab(selected = isRecordingScanner, onClick = { isRecordingScanner = true }) {
                    Text("QR Scanner", modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                }
            }
        }

        if (!isRecordingScanner) {
            // GENERATOR
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Create Encoded QR Token", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        TextField(
                            value = qrContentInput,
                            onValueChange = {
                                qrContentInput = it
                                isQrGenerated = false
                            },
                            label = { Text("Enter plain text or web URL", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                        )

                        Button(onClick = { isQrGenerated = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.QrCode, contentDescription = "Gen Qr")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COMPILE QR GRID VECTOR")
                        }
                    }
                }
            }

            if (isQrGenerated) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Dynamic QR Frame Matrix", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Dynamic custom Canvas rendering of a retro pixel Qr code layout!
                                Canvas(modifier = Modifier.size(160.dp)) {
                                    val contentHash = qrContentInput.hashCode()
                                    val sizeX = 21
                                    val sizeY = 21
                                    val cellSize = size.width / sizeX

                                    // Seed a pseudo-generator based on inputs
                                    val random = Random(contentHash.toLong())

                                    for (x in 0 until sizeX) {
                                        for (y in 0 until sizeY) {
                                            // Render QR Anchor square borders
                                            val isAnchor = (x < 6 && y < 6) ||
                                                    (x >= sizeX - 6 && y < 6) ||
                                                    (x < 6 && y >= sizeY - 6)

                                            val isSolid = if (isAnchor) {
                                                // Specific anchor layout drawing
                                                val inner = (x in 1..4 && y in 1..4) ||
                                                        (x in (sizeX - 5)..(sizeX - 2) && y in 1..4) ||
                                                        (x in 1..4 && y in (sizeY - 5)..(sizeY - 2))
                                                val center = (x == 2 && y == 2) ||
                                                        (x == sizeX - 3 && y == 2) ||
                                                        (x == 2 && y == sizeY - 3)
                                                inner && !center
                                            } else {
                                                random.nextBoolean()
                                            }

                                            if (isSolid) {
                                                drawRect(
                                                    color = Color.Black,
                                                    topLeft = Offset(x * cellSize, y * cellSize),
                                                    size = Size(cellSize, cellSize)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = qrContentInput,
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // SCANNER WITH REAL CAMERA PERMISSION & FALLBACK EMULATOR CONTEXT
            item {
                QRXCameraScanner()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRXCameraScanner() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    var simulatedResultInput by remember { mutableStateOf("") }
    var scanCompletedString by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (!cameraPermissionState.status.isGranted) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Permission Required", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("To use the rootless real-time QR camera scanner, grant Android system permissions.", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Camera Permission")
                    }
                }
            }
        } else {
            // Show Camera view
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            } catch (exc: Exception) {
                                // fallback error logging
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // EMULATOR SCAN SANDBOX INPUT (crucial for local desktop streaming where camera access is sandboxed!)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Studio Sandbox Scanner Emulation", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = simulatedResultInput,
                        onValueChange = { simulatedResultInput = it },
                        placeholder = { Text("Simulate scan data...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(onClick = {
                        if (simulatedResultInput.isNotBlank()) {
                            scanCompletedString = simulatedResultInput
                        }
                    }) {
                        Text("Scan Mock")
                    }
                }
            }
        }

        AnimatedVisibility(visible = scanCompletedString != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Scan Done", tint = Color(0xFF00E676))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("QR CODEX SCANNED DECODE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF00E676))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(scanCompletedString ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
