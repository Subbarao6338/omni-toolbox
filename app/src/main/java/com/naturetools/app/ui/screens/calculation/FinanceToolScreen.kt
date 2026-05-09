package com.naturetools.app.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlin.math.pow

@Composable
fun FinanceToolScreen(navController: NavHostController, title: String) {
    ToolScreen(
        title = title,
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "SIP Calculator" -> SipCalculator()
                "GST Calculator" -> GstCalculator()
                "Retirement Planner" -> RetirementPlanner()
                else -> Text("Finance Utility for $title")
            }
        }
    }
}

@Composable
fun SipCalculator() {
    var monthlyInvestment by remember { mutableStateOf("5000") }
    var expectedReturnRate by remember { mutableStateOf("12") }
    var timePeriod by remember { mutableStateOf("10") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("SIP Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = monthlyInvestment,
            onValueChange = { monthlyInvestment = it },
            label = { Text("Monthly Investment (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = expectedReturnRate,
            onValueChange = { expectedReturnRate = it },
            label = { Text("Expected Return Rate (% p.a.)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = timePeriod,
            onValueChange = { timePeriod = it },
            label = { Text("Time Period (Years)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val p = monthlyInvestment.toDoubleOrNull() ?: 0.0
        val r = (expectedReturnRate.toDoubleOrNull() ?: 0.0) / 100 / 12
        val n = (timePeriod.toDoubleOrNull() ?: 0.0) * 12

        if (p > 0 && r > 0 && n > 0) {
            val futureValue = p * ((1 + r).pow(n) - 1) * (1 + r) / r
            val investedAmount = p * n
            val wealthGain = futureValue - investedAmount

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Results:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Invested Amount: ₹${"%,.0f".format(investedAmount)}")
                    Text("Estimated Returns: ₹${"%,.0f".format(wealthGain)}")
                    Text("Total Value: ₹${"%,.0f".format(futureValue)}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GstCalculator() {
    var amount by remember { mutableStateOf("1000") }
    var gstRate by remember { mutableStateOf("18") }
    var isInclusive by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("GST Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Text("GST Rate: $gstRate%", modifier = Modifier.padding(top = 8.dp))
        Slider(
            value = gstRate.toFloatOrNull() ?: 18f,
            onValueChange = { gstRate = it.toInt().toString() },
            valueRange = 0f..28f,
            steps = 28
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isInclusive, onCheckedChange = { isInclusive = it })
            Text("GST Inclusive")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val amt = amount.toDoubleOrNull() ?: 0.0
        val rate = gstRate.toDoubleOrNull() ?: 0.0

        if (amt > 0) {
            val gstAmount = if (isInclusive) {
                amt - (amt * (100 / (100 + rate)))
            } else {
                amt * (rate / 100)
            }
            val netAmount = if (isInclusive) amt - gstAmount else amt
            val totalAmount = if (isInclusive) amt else amt + gstAmount

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GST Summary:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Net Amount: ₹${"%.2f".format(netAmount)}")
                    Text("GST Amount: ₹${"%.2f".format(gstAmount)}")
                    Text("Total Amount: ₹${"%.2f".format(totalAmount)}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RetirementPlanner() {
    var currentAge by remember { mutableStateOf("30") }
    var retirementAge by remember { mutableStateOf("60") }
    var monthlyExpenses by remember { mutableStateOf("30000") }
    var inflationRate by remember { mutableStateOf("6") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Retirement Planner", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = currentAge,
            onValueChange = { currentAge = it },
            label = { Text("Current Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = retirementAge,
            onValueChange = { retirementAge = it },
            label = { Text("Retirement Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = monthlyExpenses,
            onValueChange = { monthlyExpenses = it },
            label = { Text("Current Monthly Expenses (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val age = currentAge.toIntOrNull() ?: 30
        val retAge = retirementAge.toIntOrNull() ?: 60
        val exp = monthlyExpenses.toDoubleOrNull() ?: 0.0
        val inf = (inflationRate.toDoubleOrNull() ?: 6.0) / 100
        val yearsToRetire = retAge - age

        if (yearsToRetire > 0 && exp > 0) {
            val futureMonthlyExp = exp * (1 + inf).pow(yearsToRetire)
            val corpusRequired = futureMonthlyExp * 12 * 20 // Assuming 20 years post-retirement with net zero return

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Retirement Estimate:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Future Monthly Expense: ₹${"%,.0f".format(futureMonthlyExp)}")
                    Text("Corpus Required: ₹${"%,.0f".format(corpusRequired)}", fontWeight = FontWeight.Bold)
                    Text("(Assuming 20 years survival post retirement)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
