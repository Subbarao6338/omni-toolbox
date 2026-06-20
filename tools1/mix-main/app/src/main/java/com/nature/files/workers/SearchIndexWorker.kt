package com.nature.files.workers

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nature.files.data.db.AppDatabase
import com.nature.files.data.db.FileEntity
import com.nature.files.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Quality Gate: Incremental index for fast search.
 * Updated via FileObserver or periodic background work.
 */
class SearchIndexWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val db = AppDatabase.getDatabase(context)
    private val fileDao = db.fileDao()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val root = Environment.getExternalStorageDirectory()
        try {
            indexDirectory(root)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun indexDirectory(directory: File) {
        val files = directory.listFiles() ?: return
        val entities = files.map { file ->
            FileEntity(
                path = file.absolutePath,
                name = file.name,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) 0 else file.length(),
                lastModified = file.lastModified(),
                mimeType = FileUtils.getMimeType(file)
            )
        }
        fileDao.insertFiles(entities)

        files.filter { it.isDirectory }.forEach { indexDirectory(it) }
    }
}
