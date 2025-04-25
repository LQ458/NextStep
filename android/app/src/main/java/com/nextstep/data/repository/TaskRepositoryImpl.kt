package com.nextstep.data.repository

import android.util.Log
import com.nextstep.data.db.TaskDao
import com.nextstep.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TaskRepositoryImpl"

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    init {
        Log.d(TAG, "TaskRepositoryImpl initialized")
    }

    override fun getAllTasksFlow(): Flow<List<Task>> {
        return taskDao.getAllTasksFlow()
            .flowOn(Dispatchers.IO)
            .catch { e -> 
                Log.e(TAG, "Error fetching tasks flow", e) 
                throw e
            }
    }

    override fun getTasksForProject(projectId: Long): Flow<List<Task>> {
        return taskDao.getTasksForProject(projectId)
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error fetching tasks by project", e)
                throw e
            }
    }

    override fun getTasksByLabelFlow(label: String): Flow<List<Task>> {
        return taskDao.getTasksByLabelFlow(label)
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error fetching tasks by label", e)
                throw e
            }
    }

    override fun getTasksWithDueDateFlow(): Flow<List<Task>> {
        return taskDao.getTasksWithDueDateFlow()
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error fetching tasks with due dates", e)
                throw e
            }
    }

    override fun getOverdueTasksFlow(): Flow<List<Task>> {
        return taskDao.getOverdueTasksFlow()
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error fetching overdue tasks", e)
                throw e
            }
    }

    override fun getTasksForPeriod(startTime: Long, endTime: Long): Flow<List<Task>> {
        return taskDao.getTasksForPeriod(startTime, endTime)
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error fetching tasks for period", e)
                throw e
            }
    }

    override fun getTasksForToday(): Flow<List<Task>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTime = calendar.timeInMillis
        
        return getTasksForPeriod(startTime, endTime)
    }

    override fun getTasksForWeek(): Flow<List<Task>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTime = calendar.timeInMillis
        
        return getTasksForPeriod(startTime, endTime)
    }

    override fun getTasksForMonth(year: Int, month: Int): Flow<List<Task>> {
        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth())
        
        val startTime = firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endTime = lastDay.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return getTasksForPeriod(startTime, endTime)
    }

    override fun getTaskById(taskId: Long): Flow<Task?> {
        return taskDao.getTaskById(taskId)
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error fetching task by id", e)
                throw e
            }
    }

    override fun searchTasksFlow(query: String): Flow<List<Task>> {
        return taskDao.searchTasksFlow(query)
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error searching tasks", e)
                throw e
            }
    }

    override suspend fun getTasksByDate(date: LocalDate): List<Task> = withContext(Dispatchers.IO) {
        try {
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = date.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            return@withContext taskDao.getTasksInDateRangeSync(startOfDay, endOfDay)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks by date: $date", e)
            throw e
        }
    }

    override suspend fun getTaskCountInDateRange(startDate: LocalDate, endDate: LocalDate): Map<Long, Int> = withContext(Dispatchers.IO) {
        try {
            val start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            return@withContext getTaskCountInDateRange(start, end)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting task count in date range", e)
            throw e
        }
    }

    override suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        try {
            val tasks = taskDao.getAllTasks()
            Log.d(TAG, "Got ${tasks.size} tasks")
            return@withContext tasks
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all tasks", e)
            throw e
        }
    }

    override suspend fun insertTask(task: Task): Long = withContext(Dispatchers.IO) {
        try {
            val id = taskDao.insertTask(task)
            Log.d(TAG, "Task inserted with id: $id")
            return@withContext id
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting task", e)
            throw e
        }
    }

    override suspend fun updateTask(task: Task): Int = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.updateTask(task)
            Log.d(TAG, "Updated $rows task(s)")
            return@withContext rows
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
            throw e
        }
    }

    override suspend fun deleteTask(task: Task): Int = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.deleteTask(task)
            Log.d(TAG, "Deleted $rows task(s)")
            return@withContext rows
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
            throw e
        }
    }

    override suspend fun updateTaskCompletionStatus(taskId: Long, completed: Boolean): Int = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.updateTaskCompletionStatus(taskId, completed)
            Log.d(TAG, "Updated completion status of $rows task(s)")
            return@withContext rows
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task completion status", e)
            throw e
        }
    }

    override suspend fun updateTaskDueDate(taskId: Long, dueDate: Long?): Int = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.updateTaskDueDate(taskId, dueDate)
            Log.d(TAG, "Updated due date of $rows task(s)")
            return@withContext rows
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task due date", e)
            throw e
        }
    }

    override suspend fun updateTaskPriority(taskId: Long, priority: Int): Int = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.updateTaskPriority(taskId, priority)
            Log.d(TAG, "Updated priority of $rows task(s)")
            return@withContext rows
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task priority", e)
            throw e
        }
    }

    override fun getTaskCountInDateRange(startTime: Long, endTime: Long): Map<Long, Int> {
        // 这里不能直接使用suspend函数，需要使用协程库提供的runBlocking
        // 这不是最佳实践，但在这个场景下可以工作
        val tasks = kotlinx.coroutines.runBlocking {
            taskDao.getTasksInDateRangeSync(startTime, endTime)
        }
        
        // Group tasks by day and count them
        return tasks.groupBy { task -> 
            normalizeToStartOfDay(task.dueDate ?: 0)
        }.mapValues { it.value.size }
    }
    
    private fun normalizeToStartOfDay(timestamp: Long): Long {
        // Use calendar to get start of day
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override suspend fun deleteTasksByProjectId(projectId: Long): Int = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.deleteTasksByProjectId(projectId)
            Log.d(TAG, "Deleted $rows tasks for project id: $projectId")
            return@withContext rows
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting tasks by project id", e)
            throw e
        }
    }
} 