package omni.toolbox.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import omni.toolbox.ui.components.AdjustmentSlider
import java.util.*
import kotlin.math.sqrt

@Composable
fun MathToolScreen(navController: NavHostController, title: String) {
    var input1 by remember { mutableStateOf("") }
    var input2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "$title",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = input1,
                onValueChange = { input1 = it },
                label = { Text(when(title) {
                    "Binary Calculator" -> "Binary/Decimal Number"
                    "Statistics" -> "Numbers (comma separated)"
                    "Fraction Calc" -> "Fraction (e.g. 1/2 or 3 1/4)"
                    "Truth Table Gen" -> "Boolean Expression (A & B | !C)"
                    else -> "Input"
                }) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = if (title == "Statistics") KeyboardType.Number else KeyboardType.Text)
            )

            if (title == "Binary Calculator" || title == "Fraction Calc") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = input2,
                    onValueChange = { input2 = it },
                    label = { Text(if (title == "Binary Calculator") "Second Number (Optional)" else "Second Fraction") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    result = when (title) {
                        "Binary Calculator", "Binary Calc" -> solveBinary(input1, input2)
                        "Statistics" -> solveStats(input1)
                        "Fraction Calc" -> solveFraction(input1, input2)
                        "Truth Table Gen" -> solveTruthTable(input1)
                        "Matrix Calc" -> solveMatrix(input1)
                        "Equation Solver", "Eq Solver" -> solveEquation(input1)
                        else -> "Result: ${input1.reversed()} (Reverse-sim processing for $title)"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calculate")
            }

            if (result != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Result", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = result!!,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

fun solveBinary(n1: String, n2: String): String {
    return try {
        val v1 = if (n1.startsWith("0b")) n1.substring(2).toLong(2) else n1.toLong()
        val res = StringBuilder()
        res.append("Decimal: $v1\n")
        res.append("Binary: 0b${java.lang.Long.toBinaryString(v1)}\n")
        res.append("Hex: 0x${java.lang.Long.toHexString(v1).uppercase()}\n")
        res.append("Octal: 0${java.lang.Long.toOctalString(v1)}")

        if (n2.isNotEmpty()) {
            val v2 = if (n2.startsWith("0b")) n2.substring(2).toLong(2) else n2.toLong()
            res.append("\n\nSum: ${v1 + v2} (0b${java.lang.Long.toBinaryString(v1+v2)})\n")
            res.append("Diff: ${v1 - v2} (0b${java.lang.Long.toBinaryString(v1-v2)})\n")
            res.append("AND: ${v1 and v2} (0b${java.lang.Long.toBinaryString(v1 and v2)})")
        }
        res.toString()
    } catch (e: Exception) {
        "Invalid Input. Use decimal or 0b... for binary."
    }
}

fun solveStats(input: String): String {
    return try {
        val nums = input.split(",").map { it.trim().toDouble() }
        if (nums.isEmpty()) return "Enter numbers separated by commas"
        val sum = nums.sum()
        val mean = sum / nums.size
        val sorted = nums.sorted()
        val median = if (nums.size % 2 == 0) (sorted[nums.size/2] + sorted[nums.size/2-1])/2.0 else sorted[nums.size/2]
        val variance = nums.map { (it - mean) * (it - mean) }.sum() / nums.size
        val stdDev = sqrt(variance)

        "Count: ${nums.size}\nSum: $sum\nMean: $mean\nMedian: $median\nStdDev: ${String.format("%.4f", stdDev)}\nMin: ${sorted.first()}\nMax: ${sorted.last()}"
    } catch (e: Exception) {
        "Invalid Input. Format: 1, 2, 3, 4.5"
    }
}

fun solveFraction(f1: String, f2: String): String {
    return try {
        fun parse(f: String): Pair<Long, Long> {
            val parts = f.trim().split(" ")
            return if (parts.size == 2) {
                val whole = parts[0].toLong()
                val frac = parts[1].split("/")
                val num = frac[0].toLong()
                val den = frac[1].toLong()
                (whole * den + num) to den
            } else {
                val frac = f.split("/")
                if (frac.size == 2) frac[0].toLong() to frac[1].toLong()
                else f.toLong() to 1L
            }
        }

        val (n1, d1) = parse(f1)
        val (n2, d2) = if (f2.isEmpty()) 0L to 1L else parse(f2)

        fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
        fun simplify(n: Long, d: Long): String {
            val common = gcd(Math.abs(n), Math.abs(d))
            val sn = n / common
            val sd = d / common
            return if (sd == 1L) "$sn" else if (Math.abs(sn) > sd) "${sn/sd} ${Math.abs(sn%sd)}/$sd" else "$sn/$sd"
        }

        if (f2.isEmpty()) return "Improper: $n1/$d1\nDecimal: ${n1.toDouble()/d1}"

        val sumN = n1 * d2 + n2 * d1
        val sumD = d1 * d2
        val diffN = n1 * d2 - n2 * d1
        val prodN = n1 * n2
        val prodD = d1 * d2
        val divN = n1 * d2
        val divD = d1 * n2

        "Sum: ${simplify(sumN, sumD)}\nDifference: ${simplify(diffN, sumD)}\nProduct: ${simplify(prodN, prodD)}\nQuotient: ${simplify(divN, divD)}"
    } catch (e: Exception) {
        "Invalid input. Format: 1/2 or 1 1/2"
    }
}

fun solveTruthTable(expr: String): String {
    return try {
        val vars = expr.filter { it.isLetter() }.toSet().sorted()
        if (vars.isEmpty()) return "Enter expression with variables (e.g., A & B)"

        val rows = 1 shl vars.size
        val sb = StringBuilder()
        sb.append(vars.joinToString(" ") + " | Result\n")
        sb.append("-".repeat(vars.size * 2 + 8) + "\n")

        for (i in 0 until rows) {
            val values = mutableMapOf<Char, Boolean>()
            vars.forEachIndexed { index, c ->
                values[c] = (i shr (vars.size - 1 - index)) and 1 == 1
                sb.append(if (values[c]!!) "1 " else "0 ")
            }

            // Simple evaluator for &, |, !
            fun eval(e: String, v: Map<Char, Boolean>): Boolean {
                var res = e.replace(" ", "")
                v.forEach { (c, b) -> res = res.replace(c.toString(), if (b) "1" else "0") }

                while ("!" in res) {
                    res = res.replace("!1", "0").replace("!0", "1")
                }
                while ("&" in res) {
                    res = res.replace("1&1", "1").replace("1&0", "0").replace("0&1", "0").replace("0&0", "0")
                }
                while ("|" in res) {
                    res = res.replace("1|1", "1").replace("1|0", "1").replace("0|1", "1").replace("0|0", "0")
                }
                return res == "1"
            }

            sb.append("|  ${if (eval(expr, values)) "1" else "0"}\n")
        }
        sb.toString()
    } catch (e: Exception) {
        "Error evaluating expression. Use &, |, !"
    }
}

fun solveMatrix(input: String): String {
    return try {
        val nums = input.split(",").map { it.trim().toDouble() }
        when (nums.size) {
            4 -> {
                val det = nums[0] * nums[3] - nums[1] * nums[2]
                "2x2 Matrix:\n[${nums[0]} ${nums[1]}]\n[${nums[2]} ${nums[3]}]\n\nDeterminant: $det"
            }
            9 -> {
                val det = nums[0]*(nums[4]*nums[8] - nums[5]*nums[7]) -
                          nums[1]*(nums[3]*nums[8] - nums[5]*nums[6]) +
                          nums[2]*(nums[3]*nums[7] - nums[4]*nums[6])
                "3x3 Matrix:\n[${nums[0]} ${nums[1]} ${nums[2]}]\n[${nums[3]} ${nums[4]} ${nums[5]}]\n[${nums[6]} ${nums[7]} ${nums[8]}]\n\nDeterminant: $det"
            }
            else -> "Enter 4 numbers for 2x2 or 9 numbers for 3x3 matrix."
        }
    } catch (e: Exception) {
        "Invalid Input. Format: 1, 2, 3, 4"
    }
}

fun solveEquation(input: String): String {
    return try {
        val nums = input.split(",").map { it.trim().toDouble() }
        if (nums.size != 3) return "Enter 3 numbers (a,b,c) for ax² + bx + c = 0"
        val a = nums[0]; val b = nums[1]; val c = nums[2]
        val d = b * b - 4 * a * c
        when {
            d > 0 -> "Two real roots: x1=${(-b+sqrt(d))/(2*a)}, x2=${(-b-sqrt(d))/(2*a)}"
            d == 0.0 -> "One real root: x=${-b/(2*a)}"
            else -> "No real roots (Discriminant: $d)"
        }
    } catch (e: Exception) {
        "Invalid Input. Format: 1, -3, 2 (for x²-3x+2=0)"
    }
}
