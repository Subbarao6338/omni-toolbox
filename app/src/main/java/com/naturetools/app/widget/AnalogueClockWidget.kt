package com.naturetools.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.appwidget.AndroidRemoteViews
import android.widget.RemoteViews
import com.naturetools.app.R

class AnalogueClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            AnalogueClockContent(context.packageName)
        }
    }

    @Composable
    private fun AnalogueClockContent(packageName: String) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AndroidRemoteViews(
                remoteViews = RemoteViews(
                    packageName,
                    R.layout.widget_analogue_clock
                )
            )
        }
    }
}

class AnalogueClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AnalogueClockWidget()
}
