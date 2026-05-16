package com.naturetools.app.widget

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.text.FontWeight

class SystemMonitorWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        @Suppress("UNUSED_VARIABLE") val totalMem = memInfo.totalMem / (1024 * 1024 * 1024.0)
        @Suppress("UNUSED_VARIABLE") val availMem = memInfo.availMem / (1024 * 1024 * 1024.0)
        val memUsage = ((memInfo.totalMem - memInfo.availMem).toDouble() / memInfo.totalMem * 100).toInt()

        val stat = StatFs(Environment.getDataDirectory().path)
        @Suppress("UNUSED_VARIABLE") val totalStorage = (stat.blockCountLong * stat.blockSizeLong) / (1024 * 1024 * 1024.0)
        @Suppress("UNUSED_VARIABLE") val availStorage = (stat.availableBlocksLong * stat.blockSizeLong) / (1024 * 1024 * 1024.0)
        val storageUsage = (((stat.blockCountLong - stat.availableBlocksLong).toDouble() / stat.blockCountLong) * 100).toInt()

        provideContent {
            GlanceTheme {
                SystemMonitorContent(memUsage, storageUsage)
            }
        }
    }

    @Composable
    private fun SystemMonitorContent(mem: Int, storage: Int) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(8.dp)
                .cornerRadius(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "System Info",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            MonitorRow("RAM", mem)
            MonitorRow("Disk", storage)
        }
    }

    @Composable
    private fun MonitorRow(label: String, pct: Int) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$label: ", style = TextStyle(color = GlanceTheme.colors.onSurface, fontSize = 12.sp))
            Text("$pct%", style = TextStyle(color = if (pct > 90) GlanceTheme.colors.error else GlanceTheme.colors.secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold))
        }
    }
}

class SystemMonitorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SystemMonitorWidget()
}
