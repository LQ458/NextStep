package com.nextstep.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nextstep.data.model.Label
import com.nextstep.data.model.Project
import com.nextstep.data.model.Task
import com.nextstep.data.util.Converters

private const val TAG = "AppDatabase"

@Database(
    entities = [Task::class, Project::class, Label::class], 
    version = 2, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun labelDao(): LabelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加新列到tasks表
                database.execSQL("ALTER TABLE tasks ADD COLUMN dueDate INTEGER")
                database.execSQL("ALTER TABLE tasks ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE tasks ADD COLUMN projectId INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE tasks ADD COLUMN labels TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE tasks ADD COLUMN reminder INTEGER")
                database.execSQL("ALTER TABLE tasks ADD COLUMN recurrence TEXT")
                database.execSQL("ALTER TABLE tasks ADD COLUMN syncStatus INTEGER NOT NULL DEFAULT 0")
                
                // 创建projects表
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS projects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "color INTEGER NOT NULL DEFAULT 0, " +
                    "isArchived INTEGER NOT NULL DEFAULT 0, " +
                    "order INTEGER NOT NULL DEFAULT 0, " +
                    "createdAt INTEGER NOT NULL, " +
                    "syncStatus INTEGER NOT NULL DEFAULT 0)"
                )
                
                // 创建labels表
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS labels (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "color INTEGER NOT NULL DEFAULT 0, " +
                    "order INTEGER NOT NULL DEFAULT 0, " +
                    "createdAt INTEGER NOT NULL, " +
                    "syncStatus INTEGER NOT NULL DEFAULT 0)"
                )
                
                // 创建一个默认项目
                database.execSQL(
                    "INSERT INTO projects (name, createdAt) VALUES ('收集箱', " + 
                    System.currentTimeMillis() + ")"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "Creating new database instance")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nextstep_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 