package omni.toolbox.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.*

data class PanchangamDetails(
    val samvatsaram: String,
    val ayana: String,
    val rutu: String,
    val maasam: String,
    val paksha: String,
    val tithi: String,
    val nakshatram: String,
    val raashi: String,
    val yoga: String,
    val karana: String,
    val sunrise: String,
    val sunset: String,
    val rahuKalam: String,
    val yamagandam: String,
    val gulikaKalam: String,
    val durmuhurtham: String,
    val brahmaMuhurtham: String,
    val luckyNumber: String,
    val luckyColor: String,
    val luckyDay: String
)

object PanchangamLogic {
    private val samvatsarams = listOf(
        "Prabhava", "Vibhava", "Shukla", "Pramodoota", "Prajotpatti", "Aangirasa", "Shrimukha", "Bhava", "Yuva", "Dhaatu",
        "Eshwara", "Bahudhanya", "Pramadi", "Vikrama", "Vrusha", "Chitrabhanu", "Swabhanu", "Tarana", "Paarthiva", "Vyaya",
        "Sarvajitu", "Sarvadhaari", "Virodhi", "Vikruti", "Khara", "Nandana", "Vijaya", "Jaya", "Manmadha", "Durmukhi",
        "Hevilambi", "Vilambi", "Vikari", "Sharvari", "Plava", "Shubhakrutu", "Shobhakrutu", "Krodhi", "Viswaavasu", "Paridhaavi",
        "Pramadicha", "Aananda", "Rakshasa", "Nala", "Pingala", "Kaalayukti", "Siddhardhi", "Roudri", "Durmati", "Dundubhi",
        "Rudhirodgaari", "Raktaakshi", "Krodhana", "Akshaya"
    )

    private val maasams = listOf(
        "Chaitra", "Vaishakha", "Jyeshtha", "Ashadha", "Shravana", "Bhadrapada",
        "Ashwayuja", "Kartika", "Margashira", "Pushya", "Magha", "Phalguna"
    )

    private val tithis = listOf(
        "Padyami", "Vidiya", "Tadiya", "Chavithi", "Panchami", "Shashti", "Saptami", "Ashtami",
        "Navami", "Dashami", "Ekadashi", "Dwadashi", "Trayodashi", "Chaturdashi", "Pournami/Amavasya"
    )

    private val nakshatras = listOf(
        "Ashwini", "Bharani", "Kruthika", "Rohini", "Mrigashira", "Arudra", "Punarvasu", "Pushyami",
        "Aslesha", "Makha", "Pubba", "Uttara", "Hastha", "Chitra", "Swathi", "Vishakha", "Anuradha",
        "Jyeshta", "Moola", "Poorvashada", "Uttarashada", "Shravanam", "Dhanishta", "Shathabhisham",
        "Poorvabhadra", "Uttarabhadra", "Revathi"
    )

    private val yogas = listOf(
        "Vishkumbha", "Preeti", "Ayushman", "Saubhagya", "Shobhana", "Atiganda", "Sukarma", "Dhriti",
        "Shoola", "Ganda", "Vriddhi", "Dhruva", "Vyaghata", "Harshana", "Vajra", "Siddhi", "Vyatipata",
        "Variyan", "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha", "Shukla", "Brahma", "Indra", "Vaidhriti"
    )

    private val karanas = listOf(
        "Bava", "Balava", "Kaulava", "Taitila", "Garaja", "Vanija", "Vishti"
    )

    private val raashis = listOf(
        "Mesha", "Vrushabha", "Midhuna", "Karka", "Simha", "Kanya",
        "Thula", "Vrushchika", "Dhanussu", "Makara", "Kumbha", "Meena"
    )

    fun getPanchangam(dateTime: LocalDateTime): PanchangamDetails {
        val jd = calculateJulianDay(dateTime)
        val t = (jd - 2451545.0) / 36525.0

        val ayanamsha = 23.85 + 3.97030 * t + 0.00014 * t * t // Lalahiri Ayanamsha approx

        var sunLong = calculateSunLongitude(t) - ayanamsha
        sunLong = (sunLong + 360.0) % 360.0

        var moonLong = calculateMoonLongitude(t) - ayanamsha
        moonLong = (moonLong + 360.0) % 360.0

        // Tithi: (MoonLong - SunLong) / 12
        var diff = moonLong - sunLong
        if (diff < 0) diff += 360.0
        val tithiIndex = (diff / 12.0).toInt() % 30
        val tithiName = if (tithiIndex == 14) "Pournami" else if (tithiIndex == 29) "Amavasya" else tithis[tithiIndex % 15]
        val paksha = if (tithiIndex < 15) "Shukla Paksha" else "Krishna Paksha"

        // Nakshatra: MoonLong / (360/27)
        val nakshatraIndex = (moonLong / (360.0 / 27.0)).toInt() % 27
        val nakshatraName = nakshatras[nakshatraIndex]

        // Yoga: (SunLong + MoonLong) / (360/27)
        var yogaSum = sunLong + moonLong
        if (yogaSum >= 360.0) yogaSum -= 360.0
        val yogaIndex = (yogaSum / (360.0 / 27.0)).toInt() % 27
        val yogaName = yogas[yogaIndex]

        // Karana: Tithi based (2 Karanas per Tithi)
        val karanaFullIndex = (diff / 6.0).toInt() // 0 to 59
        val karanaName = when (karanaFullIndex) {
            0 -> "Kinstughna"
            in 1..57 -> karanas[(karanaFullIndex - 1) % 7]
            58 -> "Shakuni"
            59 -> "Chatushpada"
            60 -> "Nagava" // Edge case
            else -> "Bava"
        }

        // Rashi: MoonLong / 30
        val rashiIndex = (moonLong / 30.0).toInt() % 12
        val rashiName = raashis[rashiIndex]

        // Maasam (Solar month index)
        val maasamIndex = (sunLong / 30.0).toInt() % 12
        val maasam = maasams[maasamIndex]

        // Samvatsaram (Changes on Chaitra Shukla Padyami)
        // Simplified: use year but adjust if we are before Chaitra
        val year = dateTime.year
        var samvatsaraIndex = (year - 1987 + 60) % 60
        if (maasamIndex < 0) { // Should not happen with modulo
             samvatsaraIndex = (samvatsaraIndex - 1 + 60) % 60
        }
        val samvatsaram = samvatsarams[samvatsaraIndex]

        // Timings (Default Hyderabad: 17.38, 78.48)
        val lat = 17.38
        val lon = 78.48
        val sunriseTime = calculateSunrise(dateTime, lat, lon, true)
        val sunsetTime = calculateSunrise(dateTime, lat, lon, false)

        val sunriseStr = formatTime(sunriseTime)
        val sunsetStr = formatTime(sunsetTime)

        val dayDuration = sunsetTime - sunriseTime
        val rahuKalam = calculateRahuKalam(dateTime.dayOfWeek.value, sunriseTime, dayDuration)
        val yamagandam = calculateYamagandam(dateTime.dayOfWeek.value, sunriseTime, dayDuration)
        val gulikaKalam = calculateGulikaKalam(dateTime.dayOfWeek.value, sunriseTime, dayDuration)
        val brahmaMuhurtham = formatTime(sunriseTime - 1.6) // Approx 96 mins before sunrise
        val durmuhurtham = calculateDurmuhurtham(dateTime.dayOfWeek.value, sunriseTime, dayDuration)

        return PanchangamDetails(
            samvatsaram = "$samvatsaram (తెలుగు సంవత్సరం)",
            ayana = if (sunLong in 270.0..360.0 || sunLong in 0.0..90.0) "Uttarayana" else "Dakshinayana",
            rutu = calculateRutu(maasamIndex),
            maasam = "$maasam (మాసం)",
            paksha = paksha,
            tithi = "$tithiName (తిథి)",
            nakshatram = "$nakshatraName (నక్షత్రం)",
            raashi = "$rashiName (రాశి)",
            yoga = yogaName,
            karana = karanaName,
            sunrise = sunriseStr,
            sunset = sunsetStr,
            rahuKalam = rahuKalam,
            yamagandam = yamagandam,
            gulikaKalam = gulikaKalam,
            durmuhurtham = durmuhurtham,
            brahmaMuhurtham = brahmaMuhurtham,
            luckyNumber = getLuckyNumber(rashiName),
            luckyColor = getLuckyColor(rashiName),
            luckyDay = getLuckyDay(rashiName)
        )
    }

    private fun calculateJulianDay(dt: LocalDateTime): Double {
        var y = dt.year.toDouble()
        var m = dt.monthValue.toDouble()
        val d = dt.dayOfMonth.toDouble()
        val h = dt.hour + dt.minute / 60.0 + dt.second / 3600.0

        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5 + h / 24.0
    }

    private fun calculateSunLongitude(t: Double): Double {
        val l = (280.46646 + 36000.76983 * t) % 360.0
        val m = (357.52911 + 35999.05029 * t) % 360.0
        val lambda = l + 1.914602 * sin(Math.toRadians(m)) + 0.019993 * sin(Math.toRadians(2 * m))
        return (lambda + 360.0) % 360.0
    }

    private fun calculateMoonLongitude(t: Double): Double {
        val l = (218.3164477 + 481267.88123421 * t) % 360.0
        val m = (134.9633964 + 477198.8675055 * t) % 360.0 // Moon Mean Anomaly
        val d = (297.8501921 + 445267.1114034 * t) % 360.0 // Mean Elongation

        var lambda = l + 6.288774 * sin(Math.toRadians(m)) + 1.274027 * sin(Math.toRadians(2 * d - m)) +
                0.658314 * sin(Math.toRadians(2 * d)) + 0.213618 * sin(Math.toRadians(2 * m))
        return (lambda + 360.0) % 360.0
    }


    private fun calculateRutu(maasamIndex: Int): String = when (maasamIndex) {
        0, 1 -> "Vasanta"; 2, 3 -> "Grishma"; 4, 5 -> "Varsha"
        6, 7 -> "Sharad"; 8, 9 -> "Hemanta"; else -> "Shishira"
    }

    private fun calculateSunrise(dt: LocalDateTime, lat: Double, lon: Double, isSunrise: Boolean): Double {
        val dayOfYear = dt.dayOfYear
        val zenith = 90.833 // standard
        val lonHour = lon / 15.0
        val t = if (isSunrise) dayOfYear + (6.0 - lonHour) / 24.0 else dayOfYear + (18.0 - lonHour) / 24.0

        val m = (0.9856 * t) - 3.289
        var l = m + (1.916 * sin(Math.toRadians(m))) + (0.020 * sin(Math.toRadians(2 * m))) + 282.634
        l = (l + 360) % 360

        var ra = Math.toDegrees(atan(0.91764 * tan(Math.toRadians(l))))
        ra = (ra + 360) % 360
        val lQuad = floor(l / 90.0) * 90.0
        val raQuad = floor(ra / 90.0) * 90.0
        ra += (lQuad - raQuad)
        ra /= 15.0

        val sinDec = 0.39782 * sin(Math.toRadians(l))
        val cosDec = cos(asin(sinDec))

        val cosH = (cos(Math.toRadians(zenith)) - (sinDec * sin(Math.toRadians(lat)))) / (cosDec * cos(Math.toRadians(lat)))
        if (cosH > 1 || cosH < -1) return if (isSunrise) 6.0 else 18.0 // edge cases for poles

        val h = if (isSunrise) 360.0 - Math.toDegrees(acos(cosH)) else Math.toDegrees(acos(cosH))
        val hHour = h / 15.0
        val localT = hHour + ra - (0.06571 * t) - 6.622
        var ut = localT - lonHour
        ut = (ut + 24) % 24

        // Convert to IST (UTC+5.5)
        return (ut + 5.5) % 24
    }

    private fun formatTime(decimalHour: Double): String {
        val h = decimalHour.toInt()
        val m = ((decimalHour - h) * 60).toInt()
        val ampm = if (h >= 12) "PM" else "AM"
        val h12 = if (h % 12 == 0) 12 else h % 12
        return String.format("%02d:%02d %s", h12, m, ampm)
    }

    private fun calculateRahuKalam(dayOfWeek: Int, sunrise: Double, duration: Double): String {
        val octant = duration / 8.0
        val octantNum = when (dayOfWeek) {
            1 -> 2 // Mon
            2 -> 7 // Tue
            3 -> 5 // Wed
            4 -> 6 // Thu
            5 -> 4 // Fri
            6 -> 3 // Sat
            7 -> 8 // Sun
            else -> 1
        }
        val start = sunrise + (octantNum - 1) * octant
        return "${formatTime(start)} - ${formatTime(start + octant)}"
    }

    private fun calculateYamagandam(dayOfWeek: Int, sunrise: Double, duration: Double): String {
        val octant = duration / 8.0
        val octantNum = when (dayOfWeek) {
            1 -> 4 // Mon
            2 -> 3 // Tue
            3 -> 2 // Wed
            4 -> 1 // Thu
            5 -> 7 // Fri
            6 -> 6 // Sat
            7 -> 5 // Sun
            else -> 1
        }
        val start = sunrise + (octantNum - 1) * octant
        return "${formatTime(start)} - ${formatTime(start + octant)}"
    }

    private fun calculateDurmuhurtham(dayOfWeek: Int, sunrise: Double, duration: Double): String {
        val division = duration / 15.0
        return when (dayOfWeek) {
            1 -> { // Monday
                val start1 = sunrise + 8 * division
                val start2 = sunrise + 11 * division
                formatTime(start1) + " - " + formatTime(start1 + division) + ", " + formatTime(start2) + " - " + formatTime(start2 + division)
            }
            2 -> { // Tuesday
                val start1 = sunrise + 3 * division
                val start2 = sunrise + 12 * division
                formatTime(start1) + " - " + formatTime(start1 + division) + ", " + formatTime(start2) + " - " + formatTime(start2 + division)
            }
            3 -> { // Wednesday
                val start = sunrise + 7 * division
                formatTime(start) + " - " + formatTime(start + division)
            }
            4 -> { // Thursday
                val start = sunrise + 5 * division
                formatTime(start) + " - " + formatTime(start + division)
            }
            5 -> { // Friday
                val start1 = sunrise + 3 * division
                val start2 = sunrise + 8 * division
                formatTime(start1) + " - " + formatTime(start1 + division) + ", " + formatTime(start2) + " - " + formatTime(start2 + division)
            }
            6 -> { // Saturday
                val start = sunrise + 1 * division
                formatTime(start) + " - " + formatTime(start + division)
            }
            7 -> { // Sunday
                val start = sunrise + 13 * division
                formatTime(start) + " - " + formatTime(start + division)
            }
            else -> ""
        }
    }

    private fun calculateGulikaKalam(dayOfWeek: Int, sunrise: Double, duration: Double): String {
        val octant = duration / 8.0
        val octantNum = when (dayOfWeek) {
            1 -> 6 // Mon
            2 -> 5 // Tue
            3 -> 4 // Wed
            4 -> 3 // Thu
            5 -> 2 // Fri
            6 -> 1 // Sat
            7 -> 7 // Sun
            else -> 1
        }
        val start = sunrise + (octantNum - 1) * octant
        return "${formatTime(start)} - ${formatTime(start + octant)}"
    }

    fun getLuckyNumber(raashi: String): String = when (raashi) {
        "Mesha" -> "9, 1, 8"; "Vrushabha" -> "6, 2, 7"; "Midhuna" -> "5, 3, 6"; "Karka" -> "2, 7, 9"
        "Simha" -> "1, 4, 9"; "Kanya" -> "5, 3, 6"; "Thula" -> "6, 2, 7"; "Vrushchika" -> "9, 1, 8"
        "Dhanussu" -> "3, 5, 8"; "Makara" -> "8, 6, 7"; "Kumbha" -> "8, 4, 5"; "Meena" -> "3, 7, 9"
        else -> "1, 5, 9"
    }

    fun getLuckyColor(raashi: String): String = when (raashi) {
        "Mesha" -> "Red"; "Vrushabha" -> "White"; "Midhuna" -> "Green"; "Karka" -> "Silver"
        "Simha" -> "Gold"; "Kanya" -> "Dark Green"; "Thula" -> "White"; "Vrushchika" -> "Red"
        "Dhanussu" -> "Yellow"; "Makara" -> "Black"; "Kumbha" -> "Blue"; "Meena" -> "Yellow"
        else -> "Vibrant themed"
    }

    fun getLuckyDay(raashi: String): String = when (raashi) {
        "Mesha" -> "Tuesday"; "Vrushabha" -> "Friday"; "Midhuna" -> "Wednesday"; "Karka" -> "Monday"
        "Simha" -> "Sunday"; "Kanya" -> "Wednesday"; "Thula" -> "Friday"; "Vrushchika" -> "Tuesday"
        "Dhanussu" -> "Thursday"; "Makara" -> "Saturday"; "Kumbha" -> "Saturday"; "Meena" -> "Thursday"
        else -> "Auspicious Day"
    }
}
