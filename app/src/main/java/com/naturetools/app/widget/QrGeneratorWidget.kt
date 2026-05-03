package com.naturetools.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.naturetools.app.MainActivity

class QrGeneratorWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                QrWidgetContent()
            }
        }
    }

    @Composable
    private fun QrWidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(8.dp)
                .appWidgetBackground(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QR Generator",
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Button(
                text = "Create from Clipboard",
                onClick = actionStartActivity<MainActivity>()
            )
        }
    }
}

class QrGeneratorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QrGeneratorWidget()
}
