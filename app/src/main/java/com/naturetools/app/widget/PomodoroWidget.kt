package com.naturetools.app.widget
import androidx.compose.ui.unit.sp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.*
import com.naturetools.app.MainActivity

class PomodoroWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                PomodoroContent()
            }
        }
    }

    @Composable
    private fun PomodoroContent() {
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pomodoro", style = TextStyle(fontWeight = FontWeight.Bold))
            Text("25:00", style = TextStyle(fontSize = 24.sp))
            Row {
                Button("Start", onClick = actionStartActivity<MainActivity>())
            }
        }
    }
}

class PomodoroWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PomodoroWidget()
}
