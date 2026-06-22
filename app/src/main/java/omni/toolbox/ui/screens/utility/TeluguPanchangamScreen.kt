package omni.toolbox.ui.screens.utility

import android.app.DatePickerDialog
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

    var selectedDate by remember { mutableStateOf(Date()) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    val formattedGregorianDate = remember(selectedDate) {
        val df = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        df.format(selectedDate)
    }

    val panchangResult = remember(selectedDate) {
        calculatePanchangam(selectedDate)
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
                                Button(
                                    onClick = {
                                        val c = Calendar.getInstance().apply { time = selectedDate }
                                        DatePickerDialog(
                                            context,
                                            { _, y, m, d ->
                                                c.set(y, m, d)
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
                                    Text("SELECT DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
fun TeluguYearBanner(panchang: PanchangData) {
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
                    text = "${panchang.teluguYearName} నామ సంవత్సరం",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4E342E)
                )
            }
            Text(
                text = "Dakshinayana \u2022 Rutuvu: Greeshma \u2022 Masamu: Jyeshta",
                fontSize = 11.sp,
                color = Color(0xFF5D4037)
            )
        }
    }
}

@Composable
fun PanchangGrid(panchang: PanchangData) {
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
                title = "వారం (VARAMU)",
                value = panchang.varam,
                desc = "Lord: " + panchang.varamLord,
                icon = Icons.Default.Brightness5,
                tint = Color(0xFFFF7043),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PanchElementCard(
                title = "నక్షత్రం (NAKSHATRA)",
                value = panchang.nakshatra,
                desc = "Constellation orbit",
                icon = Icons.Default.Star,
                tint = Color(0xFF26A69A),
                modifier = Modifier.weight(1f)
            )
            PanchElementCard(
                title = "యోగం & కరణం",
                value = panchang.yoga,
                desc = "Karana: " + panchang.karana,
                icon = Icons.Default.TrackChanges,
                tint = Color(0xFF42A5F5),
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
fun DurationsPanel(panchang: PanchangData) {
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
            SolarTimeItem(title = "Abhijith", value = panchang.abhijitLagna, icon = Icons.Default.Timeline, modifier = Modifier.weight(1f))
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
fun AlertTimesGrid(panchang: PanchangData) {
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
            AlertTimingRow(name = "దుర్ముహూర్తం (Durmuhurtham)", time = panchang.durmuhurtham, color = Color(0xFF7B1FA2))
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

data class PanchangData(
    val selectedDate: Date,
    val teluguYearName: String,
    val tithi: String,
    val paksha: String,
    val varam: String,
    val varamLord: String,
    val nakshatra: String,
    val yoga: String,
    val karana: String,
    val sunrise: String,
    val sunset: String,
    val abhijitLagna: String,
    val rahuKalam: String,
    val yamagandam: String,
    val durmuhurtham: String
)

fun calculatePanchangam(date: Date): PanchangData {
    val cal = Calendar.getInstance().apply { time = date }
    val year = cal.get(Calendar.YEAR)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

    val teluguYears = listOf(
        "Prabhava", "Vibhava", "Shukla", "Pramodoota", "Prajotpatti", "Angirasa", "Shrimukha", "Bhava",
        "Yuva", "Dhatri", "Eashwara", "Bahudhanya", "Pramadi", "Vikrama", "Vrisha", "Chitrabhanu",
        "Swabhannu", "Tharana", "Parthiva", "Vyaya", "Sarvajeethu", "Sarvadhari", "Virodhi", "Vikruthi",
        "Khara", "Nandana", "Vijaya", "Jaya", "Manmadha", "Durmukhi", "Hevilambi", "Vilambi", "Vikari",
        "Sarvari", "Plava", "Shubhakruthi", "Sobhakruthi", "Krodhi", "Vishvavasu", "Parabhava", "Plavanga",
        "Keelaka", "Saumya", "Sadharana", "Virodhikruthi", "Paridhavi", "Pramadicha", "Ananda", "Rakshasa",
        "Nala", "Pingala", "Kalayukthi", "Siddharthi", "Raudra", "Durmathi", "Dundubhi", "Rudhirodgari",
        "Raktakshi", "Krodhana", "Akshaya"
    )
    val baseYear = 2024
    val baseIndex = 37
    val yearOffset = year - baseYear
    val calculatedYearIndex = (baseIndex + yearOffset) % 60
    val yearName = teluguYears[if (calculatedYearIndex < 0) calculatedYearIndex + 60 else calculatedYearIndex]

    val refCal = Calendar.getInstance().apply { set(2024, Calendar.JANUARY, 11, 12, 0, 0) }
    val diffMs = date.time - refCal.timeInMillis
    val actualDays = diffMs.toDouble() / 86400000.0
    val phaseAge = (actualDays % 29.5305888).let { if (it < 0) it + 29.5305888 else it }
    val isShukla = phaseAge < 14.765
    val tithiPercent = if (isShukla) phaseAge / 14.765 else (phaseAge - 14.765) / 14.765
    val tithiValue = (tithiPercent * 15).toInt().coerceIn(1, 15)

    val pakshaLabel = if (isShukla) "శుక్ల పక్షం" else "కృష్ణ పక్షం"
    val tithiNames = listOf(
        "పాడ్యమి", "విదియ", "తదియ", "చవితి", "పంచమి", "షష్టి", "సప్తమి", "అష్టమి",
        "నవమి", "దశమి", "ఏకాదశి", "ద్వాదశి", "త్రయోదశి", "చతుర్దశి", if (isShukla) "పూర్ణిమ" else "అమావాస్య"
    )
    val selectedTithi = tithiNames[tithiValue - 1]

    val varamNames = listOf("", "ఆదివారము", "సోమవారము", "మంగళవారము", "బుధవారము", "గురువారము", "శుక్రవారము", "శనివారము")
    val varamLords = listOf("", "Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn")
    val selectedVaram = varamNames[dayOfWeek]
    val selectedLord = varamLords[dayOfWeek]

    val moonSiderealAge = (actualDays % 27.32166).let { if (it < 0) it + 27.32166 else it }
    val nakshatramIndex = ((moonSiderealAge / 27.32166) * 27).toInt().coerceIn(0, 26)
    val nakshatrasList = listOf(
        "అశ్విని", "భరణి", "కృత్తిక", "రోహిణి", "మృగశిర", "ఆర్ద్ర", "పునర్వసు", "పుష్యమి",
        "ఆశ్లేష", "మఖ", "పుబ్బ", "ఉత్తర", "హస్త", "చిత్ర", "స్వాతి", "విశాఖ", "అనురాధ",
        "జ్యేష్ట", "మూల", "పూర్వాషాఢ", "ఉత్తరాషాఢ", "శ్రావణం", "ధనిష్ట", "శతభిషం",
        "పూర్వాభాద్ర", "ఉత్తరాభాద్ర", "రేవతి"
    )
    val selectedNakshatra = nakshatrasList[nakshatramIndex]

    val yogaIndex = ((actualDays * 1.015) % 27).toInt().let { if (it < 0) it + 27 else it }
    val yogasList = listOf("విష్కంబ", "ప్రీతి", "ఆయుష్మాన్", "సౌభాగ్య", "శోభన", "అతిగండ", "సుకర్మ", "ధృతి", "శూల", "గండ", "వృద్ధి", "ధ్రువ", "వ్యాఘాత", "హర్షణ", "వజ్ర", "సిద్ధి", "వ్యతీపాత", "వరియాన్", "పరిఖ", "శివ", "సిద్ధ", "సాధ్య", "శుభ", "శుక్ల", "బ్రహ్మ", "ఐంద్ర", "వైధృతి")
    val selectedYoga = yogasList[yogaIndex % yogasList.size]

    val karanaNames = listOf("బవ", "బాలవ", "కౌలవ", "తైతుల", "గరజ", "వణిజ", "భద్ర", "శకుని")
    val selectedKarana = karanaNames[(phaseAge.toInt() * 2) % karanaNames.size]

    val rahuMap = mapOf(1 to "04:30 PM - 06:00 PM", 2 to "07:30 AM - 09:00 AM", 3 to "03:00 PM - 04:30 PM", 4 to "12:00 PM - 01:30 PM", 5 to "01:30 PM - 03:00 PM", 6 to "10:30 AM - 12:00 PM", 7 to "09:00 AM - 10:30 AM")
    val yamaMap = mapOf(1 to "12:00 PM - 01:30 PM", 2 to "10:30 AM - 12:00 PM", 3 to "09:00 AM - 10:30 AM", 4 to "07:30 AM - 09:00 AM", 5 to "06:00 AM - 07:30 AM", 6 to "03:00 PM - 04:30 PM", 7 to "01:30 PM - 03:00 PM")
    val durmuMap = mapOf(1 to "04:54 PM - 05:46 PM", 2 to "12:44 PM - 01:36 PM", 3 to "08:21 AM - 09:13 AM", 4 to "11:51 AM - 12:43 PM", 5 to "11:02 AM - 11:54 AM", 6 to "08:42 AM - 09:34 AM", 7 to "07:30 AM - 08:22 AM")

    return PanchangData(
        selectedDate = date, teluguYearName = yearName, tithi = selectedTithi, paksha = pakshaLabel, varam = selectedVaram, varamLord = selectedLord, nakshatra = selectedNakshatra, yoga = selectedYoga, karana = selectedKarana,
        sunrise = "05:46 AM", sunset = "06:38 PM", abhijitLagna = "11:50 AM - 12:40 PM",
        rahuKalam = rahuMap[dayOfWeek] ?: "", yamagandam = yamaMap[dayOfWeek] ?: "", durmuhurtham = durmuMap[dayOfWeek] ?: ""
    )
}
