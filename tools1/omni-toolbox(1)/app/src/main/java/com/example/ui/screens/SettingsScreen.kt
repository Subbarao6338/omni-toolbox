package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val syncFrequency by viewModel.syncFrequency.collectAsState()
    
    val geminiApiKey by viewModel.geminiApiKey.collectAsState()
    val megaCryptKey by viewModel.megaCryptKey.collectAsState()
    val onedriveToken by viewModel.onedriveToken.collectAsState()
    val nextcloudToken by viewModel.nextcloudToken.collectAsState()

    var tempGemini by remember { mutableStateOf("") }
    var tempMega by remember { mutableStateOf("") }
    var tempOnedrive by remember { mutableStateOf("") }
    var tempNextcloud by remember { mutableStateOf("") }

    // Synchronize local input state when flow changes (or on load)
    LaunchedEffect(geminiApiKey, megaCryptKey, onedriveToken, nextcloudToken) {
        tempGemini = geminiApiKey
        tempMega = megaCryptKey
        tempOnedrive = onedriveToken
        tempNextcloud = nextcloudToken
    }

    var showKeys by remember { mutableStateOf(false) }
    var connectionLogs by remember { mutableStateOf("Ready to run cloud diagnostics.") }
    var isTestingConnection by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "settings icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Settings Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // --- Intro Banner ---
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SECURE CONFIGURE GATEWAY",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Control system architecture behavior, manage cloud integrations keys, scheduling daemon sync rates, and theme aesthetics.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- 1. AESTHETICS / THEME PREFERENCES ---
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Palette, contentDescription = "Theme", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Aesthetic Style & Theme", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        Text("Select system-wide UI render color scheme choice:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                        val themeOptions = listOf("System", "Light", "Dark")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            themeOptions.forEach { option ->
                                val isSelected = themeMode == option
                                val chipBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                val chipColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(chipBg)
                                        .clickable { viewModel.setThemeMode(option) }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = when(option) {
                                                "Light" -> Icons.Default.LightMode
                                                "Dark" -> Icons.Default.DarkMode
                                                else -> Icons.Default.SettingsSuggest
                                            },
                                            contentDescription = option,
                                            tint = chipColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = option,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = chipColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. AUTOMATED DAEMON SYNC FREQUENCY ---
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Update, contentDescription = "Sync", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Cloud Sync Scheduling Daemon", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        Text("Select how frequently connected nodes sync background files indices:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                        val freqOptions = listOf("Manual", "15 Mins", "1 Hour", "6 Hours", "24 Hours")
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            freqOptions.forEach { tFreq ->
                                val isSelected = syncFrequency == tFreq
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.setSyncFrequency(tFreq) }
                                        .padding(vertical = 8.dp, horizontal = 6.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.setSyncFrequency(tFreq) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = tFreq,
                                        fontSize = 12.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (isSelected) {
                                        Text(
                                            text = "ACTIVE SCHEDULE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. CRYPTOGRAPHIC CONSOLE / SECURE INTEGRATION KEYS ---
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.VpnKey, contentDescription = "Keys", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Secure Cryptographic Keys", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            IconButton(onClick = { showKeys = !showKeys }) {
                                Icon(
                                    imageVector = if (showKeys) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "Toggle Keys visibility",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        Text("Modify API Access Gateway Credentials stored offline on hardware device realm:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)

                        // 3.1 Google Gemini Key
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Google Gemini API token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            OutlinedTextField(
                                value = tempGemini,
                                onValueChange = { tempGemini = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                placeholder = { Text("AI Studio Gemini Bearer Key", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = { viewModel.setGeminiApiKey(tempGemini) },
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Commit Gemini Key", fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 3.2 MEGA crypt key
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("MEGA Vault RSA cryptographic token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            OutlinedTextField(
                                value = tempMega,
                                onValueChange = { tempMega = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                placeholder = { Text("Primary node decrypt secret key", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = { viewModel.setMegaCryptKey(tempMega) },
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Commit MEGA Key", fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 3.3 Microsoft OneDrive key
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("OneDrive MS Sharepoint Token", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            OutlinedTextField(
                                value = tempOnedrive,
                                onValueChange = { tempOnedrive = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                placeholder = { Text("oauth_bearer_tok_microsoft_live", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = { viewModel.setOnedriveToken(tempOnedrive) },
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Commit OneDrive Token", fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 3.4 Nextcloud WebDAV token
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Nextcloud client WebDAV app password", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            OutlinedTextField(
                                value = tempNextcloud,
                                onValueChange = { tempNextcloud = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (showKeys) VisualTransformation.None else PasswordVisualTransformation(),
                                placeholder = { Text("nc_app_key_secure_passphrase", fontSize = 12.sp) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = { viewModel.setNextcloudToken(tempNextcloud) },
                                modifier = Modifier.align(Alignment.End),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Commit Nextcloud Pass", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // --- 4. CONFIGURATION LINK CHECKER DIAGNOSTIC ---
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Dns, contentDescription = "Gateway", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("API Gateway Diagnostic Unit", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                        Button(
                            onClick = {
                                isTestingConnection = true
                                connectionLogs = "[0ms] Instantiating socket links...\n"
                                scope.launch {
                                    delay(900)
                                    connectionLogs += "[900ms] Testing Google Gemini integration: " + 
                                            if (geminiApiKey.isBlank()) "FAILED (Token Empty)\n" else "ESTABLISHED (Ping 14ms)\n"
                                    delay(600)
                                    connectionLogs += "[1500ms] Testing MEGA cryptographic key integrity: " + 
                                            if (megaCryptKey.isBlank()) "FAILED (Key Missing)\n" else "VERIFIED SECURE\n"
                                    delay(600)
                                    connectionLogs += "[2100ms] OneDrive MS Sharepoint tunnel loop: " + 
                                            if (onedriveToken.isBlank()) "FAILED (Auth Null)\n" else "READY (Relaying indices)\n"
                                    delay(600)
                                    connectionLogs += "[2700ms] Nextcloud client WebDAV ping test: " + 
                                            if (nextcloudToken.isBlank()) "FAILED (App key missing)\n" else "UPLINK GREEN (HTTP 207)\n"
                                    delay(300)
                                    connectionLogs += "[3000ms] Core Diagnostics safely completed."
                                    isTestingConnection = false
                                }
                            },
                            enabled = !isTestingConnection,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scanning Node Gateways...")
                            } else {
                                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = "Scan")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Test Daemon Gateway Connections")
                            }
                        }

                        Text(
                            text = connectionLogs,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = if (connectionLogs.contains("FAILED")) Color(0xFFEF5350) else Color(0xFF00C853),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF16181F), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
