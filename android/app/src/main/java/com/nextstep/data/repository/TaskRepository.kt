package com.nextstep.data.repository

import android.util.Log
import com.nextstep.data.db.TaskDao
import com.nextstep.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "TaskRepository"

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {
    
    init {
        Log.d(TAG, "TaskRepository initialized")
    }
    
    val allTasksFlow: Flow<List<Task>> = taskDao.getAllTasksFlow()
        .flowOn(Dispatchers.IO)
        .catch { e -> 
            Log.e(TAG, "Error fetching tasks flow", e)
            throw e
        }

    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        try {
            val tasks = taskDao.getAllTasks()
            Log.d(TAG, "Got ${tasks.size} tasks")
            return@withContext tasks
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all tasks", e)
            throw e
        }
    }

    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        try {
            val id = taskDao.insertTask(task)
            Log.d(TAG, "Task inserted with id: $id")
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting task", e)
            throw e
        }
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.updateTask(task)
            Log.d(TAG, "Updated $rows task(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
            throw e
        }
    }

    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) {
        try {
            val rows = taskDao.deleteTask(task)
            Log.d(TAG, "Deleted $rows task(s)")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task", e)
            throw e
        }
    }
} 