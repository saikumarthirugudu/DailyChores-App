package com.example.dailychoresapp.repository

import androidx.lifecycle.LiveData
import com.example.dailychoresapp.data.database.TaskDao
import com.example.dailychoresapp.data.model.Task

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }
}
