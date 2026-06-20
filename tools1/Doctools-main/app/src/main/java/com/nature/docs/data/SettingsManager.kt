package com.nature.docs.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nature_settings")

/**
 * Manages per-tool settings and general application preferences using DataStore.
 */
object SettingsManager {
    private val LAST_USED_COMPRESS_LEVEL = stringPreferencesKey("last_used_compress_level")
    private val LAST_USED_PDF_QUALITY = stringPreferencesKey("last_used_pdf_quality")

    /**
     * Persists the last used compression level.
     */
    suspend fun saveCompressLevel(context: Context, level: String) {
        context.dataStore.edit { it[LAST_USED_COMPRESS_LEVEL] = level }
    }

    /**
     * Retrieves the last used compression level.
     */
    fun getCompressLevel(context: Context): Flow<String> = context.dataStore.data.map {
        it[LAST_USED_COMPRESS_LEVEL] ?: "Recommended"
    }

    /**
     * Persists the last used PDF export quality.
     */
    suspend fun savePdfQuality(context: Context, quality: String) {
        context.dataStore.edit { it[LAST_USED_PDF_QUALITY] = quality }
    }

    /**
     * Retrieves the last used PDF export quality.
     */
    fun getPdfQuality(context: Context): Flow<String> = context.dataStore.data.map {
        it[LAST_USED_PDF_QUALITY] ?: "Standard"
    }
}
