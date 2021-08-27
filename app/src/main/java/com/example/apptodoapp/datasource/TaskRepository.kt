package com.example.apptodoapp.datasource

import androidx.annotation.WorkerThread
import com.example.apptodoapp.model.Task

class TaskRepository(private val taskDAO: TaskDAO) {

    val list = taskDAO.allTasks()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTask(task: Task) {
        taskDAO.insertTask(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTask(task: Task) {
        taskDAO.updateTask(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteTask(task: Task) {
        taskDAO.deleteAllTaskDetails(task)
    }
}