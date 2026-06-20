package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.OmniViewModel
import java.security.MessageDigest
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilitySuiteScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabLabels = listOf("Crypto Tool", "Unit Converter", "Security Gen")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Utilities icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Multi-Utility Suite",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("utility_suite_back_button")
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
            // Elegant Scrollable/Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = (selectedTab == index),
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = label,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab contents switcher container
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> CryptographyHubSection()
                    1 -> UnitConverterSection()
                    2 -> PasswordGeneratorSection()
                }
            }
        }
    }
}

@Composable
fun CryptographyHubSection() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    
    var cryptoInput by remember { mutableStateOf("") }
    
    // Derived states for MD5 & SHA-256
    val md5Hash = remember(cryptoInput) {
        if (cryptoInput.isEmpty()) "Enter source above" else hashString(cryptoInput, "MD5")
    }
    val sha256Hash = remember(cryptoInput) {
        if (cryptoInput.isEmpty()) "Enter source above" else hashString(cryptoInput, "SHA-256")
    }
    
    // Base64 encoding/decoding strings
    var base64Output by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "SYSTEM CRYPTO HASH ENGINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = cryptoInput,
                        onValueChange = { cryptoInput = it },
                        placeholder = { Text("Enter plain string or payload...", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crypto_input_field"),
                        minLines = 2,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // MD5 block
                    CryptoHashDisplay(
                        label = "MD5 Hash Hexadecimal",
                        hash = md5Hash,
                        onCopy = {
                            if (cryptoInput.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(md5Hash))
                                Toast.makeText(context, "Copied MD5 hash!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // SHA-256 block
                    CryptoHashDisplay(
                        label = "SHA-256 Hash Hexadecimal",
                        hash = sha256Hash,
                        onCopy = {
                            if (cryptoInput.isNotEmpty()) {
                                clipboardManager.setText(AnnotatedString(sha256Hash))
                                Toast.makeText(context, "Copied SHA-256 hash!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "BASE64 CODER / DECODER TUNNEL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    base64Output = android.util.Base64.encodeToString(
                                        cryptoInput.toByteArray(Charsets.UTF_8),
                                        android.util.Base64.NO_WRAP
                                    )
                                } catch (e: Exception) {
                                    base64Output = "Encoding error: ${e.message}"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_encode_base64")
                        ) {
                            Text("Encode B64", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                try {
                                    val bytes = android.util.Base64.decode(cryptoInput, android.util.Base64.DEFAULT)
                                    base64Output = String(bytes, Charsets.UTF_8)
                                } catch (e: Exception) {
                                    base64Output = "Decoding error (Invalid Base64 payload)"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_decode_base64")
                        ) {
                            Text("Decode B64", fontSize = 11.sp)
                        }
                    }

                    if (base64Output.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("RESULT ENGINE OUTPUT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(base64Output))
                                            Toast.makeText(context, "Copied Base64 value!", Toast.LENGTH_SHORT).show()
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = base64Output,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CryptoHashDisplay(
    label: String,
    hash: String,
    onCopy: () -> Unit
) {
    Column {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                .clickable { onCopy() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = hash,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy hash",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun UnitConverterSection() {
    var convertCategory by remember { mutableStateOf("Length") } // Temperature, Length, Data
    var inputVal by remember { mutableStateOf("1") }

    val numericInput = inputVal.toDoubleOrNull() ?: 0.0

    // Mappings of conversions
    val lengthOptions = listOf("Meters (m)", "Feets (ft)", "Miles (mi)", "Kilometers (km)", "Yards (yd)")
    var fromLength by remember { mutableStateOf("Meters (m)") }
    var toLength by remember { mutableStateOf("Feets (ft)") }

    val dataOptions = listOf("Bytes (B)", "Kilobytes (KB)", "Megabytes (MB)", "Gigabytes (GB)", "Terabytes (TB)")
    var fromData by remember { mutableStateOf("Bytes (B)") }
    var toData by remember { mutableStateOf("Megabytes (MB)") }

    val tempOptions = listOf("Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)")
    var fromTemp by remember { mutableStateOf("Celsius (°C)") }
    var toTemp by remember { mutableStateOf("Fahrenheit (°F)") }

    val convertedValue = remember(convertCategory, numericInput, fromLength, toLength, fromData, toData, fromTemp, toTemp) {
        when (convertCategory) {
            "Length" -> convertLength(numericInput, fromLength, toLength)
            "Data" -> convertDataStorage(numericInput, fromData, toData)
            "Temperature" -> convertTemperature(numericInput, fromTemp, toTemp)
            else -> 0.0
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Toggle Category select
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Length", "Data", "Temperature").forEach { cat ->
                    val isSelected = (convertCategory == cat)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            .clickable { convertCategory = cat }
                            .padding(horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Configuration Grid
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TextField(
                        value = inputVal,
                        onValueChange = { inputVal = it },
                        label = { Text("Input Amount") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("converter_amount_input"),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Source Unit Dropdown placeholder/selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Convert From:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))
                            UnitSelectorDropdown(
                                selected = when (convertCategory) {
                                    "Length" -> fromLength
                                    "Data" -> fromData
                                    "Temperature" -> fromTemp
                                    else -> ""
                                },
                                options = when (convertCategory) {
                                    "Length" -> lengthOptions
                                    "Data" -> dataOptions
                                    "Temperature" -> tempOptions
                                    else -> emptyList()
                                },
                                onSelect = {
                                    when (convertCategory) {
                                        "Length" -> fromLength = it
                                        "Data" -> fromData = it
                                        "Temperature" -> fromTemp = it
                                    }
                                }
                            )
                        }

                        // Target Unit Dropdown placeholder/selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Convert To:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))
                            UnitSelectorDropdown(
                                selected = when (convertCategory) {
                                    "Length" -> toLength
                                    "Data" -> toData
                                    "Temperature" -> toTemp
                                    else -> ""
                                },
                                options = when (convertCategory) {
                                    "Length" -> lengthOptions
                                    "Data" -> dataOptions
                                    "Temperature" -> tempOptions
                                    else -> emptyList()
                                },
                                onSelect = {
                                    when (convertCategory) {
                                        "Length" -> toLength = it
                                        "Data" -> toData = it
                                        "Temperature" -> toTemp = it
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Result display banner card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CONVERTED COMPUTED VALUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = String.format("%.6f", convertedValue).trimEnd('0').trimEnd('.'),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when (convertCategory) {
                            "Length" -> toLength
                            "Data" -> toData
                            "Temperature" -> toTemp
                            else -> ""
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun UnitSelectorDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(selected, fontSize = 11.sp, maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, fontSize = 12.sp) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PasswordGeneratorSection() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var passwordLength by remember { mutableStateOf(16f) }
    var includeUpper by remember { mutableStateOf(true) }
    var includeLower by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }

    var generatedPassword by remember { mutableStateOf("") }
    
    // Passphrase mode settings
    var usePassphraseMode by remember { mutableStateOf(false) }
    var wordCount by remember { mutableStateOf(4f) }

    val presetEnglishWords = remember {
        listOf(
            "correct", "horse", "battery", "staple", "apple", "banana", "slayer", "quantum",
            "cosmic", "slate", "gradient", "fender", "router", "daemon", "daemonize", "compiled",
            "benchmark", "kernel", "sandbox", "crypto", "security", "vault", "automation", "galaxy",
            "stellar", "pixel", "vector", "binary", "terminal", "vibration", "sensor", "infinite"
        )
    }

    LaunchedEffect(passwordLength, includeUpper, includeLower, includeNumbers, includeSymbols, usePassphraseMode, wordCount) {
        if (usePassphraseMode) {
            val count = wordCount.toInt()
            val list = mutableListOf<String>()
            for (i in 0 until count) {
                list.add(presetEnglishWords.random())
            }
            generatedPassword = list.joinToString("-")
        } else {
            val charPool = StringBuilder()
            if (includeUpper) charPool.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
            if (includeLower) charPool.append("abcdefghijklmnopqrstuvwxyz")
            if (includeNumbers) charPool.append("0123456789")
            if (includeSymbols) charPool.append("!@#$%^&*()_+=-[]{}|;:,.<>?")

            if (charPool.isEmpty()) {
                generatedPassword = "Select character criteria!"
            } else {
                val len = passwordLength.toInt()
                val builder = StringBuilder()
                val random = Random.Default
                for (i in 0 until len) {
                    val index = random.nextInt(charPool.length)
                    builder.append(charPool[index])
                }
                generatedPassword = builder.toString()
            }
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Options tabs inside generate settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Secure Password" to false, "Xkcd Passphrase" to true).forEach { (label, value) ->
                    val isSelected = (usePassphraseMode == value)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            .clickable { usePassphraseMode = value },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (usePassphraseMode) {
                        Text(
                            text = "A Passphrase uses combinations of memorable English words separated by hyphens. It establishes strong cryptographic entropy while remaining easier to memorize.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Phrase Words Count", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${wordCount.toInt()} words", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = wordCount,
                                onValueChange = { wordCount = it },
                                valueRange = 3f..8f,
                                modifier = Modifier.testTag("phrase_words_slider")
                            )
                        }
                    } else {
                        // Slider Length
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Password Buffer Length", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("${passwordLength.toInt()} chars", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = passwordLength,
                                onValueChange = { passwordLength = it },
                                valueRange = 8f..64f,
                                modifier = Modifier.testTag("password_len_slider")
                            )
                        }

                        // Toggle criterias
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CriteriaCheckbox(label = "Include UPPERCASE Letters (A-Z)", checked = includeUpper) { includeUpper = it }
                            CriteriaCheckbox(label = "Include lowercase Letters (a-z)", checked = includeLower) { includeLower = it }
                            CriteriaCheckbox(label = "Include Numeric Decimals (0-9)", checked = includeNumbers) { includeNumbers = it }
                            CriteriaCheckbox(label = "Include Special Symbols (!@#$%)", checked = includeSymbols) { includeSymbols = it }
                        }
                    }
                }
            }
        }

        // Generated Output Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GENERATED SECURITY STRING",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = generatedPassword,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            if (generatedPassword.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(generatedPassword))
                                Toast.makeText(context, "Copied security string!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .size(40.dp)
                            .testTag("copy_password_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CriteriaCheckbox(
    label: String,
    checked: Boolean,
    onCheck: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheck(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheck)
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

// Internal Helper Methods for Conversions and cryptography hashes
private fun hashString(input: String, algorithm: String): String {
    return try {
        val digest = MessageDigest.getInstance(algorithm)
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        "Hash calculation error"
    }
}

private fun convertLength(amount: Double, from: String, to: String): Double {
    // Standardize to Meters first
    val amtInMeters = when (from) {
        "Meters (m)" -> amount
        "Feets (ft)" -> amount * 0.3048
        "Miles (mi)" -> amount * 1609.34
        "Kilometers (km)" -> amount * 1000.0
        "Yards (yd)" -> amount * 0.9144
        else -> amount
    }

    // Convert from Meters to target unit
    return when (to) {
        "Meters (m)" -> amtInMeters
        "Feets (ft)" -> amtInMeters / 0.3048
        "Miles (mi)" -> amtInMeters / 1609.34
        "Kilometers (km)" -> amtInMeters / 1000.0
        "Yards (yd)" -> amtInMeters / 0.9144
        else -> amtInMeters
    }
}

private fun convertDataStorage(amount: Double, from: String, to: String): Double {
    // Standardize to Bytes first
    val amtInBytes = when (from) {
        "Bytes (B)" -> amount
        "Kilobytes (KB)" -> amount * 1000.0
        "Megabytes (MB)" -> amount * 1000000.0
        "Gigabytes (GB)" -> amount * 1000000000.0
        "Terabytes (TB)" -> amount * 1000000000000.0
        else -> amount
    }

    // Convert from Bytes to target unit
    return when (to) {
        "Bytes (B)" -> amtInBytes
        "Kilobytes (KB)" -> amtInBytes / 1000.0
        "Megabytes (MB)" -> amtInBytes / 1000000.0
        "Gigabytes (GB)" -> amtInBytes / 1000000000.0
        "Terabytes (TB)" -> amtInBytes / 1000000000000.0
        else -> amtInBytes
    }
}

private fun convertTemperature(amount: Double, from: String, to: String): Double {
    // Standardize to Celsius first
    val amtInCelsius = when (from) {
        "Celsius (°C)" -> amount
        "Fahrenheit (°F)" -> (amount - 32) * 5 / 9
        "Kelvin (K)" -> amount - 273.15
        else -> amount
    }

    // Convert from Celsius to target unit
    return when (to) {
        "Celsius (°C)" -> amtInCelsius
        "Fahrenheit (°F)" -> amtInCelsius * 9 / 5 + 32
        "Kelvin (K)" -> amtInCelsius + 273.15
        else -> amtInCelsius
    }
}
