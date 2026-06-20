package com.nature.docs.data.ai

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object AIHub {

    suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()
        val translator = Translation.getClient(options)

        return try {
            translator.downloadModelIfNeeded().await()
            translator.translate(text).await()
        } catch (e: Exception) {
            "Translation error"
        }
    }

    // Placeholder for local Whisper-style transcription
    suspend fun transcribeAudio(audioPath: String): String {
        return "Transcribed text from $audioPath"
    }
}
