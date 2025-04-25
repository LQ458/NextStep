package com.nextstep.data.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.nextstep.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.Date

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY priority DESC, due_date ASC, created_at DESC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE project_id = :projectId ORDER BY priority DESC, due_date ASC, created_at DESC")
    fun getTasksForProject(projectId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE labels LIKE '%' || :label || '%' ORDER BY priority DESC, due_date ASC, created_at DESC")
    fun getTasksByLabelFlow(label: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE due_date IS NOT NULL ORDER BY due_date ASC")
    fun getTasksWithDueDateFlow(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE due_date < :currentTime AND completed = 0 ORDER BY due_date ASC")
    fun getOverdueTasksFlow(currentTime: Long = System.currentTimeMillis()): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startTime AND :endTime ORDER BY due_date ASC")
    fun getTasksForPeriod(startTime: Long, endTime: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startTime AND :endTime ORDER BY due_date ASC")
    suspend fun getTasksInDateRangeSync(startTime: Long, endTime: Long): List<Task>
    
    @Query("SELECT * FROM tasks WHERE due_date BETWEEN :startTime AND :endTime ORDER BY due_date ASC")
    suspend fun getTasksByDate(startTime: Long, endTime: Long): List<Task>
    
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchTasksFlow(query: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks ORDER BY priority DESC, due_date ASC, created_at DESC")
    suspend fun getAllTasks(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task): Int

    @Delete
    suspend fun deleteTask(task: Task): Int
    
    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    suspend fun updateTaskCompletionStatus(taskId: Long, completed: Boolean): Int
    
    @Query("UPDATE tasks SET due_date = :dueDate WHERE id = :taskId")
    suspend fun updateTaskDueDate(taskId: Long, dueDate: Long?): Int
    
    @Query("UPDATE tasks SET priority = :priority WHERE id = :taskId")
    suspend fun updateTaskPriority(taskId: Long, priority: Int): Int
    
    @Query("DELETE FROM tasks WHERE project_id = :projectId")
    suspend fun deleteTasksByProjectId(projectId: Long): Int
} 