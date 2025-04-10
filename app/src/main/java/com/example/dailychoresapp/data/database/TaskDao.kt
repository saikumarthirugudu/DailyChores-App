package com.example.dailychoresapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailychoresapp.data.model.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM Task")
    fun getAllTasks(): LiveData<List<Task>>

    // Fetch a single task by ID
    @Query("SELECT * FROM Task WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): LiveData<Task?>

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)
}
