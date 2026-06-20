package com.nature.files.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nature.files.data.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileOperationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = FileRepository(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val operation = inputData.getString(KEY_OPERATION) ?: return@withContext Result.failure()
        val sourcePaths = inputData.getStringArray(KEY_SOURCE_PATHS) ?: return@withContext Result.failure()
        val destinationPath = inputData.getString(KEY_DESTINATION_PATH) ?: return@withContext Result.failure()
        val secureDelete = inputData.getBoolean(KEY_SECURE_DELETE, false)

        var successCount = 0
        val total = sourcePaths.size
        val startTime = System.currentTimeMillis()

        try {
            sourcePaths.forEachIndexed { index, path ->
                if (isStopped) return@withContext Result.retry()

                // Quality Gate: Ensure path-based mutual exclusion
                val success = com.nature.files.utils.FileOperationLock.withLocks(listOf(path, destinationPath)) {
                    try {
                        when (operation) {
                            OP_COPY -> com.nature.files.domain.usecase.CopyFileUseCase(repository.getStorageProvider())
                                .execute(com.nature.files.domain.usecase.CopyFileUseCase.Params(path, destinationPath))
                            OP_MOVE -> {
                                // For Move: ensure source is intact if copy fails or interrupted
                                val copyUC = com.nature.files.domain.usecase.CopyFileUseCase(repository.getStorageProvider())
                                val deleteUC = com.nature.files.domain.usecase.DeleteFileUseCase(repository.getStorageProvider())
                                com.nature.files.domain.usecase.MoveFileUseCase(repository.getStorageProvider(), copyUC, deleteUC)
                                    .execute(com.nature.files.domain.usecase.MoveFileUseCase.Params(path, destinationPath))
                            }
                            OP_DELETE -> com.nature.files.domain.usecase.DeleteFileUseCase(repository.getStorageProvider())
                                .execute(com.nature.files.domain.usecase.DeleteFileUseCase.Params(path, secureDelete))
                            else -> false
                        }
                    } catch (e: java.io.IOException) {
                        // Quality Gate: Catch IO exceptions (SD/OTG ejection)
                        // In a real app, we might offer a resume here
                        false
                    } catch (e: Exception) {
                        // Log failure but continue if possible or fail gracefully
                        false
                    }
                }

                if (success) successCount++

                // Quality Gate: Destination integrity check
                if (operation != OP_DELETE) {
                    val destDir = File(destinationPath)
                    if (!destDir.exists() || !destDir.canWrite()) {
                        throw Exception("Destination is not accessible or writable")
                    }
                }

                val progress = ((index + 1) * 100 / total)
                val elapsedTime = System.currentTimeMillis() - startTime
                val speed = if (elapsedTime > 0) (index + 1).toFloat() / (elapsedTime / 1000f) else 0f
                val eta = if (speed > 0) ((total - (index + 1)) / speed).toLong() else 0L

                setProgress(workDataOf(
                    KEY_PROGRESS to progress,
                    KEY_SPEED to speed.toString(),
                    KEY_ETA to eta
                ))
            }

            if (successCount == total) {
                Result.success()
            } else {
                Result.failure(workDataOf(KEY_ERROR to "Some operations failed"))
            }
        } catch (e: Exception) {
            Result.failure(workDataOf(KEY_ERROR to e.message))
        }
    }

    companion object {
        const val KEY_OPERATION = "operation"
        const val KEY_SOURCE_PATHS = "source_paths"
        const val KEY_DESTINATION_PATH = "destination_path"
        const val KEY_PROGRESS = "progress"
        const val KEY_SPEED = "speed"
        const val KEY_ETA = "eta"
        const val KEY_ERROR = "error"
        const val KEY_SECURE_DELETE = "secure_delete"

        const val OP_COPY = "copy"
        const val OP_MOVE = "move"
        const val OP_DELETE = "delete"
    }
}
