package com.example.apptodoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.apptodoapp.datasource.TaskRepository
import com.example.apptodoapp.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(private val repo: TaskRepository): ViewModel() {
    val tasks = repo.list.asLiveData()

    fun insertTask(task: Task) =
        viewModelScope.launch {
            repo.insertTask(task)
        }

    fun deleteTask(task: Task) =
        viewModelScope.launch{
            repo.deleteTask(task)
        }

    fun updateTask(task: Task) =
        viewModelScope.launch{
            repo.updateTask(task)
        }
}


class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}