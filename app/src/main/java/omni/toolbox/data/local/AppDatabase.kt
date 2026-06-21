package omni.toolbox.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import omni.toolbox.data.local.dao.NavigationDao
import omni.toolbox.data.local.entity.BeaconEntity
import omni.toolbox.data.local.entity.PathEntity
import omni.toolbox.data.local.entity.WaypointEntity
import omni.toolbox.model.*

@Database(
    entities = [
        Note::class,
        ChecklistItem::class,
        WaterLog::class,
        Password::class,
        TaskEntry::class,
        TimeLogEntry::class,
        BeaconEntity::class,
        PathEntity::class,
        WaypointEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun passwordDao(): PasswordDao
    abstract fun taskDao(): TaskDao
    abstract fun timeLogDao(): TimeLogDao
    abstract fun navigationDao(): NavigationDao

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
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO tasks (title, status, timestamp) VALUES ('Pdf to mdx', 'Todo', ${System.currentTimeMillis()})")
                        db.execSQL("INSERT INTO tasks (title, status, timestamp) VALUES ('Pdf to mhtml', 'Todo', ${System.currentTimeMillis()})")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
