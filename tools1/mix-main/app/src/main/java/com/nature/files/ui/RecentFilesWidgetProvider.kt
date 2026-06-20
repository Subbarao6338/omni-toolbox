package com.nature.files.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nature.files.MainActivity
import com.nature.files.R
import com.nature.files.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Home screen widget for "Recent Growth" (recent files).
 * Metaphor: showing the newest sprouts in the grove.
 */
class RecentFilesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.recent_files_widget)
        // Nature Design Mandate: Consistent background
        views.setInt(R.id.widget_root, "setBackgroundColor", android.graphics.Color.parseColor("#F7F9F4"))

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            // Quality Gate: Optimized query to fetch only needed items
            val recentFilesList = db.fileDao().getRecentFilesSync(5)

            views.removeAllViews(R.id.recent_files_container)

            recentFilesList.forEach { file ->
                val itemView = RemoteViews(context.packageName, R.layout.widget_item_file)
                itemView.setTextViewText(R.id.file_name, file.name)

                val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("path", File(file.path).parent)
                }
                val pendingIntent = PendingIntent.getActivity(context, file.path.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)
                itemView.setOnClickPendingIntent(R.id.file_item_root, pendingIntent)

                views.addView(R.id.recent_files_container, itemView)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
