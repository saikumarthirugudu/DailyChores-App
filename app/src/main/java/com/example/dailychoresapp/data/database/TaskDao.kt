package com.example.dailychoresapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailychoresapp.data.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getIncompleteTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): LiveData<Int>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY dueDate ASC")
    fun getTasksByCategory(category: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task>
}
