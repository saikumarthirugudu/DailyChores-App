package com.example.dailychoresapp.data.database

import android.content.Context
import androidx.room.*
import com.example.dailychoresapp.data.model.Task

@Database(entities = [Task::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks_database"
                )
                    .fallbackToDestructiveMigration()  // Clears DB if migration fails
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}