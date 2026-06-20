package com.nature.files.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nature.files.ui.theme.*

@Composable
fun NatureDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = ForestFloorBackground,
        titleContentColor = BarkBrown,
        textContentColor = BarkBrown,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = PlusJakartaSans)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        },
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        modifier = Modifier.leafLitter(LichenGrey.copy(alpha = 0.1f))
    )
}
