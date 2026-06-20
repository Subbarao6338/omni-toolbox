package com.nature.files.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FileEntity::class, FolderPreferenceEntity::class, SidebarShortcutEntity::class, AppSettingsEntity::class, CloudConnectionEntity::class], version = 8)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun folderPreferenceDao(): FolderPreferenceDao
    abstract fun sidebarShortcutDao(): SidebarShortcutDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun cloudConnectionDao(): CloudConnectionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "files-db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
