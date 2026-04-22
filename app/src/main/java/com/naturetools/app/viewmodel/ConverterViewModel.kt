package com.naturetools.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConverterViewModel : ViewModel() {
    var input by mutableStateOf("")
    var result by mutableStateOf("")
    var selectedTab by mutableIntStateOf(0)

    val tabs = listOf("Length", "Weight", "Temp", "Area")

    fun onInputChange(value: String) {
        input = value
        val numericValue = value.toDoubleOrNull() ?: 0.0
        result = when (selectedTab) {
            0 -> "${(numericValue * 0.621371).format(2)} Miles" // Km to Miles
            1 -> "${(numericValue * 2.20462).format(2)} Pounds" // Kg to Lbs
            2 -> "${(numericValue * 9 / 5 + 32).format(2)} °F" // C to F
            3 -> "${(numericValue * 10.7639).format(2)} Sq Ft" // Sq M to Sq Ft
            else -> ""
        }
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
