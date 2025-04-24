package com.nextstep.data.db

import androidx.room.*
import com.nextstep.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task): Int

    @Delete
    suspend fun deleteTask(task: Task): Int
} 