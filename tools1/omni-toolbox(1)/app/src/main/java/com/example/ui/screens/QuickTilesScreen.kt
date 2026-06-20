package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CustomQuickTile
import com.example.ui.OmniViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickTilesScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val customQuickTiles by viewModel.customQuickTiles.collectAsState()

    // Form inputs
    var tileName by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("Speed") }
    var selectedState by remember { mutableStateOf("Inactive") }
    var selectedActionType by remember { mutableStateOf("Toggle Developer Setting") }
    var actionDetail by remember { mutableStateOf("") }

    var expandedActionDropdown by remember { mutableStateOf(false) }
    var expandedStateDropdown by remember { mutableStateOf(false) }

    var showCodeTemplate by remember { mutableStateOf(false) }

    val iconChoices = listOf(
        "Speed" to Icons.Default.Speed,
        "Wifi" to Icons.Default.Wifi,
        "Battery" to Icons.Default.BatteryChargingFull,
        "Flashlight" to Icons.Default.Highlight,
        "Vibration" to Icons.Default.Vibration,
        "Terminal" to Icons.Default.Terminal,
        "Security" to Icons.Default.Security,
        "Cloud" to Icons.Default.CloudSync
    )

    val actionChoices = listOf(
        "Toggle Developer Setting",
        "Open App",
        "Run Automation",
        "Show Dialog",
        "Webhook"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DashboardCustomize,
                            contentDescription = "Quick tiles creator",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Quick Tiles Creator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("quicktiles_back_button")
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
                // Section 1: Android System Shade Simulator Panel
                item {
                    Text(
                        text = "ANDROID SYSTEM CONTROLS SHADE (SIMULATION)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    SystemShadeSimulator(
                        customTiles = customQuickTiles,
                        onTileClicked = { viewModel.toggleQuickTileStateInShade(it) }
                    )
                }

                // Section 2: Creator Studio
                item {
                    Text(
                        text = "CREATE CUSTOM TILE ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Tile Name Title
                            TextField(
                                value = tileName,
                                onValueChange = { tileName = it },
                                label = { Text("Tile Name Label") },
                                placeholder = { Text("e.g. Refresh Toggle") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("tile_name_input"),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )

                            // Icon Picker Row
                            Column {
                                Text(
                                    text = "Select Tile Display Vector Icon",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    iconChoices.forEach { (name, icon) ->
                                        val isSelected = (selectedIconName == name)
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                )
                                                .clickable { selectedIconName = name },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = name,
                                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // State & Action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Default status state dropdown button menu
                                Box(modifier = Modifier.weight(1f)) {
                                    ExposedDropdownMenuBox(
                                        expanded = expandedStateDropdown,
                                        onExpandedChange = { expandedStateDropdown = !expandedStateDropdown }
                                    ) {
                                        TextField(
                                            value = selectedState,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Default State", fontSize = 10.sp) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStateDropdown) },
                                            modifier = Modifier.menuAnchor(),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expandedStateDropdown,
                                            onDismissRequest = { expandedStateDropdown = false }
                                        ) {
                                            listOf("Active", "Inactive", "Unavailable").forEach { mode ->
                                                DropdownMenuItem(
                                                    text = { Text(mode) },
                                                    onClick = {
                                                        selectedState = mode
                                                        expandedStateDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Interactive action types dropdown selection Menu
                                Box(modifier = Modifier.weight(1.5f)) {
                                    ExposedDropdownMenuBox(
                                        expanded = expandedActionDropdown,
                                        onExpandedChange = { expandedActionDropdown = !expandedActionDropdown }
                                    ) {
                                        TextField(
                                            value = selectedActionType,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Tile Trigger Action", fontSize = 10.sp) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActionDropdown) },
                                            modifier = Modifier.menuAnchor(),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expandedActionDropdown,
                                            onDismissRequest = { expandedActionDropdown = false }
                                        ) {
                                            actionChoices.forEach { act ->
                                                DropdownMenuItem(
                                                    text = { Text(act) },
                                                    onClick = {
                                                        selectedActionType = act
                                                        expandedActionDropdown = false
                                                        actionDetail = when (act) {
                                                            "Toggle Developer Setting" -> "Set Animation Scale to 0.5x"
                                                            "Open App" -> "Developer Specs Screen"
                                                            "Run Automation" -> "Auto Cleanup Duplicate Images"
                                                            "Show Dialog" -> "Alert: Workspace Optimized!"
                                                            "Webhook" -> "https://api.webhook.org/trigger"
                                                            else -> ""
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Dynamic Helper action instruction and Detail configuration input
                            TextField(
                                value = actionDetail,
                                onValueChange = { actionDetail = it },
                                label = { Text("Action Parameter / Target Payload") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("action_payload_input"),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )

                            // Submit Button
                            Button(
                                onClick = {
                                    if (tileName.isNotBlank()) {
                                        viewModel.createQuickTile(
                                            name = tileName,
                                            iconName = selectedIconName,
                                            state = selectedState,
                                            actionType = selectedActionType,
                                            actionDetail = actionDetail
                                        )
                                        tileName = ""
                                    }
                                },
                                enabled = tileName.isNotBlank(),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("add_tile_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Tile")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("REGISTER TILE PIPELINE")
                            }
                        }
                    }
                }

                // Section 3: Manage Registered Custom Tiles
                item {
                    Text(
                        text = "MANAGE YOUR REGISTERED TILES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 1.sp
                    )
                }

                if (customQuickTiles.none { it.id.startsWith("t_") }) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier.padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No custom-built quicktiles yet.\nCreate one using the template configurator above.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                } else {
                    items(customQuickTiles.filter { it.id.startsWith("t_") }) { tile ->
                        CustomTileManagerCard(
                            tile = tile,
                            icon = iconChoices.find { it.first == tile.iconName }?.second ?: Icons.Default.Circle,
                            onDelete = { viewModel.deleteQuickTile(tile.id) }
                        )
                    }
                }

                // Code template instructions foldout card widget
                item {
                    CodeDeploymentSection(
                        showCodeTemplate = showCodeTemplate,
                        onToggleFold = { showCodeTemplate = !showCodeTemplate }
                    )
                }
            }
        }
    }
}

@Composable
fun SystemShadeSimulator(
    customTiles: List<CustomQuickTile>,
    onTileClicked: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF13151A) // Dark Status Bar shade standard background
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Simulated Status Bar Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "10:42 AM",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                    Icon(imageVector = Icons.Default.NetworkCell, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                    Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of toggles including preset Standard tiles AND custom tiles
            val standardTiles = listOf(
                "Wi-Fi" to Icons.Default.Wifi,
                "Bluetooth" to Icons.Default.Bluetooth,
                "Do Not Disturb" to Icons.Default.DoNotDisturbOn,
                "Flashlight" to Icons.Default.Highlight,
                "Airplane Mode" to Icons.Default.AirplanemodeActive,
                "Battery Saver" to Icons.Default.BatterySaver
            )

            val gridItems = remember(customTiles) {
                // Preset default tiles
                val presets = standardTiles.map { (name, icon) ->
                    SimulatedTile(
                        id = "preset_$name",
                        name = name,
                        icon = icon,
                        state = if (name == "Wi-Fi" || name == "Bluetooth" || name == "Flashlight") "Active" else "Inactive",
                        isCustom = false
                    )
                }
                // Custom created tiles mapping
                val customs = customTiles.map { tile ->
                    val vectorIcon = when (tile.iconName) {
                        "Speed" -> Icons.Default.Speed
                        "Wifi" -> Icons.Default.Wifi
                        "Battery" -> Icons.Default.BatteryChargingFull
                        "Flashlight" -> Icons.Default.Highlight
                        "Vibration" -> Icons.Default.Vibration
                        "Terminal" -> Icons.Default.Terminal
                        "Security" -> Icons.Default.Security
                        "Cloud" -> Icons.Default.CloudSync
                        else -> Icons.Default.PowerSettingsNew
                    }
                    SimulatedTile(
                        id = tile.id,
                        name = tile.name,
                        icon = vectorIcon,
                        state = tile.state,
                        isCustom = true
                    )
                }
                presets + customs
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(210.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gridItems) { tile ->
                    ShadeItemToggle(
                        tile = tile,
                        onClick = {
                            if (tile.isCustom) {
                                onTileClicked(tile.id)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = Color.White.copy(alpha = 0.08f))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.4f)))
                }
                Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
            }
        }
    }
}

data class SimulatedTile(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val state: String, // "Active", "Inactive", "Unavailable"
    val isCustom: Boolean
)

@Composable
fun ShadeItemToggle(
    tile: SimulatedTile,
    onClick: () -> Unit
) {
    val isActive = (tile.state == "Active")
    val isUnavailable = (tile.state == "Unavailable")

    val tileBg = when {
        isUnavailable -> Color.White.copy(alpha = 0.05f)
        isActive -> Color(0xFF00FF88) // Stunning Glowing Android Green action state
        else -> Color.White.copy(alpha = 0.08f)
    }

    val contentColor = when {
        isActive -> Color.Black
        isUnavailable -> Color.White.copy(alpha = 0.2f)
        else -> Color.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(tileBg)
            .border(
                1.dp,
                if (tile.isCustom) Color(0xFF00FF88).copy(alpha = 0.35f) else Color.Transparent,
                RoundedCornerShape(24.dp)
            )
            .clickable(enabled = !isUnavailable) { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isActive) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.04f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(15.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tile.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = contentColor,
                maxLines = 1
            )
            Text(
                text = if (isUnavailable) "Unavailable" else if (isActive) "Active" else "Inactive",
                fontSize = 8.sp,
                color = contentColor.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun CustomTileManagerCard(
    tile: CustomQuickTile,
    icon: ImageVector,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tile.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${tile.actionType} → ${tile.actionDetail}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_tile_" + tile.id)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CodeDeploymentSection(
    showCodeTemplate: Boolean,
    onToggleFold: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { onToggleFold() }
                    .padding(14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Android System Integration Service Code",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = if (showCodeTemplate) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            if (showCodeTemplate) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFF0F1115))
                        .padding(14.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "1. DECLARE IN THE ANDROIDMANIFEST.XML",
                        fontSize = 9.sp,
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = """
<service
    android:name=".MyTileService"
    android:label="Power Tile"
    android:icon="@drawable/ic_tile"
    android:permission="android.permission.BIND_QUICK_SETTINGS_TILE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.service.quicksettings.action.QS_TILE" />
    </intent-filter>
</service>
                        """.trimIndent(),
                        color = Color(0xFFA5D6A7),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "2. CREATE EXPENDED SYSTEM SERVICES KOTLIN CLASS",
                        fontSize = 9.sp,
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = """
import android.service.quicksettings.TileService
import android.service.quicksettings.Tile

class MyTileService : TileService() {
    override fun onClick() {
        val tile = qsTile
        val nextState = if (tile.state == Tile.STATE_ACTIVE) {
            Tile.STATE_INACTIVE
        } else {
            Tile.STATE_ACTIVE
        }
        tile.state = nextState
        tile.updateTile()
        // Trigger app logic
    }
}
                        """.trimIndent(),
                        color = Color(0xFFA5D6A7),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
