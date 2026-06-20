package com.nature.files.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDao {
    @Query("SELECT * FROM file_index WHERE path = :path")
    suspend fun getFileByPath(path: String): FileEntity?

    @Query("SELECT * FROM file_index")
    suspend fun getAllFiles(): List<FileEntity>

    @Query("SELECT * FROM file_index")
    fun getAllFilesSync(): List<FileEntity>

    @Query("SELECT * FROM file_index ORDER BY lastModified DESC LIMIT :limit")
    fun getRecentFilesSync(limit: Int): List<FileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileEntity>)

    @Query("DELETE FROM file_index WHERE path = :path")
    suspend fun deleteFileByPath(path: String)

    @Query("SELECT * FROM file_index WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getFilesByTag(tag: String): List<FileEntity>

    @Query("UPDATE file_index SET tags = :tags WHERE path = :path")
    suspend fun updateTags(path: String, tags: String?)

    @Query("SELECT * FROM file_index WHERE name LIKE '%' || :query || '%' LIMIT 1000")
    suspend fun searchFiles(query: String): List<FileEntity>
}
