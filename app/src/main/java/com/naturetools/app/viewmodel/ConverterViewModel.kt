package com.naturetools.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ConverterViewModel : ViewModel() {
    var input by mutableStateOf("")
    var result by mutableStateOf("")

    var selectedCategoryIndex by mutableIntStateOf(0)
    var fromUnitIndex by mutableIntStateOf(0)
    var toUnitIndex by mutableIntStateOf(0)

    val categories = listOf(
        ConversionCategory("Length", listOf(Unit("Meter", 1.0), Unit("Kilometer", 0.001), Unit("Mile", 0.000621371), Unit("Foot", 3.28084))),
        ConversionCategory("Weight", listOf(Unit("Kilogram", 1.0), Unit("Gram", 1000.0), Unit("Pound", 2.20462), Unit("Ounce", 35.274))),
        ConversionCategory("Temperature", listOf(Unit("Celsius", 1.0), Unit("Fahrenheit", 0.0), Unit("Kelvin", 0.0))),
        ConversionCategory("Area", listOf(Unit("Sq Meter", 1.0), Unit("Sq Kilometer", 0.000001), Unit("Sq Mile", 3.861e-7), Unit("Acre", 0.000247105))),
        ConversionCategory("Volume", listOf(Unit("Liter", 1.0), Unit("Milliliter", 1000.0), Unit("Gallon (US)", 0.264172), Unit("Cubic Meter", 0.001))),
        ConversionCategory("Digital", listOf(Unit("Byte", 1.0), Unit("Bit", 8.0), Unit("Kilobyte", 1.0/1024), Unit("Megabyte", 1.0/(1024*1024)), Unit("Gigabyte", 1.0/(1024.0*1024*1024)), Unit("Terabyte", 1.0/(1024.0*1024*1024*1024)))),
        ConversionCategory("Pressure", listOf(Unit("Pascal", 1.0), Unit("Bar", 1e-5), Unit("PSI", 0.000145038), Unit("Atmosphere", 9.8692e-6))),
        ConversionCategory("Fuel", listOf(Unit("L/100km", 1.0), Unit("MPG (US)", 0.0), Unit("MPG (UK)", 0.0))),
        ConversionCategory("Speed", listOf(Unit("m/s", 1.0), Unit("km/h", 3.6), Unit("mph", 2.23694), Unit("knot", 1.94384))),
        ConversionCategory("Frequency", listOf(Unit("Hertz", 1.0), Unit("Kilohertz", 0.001), Unit("Megahertz", 1e-6), Unit("Gigahertz", 1e-9))),
        ConversionCategory("Power", listOf(Unit("Watt", 1.0), Unit("Kilowatt", 0.001), Unit("Horsepower", 0.00134102))),
        ConversionCategory("Torque", listOf(Unit("Newton-meter", 1.0), Unit("Foot-pound", 0.737562)))
    )

    fun onInputChange(value: String) {
        input = value
        convert()
    }

    fun onCategoryChange(index: Int) {
        selectedCategoryIndex = index
        fromUnitIndex = 0
        toUnitIndex = 1
        convert()
    }

    fun onFromUnitChange(index: Int) {
        fromUnitIndex = index
        convert()
    }

    fun onToUnitChange(index: Int) {
        toUnitIndex = index
        convert()
    }

    private fun convert() {
        val value = input.toDoubleOrNull() ?: return
        val category = categories[selectedCategoryIndex]
        val fromUnit = category.units[fromUnitIndex]
        val toUnit = category.units[toUnitIndex]

        result = if (category.name == "Temperature") {
            convertTemperature(value, fromUnit.name, toUnit.name)
        } else if (category.name == "Fuel") {
            convertFuel(value, fromUnit.name, toUnit.name)
        } else {
            val baseValue = value / fromUnit.factor
            (baseValue * toUnit.factor).format(4)
        }
    }

    private fun convertFuel(value: Double, from: String, to: String): String {
        if (value <= 0) return "0"
        val l100km = when (from) {
            "L/100km" -> value
            "MPG (US)" -> 235.215 / value
            "MPG (UK)" -> 282.481 / value
            else -> value
        }
        val resultValue = when (to) {
            "L/100km" -> l100km
            "MPG (US)" -> 235.215 / l100km
            "MPG (UK)" -> 282.481 / l100km
            else -> l100km
        }
        return resultValue.format(2)
    }

    private fun convertTemperature(value: Double, from: String, to: String): String {
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5 / 9
            "Kelvin" -> value - 273.15
            else -> value
        }
        val resultValue = when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> celsius * 9 / 5 + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
        return resultValue.format(2)
    }

    private fun Double.format(digits: Int) = java.lang.String.format(java.util.Locale.US, "%.${digits}f", this).trimEnd('0').trimEnd('.')

    data class ConversionCategory(val name: String, val units: List<Unit>)
    data class Unit(val name: String, val factor: Double)
}
