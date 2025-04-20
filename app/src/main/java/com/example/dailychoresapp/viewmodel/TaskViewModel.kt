package com.example.dailychoresapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.repository.TaskRepository
import com.example.dailychoresapp.util.ReminderScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel(application: Application, private val repository: TaskRepository) : AndroidViewModel(application) {

    val allTasks: LiveData<List<Task>> = repository.allTasks
    val incompleteTasks: LiveData<List<Task>> = repository.incompleteTasks
    val completedTasks: LiveData<List<Task>> = repository.completedTasks
    val completedCount: LiveData<Int> = repository.completedCount
    val totalTaskCount: LiveData<Int> = repository.totalTaskCount

    private val _searchResults = MutableLiveData<List<Task>>()
    val searchResults: LiveData<List<Task>> get() = _searchResults

    fun insert(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
        val dueDateMillis = parseDateToMillis(task.dueDate)
        ReminderScheduler.scheduleReminder(getApplication(), task.id, task.title, dueDateMillis)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
        val dueDateMillis = parseDateToMillis(task.dueDate)
        if (!task.isCompleted) {
            ReminderScheduler.scheduleReminder(getApplication(), task.id, task.title, dueDateMillis)
        } else {
            ReminderScheduler.cancelReminder(getApplication(), task.id)
        }
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
        ReminderScheduler.cancelReminder(getApplication(), task.id)
    }

    fun deleteAllCompletedTasks() = viewModelScope.launch {
        repository.deleteAllCompletedTasks()
        // Optional: Cancel all reminders if needed (not implemented per-task here)
    }

    fun searchTasks(query: String) {
        repository.searchTasks(query).observeForever {
            _searchResults.postValue(it)
        }
    }

    fun getTasksByCategory(category: String): LiveData<List<Task>> {
        return repository.getTasksByCategory(category)
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return repository.getTaskById(taskId)
    }

    private fun parseDateToMillis(dateString: String): Long {
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formatter.parse(dateString)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}

class TaskViewModelFactory(
    private val application: Application,
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}