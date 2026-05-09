package com.naturetools.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.action.clickable
import com.naturetools.app.MainActivity
import com.naturetools.app.ui.theme.NatureToolsTheme

class AltimeterWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                AltimeterWidgetContent()
            }
        }
    }

    @Composable
    private fun AltimeterWidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Altimeter",
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
            Text(
                text = "124m",
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = androidx.compose.ui.unit.TextUnit.Unspecified
                )
            )
        }
    }
}

class AltimeterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AltimeterWidget()
}
