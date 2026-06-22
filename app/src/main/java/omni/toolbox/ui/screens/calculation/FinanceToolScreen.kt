package omni.toolbox.ui.screens.calculation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
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
                "Dividend Calc" -> DividendCalculator()
                "Inflation Calc" -> InflationCalculator()
                "ROI Calculator" -> RoiCalculator()
                "Salary Calc" -> SalaryCalculator()
                "Stock Profit" -> StockProfitCalculator()
                "Expense Tracker" -> ExpenseTracker()
                "CAGR Calculator", "CAGR Calc" -> CagrCalculator()
                "DCF Calculator", "DCF Calc" -> DcfCalculator()
                "Coin Tracker" -> CoinTracker()
                "NFT Viewer" -> NftViewer()
                "Currency Trends" -> CurrencyTrends()
                "Wallet Explorer" -> WalletExplorer()
                else -> Text("Finance Utility for $title")
            }
        }
    }
}

@Composable
fun CagrCalculator() {
    var startValue by remember { mutableStateOf("1000") }
    var endValue by remember { mutableStateOf("2000") }
    var periods by remember { mutableStateOf("5") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("CAGR Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = startValue, onValueChange = { startValue = it }, label = { Text("Initial Value") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = endValue, onValueChange = { endValue = it }, label = { Text("Final Value") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = periods, onValueChange = { periods = it }, label = { Text("Years") }, modifier = Modifier.fillMaxWidth())

        val start = startValue.toDoubleOrNull() ?: 1.0
        val end = endValue.toDoubleOrNull() ?: 1.0
        val years = periods.toDoubleOrNull() ?: 1.0

        if (years > 0 && start > 0) {
            val cagr = ((end / start).pow(1 / years) - 1) * 100
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("CAGR: %.2f%%".format(cagr), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
fun DcfCalculator() {
    var cashFlow by remember { mutableStateOf("1000") }
    var rate by remember { mutableStateOf("10") }
    var years by remember { mutableStateOf("5") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("DCF Calculator (NPV)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = cashFlow, onValueChange = { cashFlow = it }, label = { Text("Annual Cash Flow") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Discount Rate (%)") }, modifier = Modifier.fillMaxWidth())

        val cf = cashFlow.toDoubleOrNull() ?: 0.0
        val r = (rate.toDoubleOrNull() ?: 10.0) / 100
        val y = years.toIntOrNull() ?: 5

        var npv = 0.0
        for (i in 1..y) {
            npv += cf / (1 + r).pow(i)
        }

        Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Present Value (NPV): %.2f".format(npv), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun ExpenseTracker() {
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }
    val expenses = remember { mutableStateListOf<Pair<String, Double>>() }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Expense Tracker", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        Button(onClick = {
            val amt = amount.toDoubleOrNull() ?: 0.0
            if (amt > 0) {
                expenses.add(category to amt)
                amount = ""
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Expense")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: ${expenses.sumOf { it.second }}", style = MaterialTheme.typography.headlineSmall)

        expenses.forEach { (cat, amt) ->
            ListItem(headlineContent = { Text(cat) }, trailingContent = { Text("%.2f".format(amt)) })
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
fun DividendCalculator() {
    var stockPrice by remember { mutableStateOf("100") }
    var dividendYield by remember { mutableStateOf("4") }
    var sharesOwned by remember { mutableStateOf("10") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Dividend Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = stockPrice, onValueChange = { stockPrice = it }, label = { Text("Stock Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dividendYield, onValueChange = { dividendYield = it }, label = { Text("Dividend Yield (%)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = sharesOwned, onValueChange = { sharesOwned = it }, label = { Text("Shares Owned") }, modifier = Modifier.fillMaxWidth())

        val price = stockPrice.toDoubleOrNull() ?: 0.0
        val yield = (dividendYield.toDoubleOrNull() ?: 0.0) / 100
        val shares = sharesOwned.toDoubleOrNull() ?: 0.0

        if (price > 0 && shares > 0) {
            val annualDividend = price * yield * shares
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Annual Dividend: $${"%.2f".format(annualDividend)}")
                    Text("Monthly Dividend: $${"%.2f".format(annualDividend / 12)}")
                }
            }
        }
    }
}

@Composable
fun InflationCalculator() {
    var amount by remember { mutableStateOf("1000") }
    var inflationRate by remember { mutableStateOf("3") }
    var years by remember { mutableStateOf("10") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Inflation Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = inflationRate, onValueChange = { inflationRate = it }, label = { Text("Inflation Rate (%)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = years, onValueChange = { years = it }, label = { Text("Years") }, modifier = Modifier.fillMaxWidth())

        val amt = amount.toDoubleOrNull() ?: 0.0
        val rate = (inflationRate.toDoubleOrNull() ?: 0.0) / 100
        val yr = years.toDoubleOrNull() ?: 0.0

        if (amt > 0 && yr > 0) {
            val futureValue = amt * (1 + rate).pow(yr)
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Future Value: ${"%.2f".format(futureValue)}")
                    Text("Purchasing Power Loss: ${"%.2f".format(futureValue - amt)}")
                }
            }
        }
    }
}

@Composable
fun RoiCalculator() {
    var initialValue by remember { mutableStateOf("1000") }
    var finalValue by remember { mutableStateOf("1500") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("ROI Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = initialValue, onValueChange = { initialValue = it }, label = { Text("Initial Investment") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = finalValue, onValueChange = { finalValue = it }, label = { Text("Final Value") }, modifier = Modifier.fillMaxWidth())

        val initial = initialValue.toDoubleOrNull() ?: 0.0
        val final = finalValue.toDoubleOrNull() ?: 0.0

        if (initial > 0) {
            val roi = ((final - initial) / initial) * 100
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Return on Investment (ROI): ${"%.2f".format(roi)}%")
                    Text("Net Profit: ${"%.2f".format(final - initial)}")
                }
            }
        }
    }
}


@Composable
fun SalaryCalculator() {
    var hourlyRate by remember { mutableStateOf("25") }
    var hoursPerWeek by remember { mutableStateOf("40") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Salary Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = hourlyRate, onValueChange = { hourlyRate = it }, label = { Text("Hourly Rate") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = hoursPerWeek, onValueChange = { hoursPerWeek = it }, label = { Text("Hours Per Week") }, modifier = Modifier.fillMaxWidth())

        val rate = hourlyRate.toDoubleOrNull() ?: 0.0
        val hours = hoursPerWeek.toDoubleOrNull() ?: 0.0

        if (rate > 0 && hours > 0) {
            val weekly = rate * hours
            val yearly = weekly * 52
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Weekly Salary: ${"%.2f".format(weekly)}")
                    Text("Monthly Salary: ${"%.2f".format(yearly / 12)}")
                    Text("Yearly Salary: ${"%.2f".format(yearly)}")
                }
            }
        }
    }
}

@Composable
fun StockProfitCalculator() {
    var buyPrice by remember { mutableStateOf("100") }
    var sellPrice by remember { mutableStateOf("120") }
    var shares by remember { mutableStateOf("10") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Stock Profit Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = buyPrice, onValueChange = { buyPrice = it }, label = { Text("Buy Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = sellPrice, onValueChange = { sellPrice = it }, label = { Text("Sell Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = shares, onValueChange = { shares = it }, label = { Text("Shares") }, modifier = Modifier.fillMaxWidth())

        val buy = buyPrice.toDoubleOrNull() ?: 0.0
        val sell = sellPrice.toDoubleOrNull() ?: 0.0
        val sh = shares.toDoubleOrNull() ?: 0.0

        if (sh > 0) {
            val profit = (sell - buy) * sh
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Profit: ${"%.2f".format(profit)}", color = if (profit >= 0) Color(0xFF4CAF50) else Color.Red)
                    Text("Profit Percentage: ${"%.2f".format(((sell - buy) / buy) * 100)}%")
                }
            }
        }
    }
}

@Composable
fun UnitPriceCalculator() {
    var totalPrice by remember { mutableStateOf("10") }
    var quantity by remember { mutableStateOf("2") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Unit Price Calculator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = totalPrice, onValueChange = { totalPrice = it }, label = { Text("Total Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())

        val price = totalPrice.toDoubleOrNull() ?: 0.0
        val qty = quantity.toDoubleOrNull() ?: 0.0

        if (qty > 0) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Price per Unit: ${"%.2f".format(price / qty)}")
                }
            }
        }
    }
}

@Composable
fun CoinTracker() {
    val coins = remember { listOf("Bitcoin" to 65432.10, "Ethereum" to 3456.78, "Solana" to 145.67, "Cardano" to 0.45) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Market Prices (Live-sim)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        coins.forEach { (name, price) ->
            ListItem(
                headlineContent = { Text(name) },
                trailingContent = { Text("$${"%,.2f".format(price)}") },
                leadingContent = { Icon(Icons.Default.MonetizationOn, null) }
            )
        }
    }
}

@Composable
fun NftViewer() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("NFT Portfolio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Icon(Icons.Default.Token, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.secondary)
        Text("Connect wallet to view your digital collectibles.")
        Button(onClick = {}, Modifier.padding(16.dp)) { Text("Connect Wallet") }
    }
}

@Composable
fun CurrencyTrends() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Currency Trends (24h)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        listOf("USD/EUR" to "+0.25%", "GBP/USD" to "-0.12%", "USD/JPY" to "+0.45%").forEach { (pair, trend) ->
            ListItem(
                headlineContent = { Text(pair) },
                trailingContent = { Text(trend, color = if (trend.startsWith("+")) Color.Green else Color.Red) }
            )
        }
    }
}

@Composable
fun WalletExplorer() {
    var address by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Blockchain Explorer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Wallet Address") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {}, Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Explore Transactions") }
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
