package com.nature.files.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nature.files.ui.theme.*

@Composable
fun ColorPickerDialog(
    onDismiss: () -> Unit,
    onColorSelected: (String) -> Unit
) {
    val naturePresets = listOf(
        CanopyGreen, BarkBrown, SunbeamYellow, Clay, LichenGrey,
        MapleRed, HarvestOrange, Moonlight, FrostBlue, MagmaRed
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Folder Essence") },
        text = {
            Column {
                Text("Select a color to represent this grove:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(naturePresets) { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, shape = CircleShape)
                                .clickable {
                                    onColorSelected("#" + Integer.toHexString(color.toArgb()).substring(2))
                                }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun Color.toArgb(): Int {
    return (alpha * 255.0f + 0.5f).toInt() shl 24 or
           (red * 255.0f + 0.5f).toInt() shl 16 or
           (green * 255.0f + 0.5f).toInt() shl 8 or
           (blue * 255.0f + 0.5f).toInt()
}
