package com.nextstep.ui.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.data.model.Task
import com.nextstep.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TaskViewModel"

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    init {
        Log.d(TAG, "TaskViewModel initialized")
    }

    val tasks: StateFlow<List<Task>> = taskRepository.allTasksFlow
        .catch { e -> 
            Log.e(TAG, "Error collecting tasks", e) 
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            try {
                val task = Task(
                    title = title,
                    description = description
                )
                taskRepository.insertTask(task)
                Log.d(TAG, "Task added: $title")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding task", e)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.updateTask(task)
                Log.d(TAG, "Task updated: ${task.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating task", e)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
                Log.d(TAG, "Task deleted: ${task.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting task", e)
            }
        }
    }
} 