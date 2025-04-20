package com.example.dailychoresapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailychoresapp.data.model.Task

@Dao
interface TaskDao {

    // Insert or update a task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Fetch all tasks, ordered by due date
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): LiveData<List<Task>>

    // Incomplete and Completed task lists
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getIncompleteTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY dueDate DESC")
    fun getCompletedTasks(): LiveData<List<Task>>

    // Search tasks by title or description
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): LiveData<List<Task>>

    // Delete all completed tasks
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()

    // Count queries
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): LiveData<Int>

    // Filter by category
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY dueDate ASC")
    fun getTasksByCategory(category: String): LiveData<List<Task>>

    // Fetch a task by its ID (used in Task Detail screen)
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): LiveData<Task>
}