package com.naturetools.app.widget

import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.naturetools.app.R

class StorageWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalGB = totalBlocks * blockSize / (1024 * 1024 * 1024)
            val availableGB = availableBlocks * blockSize / (1024 * 1024 * 1024)
            val usedGB = totalGB - availableGB
            val percentUsed = (usedGB.toFloat() / totalGB.toFloat() * 100).toInt()

            StorageWidgetContent(usedGB, totalGB, percentUsed)
        }
    }

    @Composable
    private fun StorageWidgetContent(used: Long, total: Long, percent: Int) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(android.R.drawable.dialog_holo_light_frame))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Storage", style = TextStyle(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified))
            Text("$used GB / $total GB", style = TextStyle(fontWeight = androidx.glance.text.FontWeight.Bold))
            Text("$percent% Used")
        }
    }
}

class StorageWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StorageWidget()
}
