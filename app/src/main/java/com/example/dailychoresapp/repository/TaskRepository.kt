package com.example.dailychoresapp.repository

import androidx.lifecycle.LiveData
import com.example.dailychoresapp.data.database.TaskDao
import com.example.dailychoresapp.data.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val incompleteTasks: LiveData<List<Task>> = taskDao.getIncompleteTasks()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks()
    val completedCount: LiveData<Int> = taskDao.getCompletedCount()
    val totalTaskCount: LiveData<Int> = taskDao.getTotalTaskCount()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteAllCompletedTasks() {
        taskDao.deleteAllCompletedTasks()
    }

    fun searchTasks(query: String): LiveData<List<Task>> {
        return taskDao.searchTasks(query)
    }

    fun getTasksByCategory(category: String): LiveData<List<Task>> {
        return taskDao.getTasksByCategory(category)
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }
}
