package omni.toolbox

import omni.toolbox.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {
    @Test
    fun testAddition() {
        val viewModel = CalculatorViewModel()
        viewModel.onDigit("5")
        viewModel.onOperator("+")
        viewModel.onDigit("3")
        viewModel.calculate()
        assertEquals("8", viewModel.display)
    }
}
