package com.nature.docs.data

import android.content.Context
import android.net.Uri
import androidx.work.*
import com.nature.docs.data.pdf.PdfEngine
import java.io.File

class ProcessingWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val inputUriStr = inputData.getString("input_uri") ?: return Result.failure()
        val toolType = inputData.getString("tool_type") ?: return Result.failure()
        val inputUri = Uri.parse(inputUriStr)

        return try {
            when (toolType) {
                "compress" -> {
                    val doc = PdfEngine.loadDocument(applicationContext, inputUri) ?: return Result.failure()
                    val output = FileVault.getTempFile(applicationContext, ".pdf")
                    PdfEngine.saveDocument(doc, output)
                    Result.success(workDataOf("output_path" to output.absolutePath))
                }
                else -> Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun enqueue(context: Context, inputUri: String, toolType: String) {
            val workRequest = OneTimeWorkRequestBuilder<ProcessingWorker>()
                .setInputData(workDataOf("input_uri" to inputUri, "tool_type" to toolType))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
