package com.example.apptodoapp.datasource

import androidx.room.*
import com.example.apptodoapp.model.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDAO {

    @Query("SELECT * FROM tasks_table ORDER BY date, time")
    fun allTasks():  Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteAllTaskDetails(task: Task)
}