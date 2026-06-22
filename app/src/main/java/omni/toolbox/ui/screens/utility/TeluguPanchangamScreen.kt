package omni.toolbox.ui.screens.utility

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.viewmodel.OmniViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TeluguPanchangamScreen(
    navController: NavHostController,
    viewModel: OmniViewModel
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf(Calendar.getInstance().time) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    val formattedGregorianDate = remember(selectedDate) {
        val df = SimpleDateFormat("EEEE, d MMMM yyyy, hh:mm a", Locale.getDefault())
        df.format(selectedDate)
    }

    val panchangResult = remember(selectedDate) {
        val localDateTime = java.time.LocalDateTime.ofInstant(selectedDate.toInstant(), java.time.ZoneId.systemDefault())
        omni.toolbox.utils.PanchangamLogic.getPanchangam(localDateTime)
    }

    val goldHeaderGrad = Brush.horizontalGradient(
        listOf(Color(0xFFE65100), Color(0xFFFFB300))
    )

    ToolScreen(
        title = "తెలుగు పంచాంగం",
        onBack = { navController.popBackStack() },
        actions = {
            IconButton(onClick = { showExplanationDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Learn More",
                    tint = Color(0xFFFF8F00)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Header Date Selector Panel
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .background(goldHeaderGrad)
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "ALMANAC ENGINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = formattedGregorianDate,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val c = Calendar.getInstance().apply { time = selectedDate }
                                            DatePickerDialog(
                                                context,
                                                { _, y, m, d ->
                                                    c.set(Calendar.YEAR, y)
                                                    c.set(Calendar.MONTH, m)
                                                    c.set(Calendar.DAY_OF_MONTH, d)
                                                    selectedDate = c.time
                                                },
                                                c.get(Calendar.YEAR),
                                                c.get(Calendar.MONTH),
                                                c.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color(0xFFE65100)
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val c = Calendar.getInstance().apply { time = selectedDate }
                                            TimePickerDialog(
                                                context,
                                                { _, h, m ->
                                                    c.set(Calendar.HOUR_OF_DAY, h)
                                                    c.set(Calendar.MINUTE, m)
                                                    selectedDate = c.time
                                                },
                                                c.get(Calendar.HOUR_OF_DAY),
                                                c.get(Calendar.MINUTE),
                                                false
                                            ).show()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color(0xFFE65100)
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("TIME", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    TeluguYearBanner(panchang = panchangResult)
                }

                item {
                    Text(
                        text = "FIVE VEDIC ELEMENTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFE65100),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    PanchangGrid(panchang = panchangResult)
                }

                item {
                    Text(
                        text = "SOLAR TIMINGS & TRANSITS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFE65100),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    DurationsPanel(panchang = panchangResult)
                }

                item {
                    Text(
                        text = "DAILY ROSTER TIMINGS",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFF4D00),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    AlertTimesGrid(panchang = panchangResult)
                }
            }
        }
    }

    if (showExplanationDialog) {
        AlertDialog(
            onDismissRequest = { showExplanationDialog = false },
            title = { Text("What is Telugu Panchangam?", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFE65100)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "The word 'Panchangam' comes from Sanskrit (Pancha + Anga), representing the five vital elements of Vedic astrology:",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "1. Tithi: Phase of the Moon\n2. Varamu: Day of the week\n3. Nakshatram: Star constellation\n4. Yogamu: Angular correlation\n5. Karanam: Half of a Tithi",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showExplanationDialog = false }) {
                    Text("Understood", color = Color(0xFFE65100))
                }
            }
        )
    }
}

@Composable
fun TeluguYearBanner(panchang: omni.toolbox.utils.PanchangamDetails) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "KALIYUGA ERA : 5127",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = panchang.samvatsaram,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4E342E)
                )
            }
            Text(
                text = "${panchang.ayana} \u2022 Rutuvu: ${panchang.rutu} \u2022 ${panchang.maasam}",
                fontSize = 11.sp,
                color = Color(0xFF5D4037)
            )
        }
    }
}

@Composable
fun PanchangGrid(panchang: omni.toolbox.utils.PanchangamDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PanchElementCard(
                title = "తిథి (TITHI)",
                value = panchang.tithi,
                desc = "Phase: " + panchang.paksha,
                icon = Icons.Default.NightlightRound,
                tint = Color(0xFFAB47BC),
                modifier = Modifier.weight(1f)
            )
            PanchElementCard(
                title = "నక్షత్రం (NAKSHATRA)",
                value = panchang.nakshatram,
                desc = "Constellation orbit",
                icon = Icons.Default.Star,
                tint = Color(0xFF26A69A),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PanchElementCard(
                title = "యోగం (YOGA)",
                value = panchang.yoga,
                desc = "Auspicious index",
                icon = Icons.Default.TrackChanges,
                tint = Color(0xFF42A5F5),
                modifier = Modifier.weight(1f)
            )
            PanchElementCard(
                title = "కరణం (KARANA)",
                value = panchang.karana,
                desc = "Lunar half-day",
                icon = Icons.Default.Grain,
                tint = Color(0xFFFF7043),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PanchElementCard(
                title = "రాశి (RASHI)",
                value = panchang.raashi,
                desc = "Moon Sign",
                icon = Icons.Default.Public,
                tint = Color(0xFFE65100),
                modifier = Modifier.weight(1f)
            )
            PanchElementCard(
                title = "అదృష్ట (LUCKY)",
                value = "No: ${panchang.luckyNumber}",
                desc = "Color: ${panchang.luckyColor}",
                icon = Icons.Default.AutoAwesome,
                tint = Color(0xFFFFD600),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PanchElementCard(
    title: String,
    value: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(tint.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun DurationsPanel(panchang: omni.toolbox.utils.PanchangamDetails) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SolarTimeItem(title = "Sunrise", value = panchang.sunrise, icon = Icons.Default.WbSunny, modifier = Modifier.weight(1f))
            SolarTimeItem(title = "Sunset", value = panchang.sunset, icon = Icons.Default.WbTwilight, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SolarTimeItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = title, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun AlertTimesGrid(panchang: omni.toolbox.utils.PanchangamDetails) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AlertTimingRow(name = "రాహుకాలం (Rahu Kalam)", time = panchang.rahuKalam, color = Color(0xFFD32F2F))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
            AlertTimingRow(name = "యమగండం (Yamagandam)", time = panchang.yamagandam, color = Color(0xFFF57C00))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
            AlertTimingRow(name = "గుళికా కాలం (Gulika Kalam)", time = panchang.gulikaKalam, color = Color(0xFF2E7D32))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
            AlertTimingRow(name = "దుర్ముహూర్తం (Durmuhurtham)", time = panchang.durmuhurtham, color = Color(0xFF7B1FA2))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
            AlertTimingRow(name = "బ్రహ్మ ముహూర్తం (Brahma Muhurtham)", time = panchang.brahmaMuhurtham, color = Color(0xFFE65100))
        }
    }
}

@Composable
fun AlertTimingRow(
    name: String,
    time: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = time,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = color
            )
        }
    }
}

