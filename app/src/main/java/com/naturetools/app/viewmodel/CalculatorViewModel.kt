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
            calculateInternal()
        }
        lastOperator = op
        isNewInput = true
    }

    fun onScientific(op: String) {
        val currentValue = display.toDoubleOrNull() ?: return
        val result = try {
            when (op) {
                "sin" -> sin(Math.toRadians(currentValue))
                "cos" -> cos(Math.toRadians(currentValue))
                "tan" -> tan(Math.toRadians(currentValue))
                "log" -> log10(currentValue)
                "ln" -> ln(currentValue)
                "√" -> if (currentValue >= 0) sqrt(currentValue) else Double.NaN
                "mod" -> {
                    // This is a bit tricky for a single-operand scientific function.
                    // Usually mod needs two. Let's assume it's for current result % something,
                    // but most calculators use it as a binary operator.
                    // Let's implement it as binary in calculateInternal instead,
                    // and keep scientific for unary.
                    // If we want exp(x) as e^x:
                    exp(currentValue)
                }
                "eˣ" -> exp(currentValue)
                "x²" -> currentValue * currentValue
                "abs" -> abs(currentValue)
                "π" -> PI
                else -> currentValue
            }
        } catch (e: Exception) {
            Double.NaN
        }

        val formattedResult = formatResult(result)
        val entry = "$op($currentValue) = $formattedResult"
        history.add(0, entry)
        display = formattedResult
        isNewInput = true
    }

    fun calculate() {
        calculateInternal()
        lastValue = null
        lastOperator = null
    }

    private fun calculateInternal() {
        val currentValue = display.toDoubleOrNull() ?: return
        val operator = lastOperator ?: return
        val result = when (operator) {
            "+" -> lastValue!! + currentValue
            "-" -> lastValue!! - currentValue
            "*" -> lastValue!! * currentValue
            "/" -> if (currentValue != 0.0) lastValue!! / currentValue else Double.NaN
            "mod" -> lastValue!! % currentValue
            "pow" -> lastValue!!.pow(currentValue)
            else -> currentValue
        }

        val entry = "${formatValue(lastValue!!)} $operator ${formatValue(currentValue)} = ${formatResult(result)}"
        history.add(0, entry)

        display = formatResult(result)
        lastValue = result
        isNewInput = true
    }

    private fun formatValue(value: Double): String {
        return if (value % 1.0 == 0.0) value.toLong().toString() else "%.4f".format(value).trimEnd('0').trimEnd('.')
    }

    private fun formatResult(result: Double): String {
        return if (result.isNaN()) "Error"
        else if (result.isInfinite()) "Infinity"
        else if (result % 1.0 == 0.0) result.toLong().toString()
        else "%.6f".format(result).trimEnd('0').trimEnd('.')
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
