package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenSettingsScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    // Collect settings states from OmniViewModel
    val animationScale by viewModel.animationScale.collectAsState()
    val backgroundProcessLimit by viewModel.backgroundProcessLimit.collectAsState()
    val standbyBucket by viewModel.standbyBucket.collectAsState()
    val privateDnsMode by viewModel.privateDnsMode.collectAsState()
    val privateDnsHost by viewModel.privateDnsHost.collectAsState()
    val force4xMsaa by viewModel.force4xMsaa.collectAsState()
    val disableHwOverlays by viewModel.disableHwOverlays.collectAsState()
    val showGpuOverdraw by viewModel.showGpuOverdraw.collectAsState()
    val screenRefreshRate by viewModel.screenRefreshRate.collectAsState()
    val logBufferSize by viewModel.logBufferSize.collectAsState()

    var showDnsDialog by remember { mutableStateOf(false) }
    var dnsInputHost by remember { mutableStateOf(privateDnsHost) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SettingsSuggest,
                            contentDescription = "Hidden icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Hidden Settings Console",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("hidden_settings_back_button")
                    ) {
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
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Warning Notice Card
                item {
                    SettingsWarningNotice()
                }

                // Section 1: Window Animation & Transition Speeds
                item {
                    Text(
                        text = "ANIMATION & TRANSITION SPEEDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    AnimationScaleSelectorWidget(
                        selectedScale = animationScale,
                        onScaleChanged = { viewModel.updateAnimationScale(it) }
                    )
                }

                // Section 2: Display & Rendering Core
                item {
                    Text(
                        text = "DISPLAY & ACTIVE RENDER GRAPHICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    DisplayRenderingCard(
                        screenRefreshRate = screenRefreshRate,
                        force4xMsaa = force4xMsaa,
                        disableOverlays = disableHwOverlays,
                        gpuOverdraw = showGpuOverdraw,
                        onHzChanged = { viewModel.updateScreenRefreshRate(it) },
                        onMsaaChanged = { viewModel.toggleForce4xMsaa(it) },
                        onOverlaysChanged = { viewModel.toggleDisableHwOverlays(it) },
                        onOverdrawChanged = { viewModel.toggleShowGpuOverdraw(it) }
                    )
                }

                // Section 3: Logcat & Network private config
                item {
                    Text(
                        text = "SECURE NETWORK & DIAGNOSTICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    NetworkDiagnosticsWidget(
                        dnsMode = privateDnsMode,
                        dnsHost = privateDnsHost,
                        logSize = logBufferSize,
                        onDnsClicked = {
                            dnsInputHost = privateDnsHost
                            showDnsDialog = true
                        },
                        onLogSizeChanged = { viewModel.updateLogBufferSize(it) }
                    )
                }

                // Section 4: Power Management Standby Buckets
                item {
                    Text(
                        text = "POWER MANAGEMENT & PROCESSES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    PowerManagementCard(
                        standbyBucket = standbyBucket,
                        processLimit = backgroundProcessLimit,
                        onStandbyBucketChanged = { viewModel.updateStandbyBucket(it) },
                        onProcessLimitChanged = { viewModel.updateBackgroundProcessLimit(it) }
                    )
                }
            }
        }
    }

    // DNS configuration overlay Dialog
    if (showDnsDialog) {
        AlertDialog(
            onDismissRequest = { showDnsDialog = false },
            title = { Text("Encrypted Private DNS", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select DNS tunneling behavior. Custom hostname routes traffic securely.", fontSize = 12.sp)

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

                    listOf("Off", "Automatic", "Custom Private Hostname").forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (mode != "Custom Private Hostname") {
                                        viewModel.updatePrivateDns(mode, privateDnsHost)
                                    } else {
                                        viewModel.updatePrivateDns(mode, dnsInputHost)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (privateDnsMode == mode),
                                onClick = {
                                    if (mode != "Custom Private Hostname") {
                                        viewModel.updatePrivateDns(mode, privateDnsHost)
                                    } else {
                                        viewModel.updatePrivateDns(mode, dnsInputHost)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(mode, fontSize = 13.sp)
                        }
                    }

                    if (privateDnsMode == "Custom Private Hostname") {
                        TextField(
                            value = dnsInputHost,
                            onValueChange = {
                                dnsInputHost = it
                                viewModel.updatePrivateDns("Custom Private Hostname", it)
                            },
                            label = { Text("Private DNS provider address") },
                            placeholder = { Text("dns.adguard.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("dns_address_input")
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDnsDialog = false },
                    modifier = Modifier.testTag("dns_confirm_button")
                ) {
                    Text("Save Config")
                }
            }
        )
    }
}

@Composable
fun SettingsWarningNotice() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Danger sign",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "System Namespace Override Node",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Altering active standby policies, GPU Overlays, or display intervals may impact battery discharge rate. These hooks represent core configuration intents.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun AnimationScaleSelectorWidget(
    selectedScale: Float,
    onScaleChanged: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title = "Window & Transition Animation Scale",
                desc = "Sets duration variables for system UI transitions to speed up interactions."
            )

            Spacer(modifier = Modifier.height(16.dp))

            val options = listOf(
                "Disabled" to 0.0f,
                "0.5x Fast" to 0.5f,
                "1.0x Normal" to 1.0f,
                "1.5x Smooth" to 1.5f,
                "2.0x Slower" to 2.0f
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { (label, value) ->
                    val isSelected = (selectedScale == value)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { onScaleChanged(value) }
                            .border(
                                1.dp,
                                if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label.split(" ").first(), // just show short acronym
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayRenderingCard(
    screenRefreshRate: Int,
    force4xMsaa: Boolean,
    disableOverlays: Boolean,
    gpuOverdraw: Boolean,
    onHzChanged: (Int) -> Unit,
    onMsaaChanged: (Boolean) -> Unit,
    onOverlaysChanged: (Boolean) -> Unit,
    onOverdrawChanged: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Refresh Rate
            Column {
                Text(
                    title = "Display Peak Refresh Interval",
                    desc = "Forces the hardware panel to refresh at a specific rate for maximum fluidity."
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val hzOptions = listOf(60, 90, 120)
                    hzOptions.forEach { hz ->
                        val isSelected = (screenRefreshRate == hz)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { onHzChanged(hz) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$hz Hz",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

            // Switch entries
            SwitchRow(
                title = "Force 4x MSAA rendering",
                desc = "Enables multi-sample anti-aliasing in OpenGLES graphics apps for crisper vector lines.",
                checked = force4xMsaa,
                onCheckedChange = onMsaaChanged
            )

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

            SwitchRow(
                title = "Disable HW Composition Overlays",
                desc = "Forces screen compost overlays directly onto the GPU hardware pipeline.",
                checked = disableOverlays,
                onCheckedChange = onOverlaysChanged
            )

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

            SwitchRow(
                title = "Profile GPU Overdraw overlays",
                desc = "Colorizes pixels according to the amount of redundant graphics draws.",
                checked = gpuOverdraw,
                onCheckedChange = onOverdrawChanged
            )
        }
    }
}

@Composable
fun NetworkDiagnosticsWidget(
    dnsMode: String,
    dnsHost: String,
    logSize: String,
    onDnsClicked: () -> Unit,
    onLogSizeChanged: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // DNS clickable options card row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDnsClicked() }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title = "Encrypted Private DNS Config",
                        desc = "Routes cellular and local WiFi traffic through TLS private tunnels."
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Current: $dnsMode " + if (dnsMode == "Custom Private Hostname") "($dnsHost)" else "",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

            // Log buffer selector
            Column {
                Text(
                    title = "Logcat Logger Buffer Capacity",
                    desc = "Allocates circular heap buffers in bytes for system auditing logs."
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val logOptions = listOf("64K", "256K", "1M", "16M")
                    logOptions.forEach { size ->
                        val isSelected = (logSize == size)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { onLogSizeChanged(size) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = size,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PowerManagementCard(
    standbyBucket: String,
    processLimit: String,
    onStandbyBucketChanged: (String) -> Unit,
    onProcessLimitChanged: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text(
                    title = "App Standby Optimization State",
                    desc = "Locks memory lifecycle flags onto application categories."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val buckets = listOf("ACTIVE", "WORKING_SET", "FREQUENT", "RARE")
                    buckets.forEach { bucket ->
                        val isSelected = (standbyBucket == bucket)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { onStandbyBucketChanged(bucket) }
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bucket.replace("_", " "),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f))

            Column {
                Text(
                    title = "Background Process Scheduler Limits",
                    desc = "Controls how many live caching processes Android suspends in background."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val processLimits = listOf("Standard limit", "No background", "At most 2", "At most 4")
                    processLimits.forEach { limit ->
                        val isSelected = (processLimit == limit)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .clickable { onProcessLimitChanged(limit) }
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = limit.split(" ").take(2).joinToString(" "),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Text(title: String, desc: String) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = desc,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            lineHeight = 13.sp
        )
    }
}

@Composable
fun SwitchRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title = title, desc = desc)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag("switch_" + title.replace(" ", "_").lowercase())
        )
    }
}
