package com.naturetools.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.color.ColorProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import com.naturetools.app.MainActivity
import android.content.ComponentName

class UnitConverterWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                ConverterWidgetContent()
            }
        }
    }

    @Composable
    private fun ConverterWidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(12.dp)
                .clickable(actionStartActivity(ComponentName("com.naturetools.app", "com.naturetools.app.MainActivity"))),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Unit Converter",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = GlanceModifier.padding(4.dp).background(GlanceTheme.colors.primaryContainer)) {
                    Text("km", style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onPrimaryContainer))
                }
                Text(" ↔ ", style = TextStyle(fontSize = 16.sp))
                Box(modifier = GlanceModifier.padding(4.dp).background(GlanceTheme.colors.primaryContainer)) {
                    Text("mi", style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onPrimaryContainer))
                }
            }
        }
    }
}

class UnitConverterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UnitConverterWidget()
}
