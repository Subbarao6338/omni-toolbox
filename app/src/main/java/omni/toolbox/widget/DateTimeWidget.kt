package omni.toolbox.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import omni.toolbox.R
import androidx.glance.text.FontWeight
import androidx.glance.appwidget.AndroidRemoteViews
import android.widget.RemoteViews

class DateTimeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            DateTimeContent(context.packageName)
        }
    }

    @Composable
    private fun DateTimeContent(packageName: String) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                AndroidRemoteViews(
                    remoteViews = RemoteViews(
                        packageName,
                        R.layout.widget_date_time
                    )
                )
            }
            Spacer(modifier = GlanceModifier.width(16.dp))
            Column(
                modifier = GlanceModifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nature",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Tools",
                    style = TextStyle(
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

class DateTimeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DateTimeWidget()
}
