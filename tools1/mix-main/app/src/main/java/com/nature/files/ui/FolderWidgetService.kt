package com.nature.files.ui

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.nature.files.R
import com.nature.files.data.FileItem
import com.nature.files.data.FileRepository
import com.nature.files.data.SortOrder
import java.io.File

class FolderWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FolderWidgetFactory(this.applicationContext, intent)
    }
}

class FolderWidgetFactory(private val context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    private var files: List<FileItem> = emptyList()
    private val repository = FileRepository(context)
    private val rootPath = Environment.getExternalStorageDirectory().absolutePath

    override fun onCreate() {}

    override fun onDataSetChanged() {
        // In a real app, the folder path would be configurable.
        // Defaulting to root for this implementation.
        files = repository.getFiles(rootPath, SortOrder.NAME_ASC, false)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = files.size

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= files.size) return RemoteViews(context.packageName, R.layout.widget_item_file)

        val file = files[position]
        val views = RemoteViews(context.packageName, R.layout.widget_item_file)
        views.setTextViewText(R.id.file_name, file.name)

        val fillInIntent = Intent().apply {
            putExtra("path", if (file.isDirectory) file.path else File(file.path).parent)
        }
        views.setOnClickFillInIntent(R.id.file_item_root, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
