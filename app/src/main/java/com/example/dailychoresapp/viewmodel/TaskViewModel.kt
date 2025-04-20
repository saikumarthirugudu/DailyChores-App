package com.example.dailychoresapp.viewmodel

import androidx.lifecycle.*
import com.example.dailychoresapp.data.model.Task
import com.example.dailychoresapp.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<Task>> = repository.allTasks
    val incompleteTasks: LiveData<List<Task>> = repository.incompleteTasks
    val completedTasks: LiveData<List<Task>> = repository.completedTasks
    val completedCount: LiveData<Int> = repository.completedCount
    val totalTaskCount: LiveData<Int> = repository.totalTaskCount

    private val _searchResults = MutableLiveData<List<Task>>()
    val searchResults: LiveData<List<Task>> get() = _searchResults

    private fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun insert(task: Task) = insertTask(task) // Alias for UI calls

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun deleteAllCompletedTasks() = viewModelScope.launch {
        repository.deleteAllCompletedTasks()
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
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
