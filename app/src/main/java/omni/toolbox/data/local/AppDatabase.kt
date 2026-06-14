package omni.toolbox.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import omni.toolbox.model.*

@Database(
    entities = [
        Note::class,
        ChecklistItem::class,
        WaterLog::class,
        Password::class,
        TaskEntry::class,
        TimeLogEntry::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun passwordDao(): PasswordDao
    abstract fun taskDao(): TaskDao
    abstract fun timeLogDao(): TimeLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nature_tools_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
