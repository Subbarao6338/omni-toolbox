package omni.toolbox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdjustmentSlider(
    label: String,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    initialValue: Float = 0.5f,
    onValueChange: (Float) -> Unit = {}
) {
    var value by remember { mutableFloatStateOf(initialValue) }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(java.lang.String.format(java.util.Locale.US, "%.2f", value), style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = {
                value = it
                onValueChange(it)
            },
            valueRange = valueRange
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}
