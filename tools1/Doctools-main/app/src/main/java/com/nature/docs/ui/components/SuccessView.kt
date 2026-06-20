package com.nature.docs.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.ui.theme.*

@Composable
fun SuccessView(
    message: String = "Task Complete",
    subMessage: String = "Saved successfully",
    processingTime: String = "", 
    onDone: () -> Unit, 
    onProcessMore: () -> Unit,
    onPreview: (() -> Unit)? = null,
    showPreviewButton: Boolean = true,
    accentColor: Color = BotanicalGreen
) {
    Column(
        modifier = Modifier.fillMaxSize(), 
        horizontalAlignment = Alignment.CenterHorizontally, 
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp), 
            shape = CircleShape, 
            color = BotanicalGreen.copy(alpha = 0.1f)
        ) {
            Icon(
                Icons.Filled.Check, 
                null, 
                tint = BotanicalGreen,
                modifier = Modifier.padding(24.dp).size(48.dp)
            )
        }
        
        Spacer(Modifier.height(32.dp))
        
        Text(message, style = MaterialTheme.typography.displayLarge, color = InkBrown)
        
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
            if (subMessage.isNotBlank()) {
                Text(subMessage, style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.6f))
            }
            if (processingTime.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = BotanicalGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "DONE IN $processingTime", 
                        style = MaterialTheme.typography.labelSmall,
                        color = BotanicalGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
        
        Spacer(Modifier.height(64.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onPreview != null && showPreviewButton) {
                Button(
                    onClick = { onPreview.invoke() },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BotanicalGreen, contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.Visibility, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("OPEN RESULTS", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }

            OutlinedButton(
                onClick = onProcessMore,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BotanicalGreen.copy(0.3f))
            ) {
                Text("PROCESS MORE", style = MaterialTheme.typography.labelSmall, color = BotanicalGreen)
            }

            
            TextButton(
                onClick = onDone,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("BACK TO CATALOG", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.4f))
            }
        }
    }
}
