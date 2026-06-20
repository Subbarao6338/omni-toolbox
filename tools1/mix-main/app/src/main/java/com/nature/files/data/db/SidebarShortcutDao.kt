package com.nature.files.data.db

import androidx.room.*

@Dao
interface SidebarShortcutDao {
    @Query("SELECT * FROM sidebar_shortcuts ORDER BY `order` ASC")
    suspend fun getAllShortcuts(): List<SidebarShortcutEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcuts(shortcuts: List<SidebarShortcutEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortcut(shortcut: SidebarShortcutEntity)

    @Query("DELETE FROM sidebar_shortcuts WHERE id = :id")
    suspend fun deleteShortcut(id: String)
}
