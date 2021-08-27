package com.example.apptodoapp.datasource

import android.app.Application

class TaskApplication: Application() {
    private val database by lazy { TaskDatabase.database(this)}
    val repository by lazy { TaskRepository(database.taskDao())}
}
