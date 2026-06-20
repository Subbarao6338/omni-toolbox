package com.nature.docs.data.ml

import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.tasks.await

object HandwritingEngine {

    suspend fun recognizeHandwriting(ink: Ink): String {
        val modelIdentifier = com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US") ?: return ""
        val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
        val recognizer = DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build()
        )

        return try {
            val result = recognizer.recognize(ink).await()
            result.candidates.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
