package com.naturetools.app.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class PanchangamDetails(
    val samvatsaram: String,
    val ayana: String,
    val rutu: String,
    val maasam: String,
    val paksha: String,
    val tithi: String,
    val nakshatram: String,
    val raashi: String,
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

    fun getPanchangam(dateTime: LocalDateTime): PanchangamDetails {
        val year = dateTime.year
        val month = dateTime.monthValue
        val day = dateTime.dayOfMonth

        // Heuristic calculation for Samvatsaram (simplified)
        val samvatsaraIndex = (year - 1987 + 60) % 60
        val samvatsaram = samvatsarams.getOrElse(samvatsaraIndex) { "Krodhi" }

        // Heuristic for Maasam
        val maasams = listOf("Chaitra", "Vaishakha", "Jyeshtha", "Ashadha", "Shravana", "Bhadrapada", "Ashwayuja", "Kartika", "Margashira", "Pushya", "Magha", "Phalguna")
        val maasam = maasams[(month + 8) % 12]

        // Heuristic for Tithi
        val tithi = when (day % 15) {
            1 -> "Padyami"; 2 -> "Vidiya"; 3 -> "Tadiya"; 4 -> "Chavithi"
            5 -> "Panchami"; 6 -> "Shashti"; 7 -> "Saptami"; 8 -> "Ashtami"
            9 -> "Navami"; 10 -> "Dashami"; 11 -> "Ekadashi"; 12 -> "Dwadashi"
            13 -> "Trayodashi"; 14 -> "Chaturdashi"; 0 -> if (day > 15) "Amavasya" else "Pournami"
            else -> "Ashtami"
        }

        val nakshatras = listOf("Ashwini", "Bharani", "Kruthika", "Rohini", "Mrigashira", "Arudra", "Punarvasu", "Pushyami", "Aslesha", "Makha", "Pubba", "Uttara", "Hastha", "Chitra", "Swathi", "Vishakha", "Anuradha", "Jyeshta", "Moola", "Poorvashada", "Uttarashada", "Shravanam", "Dhanishta", "Shathabhisham", "Poorvabhadra", "Uttarabhadra", "Revathi")
        val nakshatram = nakshatras[(day + month) % 27]

        val raashis = listOf("Mesha", "Vrushabha", "Midhuna", "Karka", "Simha", "Kanya", "Thula", "Vrushchika", "Dhanussu", "Makara", "Kumbha", "Meena")
        val raashi = raashis[(day % 12)]

        return PanchangamDetails(
            samvatsaram = "$samvatsaram (తెలుగు సంవత్సరం)",
            ayana = if (month in 1..6) "Uttarayana" else "Dakshinayana",
            rutu = when (month) {
                in 3..4 -> "Vasanta"; in 5..6 -> "Grishma"; in 7..8 -> "Varsha"
                in 9..10 -> "Sharad"; in 11..12 -> "Hemanta"; else -> "Shishira"
            },
            maasam = "$maasam (మాసం)",
            paksha = if (day <= 15) "Shukla Paksha" else "Krishna Paksha",
            tithi = "$tithi (తిథి)",
            nakshatram = "$nakshatram (నక్షత్రం) - Padam ${(day % 4) + 1}",
            raashi = "$raashi (రాశి)",
            luckyNumber = when(raashi) {
                "Mesha" -> "9, 1, 8"; "Vrushabha" -> "6, 2, 7"; "Midhuna" -> "5, 3, 6"; "Karka" -> "2, 7, 9"
                "Simha" -> "1, 4, 9"; "Kanya" -> "5, 3, 6"; "Thula" -> "6, 2, 7"; "Vrushchika" -> "9, 1, 8"
                "Dhanussu" -> "3, 5, 8"; "Makara" -> "8, 6, 7"; "Kumbha" -> "8, 4, 5"; "Meena" -> "3, 7, 9"
                else -> "1, 5, 9"
            },
            luckyColor = "Vibrant themed",
            luckyDay = "Auspicious Day"
        )
    }
}
