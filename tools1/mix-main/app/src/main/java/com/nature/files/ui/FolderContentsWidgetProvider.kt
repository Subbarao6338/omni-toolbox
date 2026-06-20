package com.nature.files.ui

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.nature.files.MainActivity
import com.nature.files.R

/**
 * Home screen widget: Folder Contents ("Grove Patch").
 * Resizable 2x2 or 4x2, shows contents of a user-selected folder.
 */
class FolderContentsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val intent = Intent(context, FolderWidgetService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }

        val views = RemoteViews(context.packageName, R.layout.folder_contents_widget).apply {
            // Nature Design Mandate: Consistent background
            setInt(R.id.widget_root, "setBackgroundColor", android.graphics.Color.parseColor("#F7F9F4"))

            setRemoteAdapter(R.id.folder_list_view, intent)
            setEmptyView(R.id.folder_list_view, R.id.empty_view)

            // Template for item clicks
            val clickIntent = Intent(context, MainActivity::class.java)
            val clickPendingIntent = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setPendingIntentTemplate(R.id.folder_list_view, clickPendingIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
