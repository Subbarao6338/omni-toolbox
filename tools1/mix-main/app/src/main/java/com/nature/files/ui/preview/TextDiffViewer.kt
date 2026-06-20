package com.nature.files.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.Spectral
import com.nature.files.ui.theme.leafLitter

@Composable
fun TextDiffViewer(
    file1Content: String,
    file2Content: String,
    file1Name: String,
    file2Name: String,
    onClose: () -> Unit
) {
    val lines1 = file1Content.lines()
    val lines2 = file2Content.lines()
    val maxLines = maxOf(lines1.size, lines2.size)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .leafLitter(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Forest Difference Engine",
                style = MaterialTheme.typography.titleLarge,
                color = BarkBrown
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                file1Name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = BarkBrown.copy(alpha = 0.7f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                file2Name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                color = BarkBrown.copy(alpha = 0.7f)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items((0 until maxLines).toList()) { index ->
                val line1 = lines1.getOrNull(index)
                val line2 = lines2.getOrNull(index)

                val diffColor = when {
                    line1 == line2 -> Color.Transparent
                    line1 == null -> Color.Green.copy(alpha = 0.1f) // Added
                    line2 == null -> Color.Red.copy(alpha = 0.1f)   // Removed
                    else -> Color.Yellow.copy(alpha = 0.1f)         // Changed
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(diffColor)
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = line1 ?: "",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        color = BarkBrown
                    )
                    Divider(modifier = Modifier.fillMaxHeight().width(1.dp).background(BarkBrown.copy(alpha = 0.1f)))
                    Text(
                        text = line2 ?: "",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        ),
                        color = BarkBrown
                    )
                }
            }
        }
    }
}
