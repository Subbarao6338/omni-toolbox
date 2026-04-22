package com.naturetools.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.*

class CalculatorViewModel : ViewModel() {
    var display by mutableStateOf("")
        private set

    val history = mutableStateListOf<String>()

    private var lastValue: Double? = null
    private var lastOperator: String? = null
    private var isNewInput = true

    fun onDigit(digit: String) {
        if (isNewInput) {
            display = digit
            isNewInput = false
        } else {
            display += digit
        }
    }

    fun onOperator(op: String) {
        val currentValue = display.toDoubleOrNull() ?: return
        if (lastValue == null) {
            lastValue = currentValue
        } else {
            calculate()
        }
        lastOperator = op
        isNewInput = true
    }

    fun onScientific(op: String) {
        val currentValue = display.toDoubleOrNull() ?: return
        val result = when (op) {
            "sin" -> sin(Math.toRadians(currentValue))
            "cos" -> cos(Math.toRadians(currentValue))
            "tan" -> tan(Math.toRadians(currentValue))
            "log" -> log10(currentValue)
            "ln" -> ln(currentValue)
            "√" -> sqrt(currentValue)
            else -> currentValue
        }
        val entry = "$op($currentValue) = ${formatResult(result)}"
        history.add(0, entry)
        display = formatResult(result)
        isNewInput = true
    }

    fun calculate() {
        val currentValue = display.toDoubleOrNull() ?: return
        val operator = lastOperator ?: return
        val result = when (operator) {
            "+" -> lastValue!! + currentValue
            "-" -> lastValue!! - currentValue
            "*" -> lastValue!! * currentValue
            "/" -> if (currentValue != 0.0) lastValue!! / currentValue else Double.NaN
            else -> currentValue
        }

        val entry = "${lastValue} $operator $currentValue = ${formatResult(result)}"
        history.add(0, entry)

        display = formatResult(result)
        lastValue = result
        lastOperator = null
        isNewInput = true
    }

    private fun formatResult(result: Double): String {
        return if (result.isNaN()) "Error"
        else if (result % 1.0 == 0.0) result.toLong().toString()
        else "%.4f".format(result)
    }

    fun clear() {
        display = ""
        lastValue = null
        lastOperator = null
        isNewInput = true
    }

    fun clearHistory() {
        history.clear()
    }
}
