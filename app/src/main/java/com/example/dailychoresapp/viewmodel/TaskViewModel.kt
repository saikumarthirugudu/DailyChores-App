package com.example.dailychoresapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.dailychoresapp.data.database.AppDatabase
import com.example.dailychoresapp.data.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val taskDao = database.taskDao()

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // ✅ Fetch task by ID
    fun getTaskById(taskId: Int): LiveData<Task?> {
        return taskDao.getTaskById(taskId)
    }

    fun insert(task: Task) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }
}
