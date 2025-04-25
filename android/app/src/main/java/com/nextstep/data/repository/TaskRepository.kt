package com.nextstep.data.repository

import com.nextstep.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun getAllTasksFlow(): Flow<List<Task>>
    
    fun getTasksForProject(projectId: Long): Flow<List<Task>>
    
    fun getTasksByLabelFlow(label: String): Flow<List<Task>>
    
    fun getTasksWithDueDateFlow(): Flow<List<Task>>
    
    fun getOverdueTasksFlow(): Flow<List<Task>>
    
    fun getTasksForPeriod(startTime: Long, endTime: Long): Flow<List<Task>>
    
    fun getTaskById(taskId: Long): Flow<Task?>
    
    fun searchTasksFlow(query: String): Flow<List<Task>>
    
    suspend fun getAllTasks(): List<Task>
    
    suspend fun insertTask(task: Task): Long
    
    suspend fun updateTask(task: Task): Int
    
    suspend fun deleteTask(task: Task): Int
    
    suspend fun updateTaskCompletionStatus(taskId: Long, completed: Boolean): Int
    
    suspend fun updateTaskDueDate(taskId: Long, dueDate: Long?): Int
    
    suspend fun updateTaskPriority(taskId: Long, priority: Int): Int
    
    fun getTaskCountInDateRange(startTime: Long, endTime: Long): Map<Long, Int>
    
    fun getTasksForToday(): Flow<List<Task>>
    
    fun getTasksForWeek(): Flow<List<Task>>
    
    fun getTasksForMonth(year: Int, month: Int): Flow<List<Task>>
    
    suspend fun getTasksByDate(date: LocalDate): List<Task>
    
    suspend fun getTaskCountInDateRange(startDate: LocalDate, endDate: LocalDate): Map<Long, Int>
    
    suspend fun deleteTasksByProjectId(projectId: Long): Int
} 