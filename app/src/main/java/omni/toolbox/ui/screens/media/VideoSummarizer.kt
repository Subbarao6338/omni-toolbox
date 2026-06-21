package omni.toolbox.ui.screens.media

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VideoSummarizer {
    suspend fun summarize(apiKey: String, captions: String): String = withContext(Dispatchers.IO) {
        try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
            val response = generativeModel.generateContent(content {
                text("Summarize the following video captions concisely: \n\n $captions")
            })
            response.text ?: "No summary generated."
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
