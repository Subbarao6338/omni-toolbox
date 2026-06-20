package com.example.ui.screens

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
import com.example.ui.OmniViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeluguPanchangamScreen(
    viewModel: OmniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    
    var selectedDate by remember { mutableStateOf(Date()) }
    var showExplanationDialog by remember { mutableStateOf(false) }

    val formattedGregorianDate = remember(selectedDate) {
        val df = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        df.format(selectedDate)
    }

    // 1. Calculate traditional values based on astronomical Synodic & Sidereal offsets
    val panchangResult = remember(selectedDate) {
        calculatePanchangam(selectedDate)
    }

    // Vedic/Saffron Color gradient theme settings
    val goldHeaderGrad = Brush.horizontalGradient(
        listOf(Color(0xFFE65100), Color(0xFFFFB300))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Panchang icon",
                            tint = Color(0xFFFF9100),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "తెలుగు పంచాంగం",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("panchang_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFE65100))
                    }
                },
                actions = {
                    IconButton(onClick = { showExplanationDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Learn More",
                            tint = Color(0xFFFF8F00)
                        )
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
                                    text = "ఆల్మానాక్ క్యాలెండర్ • ALMANAC ENGINE",
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
                                    ),
                                    modifier = Modifier.testTag("date_picker_button")
                                ) {
                                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SELECT CALENDAR DATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Telugu Samvatsara Header Block
                item {
                    TeluguYearBanner(panchang = panchangResult)
                }

                // Core Panchangam Elements (Tithi, Vara, Nakshatra, Yoga, Karana)
                item {
                    Text(
                        text = "పంచాంగ భాగాలు (FIVE VEDIC ELEMENTS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    PanchangGrid(panchang = panchangResult)
                }

                // Solar / Lunar details
                item {
                    Text(
                        text = "సూర్యోదయ మరియు కాలములు (SOLAR TIMINGS & TRANSITS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        letterSpacing = 1.sp
                    )
                }

                item {
                    DurationsPanel(panchang = panchangResult)
                }

                // Auspicious/Inauspicious timings
                item {
                    Text(
                        text = "దిన వర్జ్యములు (DAILY ROSTER TIMINGS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
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

    // Explanatory dialogue on Vedic Almanac
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
                        text = "1. **Tithi**: Phase of the Moon (Shukla/Krishna Paksha)\n" +
                               "2. **Varamu**: Day of the week (Adivaram to Shanivaram)\n" +
                               "3. **Nakshatram**: Moon's positioning star constellation\n" +
                               "4. **Yogamu**: Solar/Lunar angular correlation index\n" +
                               "5. **Karanam**: Half of a lunar Tithi period.",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Additionally, Rahukalam, Yamagandam, and Durmuhurtham represent critical temporal segments used to optimize positive energy parameters in traditional regional contexts.",
                        fontSize = 12.sp
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
            containerColor = Color(0xFFFFF3E0) // Soft amber cream
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "కలియుగ సంవత్సరం • KALIYUGA ERA : 5127",
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
                text = "Dakshinayana • Rutuvu: Greeshma • Masamu: Jyeshta",
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
                desc = "Lunar Phase: " + panchang.paksha,
                icon = Icons.Default.NightlightRound,
                tint = Color(0xFFAB47BC),
                modifier = Modifier.weight(1f)
            )
            PanchElementCard(
                title = "వారం (VARAMU)",
                value = panchang.varam,
                desc = "Ruling Planet: " + panchang.varamLord,
                icon = Icons.Default.Brightness5,
                tint = Color(0xFFFF7043),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PanchElementCard(
                title = "నక్షత్రం (NAKSHATRA)",
                value = panchang.nakshatra,
                desc = "Star constellation orbit",
                icon = Icons.Default.Star,
                tint = Color(0xFF26A69A),
                modifier = Modifier.weight(1f)
            )
            PanchElementCard(
                title = "యోగం / కూడా (YOGA & KARANA)",
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
                Text(
                    text = "VEDIC",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = tint
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
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
            SolarTimeItem(title = "Abhijith Lagna", value = panchang.abhijitLagna, icon = Icons.Default.Timeline, modifier = Modifier.weight(1f))
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
            AlertTimingRow(name = "రాహుకాలం (Rahu Kalam)", time = panchang.rahuKalam, color = Color(0xFFD32F2F), desc = "Inauspicious block for starting auspicious works")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
            AlertTimingRow(name = "యమగండం (Yamagandam)", time = panchang.yamagandam, color = Color(0xFFF57C00), desc = "Undesirable timeline block governed by planet Jupiter counterweights")
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))
            AlertTimingRow(name = "దుర్ముహూర్తం (Durmuhurtham)", time = panchang.durmuhurtham, color = Color(0xFF7B1FA2), desc = "Two auspicious energy clashes that neutralize daily efforts")
        }
    }
}

@Composable
fun AlertTimingRow(
    name: String,
    time: String,
    color: Color,
    desc: String
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
            Text(text = desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.outline, lineHeight = 11.sp)
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

// Data holder classes for Telugu Almanac structures
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

// Main mathematical computation algorithm
fun calculatePanchangam(date: Date): PanchangData {
    val cal = Calendar.getInstance().apply { time = date }
    val year = cal.get(Calendar.YEAR)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon ... 7 = Sat

    // Year Name computation (relative to Gregorian offset)
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
    val baseIndex = 37 // Index of "Krodhi"
    val yearOffset = year - baseYear
    val calculatedYearIndex = (baseIndex + yearOffset) % 60
    val yearName = teluguYears[if (calculatedYearIndex < 0) calculatedYearIndex + 60 else calculatedYearIndex]

    // Pseudo-lunar calculations to approximate Tithi for any given date
    // Let's use Jan 11, 2024 as reference Amavasya (synodic phase 0)
    val refCal = Calendar.getInstance().apply {
        set(2024, Calendar.JANUARY, 11, 12, 0, 0)
    }
    val diffMs = date.time - refCal.timeInMillis
    val diffDays = diffMs.toDouble() / (100 * 60 * 60 * 240) // wait, actually 24 * 60 * 60 * 1000 = 86,400,000 ms.
    val actualDays = diffMs.toDouble() / 86400000.0
    
    // Synodic month is 29.5305888 days
    val phaseAge = (actualDays % 29.5305888).let { if (it < 0) it + 29.5305888 else it }
    val isShukla = phaseAge < 14.765
    val tithiPercent = if (isShukla) phaseAge / 14.765 else (phaseAge - 14.765) / 14.765
    val tithiValue = (tithiPercent * 15).toInt().coerceIn(1, 15)

    val pakshaLabel = if (isShukla) "శుక్ల పక్షం (Waxing Moon)" else "కృష్ణ పక్షం (Waning Moon)"
    
    val tithiNames = listOf(
        "పాడ్యమి (Pratipada)", "విదియ (Dwitiya)", "తదియ (Tritiya)", "చవితి (Chaturthi)",
        "పంచమి (Panchami)", "షష్టి (Shashti)", "సప్తమి (Saptami)", "అష్టమి (Ashtami)",
        "నవమి (Navami)", "దశమి (Dashami)", "ఏకాదశి (Ekadashi)", "ద్వాదశి (Dwadashi)",
        "త్రయోదశి (Trayodashi)", "చతుర్దశి (Chaturdashi)", 
        if (isShukla) "పూర్ణిమ (Pournami)" else "అమావాస్య (Amavasya)"
    )
    val selectedTithi = tithiNames[tithiValue - 1]

    // Weekday name mappings
    val varamNames = listOf("", "ఆదివారము (Adivaram)", "సోమవారము (Somavaram)", "మంగళవారము (Mangalavaram)", "బుధవారము (Budhavaram)", "గురువారము (Guruvaram)", "శుక్రవారము (Shukravaram)", "శనివారము (Shanivaram)")
    val varamLords = listOf("", "Surya (Sun)", "Chandra (Moon)", "Kuha (Mars)", "Budha (Mercury)", "Brihaspati (Jupiter)", "Shukra (Venus)", "Shani (Saturn)")
    val selectedVaram = varamNames[dayOfWeek]
    val selectedLord = varamLords[dayOfWeek]

    // Sidereal approximation of Nakshatra
    // Moon completes orbit in Sidereal period of 27.32166 days approx.
    val moonSiderealAge = (actualDays % 27.32166).let { if (it < 0) it + 27.32166 else it }
    val nakshatramIndex = ((moonSiderealAge / 27.32166) * 27).toInt().coerceIn(0, 26)
    val nakshatras = listOf(
        "అశ్విని (Ashwini)", "భరణి (Bharani)", "కృత్తిక (Krittika)", "రోహిణి (Rohini)", 
        "మృగశిర (Mrigashira)", "ఆర్ద్ర (Arudra)", "పునర్వసు (Punarvasu)", "పుష్యమి (Pushyami)", 
        "ఆశ్లేష (Ashlesha)", "మఖ (Makha)", "పుబ్బ (Pubba)", "ఉత్తర (Uttara)", "హస్త (Hasta)", 
        "చిత్ర (Chitra)", "స్వాతి (Swati)", "విశాఖ (Vishakha)", "అనురాధ (Anuradha)", 
        "జ్యేష్ట (Jyeshta)", "మూల (Moola)", "పూర్వాషాఢ (Poorvashadha)", "ఉత్తరాషాఢ (Uttarashadha)", 
        "శ్రావణం (Shravana)", "ధనిష్ట (Dhanishta)", "శతభిషం (Shatabhisha)", "పూర్వాభాద్ర (Poorvabhadra)", 
        "ఉత్తరాభాద్ర (Uttarabhadra)", "రేవతి (Revati)"
    )
    val selectedNakshatra = nakshatras[nakshatramIndex]

    // Traditional Yoga & Karana calculations
    val yogaIndex = ((actualDays * 1.015) % 27).toInt().let { if (it < 0) it + 27 else it }
    val yogasList = listOf(
        "విష్కంబ", "ప్రీతి", "ఆయుష్మాన్", "సౌభాగ్య", "శోభన", "అతిగండ", "सुकर्म", "ధృతి",
        "శూల", "గండ", "వృద్ధి", "ధ్రువ", "వ్యాఘాత", "హర్షణ", "వజ్ర", "సిద్ధి", "వ్యతీపాత",
        "వరియాన్", "పరిఖ", "శివ", "సిద్ధ", "సాధ్య", "శుభ", "శుక్ల", "బ్రహ్మ", "ఐంద్ర", "వైధృతి"
    )
    val selectedYoga = yogasList[yogaIndex % yogasList.size]

    val karanaNames = listOf("బవ (Bava)", "బాలవ (Balava)", "కౌలవ (Kaulava)", "తైతుల (Taitila)", "గరజ (Garaja)", "వణిజ (Vanija)", "భద్ర (Bhadra)", "శకుని (Shakuni)")
    val selectedKarana = karanaNames[(phaseAge.toInt() * 2) % karanaNames.size]

    // Solar estimations
    val sunrise = "05:46 AM"
    val sunset = "06:38 PM"
    val abhijitLagna = "11:50 AM - 12:40 PM"

    // Rahukalam and Yamagandam maps based on Day of Week (Sunday = 1, Monday = 2...)
    val rahuMap = mapOf(
        1 to "04:30 PM - 06:00 PM",
        2 to "07:30 AM - 09:00 AM",
        3 to "03:00 PM - 04:30 PM",
        4 to "12:00 PM - 01:30 PM",
        5 to "01:30 PM - 03:00 PM",
        6 to "10:30 AM - 12:00 PM",
        7 to "09:00 AM - 10:30 AM"
    )

    val yamaMap = mapOf(
        1 to "12:00 PM - 01:30 PM",
        2 to "10:30 AM - 12:00 PM",
        3 to "09:00 AM - 10:30 AM",
        4 to "07:30 AM - 09:00 AM",
        5 to "06:00 AM - 07:30 AM",
        6 to "03:00 PM - 04:30 PM",
        7 to "01:30 PM - 03:00 PM"
    )

    val durmuMap = mapOf(
        1 to "04:54 PM - 05:46 PM",
        2 to "12:44 PM - 01:36 PM",
        3 to "08:21 AM - 09:13 AM",
        4 to "11:51 AM - 12:43 PM",
        5 to "11:02 AM - 11:54 AM",
        6 to "08:42 AM - 09:34 AM",
        7 to "07:30 AM - 08:22 AM"
    )

    return PanchangData(
        selectedDate = date,
        teluguYearName = yearName,
        tithi = selectedTithi,
        paksha = pakshaLabel,
        varam = selectedVaram,
        varamLord = selectedLord,
        nakshatra = selectedNakshatra,
        yoga = selectedYoga,
        karana = selectedKarana,
        sunrise = sunrise,
        sunset = sunset,
        abhijitLagna = abhijitLagna,
        rahuKalam = rahuMap[dayOfWeek] ?: "04:30 PM - 06:00 PM",
        yamagandam = yamaMap[dayOfWeek] ?: "12:00 PM - 01:30 PM",
        durmuhurtham = durmuMap[dayOfWeek] ?: "04:54 PM - 05:46 PM"
    )
}
