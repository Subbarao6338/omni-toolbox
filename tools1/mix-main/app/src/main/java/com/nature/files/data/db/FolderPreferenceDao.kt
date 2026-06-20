package com.nature.files.data.db

import androidx.room.*

@Dao
interface FolderPreferenceDao {
    @Query("SELECT * FROM folder_preferences WHERE path = :path")
    suspend fun getPreference(path: String): FolderPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: FolderPreferenceEntity)
}
