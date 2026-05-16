package com.naturetools.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.GlanceTheme
import com.naturetools.app.MainActivity
import com.naturetools.app.R

class CurrencyConverterWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CurrencyWidgetContent(context)
            }
        }
    }

    @Composable
    private fun CurrencyWidgetContent(@Suppress("UNUSED_PARAMETER") context: Context) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Currency Converter",
                style = TextStyle(color = GlanceTheme.colors.primary)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("USD", style = TextStyle(fontWeight = androidx.glance.text.FontWeight.Bold, color = GlanceTheme.colors.onSurface))
                Text(" ⇄ ", style = TextStyle(color = GlanceTheme.colors.secondary))
                Text("EUR", style = TextStyle(fontWeight = androidx.glance.text.FontWeight.Bold, color = GlanceTheme.colors.onSurface))
            }
        }
    }
}

class CurrencyConverterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CurrencyConverterWidget()
}
