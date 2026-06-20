package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // "Low", "Medium", "High"
    val dueDate: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "account_profiles")
data class AccountProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val platform: String, // "GDrive", "Mega", "Notion"
    val accountName: String,
    val email: String,
    val credentialToken: String = "",
    val isActive: Boolean = false
)

@Entity(tableName = "automation_rules")
data class AutomationRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val triggerType: String, // "SHAKE", "TIMER", "BATTERY_HEALTH", "SCREEN_ON"
    val actionType: String, // "CLEAN_CACHE", "PASSWORD_GEN", "LOG_STATS", "SYNC_TASKS"
    val isActive: Boolean = true
)

@Entity(tableName = "documents")
data class DocumentItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val fileType: String, // "PDF", "DOCX", "TXT", "HTML", "MHTML", "MD"
    val content: String,
    val accountEmail: String = "offline", // associated profile
    val lastAccessed: Long = System.currentTimeMillis(),
    val bookmarkedPage: Int = 0,
    val isLocal: Boolean = true
)

@Entity(tableName = "scraped_threads")
data class ScrapedThread(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val forumName: String,
    val threadUrl: String,
    val threadTitle: String,
    val parsedContent: String,
    val pagesCount: Int = 1,
    val notionPageId: String = "",
    val isSyncedToNotion: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val extractedMedia: String = "", // e.g. "image_01.png, video_embed_yt.mp4"
    val subpagesList: String = "" // e.g. "Page 1: Introduction, Page 2: General Settings, Page 3: Optimizations"
)

@Entity(tableName = "scraping_rules")
data class ScrapingRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ruleName: String,
    val targetDomain: String,
    val threadLevels: Int = 3,
    val maxPagesPerThread: Int = 5,
    val extractImages: Boolean = true,
    val extractVideos: Boolean = true,
    val extractDocuments: Boolean = true,
    val notionProfileId: Int = 0,
    val isActive: Boolean = true
)
