package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    // --- Account Profiles ---
    @Query("SELECT * FROM account_profiles ORDER BY platform ASC")
    fun getAllProfiles(): Flow<List<AccountProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: AccountProfile)

    @Query("DELETE FROM account_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Int)

    @Query("UPDATE account_profiles SET isActive = (id = :activeId) WHERE platform = :platform")
    suspend fun setActiveProfile(activeId: Int, platform: String)

    // --- Automation Rules ---
    @Query("SELECT * FROM automation_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<AutomationRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: AutomationRule)

    @Query("DELETE FROM automation_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Int)

    // --- Documents ---
    @Query("SELECT * FROM documents ORDER BY lastAccessed DESC")
    fun getAllDocuments(): Flow<List<DocumentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentItem)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Int)

    // --- Scraped Threads ---
    @Query("SELECT * FROM scraped_threads ORDER BY timestamp DESC")
    fun getAllThreads(): Flow<List<ScrapedThread>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ScrapedThread)

    @Query("DELETE FROM scraped_threads WHERE id = :id")
    suspend fun deleteThreadById(id: Int)

    // --- Scraping Rules ---
    @Query("SELECT * FROM scraping_rules ORDER BY id DESC")
    fun getAllScrapingRules(): Flow<List<ScrapingRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrapingRule(rule: ScrapingRule)

    @Query("DELETE FROM scraping_rules WHERE id = :id")
    suspend fun deleteScrapingRuleById(id: Int)
}
