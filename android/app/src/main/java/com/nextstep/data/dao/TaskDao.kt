package com.nextstep.data.dao

import androidx.room.*
import com.nextstep.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY priority DESC, due_date ASC, created_at DESC")
    fun getAllTasksFlow(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>
    
    @Query("SELECT * FROM tasks WHERE project_id = :projectId ORDER BY priority DESC, due_date ASC, created_at DESC")
    fun getTasksForProject(projectId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startTime AND :endTime ORDER BY due_date ASC")
    fun getTasksForPeriod(startTime: Long, endTime: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startTime AND :endTime ORDER BY due_date ASC")
    suspend fun getTasksInDateRangeSync(startTime: Long, endTime: Long): List<Task>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Update
    suspend fun updateTask(task: Task): Int
    
    @Delete
    suspend fun deleteTask(task: Task): Int
    
    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    suspend fun updateTaskCompletionStatus(taskId: Long, completed: Boolean): Int
} 