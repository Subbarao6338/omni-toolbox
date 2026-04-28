package com.naturetools.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import android.content.ComponentName
import com.naturetools.app.MainActivity

class CompassWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ImageProvider(android.R.drawable.dialog_holo_light_frame))
                    .padding(8.dp)
                    .clickable(actionStartActivity(ComponentName(context, MainActivity::class.java))),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Compass", style = TextStyle(fontWeight = androidx.glance.text.FontWeight.Bold))
                Text("Tap to Open")
            }
        }
    }
}

class CompassWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CompassWidget()
}
