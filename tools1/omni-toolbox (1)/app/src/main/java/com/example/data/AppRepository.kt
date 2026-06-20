package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    // --- Tasks ---
    val allTasks: Flow<List<Task>> = appDao.getAllTasks()
    suspend fun insertTask(task: Task) = appDao.insertTask(task)
    suspend fun updateTask(task: Task) = appDao.updateTask(task)
    suspend fun deleteTaskById(id: Int) = appDao.deleteTaskById(id)

    // --- Profiles ---
    val allProfiles: Flow<List<AccountProfile>> = appDao.getAllProfiles()
    suspend fun insertProfile(profile: AccountProfile) = appDao.insertProfile(profile)
    suspend fun deleteProfileById(id: Int) = appDao.deleteProfileById(id)
    suspend fun setActiveProfile(activeId: Int, platform: String) = appDao.setActiveProfile(activeId, platform)

    // --- Automation Rules ---
    val allRules: Flow<List<AutomationRule>> = appDao.getAllRules()
    suspend fun insertRule(rule: AutomationRule) = appDao.insertRule(rule)
    suspend fun deleteRuleById(id: Int) = appDao.deleteRuleById(id)

    // --- Documents ---
    val allDocuments: Flow<List<DocumentItem>> = appDao.getAllDocuments()
    suspend fun insertDocument(doc: DocumentItem) = appDao.insertDocument(doc)
    suspend fun deleteDocumentById(id: Int) = appDao.deleteDocumentById(id)

    // --- Scraped Threads ---
    val allThreads: Flow<List<ScrapedThread>> = appDao.getAllThreads()
    suspend fun insertThread(thread: ScrapedThread) = appDao.insertThread(thread)
    suspend fun deleteThreadById(id: Int) = appDao.deleteThreadById(id)

    // --- Scraping Rules ---
    val allScrapingRules: Flow<List<ScrapingRule>> = appDao.getAllScrapingRules()
    suspend fun insertScrapingRule(rule: ScrapingRule) = appDao.insertScrapingRule(rule)
    suspend fun deleteScrapingRuleById(id: Int) = appDao.deleteScrapingRuleById(id)
}
