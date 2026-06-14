package omni.toolbox.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.*

class WorldClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface).padding(8.dp)
                ) {
                    Text("World Clock", style = TextStyle(fontWeight = FontWeight.Bold))
                    ClockItem("London", "12:00 PM")
                    ClockItem("New York", "07:00 AM")
                    ClockItem("Tokyo", "09:00 PM")
                }
            }
        }
    }

    @Composable
    private fun ClockItem(city: String, time: String) {
        Row(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(city, modifier = GlanceModifier.defaultWeight())
            Text(time, style = TextStyle(fontWeight = FontWeight.Medium))
        }
    }
}

class WorldClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WorldClockWidget()
}
